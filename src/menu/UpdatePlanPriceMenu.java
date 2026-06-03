package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ8①][REQ12] UPDATE① 플랜 가격 변경
 * 담당: 조수민
 * - 플랜 ID, 새 가격 입력 → subscription_plan.current_price UPDATE
 * - price_history INSERT (변경 이력 기록) [REQ13]
 * - 트랜잭션 처리: autoCommit=false → commit / rollback [REQ12]
 * - PreparedStatement 사용 [REQ10]
 */
public class UpdatePlanPriceMenu {

    public void run(Scanner scanner) {
        // TODO [조수민]: 구현
        System.out.println("미구현 - UPDATE① 플랜 가격 변경");
    }
}
