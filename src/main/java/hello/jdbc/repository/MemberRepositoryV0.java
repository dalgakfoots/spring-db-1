package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 * */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException{
        String sql = "insert into member(member_id , money) " +
                "values (?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            int count = pstmt.executeUpdate(); // 영향 받은 row count

            return member;
        } catch (SQLException e) {
            log.error("db error ",e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstm = con.prepareStatement(sql);
            pstm.setString(1, memberId);

            rs = pstm.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstm , rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = getConnection();
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, money);
            pstm.setString(2, memberId);
            int resultSize = pstm.executeUpdate();
            log.info("result size = {}",resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = getConnection();
            pstm = con.prepareStatement(sql);
            pstm.setString(1, memberId);
            int resultSize = pstm.executeUpdate();
            log.info("result size = {}",resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("error ",e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("error ",e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("error ",e);
            }
        }

    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
