package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ7④] SELECT④ 장르별 평균 시청시간 및 콘텐츠 수
 * 담당: 최보경
 * - 장르명 입력 → 해당 장르의 평균 시청시간(AVG), 콘텐츠 수(COUNT) 조회
 * - content + watch_history JOIN, GROUP BY genre
 * - PreparedStatement 사용 [REQ10]
 */
public class SelectGenreWatchMenu {

    public void run(Scanner scanner) {
        System.out.println("\n=== [SELECT④] 장르별 평균 시청시간 및 콘텐츠 수 ===");

        // 1. 장르 선택
        String genre = MenuHelper.selectGenre(scanner);

        // 2. 집계 조회 (JOIN + GROUP BY) [REQ7]
        // content와 watch_history를 JOIN하여 장르별 평균 시청시간과 콘텐츠 수 집계
        String sql =
            "SELECT c.genre, "
          + "       COUNT(DISTINCT c.content_id) AS content_count, "
          + "       ROUND(AVG(wh.watch_duration), 2) AS avg_watch_duration "
          + "FROM content c "
          + "LEFT JOIN watch_history wh ON c.content_id = wh.content_id "
          + "WHERE c.genre = ? "
          + "GROUP BY c.genre";

        try (PreparedStatement pstmt =
                     DBUtil.getInstance().getConnection().prepareStatement(sql)) {

            pstmt.setString(1, genre);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String g = rs.getString("genre");
                    int count = rs.getInt("content_count");
                    double avg = rs.getDouble("avg_watch_duration");
                    boolean noWatch = (rs.getObject("avg_watch_duration") == null);

                    System.out.println("\n========================================");
                    System.out.printf("  장르: %s%n", g);
                    System.out.printf("  콘텐츠 수: %d개%n", count);
                    if (noWatch) {
                        System.out.println("  평균 시청시간: 시청 기록 없음");
                    } else {
                        System.out.printf("  평균 시청시간: %.2f분%n", avg);
                    }
                    System.out.println("========================================");
                } else {
                    System.out.println("해당 장르의 콘텐츠가 없습니다.");
                }
            }

        } catch (SQLException e) {
            System.out.println("[오류] 조회 중 문제가 발생했습니다: " + e.getMessage());
        }
    }

}
