# StreamHub — 온라인 동영상 스트리밍 구독 관리 시스템

Ewha Womans University Database Team Project — Java + JDBC + MySQL

---

## 프로젝트 정보

| 항목 | 내용 |
|------|------|
| 주제 | **StreamHub** — 온라인 동영상 스트리밍 구독 관리 시스템 |
| 제출 마감 | **2026-06-09 23:59** |
| 발표 | 2026-06-11 (수업 시간) |

---

## 개발 환경

| 항목 | 내용 |
|------|------|
| JDK | 17 이상 |
| MySQL | Oracle MySQL HeatWave (Free tier) 또는 MySQL 9.x |
| JDBC Driver | mysql-connector-j-8.0.33 (lib/ 폴더에 포함) |
| IDE | IntelliJ IDEA / Eclipse |
| 메인 클래스 | `Main` (`src/Main.java`) |

---

## 실행 방법

### 1. DB 연결 설정

`src/config/DBConfig.java` 파일에서 본인 환경에 맞게 수정하세요:

```java
public static final String DB_URL  = "jdbc:mysql://<HOST>:3306/<DB명>?useSSL=false&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true";
public static final String DB_USER = "your_mysql_user";
public static final String DB_PASS = "your_mysql_password";
```

> ⚠ `DBConfig.java`는 `.gitignore`에 포함되어 있습니다. 비밀번호를 커밋하지 마세요.

### 2. DB 스키마 초기화

MySQL CLI 또는 Workbench에서 **반드시 아래 순서대로** 실행하세요:

```sql
source sql/dropschema.sql;    -- ① 기존 테이블 삭제 (⚠ 데이터 전부 삭제됨)
source sql/createschema.sql;  -- ② 테이블 / 뷰 / 인덱스 생성
source sql/initdata.sql;      -- ③ 초기 데이터 삽입
```

> ⚠ SQL 파일에 `USE DATABASE` 구문이 없으므로, 실행 전 MySQL에서 직접 사용할 DB를 선택하세요:
> ```sql
> CREATE DATABASE IF NOT EXISTS streamhub;
> USE streamhub;
> ```

### 3. 애플리케이션 실행

**JAR 파일 실행 (권장):**

```bash
java -jar join-app.jar
```

`join-app.jar`는 MySQL Connector/J가 포함된 Fat JAR입니다. 별도 classpath 설정이 필요 없습니다.

**IDE에서 실행:**

`src/Main.java`를 메인 클래스로 직접 실행하세요. classpath에 `lib/mysql-connector-j-8.0.33.jar`를 추가해야 합니다.

---

## 메뉴 구성

| 번호 | 기능 | 담당 | 관련 REQ |
|------|------|------|---------|
| 1 | INSERT① 콘텐츠 등록 | 최보경 | REQ5 |
| 2 | INSERT② 구독 등록 | 이태영 | REQ5 |
| 3 | SELECT① 장르별 시청 통계 조회 | 곽성은 | REQ6 |
| 4 | SELECT② 회원별 구독·결제 이력 조회 | 하지수 | REQ6 |
| 5 | SELECT③ 월별 구독 매출 및 ARPU | 곽성은 | REQ7 |
| 6 | SELECT④ 장르별 평균 시청시간 | 최보경 | REQ7 |
| 7 | UPDATE① 플랜 가격 변경 | 조수민 | REQ8, REQ12 |
| 8 | UPDATE② 구독 플랜 변경 | 이태영 | REQ8 |
| 9 | DELETE① 회원 탈퇴 | 박나림 | REQ9 |
| 10 | DELETE② 콘텐츠 삭제 | 최보경 | REQ9 |
| 11 | 분석① 플랜 가격 변동 전후 매출 비교 | 신우림 | REQ13 |
| 12 | 분석② 회원 인적사항 변경 전후 매출 분석 | 박나림 | REQ14 |
| 0 | 종료 | | |

---

## 폴더 구조

