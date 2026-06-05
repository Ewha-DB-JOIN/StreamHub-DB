package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ8①][REQ12] UPDATE① 플랜 가격 변경
 * 담당: 조수민
 * - 플랜 ID, 새 가격 입력 → subscription_plan.current_price UPDATE
 * - price_history INSERT (변경 이력 기록) [REQ13]
 * - 트랜잭션 처리: autoCommit=false → commit / rollback [REQ12]
 * - PreparedStatement 사용 [REQ10]
 */
public class UpdatePlanPriceMenu {

    public void run(Scanner sc) {

        System.out.println("\n=== [UPDATE①] 플랜 가격 변경 (조수민) ===");
        printPlans();
        System.out.print("변경할 플랜 ID(plan_id) 입력: ");
        int planId;
        try {
            planId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("올바른 숫자를 입력해주세요.");
            return;
        }

        System.out.print("새로운 가격 입력: ");
        int newPrice;
        try {
            newPrice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("올바른 숫자를 입력해주세요.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 1. DB 연결 및 트랜잭션 시작 (REQ12 연계)
            conn = DBUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 2. 플랜 존재 여부 및 기존 가격(old_price) 확인
            String checkSql = "SELECT current_price FROM subscription_plan WHERE plan_id = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, planId);
            rs = pstmt.executeQuery();

            // [롤백 시나리오 테스트 대응] 잘못된 plan_id 입력 시 예외 발생 처리
            if (!rs.next()) {
                System.out.println("\n[⚠️ 경고] 존재하지 않는 플랜 ID입니다.");
                throw new SQLException("존재하지 않는 plan_id가 입력되어 강제로 트랜잭션 롤백을 수행합니다.");
            }

            int oldPrice = rs.getInt("current_price");

            // 사용한 조회 자원 반납
            rs.close();
            pstmt.close();

            // 3. subscription_plan.current_price UPDATE
            String updateSql = "UPDATE subscription_plan SET current_price = ? WHERE plan_id = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setInt(1, newPrice);
            pstmt.setInt(2, planId);
            pstmt.executeUpdate();

            pstmt.close();

            // 4. price_history에 이력 INSERT (valid_from = NOW())
            String insertSql = """
                    INSERT INTO price_history (plan_id, old_price, new_price, valid_from)
                    VALUES (?, ?, ?, NOW())
                    """;
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, planId);
            pstmt.setInt(2, oldPrice);
            pstmt.setInt(3, newPrice);
            pstmt.executeUpdate();

            // 5. 모든 작업 성공 시 커밋 실행
            conn.commit();
            System.out.println("✅ 성공: 플랜 가격 변경 및 변경 이력(price_history) 기록이 완료되었습니다.");

        } catch (SQLException e) {
            // 6. 예외 발생 시 안전하게 롤백 처리
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("\n❌ [트랜잭션 롤백 발생]: " + e.getMessage());
                    System.out.println("❌ 데이터베이스에 아무런 변경 사항도 반영되지 않고 안전하게 취소되었습니다.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // 7. 자원 반납 및 오토커밋 상태 복구 (수동 닫기 정석)
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void printPlans() {
        String sql = "SELECT plan_id, plan_name, current_price FROM subscription_plan ORDER BY plan_id";
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("[구독 플랜 목록]");
            while (rs.next()) {
                System.out.printf("  [%d] %s — %,.0f원%n",
                        rs.getInt("plan_id"),
                        rs.getString("plan_name"),
                        rs.getDouble("current_price"));
            }
        } catch (SQLException e) {
            // 목록 조회 실패 시 무시
        }
    }
}
