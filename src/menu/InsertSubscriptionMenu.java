package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

public class InsertSubscriptionMenu {

    public void execute() {
        Scanner sc = new Scanner(System.in);

        System.out.print("member_id 입력: ");
        int memberId = sc.nextInt();

        System.out.print("plan_id 입력: ");
        int planId = sc.nextInt();

        Connection conn = null;

        try {
            conn=DBUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. member 존재 확인 + region 조회
            String region = getMemberregion(conn, memberId);

            if (region == null || region.isBlank()) {
                System.out.println("회원의 지역 정보가 없습니다.");
                conn.rollback();
                return;
            }

            // 2. subscription_plan 존재 확인
            if (!existsPlan(conn, planId)) {
                System.out.println("존재하지 않는 plan_id입니다.");
                conn.rollback();
                return;
            }

            // 3. subscription INSERT
            String sql = """
                    INSERT INTO subscription
                    (member_id, plan_id, start_date, region_snapshot, status)
                    VALUES (?, ?, CURRENT_DATE, ?, 'active')
                    """;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, memberId);
                pstmt.setInt(2, planId);
                pstmt.setString(3, region);

                int result = pstmt.executeUpdate();

                if (result > 0) {
                    conn.commit();
                    System.out.println("구독 등록이 완료되었습니다.");
                } else {
                    conn.rollback();
                    System.out.println("구독 등록에 실패했습니다.");
                }
            }

        } catch (SQLException e) {
            rollback(conn);
            System.out.println("구독 등록 중 오류가 발생했습니다.");
            System.out.println("오류 내용: " + e.getMessage());
        } finally {
            close(conn);
        }
    }

    private String getMemberregion(Connection conn, int memberId) throws SQLException {
        String sql = "SELECT region FROM member WHERE member_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("region");
                }
            }
        }

        return null;
    }

    private boolean existsPlan(Connection conn, int planId) throws SQLException {
        String sql = "SELECT 1 FROM subscription_plan WHERE plan_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, planId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.out.println("rollback 실패: " + e.getMessage());
            }
        }
    }

    private void close(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.out.println("DB 연결 종료 실패: " + e.getMessage());
            }
        }
    }
}