```
StreamHub-DB/
├── join-app.jar               # 실행 가능한 Fat JAR [REQ18]
├── README.md                  # 실행 방법 안내 [REQ19]
├── sql/
│   ├── createschema.sql       # 테이블 8개, 뷰 2개, 인덱스 7개 생성 [REQ1~3][REQ11]
│   ├── initdata.sql           # 초기 데이터 삽입 (각 테이블 10~100 튜플) [REQ4]
│   └── dropschema.sql         # 전체 테이블·뷰 삭제
├── src/
│   ├── Main.java              # 메인 진입점 — 텍스트 메뉴 [REQ15]
│   ├── config/
│   │   └── DBConfig.java      # DB 접속 정보 (로컬 설정, gitignore)
│   ├── util/
│   │   └── DBUtil.java        # JDBC 연결 싱글톤
│   └── menu/
│       ├── MenuHelper.java    # 공통 UX 헬퍼 (목록 출력, 번호 선택)
│       ├── InsertContentMenu.java
│       ├── InsertSubscriptionMenu.java
│       ├── SelectGenreStatsMenu.java
│       ├── SelectMemberBillingMenu.java
│       ├── SelectMonthlyRevenueMenu.java
│       ├── SelectGenreWatchMenu.java
│       ├── UpdatePlanPriceMenu.java
│       ├── UpdateSubscriptionPlanMenu.java
│       ├── DeleteMemberMenu.java
│       ├── DeleteContentMenu.java
│       ├── AnalyzePriceHistoryMenu.java
│       └── AnalyzeMemberProfileMenu.java
├── lib/
│   └── mysql-connector-j-8.0.33.jar   # JDBC 드라이버
├── docs/
│   ├── report.md              # 최종 보고서 [REQ20]
│   └── proposal.md            # 제안서
└── Screenshots/               # 실행 결과 캡처 이미지 [REQ20]
```

---

## 요구사항 체크리스트

| REQ | 내용 | 담당 | 상태 |
|-----|------|------|------|
| REQ1 | HW2-1 스키마 확장 (8개 테이블) | 신우림 | ✅ |
| REQ2 | 테이블 8개 + 뷰 2개 | 신우림 | ✅ |
| REQ3 | PK·FK·비PK 인덱스 포함 | 신우림 | ✅ |
| REQ4 | 각 테이블 10~100 튜플 | 전원 | ✅ |
| REQ5 | INSERT 메뉴 2개 (사용자 입력) | 최보경①, 이태영② | ✅ |
| REQ6 | SELECT 메뉴 2개 (사용자 입력 + JOIN + VIEW) | 곽성은①, 하지수② | ✅ |
| REQ7 | SELECT 메뉴 2개 (사용자 입력 + 집계 + GROUP BY) | 곽성은③, 최보경④ | ✅ |
| REQ8 | UPDATE 메뉴 2개 (사용자 입력) | 조수민①, 이태영② | ✅ |
| REQ9 | DELETE 메뉴 2개 (사용자 입력) | 박나림①, 최보경② | ✅ |
| REQ10 | PreparedStatement 사용 (사용자 입력 시) | 전원 | ✅ |
| REQ11 | 스키마에 뷰 + 비PK 인덱스 포함 | 신우림 | ✅ |
| REQ12 | UPDATE 트랜잭션 처리 | 조수민 | ✅ |
| REQ13 | 단가 변경 이력 관리 + 전후 매출 분석 메뉴 | 신우림 | ✅ |
| REQ14 | 고객 인적사항 변경 이력 관리 + 전후 매출 분석 메뉴 | 박나림 | ✅ |
| REQ15 | 텍스트 기반 UI | 신우림 | ✅ |
| REQ16 | SQL 스크립트 3종 (USE DATABASE 없음) | 신우림 | ✅ |
| REQ17 | Java 소스코드 (.java) 제출 | 전원 | ✅ |
| REQ18 | 실행 가능한 Fat JAR 제출 | 하지수 | ✅ |
| REQ19 | README (실행 방법, 메인 클래스명 포함) | 신우림 | ✅ |
| REQ20 | 보고서 (ER다이어그램, 코드 설명, 실행 캡처, REQ 충족 설명) | 신우림 (취합), 전원 (캡처) | ✅ |
