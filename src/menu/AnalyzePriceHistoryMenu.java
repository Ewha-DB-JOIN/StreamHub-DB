package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ13] 분석① 플랜 가격 변동 전후 매출 비교
 * 담당: 신우림
 * - 플랜 ID 입력 → price_history 기준 가격 변동 시점별 매출 집계
 * - billing.applied_price 스냅샷 기준으로 과거 결제액 정확히 반영
 * - PreparedStatement 사용 [REQ10]
 */
public class AnalyzePriceHistoryMenu {

    public void run(Scanner scanner) {
        System.out.println("\n=== [분석①] 플랜 가격 변동 전후 매출 비교 ===");

        // 1. 플랜 ID 입력
        int planId;
        while (true) {
            System.out.print("분석할 플랜 ID: ");
            String input = scanner.nextLine().trim();
            try {
                planId = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력하세요.");
            }
        }

        Connection conn = DBUtil.getInstance().getConnection();

        // 2. 플랜 존재 여부 확인 + 플랜명 출력
        String planName = getPlanName(conn, planId);
        if (planName == null) {
            System.out.println("존재하지 않는 플랜 ID입니다.");
            return;
        }
        System.out.println("\n플랜: [" + planId + "] " + planName);

        // 3. 가격 변동 기간별 매출 집계
        // price_history 기간에 billing_date가 해당하는 billing 행의 applied_price 합산
        String sql =
            "SELECT " +
            "    ph.price_id, " +
            "    ph.old_price, " +
            "    ph.new_price, " +
            "    ph.valid_from, " +
            "    ph.valid_to, " +
            "    COUNT(b.billing_id)          AS billing_count, " +
            "    COALESCE(SUM(b.applied_price), 0) AS total_revenue " +
            "FROM price_history ph " +
            "LEFT JOIN subscription s ON s.plan_id = ph.plan_id " +
            "LEFT JOIN billing b " +
            "    ON b.sub_id = s.sub_id " +
            "    AND b.billing_date >= DATE(ph.valid_from) " +
            "    AND (ph.valid_to IS NULL OR b.billing_date < DATE(ph.valid_to)) " +
            "WHERE ph.plan_id = ? " +
            "GROUP BY ph.price_id, ph.old_price, ph.new_price, ph.valid_from, ph.valid_to " +
            "ORDER BY ph.valid_from";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, planId);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println();
                System.out.printf("%-6s %-12s %-12s %-22s %-22s %-8s %-14s%n",
                        "구간", "이전 가격", "변경 가격", "적용 시작", "적용 종료", "결제 건수", "구간 총 매출");
                System.out.println("-".repeat(100));

                int seq = 1;
                long grandTotal = 0;
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    long revenue = rs.getLong("total_revenue");
                    grandTotal += revenue;

                    String validTo = rs.getString("valid_to");

                    System.out.printf("%-6d %-12.0f %-12.0f %-22s %-22s %-8d %-14s%n",
                            seq++,
                            rs.getDouble("old_price"),
                            rs.getDouble("new_price"),
                            rs.getString("valid_from"),
                            validTo == null ? "(현재)" : validTo,
                            rs.getInt("billing_count"),
                            String.format("%,d 원", revenue));
                }

                if (!found) {
                    System.out.println("해당 플랜의 가격 변동 이력이 없습니다.");
                    return;
                }

                System.out.println("-".repeat(100));
                System.out.printf("%-80s %,d 원%n", "전체 누적 매출:", grandTotal);
            }

        } catch (SQLException e) {
            System.out.println("[오류] 분석 중 문제가 발생했습니다: " + e.getMessage());
        }
    }

    private String getPlanName(Connection conn, int planId) {
        String sql = "SELECT plan_name FROM subscription_plan WHERE plan_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, planId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("plan_name");
            }
        } catch (SQLException e) {
            System.out.println("[오류] 플랜 조회 실패: " + e.getMessage());
        }
        return null;
    }
}
