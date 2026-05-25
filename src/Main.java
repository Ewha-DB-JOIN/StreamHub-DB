import util.DBUtil;
import menu.BillingMenu;
import java.util.Scanner;


/**
 * 메인 진입점 - 텍스트 기반 메뉴
 *
 * 각 팀원은 담당 메서드를 구현하고, 해당 case에 호출을 추가하세요.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        DBUtil db = DBUtil.getInstance();
        BillingMenu billingMenu = new BillingMenu();

        while (true) {
            printMenu();
            int choice = getIntInput("선택: ");

            switch (choice) {
                case 1:
                    // TODO: [REQ??] 담당자: 이름
                    // featureOne();
                    System.out.println("미구현");
                    break;
                case 2:
                    // TODO: [REQ6-2] 담당자: 하지수
                    billingMenu.showMemberBillingHistory();
                    break;
                case 3:
                    // TODO: [REQ??] 담당자: 이름
                    // featureThree();
                    System.out.println("미구현");
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
        System.out.println("\n=============================");
        System.out.println("  JOIN 프로젝트 메인 메뉴");
        System.out.println("=============================");
        System.out.println(" 1. 기능 1 (TODO)");
        System.out.println(" 2. 회원별 결제 이력 조회 [REQ6-2]");
        System.out.println(" 3. 기능 3 (TODO)");
        System.out.println(" 0. 종료");
        System.out.println("=============================");
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
