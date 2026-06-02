package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;
import java.math.BigDecimal;
/**
 * [REQ6②] SELECT② 회원별 구독·결제 이력 조회
 * 담당: 하지수
 * - 회원 ID 입력 → member_billing_summary_view 조회
 * - VIEW + JOIN 사용 [REQ6]
 * - PreparedStatement 사용 [REQ10]
 * - 결과: 회원별 구독 이력, 플랜명, 구독 기간, 상태, 지역, 총 결제액 출력
 */
public class SelectMemberBillingMenu {

    public void run(Scanner scanner) {
        System.out.print("조회할 회원 ID를 입력하세요: ");

        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("숫자를 입력하세요: ");
        }

        int memberId = scanner.nextInt();

        String sql =
                "SELECT member_id, name, email, sub_id, plan_name, start_date, end_date, " +
                "status, region_snapshot, total_billed " +
                "FROM member_billing_summary_view " +
                "WHERE member_id = ? " +
                "ORDER BY sub_id";

        try (
                Connection conn = DBUtil.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n==============================================================================================================");
                System.out.println("                                      회원별 구독·결제 이력 조회");
                System.out.println("==============================================================================================================");

                System.out.printf(
                        "%-8s %-10s %-25s %-8s %-15s %-12s %-12s %-10s %-10s %-12s%n",
                        "회원ID", "이름", "이메일", "구독ID", "플랜명",
                        "시작일", "종료일", "상태", "지역", "총결제액"
                );

                System.out.println("--------------------------------------------------------------------------------------------------------------");

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    Date startDate = rs.getDate("start_date");
                    Date endDate = rs.getDate("end_date");
                    BigDecimal totalBilled = rs.getBigDecimal("total_billed");

                    System.out.printf(
                            "%-8d %-10s %-25s %-8d %-15s %-12s %-12s %-10s %-10s %-12.2f%n",
                            rs.getInt("member_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getInt("sub_id"),
                            rs.getString("plan_name"),
                            startDate == null ? "NULL" : startDate.toString(),
                            endDate == null ? "NULL" : endDate.toString(),
                            rs.getString("status"),
                            rs.getString("region_snapshot"),
                            totalBilled == null ? 0.0 : totalBilled.doubleValue()
                    );
                }

                if (!found) {
                    System.out.println("해당 회원의 구독·결제 이력이 없습니다.");
                }
            }

        } catch (SQLException e) {
            System.out.println("회원별 구독·결제 이력 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}