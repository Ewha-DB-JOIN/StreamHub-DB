package menu;

import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * 담당자: 하지수
 * [REQ6-2] 회원별 결제 이력 조회
 **/

public class BillingMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void showMemberBillingHistory() {
        /**
        * 회원 ID를 입력받아 결제 이력을 조회
        */
        System.out.print("조회할 회원 ID를 입력하세요: ");

        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("숫자를 입력하세요: ");
        }

        int memberId = scanner.nextInt();

        String sql =
                "SELECT member_id, member_name, sub_id, plan_name, region_snapshot, " +
                "billing_id, billing_date, applied_price, total_amount " +
                "FROM vw_member_billing_history " +
                "WHERE member_id = ? " +
                "ORDER BY billing_date";

        try {
            Connection conn = DBUtil.getInstance().getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, memberId);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n==============================================================");
            System.out.println("                  회원별 결제 이력 조회");
            System.out.println("==============================================================");

            System.out.printf(
                    "%-8s %-10s %-8s %-15s %-10s %-12s %-12s %-12s%n",
                    "회원ID",
                    "회원명",
                    "구독ID",
                    "플랜명",
                    "지역",
                    "결제일",
                    "적용가격",
                    "총금액"
            );

            System.out.println("--------------------------------------------------------------");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.printf(
                        "%-8d %-10s %-8d %-15s %-10s %-12s %-12.2f %-12.2f%n",
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        rs.getInt("sub_id"),
                        rs.getString("plan_name"),
                        rs.getString("region_snapshot"),
                        rs.getDate("billing_date"),
                        rs.getBigDecimal("applied_price"),
                        rs.getBigDecimal("total_amount")
                );
            }

            if (!found) {
                System.out.println("해당 회원의 결제 이력이 없습니다.");
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            System.out.println("결제 이력 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}