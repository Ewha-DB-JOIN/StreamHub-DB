package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ9②] DELETE② 콘텐츠 삭제
 * 담당: 최보경
 * - content_id 입력 → 존재 확인 후 삭제
 * - watch_history FK는 ON DELETE SET NULL이라 시청 이력은 보존됨
 * - PreparedStatement 사용 [REQ10]
 */
public class DeleteContentMenu {

    public void run(Scanner scanner) {
        System.out.println("\n=== [DELETE②] 콘텐츠 삭제 ===");

        // 1. content_id 입력 (필수, 정수)
        int contentId;
        while (true) {
            System.out.print("삭제할 콘텐츠 ID: ");
            String input = scanner.nextLine().trim();
            try {
                contentId = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력하세요.");
            }
        }

        Connection conn = DBUtil.getInstance().getConnection();

        try {
            // 2. 존재 여부 확인 + 정보 출력
            String selectSql = "SELECT title, genre, unit_price FROM content WHERE content_id = ?";
            String title = null;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, contentId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        title = rs.getString("title");
                        System.out.println("\n[삭제 대상]");
                        System.out.println("  제목: " + title);
                        System.out.println("  장르: " + rs.getString("genre"));
                        System.out.println("  단가: " + rs.getBigDecimal("unit_price"));
                    } else {
                        System.out.println("해당 ID의 콘텐츠가 존재하지 않습니다.");
                        return;
                    }
                }
            }

            // 3. 삭제 확인
            System.out.print("\n정말 삭제하시겠습니까? (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("삭제가 취소되었습니다.");
                return;
            }

            // 4. DELETE 실행 (PreparedStatement) [REQ10]
            String deleteSql = "DELETE FROM content WHERE content_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, contentId);
                int rows = deleteStmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("✅ '" + title + "' 콘텐츠가 삭제되었습니다.");
                } else {
                    System.out.println("⚠ 삭제에 실패했습니다.");
                }
            }

        } catch (SQLException e) {
            System.out.println("[오류] 콘텐츠 삭제 중 문제가 발생했습니다: " + e.getMessage());
        }
    }
}
