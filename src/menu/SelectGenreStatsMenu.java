package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ6①] SELECT① 장르별 시청 통계 조회
 * 담당: 곽성은
 * - 장르 입력 → genre_watch_stats_view JOIN content 조회
 * - VIEW + JOIN 사용 [REQ6]
 * - PreparedStatement 사용 [REQ10]
 */

public class SelectGenreStatsMenu {

    public void run(Scanner scanner) {
        System.out.println("\n=== SELECT① 장르별 시청 통계 조회 ===");
        printAvailableGenres();
        System.out.print("장르를 입력하세요: ");
        String genre = scanner.nextLine().trim();

        String sql = """
                SELECT
                    c.content_id,
                    c.title,
                    c.genre,
                    c.release_date,
                    g.content_count,
                    g.subscriber_count,
                    g.avg_watch_duration_min
                FROM content c
                JOIN genre_watch_stats_view g ON c.genre = g.genre
                WHERE c.genre = ?
                ORDER BY c.release_date DESC
                """;

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, genre);
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;

            while (rs.next()) {
                if (!found) {
                    System.out.printf("\n[장르: %s] 통계 요약%n", genre);
                    System.out.printf("  콘텐츠 수: %d | 구독자 수: %d | 평균 시청시간: %.2f분%n",
                            rs.getInt("content_count"),
                            rs.getInt("subscriber_count"),
                            rs.getDouble("avg_watch_duration_min"));
                    System.out.println("\n콘텐츠 목록:");
                    System.out.println("--------------------------------------------------");
                    found = true;
                }
                System.out.printf("  [%d] %s (출시: %s)%n",
                        rs.getInt("content_id"),
                        rs.getString("title"),
                        rs.getString("release_date"));
            }

            if (!found) {
                System.out.println("해당 장르의 콘텐츠가 없습니다.");
            }

        } catch (SQLException e) {
            System.err.println("[오류] " + e.getMessage());
        }
    }

    private void printAvailableGenres() {
        String sql = "SELECT DISTINCT genre FROM content ORDER BY genre";
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            StringBuilder sb = new StringBuilder("사용 가능한 장르: ");
            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(", ");
                sb.append(rs.getString("genre"));
                first = false;
            }
            System.out.println(sb);
        } catch (SQLException e) {
            // 장르 목록 조회 실패 시 무시
        }
    }
}