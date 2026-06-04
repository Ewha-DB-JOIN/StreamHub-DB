package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ9①] DELETE① 회원 탈퇴
 * 담당: 박나림
 */
public class DeleteMemberMenu {

    public void run(Scanner scanner) {
        System.out.println("\n=== [REQ9] 회원 탈퇴 처리 ===");
        System.out.print("탈퇴할 회원 ID 입력: ");

        int memberId;
        try {
            memberId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("올바른 숫자를 입력해주세요.");
            return;
        }

        Connection conn = DBUtil.getInstance().getConnection();

        // 1. 회원 존재 확인 + 정보 출력
        String selectSQL = "SELECT name, email, region FROM member WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("해당 ID의 회원이 존재하지 않습니다.");
                return;
            }
            System.out.println("\n[삭제 대상 회원]");
            System.out.printf("  이름: %s | 이메일: %s | 지역: %s%n",
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("region"));
        } catch (SQLException e) {
            System.out.println("회원 조회 오류: " + e.getMessage());
            return;
        }

        // 2. 최종 확인
        System.out.print("\n정말 탈퇴 처리하시겠습니까? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("탈퇴가 취소되었습니다.");
            return;
        }

        // 3. member 삭제
        // - member_profile_history → CASCADE 자동 삭제
        // - subscription.member_id → SET NULL 자동 처리
        // - billing은 subscription 행 유지되므로 보존됨
        String deleteSQL = "DELETE FROM member WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteSQL)) {
            ps.setInt(1, memberId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("회원 탈퇴 처리가 완료되었습니다.");
                System.out.println("  - 인적사항 변경 이력 삭제 완료 (CASCADE)");
                System.out.println("  - 구독 기록 보존 (member_id SET NULL)");
                System.out.println("  - 결제 기록 보존 (billing 유지)");
            } else {
                System.out.println("삭제 실패: 대상 회원을 찾을 수 없습니다.");
            }
        } catch (SQLException e) {
            System.out.println("삭제 오류: " + e.getMessage());
        }

        System.out.println("=".repeat(50));
    }
}