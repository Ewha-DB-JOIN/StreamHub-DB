import menu.*;
import util.DBUtil;

import menu.InsertSubscriptionMenu;
import menu.UpdateSubscriptionPlanMenu;

import java.util.Scanner;


/**
 * [REQ15] 메인 진입점 — 텍스트 기반 while-switch 메뉴
 * 담당: 신우림 (Task 3.1.2 / Task 3.2.13)
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        DBUtil db = DBUtil.getInstance();


        // TODO [Task 3.2.13 신우림]: 각 메뉴 인스턴스 생성 후 아래 case에 run() 연결
        InsertContentMenu        insertContent        = new InsertContentMenu();
        InsertSubscriptionMenu   insertSubscription   = new InsertSubscriptionMenu();
        SelectGenreStatsMenu     selectGenreStats     = new SelectGenreStatsMenu();
        SelectMemberBillingMenu  selectMemberBilling  = new SelectMemberBillingMenu();
        SelectMonthlyRevenueMenu selectMonthlyRevenue = new SelectMonthlyRevenueMenu();
        SelectGenreWatchMenu     selectGenreWatch     = new SelectGenreWatchMenu();
        UpdatePlanPriceMenu      updatePlanPrice      = new UpdatePlanPriceMenu();
        UpdateSubscriptionPlanMenu updateSubPlan      = new UpdateSubscriptionPlanMenu();
        DeleteMemberMenu         deleteMember         = new DeleteMemberMenu();
        DeleteContentMenu        deleteContent        = new DeleteContentMenu();
        AnalyzePriceHistoryMenu  analyzePriceHistory  = new AnalyzePriceHistoryMenu();
        AnalyzeMemberProfileMenu analyzeMemberProfile = new AnalyzeMemberProfileMenu();

        while (true) {
            printMenu();
            int choice = getIntInput("선택: ");

            switch (choice) {
                case 1:  // [REQ5①]  INSERT① 콘텐츠 등록 — 최보경
                    insertContent.run(scanner);
                    break;
                case 2:  // [REQ5②]  INSERT② 구독 등록 — 이태영
                    insertSubscription.run(scanner);
                    break;
                case 3:  // [REQ6①]  SELECT① 장르별 시청 통계 (VIEW+JOIN) — 곽성은
                    selectGenreStats.run(scanner);
                    break;
                case 4:  // [REQ6②]  SELECT② 회원별 결제 이력 (VIEW+JOIN) — 하지수
                    selectMemberBilling.run(scanner);
                    break;
                case 5:  // [REQ7③]  SELECT③ 월별 구독 매출 및 ARPU (GROUP BY) — 곽성은
                    selectMonthlyRevenue.run(scanner);
                    break;
                case 6:  // [REQ7④]  SELECT④ 장르별 평균 시청시간 (GROUP BY) — 최보경
                    selectGenreWatch.run(scanner);
                    break;
                case 7:  // [REQ8①][REQ12] UPDATE① 플랜 가격 변경 (트랜잭션) — 조수민
                    updatePlanPrice.run(scanner);
                    break;
                case 8:  // [REQ8②]  UPDATE② 구독 플랜 변경 — 이태영
                    updateSubPlan.run(scanner);
                    break;
                case 9:  // [REQ9①]  DELETE① 회원 탈퇴 — 박나림
                    deleteMember.run(scanner);
                    break;
                case 10: // [REQ9②]  DELETE② 콘텐츠 삭제 — 최보경
                    deleteContent.run(scanner);
                    break;
                case 11: // [REQ13]  분석① 플랜 가격 변동 전후 매출 비교 — 신우림
                    analyzePriceHistory.run(scanner);
                    break;
                case 12: // [REQ14]  분석② 회원 인적사항 변경 전후 매출 분석 — 박나림
                    analyzeMemberProfile.run(scanner);
                    break;
                case 0:
                    db.close();
                    System.out.println("프로그램을 종료합니다.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 다시 선택하세요.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n======================================");
        System.out.println("         StreamHub 메인 메뉴          ");
        System.out.println("======================================");
        System.out.println("  1.  [INSERT①] 콘텐츠 등록");
        System.out.println("  2.  [INSERT②] 구독 등록");
        System.out.println("  3.  [SELECT①] 장르별 시청 통계 조회");
        System.out.println("  4.  [SELECT②] 회원별 구독·결제 이력 조회");
        System.out.println("  5.  [SELECT③] 월별 구독 매출 및 ARPU");
        System.out.println("  6.  [SELECT④] 장르별 평균 시청시간");
        System.out.println("  7.  [UPDATE①] 플랜 가격 변경");
        System.out.println("  8.  [UPDATE②] 구독 플랜 변경");
        System.out.println("  9.  [DELETE①] 회원 탈퇴");
        System.out.println("  10. [DELETE②] 콘텐츠 삭제");
        System.out.println("  11. [분석①]   플랜 가격 변동 전후 매출 비교");
        System.out.println("  12. [분석②]   회원 인적사항 변경 전후 매출 분석");
        System.out.println("  0.  종료");
        System.out.println("======================================");
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("숫자를 입력하세요: ");
        }
        return scanner.nextInt();
    }
}
