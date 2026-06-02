package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ7③] SELECT③ 월별 구독 매출 합계 및 ARPU
 * 담당: 곽성은
 * - 연도 입력 → billing 기준 월별 GROUP BY 집계
 * - 집계 함수 + GROUP BY 사용 [REQ7]
 * - PreparedStatement 사용 [REQ10]
 */

public class SelectMonthlyRevenueMenu {

    public void run(Scanner scanner) {
        System.out.println("\n=== SELECT③ 월별 구독 매출 및 ARPU ===");
        System.out.print("조회할 연도를 입력하세요 (예: 2024): ");
        int year = scanner.nextInt();

        String sql = """
                SELECT
                    MONTH(b.billing_date)            AS month,
                    SUM(b.total_amount)              AS total_revenue,
                    COUNT(DISTINCT s.member_id)      AS member_count,
                    ROUND(SUM(b.total_amount) / COUNT(DISTINCT s.member_id), 2) AS arpu
                FROM billing b
                JOIN subscription s ON b.sub_id = s.sub_id
                WHERE YEAR(b.billing_date) = ?
                GROUP BY MONTH(b.billing_date)
                ORDER BY month
                """;

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n[" + year + "년 월별 구독 매출]");
            System.out.println("--------------------------------------------------");
            System.out.printf("%-6s %-15s %-10s %-10s%n", "월", "총매출(원)", "구독자수", "ARPU");
            System.out.println("--------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-6d %-15.2f %-10d %-10.2f%n",
                        rs.getInt("month"),
                        rs.getDouble("total_revenue"),
                        rs.getInt("member_count"),
                        rs.getDouble("arpu"));
            }

            if (!found) {
                System.out.println("해당 연도의 데이터가 없습니다.");
            }

        } catch (SQLException e) {
            System.err.println("[오류] " + e.getMessage());
        }
    }
}