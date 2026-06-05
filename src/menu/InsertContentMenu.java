package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ5①] INSERT① 콘텐츠 등록
 * 담당: 최보경
 * - 제목, 장르, 출시일, 단가, 설명 입력 → content 테이블 INSERT
 * - PreparedStatement 사용 [REQ10]
 */
public class InsertContentMenu {

    public void run(Scanner scanner) {
        System.out.println("\n=== [INSERT①] 콘텐츠 등록 ===");

        // 1. 제목 (필수)
        String title;
        while (true) {
            System.out.print("제목: ");
            title = scanner.nextLine().trim();
            if (!title.isEmpty()) break;
            System.out.println("제목은 필수 입력입니다. 다시 입력하세요.");
        }

        // 2. 장르 (선택, 번호 선택)
        String genre = MenuHelper.selectOptionalGenre(scanner);

        // 3. 출시일 (선택, YYYY-MM-DD)
        String releaseDate;
        while (true) {
            System.out.print("출시일 (YYYY-MM-DD / 없으면 Enter): ");
            releaseDate = scanner.nextLine().trim();
            if (releaseDate.isEmpty()) {
                releaseDate = null;
                break;
            }
            if (releaseDate.matches("\\d{4}-\\d{2}-\\d{2}")) break;
            System.out.println("날짜 형식이 올바르지 않습니다. (예: 2024-01-15)");
        }

        // 4. 단가 (필수, 0 이상, 원 단위 정수)
        double unitPrice;
        while (true) {
            System.out.print("단가(원, 쉼표 없이 숫자만 / 예: 9900): ");
            String priceInput = scanner.nextLine().trim();
            try {
                unitPrice = Double.parseDouble(priceInput);
                if (unitPrice >= 0) break;
                System.out.println("단가는 0 이상이어야 합니다.");
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력하세요. (예: 9.99)");
            }
        }

        // 5. 설명 (선택)
        System.out.print("설명 (없으면 Enter): ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) description = null;

        // 6. INSERT 실행 (PreparedStatement) [REQ10]
        String sql = "INSERT INTO content (title, genre, release_date, unit_price, description) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt =
                     DBUtil.getInstance().getConnection().prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, genre);
            if (releaseDate == null) {
                pstmt.setNull(3, Types.DATE);
            } else {
                pstmt.setDate(3, Date.valueOf(releaseDate));
            }
            pstmt.setDouble(4, unitPrice);
            pstmt.setString(5, description);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ 콘텐츠가 성공적으로 등록되었습니다.");
            } else {
                System.out.println("⚠ 등록에 실패했습니다.");
            }

        } catch (SQLException e) {
            System.out.println("[오류] 콘텐츠 등록 중 문제가 발생했습니다: " + e.getMessage());
        }
    }
}
