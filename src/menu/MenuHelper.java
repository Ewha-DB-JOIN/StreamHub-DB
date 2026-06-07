package menu;

import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuHelper {

    /** 장르 번호 선택 — 선택된 장르 문자열 반환 */
    public static String selectGenre(Scanner scanner) {
        List<String> genres = new ArrayList<>();
        String sql = "SELECT DISTINCT genre FROM content WHERE genre IS NOT NULL ORDER BY genre";
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) genres.add(rs.getString("genre"));
        } catch (SQLException e) {
            // 목록 조회 실패 시 직접 입력으로 대체
        }

        if (genres.isEmpty()) {
            System.out.print("장르 입력: ");
            return scanner.nextLine().trim();
        }

        System.out.println("[장르 목록]");
        for (int i = 0; i < genres.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, genres.get(i));
        }

        while (true) {
            System.out.printf("번호 선택 (1~%d): ", genres.size());
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= genres.size()) return genres.get(choice - 1);
                System.out.printf("1~%d 사이의 번호를 입력하세요.%n", genres.size());
            } catch (NumberFormatException e) {
                System.out.println("번호를 입력하세요.");
            }
        }
    }

    /** 장르 선택 (선택사항) — 0 또는 Enter 시 null 반환 */
    public static String selectOptionalGenre(Scanner scanner) {
        List<String> genres = new ArrayList<>();
        String sql = "SELECT DISTINCT genre FROM content WHERE genre IS NOT NULL ORDER BY genre";
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) genres.add(rs.getString("genre"));
        } catch (SQLException e) {
            // ignore
        }

        if (genres.isEmpty()) {
            System.out.print("장르 (없으면 Enter): ");
            String g = scanner.nextLine().trim();
            return g.isEmpty() ? null : g;
        }

        System.out.println("[장르 목록]");
        System.out.println("  [0] 없음 (Enter)");
        for (int i = 0; i < genres.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, genres.get(i));
        }

        while (true) {
            System.out.printf("번호 선택 (0~%d, 없으면 0): ", genres.size());
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return null;
            try {
                int choice = Integer.parseInt(input);
                if (choice == 0) return null;
                if (choice >= 1 && choice <= genres.size()) return genres.get(choice - 1);
                System.out.printf("0~%d 사이의 번호를 입력하세요.%n", genres.size());
            } catch (NumberFormatException e) {
                System.out.println("번호를 입력하세요.");
            }
        }
    }

    /** 회원 목록 출력 */
    public static void printMemberList() {
        String sql = "SELECT member_id, name, email, region FROM member ORDER BY member_id";
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("[회원 목록]");
            System.out.printf("  %-6s %-10s %-26s %-10s%n", "ID", "이름", "이메일", "지역");
            System.out.println("  " + "-".repeat(56));
            while (rs.next()) {
                System.out.printf("  %-6d %-10s %-26s %-10s%n",
                        rs.getInt("member_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("region"));
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    /** 콘텐츠 목록 출력 */
    public static void printContentList() {
        String sql = "SELECT content_id, title, genre, unit_price FROM content ORDER BY content_id";
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("[콘텐츠 목록]");
            System.out.printf("  %-6s %-32s %-12s %-10s%n", "ID", "제목", "장르", "단가(원)");
            System.out.println("  " + "-".repeat(64));
            while (rs.next()) {
                System.out.printf("  %-6d %-32s %-12s %,.0f%n",
                        rs.getInt("content_id"),
                        rs.getString("title"),
                        rs.getString("genre") != null ? rs.getString("genre") : "-",
                        rs.getDouble("unit_price"));
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    /** 구독 목록 출력 (활성 구독 우선) */
    public static void printSubscriptionList() {
        String sql = """
                SELECT s.sub_id, COALESCE(m.name, '(탈퇴)') AS name,
                       p.plan_name, s.status, s.start_date
                FROM subscription s
                LEFT JOIN member m ON s.member_id = m.member_id
                JOIN subscription_plan p ON s.plan_id = p.plan_id
                ORDER BY FIELD(s.status,'active','cancelled','expired'), s.sub_id
                """;
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("[구독 목록]");
            System.out.printf("  %-8s %-10s %-18s %-10s %-12s%n",
                    "구독ID", "회원명", "플랜명", "상태", "시작일");
            System.out.println("  " + "-".repeat(62));
            while (rs.next()) {
                System.out.printf("  %-8d %-10s %-18s %-10s %-12s%n",
                        rs.getInt("sub_id"),
                        rs.getString("name"),
                        rs.getString("plan_name"),
                        rs.getString("status"),
                        rs.getString("start_date"));
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    /** 결제 데이터가 있는 연도 목록 출력 */
    public static void printBillingYears() {
        String sql = "SELECT DISTINCT YEAR(billing_date) AS yr FROM billing ORDER BY yr";
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            StringBuilder sb = new StringBuilder("데이터 있는 연도: ");
            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(", ");
                sb.append(rs.getInt("yr")).append("년");
                first = false;
            }
            if (!first) System.out.println(sb);
        } catch (SQLException e) {
            // ignore
        }
    }
}
