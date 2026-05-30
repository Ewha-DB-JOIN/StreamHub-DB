package menu;

import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdateSubscriptionPlanMenu {

    public void execute() {
        Scanner sc = new Scanner(System.in);

        System.out.print("sub_id 입력: ");
        int subId = sc.nextInt();

        System.out.print("새 plan_id 입력: ");
        int newPlanId = sc.nextInt();

        Connection conn = null;

        try {
            conn = DBUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. subscription 존재 확인
            if (!existsSubscription(conn, subId)) {
                System.out.println("존재하지 않는 구독 아이디입니다.");
                conn.rollback();
                return;
            }

            // 2. subscription_plan 존재 확인
            if (!existsPlan(conn, newPlanId)) {
                System.out.println("존재하지 않는 플랜 아이디입니다.");
                conn.rollback();
                return;
            }

            // 3. plan_id UPDATE
            String sql = """
                    UPDATE subscription
                    SET plan_id = ?
                    WHERE sub_id = ?
                    """;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, newPlanId);
                pstmt.setInt(2, subId);

                int result = pstmt.executeUpdate();

                if (result > 0) {
                    conn.commit();
                    System.out.println("구독 플랜 변경이 완료되었습니다.");
                } else {
                    conn.rollback();
                    System.out.println("구독 플랜 변경에 실패했습니다.");
                }
            }

        } catch (SQLException e) {
            rollback(conn);
            System.out.println("구독 플랜 변경 중 오류가 발생했습니다.");
            System.out.println("오류 내용: " + e.getMessage());
        } finally {
            close(conn);
        }
    }

    private boolean existsSubscription(Connection conn, int subId) throws SQLException {
        String sql = "SELECT 1 FROM subscription WHERE sub_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, subId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
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