package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ14] 분석② 회원 인적사항 변경 전후 구독 매출 분석
 * 담당: 박나림
 */
public class AnalyzeMemberProfileMenu {

    public void run(Scanner scanner) {
        System.out.println("\n=== [REQ14] 회원 인적사항 변경 전후 매출 분석 ===");
        System.out.print("분석할 회원 ID 입력: ");

        int memberId;
        try {
            memberId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("올바른 숫자를 입력해주세요.");
            return;
        }

        Connection conn = DBUtil.getInstance().getConnection();

        // 1. 회원 존재 확인
        String memberSQL = "SELECT name, email, region FROM member WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(memberSQL)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("해당 ID의 회원이 존재하지 않습니다.");
                return;
            }
            System.out.println("\n[회원 정보]");
            System.out.printf("  이름: %s | 이메일: %s | 현재 지역: %s%n",
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("region"));
        } catch (SQLException e) {
            System.out.println("회원 조회 오류: " + e.getMessage());
            return;
        }

        // 2. 인적사항 변경 이력 조회
        String historySQL = """
                SELECT history_id, field_name, old_value, new_value, changed_at
                FROM member_profile_history
                WHERE member_id = ?
                ORDER BY changed_at ASC
                """;

        System.out.println("\n[인적사항 변경 이력]");
        System.out.printf("%-5s %-12s %-20s %-20s %-20s%n",
                "ID", "필드", "변경 전", "변경 후", "변경 시각");
        System.out.println("-".repeat(80));

        Timestamp latestChangedAt = null;

        try (PreparedStatement ps = conn.prepareStatement(historySQL)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            boolean hasHistory = false;
            while (rs.next()) {
                hasHistory = true;
                latestChangedAt = rs.getTimestamp("changed_at");
                System.out.printf("%-5d %-12s %-20s %-20s %-20s%n",
                        rs.getInt("history_id"),
                        rs.getString("field_name"),
                        rs.getString("old_value"),
                        rs.getString("new_value"),
                        latestChangedAt.toString());
            }
            if (!hasHistory) {
                System.out.println("  변경 이력이 없습니다.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("이력 조회 오류: " + e.getMessage());
            return;
        }

        // 3. 가장 최근 변경 시점 기준 변경 전/후 매출 비교
        System.out.println("\n[변경 전/후 구독 매출 비교] (가장 최근 변경 기준: " + latestChangedAt + ")");
        System.out.printf("%-10s %-15s %-15s%n", "구간", "구독 건수", "총 매출(원)");
        System.out.println("-".repeat(45));

        String analysisSQL = """
                SELECT
                    CASE
                        WHEN s.start_date < ? THEN 'BEFORE'
                        ELSE 'AFTER'
                    END AS period,
                    COUNT(DISTINCT s.sub_id)    AS subscription_count,
                    COALESCE(SUM(b.total_amount), 0) AS total_revenue
                FROM subscription s
                LEFT JOIN billing b ON s.sub_id = b.sub_id
                WHERE s.member_id = ?
                GROUP BY period
                ORDER BY period DESC
                """;

        try (PreparedStatement ps = conn.prepareStatement(analysisSQL)) {
            ps.setTimestamp(1, latestChangedAt);
            ps.setInt(2, memberId);
            ResultSet rs = ps.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String period = rs.getString("period").equals("BEFORE") ? "변경 전" : "변경 후";
                System.out.printf("%-10s %-15d %-15.2f%n",
                        period,
                        rs.getInt("subscription_count"),
                        rs.getDouble("total_revenue"));
            }
            if (!hasData) {
                System.out.println("  구독/결제 데이터가 없습니다.");
            }
        } catch (SQLException e) {
            System.out.println("매출 분석 오류: " + e.getMessage());
        }

        System.out.println("=".repeat(50));
    }
}