package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ9①] DELETE① 회원 탈퇴
 * 담당: 박나림
 * - 회원 ID 입력 → member 삭제
 * - 삭제 순서: member_profile_history(CASCADE) → subscription.member_id SET NULL → member DELETE
 * - billing은 보존됨 (subscription 행 유지, sub_id 참조 살아있음)
 * - PreparedStatement 사용 [REQ10]
 */
public class DeleteMemberMenu {

    public void run(Scanner scanner) {
        // TODO [박나림]: 구현
        System.out.println("미구현 - DELETE① 회원 탈퇴");
    }
}
