package menu;

import util.DBUtil;
import java.sql.*;
import java.util.Scanner;

/**
 * [REQ13] 분석① 플랜 가격 변동 전후 매출 비교
 * 담당: 신우림
 * - 플랜 ID 입력 → price_history 기준 가격 변동 시점 조회
 * - 변동 전후 기간의 billing.applied_price 합계 비교
 * - applied_price 스냅샷 덕분에 과거 결제 금액 정확히 반영
 * - PreparedStatement 사용 [REQ10]
 */
public class AnalyzePriceHistoryMenu {

    public void run(Scanner scanner) {
        // TODO [신우림]: 구현
        System.out.println("미구현 - 분석① 플랜 가격 변동 전후 매출 비교");
    }
}
