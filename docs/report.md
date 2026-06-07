---
title: "StreamHub — 온라인 동영상 스트리밍 구독 관리 시스템"
subtitle: "데이터베이스 팀 프로젝트 최종 보고서"
author: "이화여자대학교 데이터베이스 팀 JOIN (7인)"
date: "2026년 6월"
geometry: margin=2.5cm
fontsize: 11pt
mainfont: "NanumGothic"
---

---

# 1. 프로젝트 개요

## 1.1 주제 및 목적

**StreamHub**는 Netflix, Watcha 등 OTT(Over-The-Top) 플랫폼의 구독·콘텐츠 관리 시스템을 모델링한 데이터베이스 응용 프로그램이다. HW2-1의 기본 스키마를 스트리밍 도메인에 맞게 확장하여 구독 회원, 콘텐츠, 구독 플랜, 결제, 시청 이력을 통합 관리한다.

주요 시나리오는 다음과 같다:

- 회원이 구독 플랜을 선택하고 결제하면 콘텐츠를 시청할 수 있다.
- 시청 이력과 결제 이력이 누적되며 통계 분석에 활용된다.
- 구독 플랜 가격 변동 시 과거 결제 내역은 변경되지 않고 이력으로 보존된다.
- 회원의 인적사항(지역 등) 변경 전후 구독 매출 분석이 가능하다.

## 1.2 기술 스택

| 항목 | 내용 |
|------|------|
| 언어 | Java 17 |
| 데이터베이스 | Oracle MySQL HeatWave (Free tier) / MySQL 9.x |
| DB 연결 | JDBC (mysql-connector-j-8.0.33) |
| IDE | IntelliJ IDEA / Eclipse |
| 형상 관리 | GitHub (Ewha-DB-JOIN/StreamHub-DB) |

## 1.3 팀 구성

| 이름 | 담당 테이블 | 담당 메뉴 |
|------|------------|---------|
| 신우림 | `price_history` | 분석① (REQ13), Main.java, SQL 통합, 보고서 |
| 박나림 | `member`, `member_profile_history` | DELETE① 회원 탈퇴, 분석② 인적사항 분석 |
| 최보경 | `content` | INSERT① 콘텐츠 등록, DELETE② 콘텐츠 삭제, SELECT④ 장르별 시청시간 |
| 조수민 | `subscription_plan` | UPDATE① 플랜 가격 변경 (트랜잭션) |
| 이태영 | `subscription` | INSERT② 구독 등록, UPDATE② 구독 플랜 변경 |
| 곽성은 | `watch_history` | SELECT① 장르별 시청 통계, SELECT③ 월별 매출 |
| 하지수 | `billing` | SELECT② 회원별 결제 이력, .jar 빌드 |

---

# 2. 데이터베이스 설계

## 2.1 ER 다이어그램

> **[이미지 삽입 요청]** ERD 이미지 파일(예: `erd.png`)을 이 위치에 삽입해 주세요.

\bigskip

## 2.2 Relational Schema 다이어그램

> **[이미지 삽입 요청]** Relational Schema 다이어그램 이미지(예: `schema_diagram.png`)를 이 위치에 삽입해 주세요.  
> 참고: 수업 교재의 University Database Relational Schema 형식을 따릅니다.

\bigskip

## 2.3 테이블 상세 설명

**[REQ1] HW2-1 스키마 확장 대응표**

| HW2-1 테이블 | StreamHub 테이블 | 주요 변경/추가 사항 |
|-------------|----------------|-----------------|
| `customer` | `member` | `birth_date`, `region`, `created_at` 추가 |
| `product` | `content` | `genre`, `release_date`, `description` 추가 |
| `store` | `subscription_plan` | `max_devices`, `ads_included` 추가 |
| `sales` | `subscription` | `region_snapshot`, `status` 추가 |
| `market_basket` | `watch_history` | `watch_duration`, `watched_at` 추가 |
| `total_sales` | `billing` | `applied_price` 스냅샷 컬럼 추가 |
| _(신규)_ | `price_history` | 플랜 가격 변동 이력 관리 [REQ13] |
| _(신규)_ | `member_profile_history` | 회원 인적사항 변동 이력 관리 [REQ14] |

\bigskip

**테이블별 스키마 요약**

```
member (member_id PK, name, email UNIQUE, birth_date, region, created_at)

content (content_id PK, title, genre, release_date, unit_price, description)

subscription_plan (plan_id PK, plan_name UNIQUE, current_price,
                   max_devices, ads_included, description)

subscription (sub_id PK, member_id FK→member SET NULL,
              plan_id FK→subscription_plan RESTRICT,
              start_date, end_date, region_snapshot, status)

watch_history (watch_id PK, sub_id FK→subscription CASCADE,
               content_id FK→content SET NULL,
               watched_at, watch_duration)

billing (billing_id PK, sub_id FK→subscription RESTRICT,
         billing_date, applied_price, total_amount)

price_history (price_id PK, plan_id FK→subscription_plan RESTRICT,
               old_price, new_price, valid_from, valid_to)

member_profile_history (history_id PK, member_id FK→member CASCADE,
                        field_name, old_value, new_value, changed_at)
```

## 2.4 뷰(View) 설계 [REQ2][REQ11]

**뷰 1: `genre_watch_stats_view`** — 장르별 평균 시청시간, 구독자 수, 콘텐츠 수 집계

```sql
CREATE VIEW genre_watch_stats_view AS
SELECT c.genre,
       COUNT(DISTINCT c.content_id)     AS content_count,
       COUNT(DISTINCT wh.sub_id)        AS subscriber_count,
       ROUND(AVG(wh.watch_duration), 2) AS avg_watch_duration_min
FROM content c
LEFT JOIN watch_history wh ON c.content_id = wh.content_id
GROUP BY c.genre;
```

**뷰 2: `member_billing_summary_view`** — 회원별 구독 이력 및 누적 결제금액

```sql
CREATE VIEW member_billing_summary_view AS
SELECT m.member_id, m.name, m.email, s.sub_id, sp.plan_name,
       s.start_date, s.end_date, s.status, s.region_snapshot,
       COALESCE(SUM(b.total_amount), 0) AS total_billed
FROM member m
JOIN subscription s       ON m.member_id = s.member_id
JOIN subscription_plan sp ON s.plan_id   = sp.plan_id
LEFT JOIN billing b       ON s.sub_id    = b.sub_id
GROUP BY m.member_id, m.name, m.email, s.sub_id, sp.plan_name,
         s.start_date, s.end_date, s.status, s.region_snapshot;
```

## 2.5 비PK 인덱스 [REQ3][REQ11]

```sql
CREATE INDEX idx_subscription_member   ON subscription(member_id);
CREATE INDEX idx_subscription_plan     ON subscription(plan_id);
CREATE INDEX idx_watch_history_content ON watch_history(content_id);
CREATE INDEX idx_watch_history_sub     ON watch_history(sub_id);
CREATE INDEX idx_billing_sub           ON billing(sub_id);
CREATE INDEX idx_billing_date          ON billing(billing_date);
CREATE INDEX idx_price_history_plan    ON price_history(plan_id);
```

## 2.6 외래키(FK) 정책

| 관계 | FK 컬럼 | ON DELETE 정책 | 이유 |
|------|---------|--------------|------|
| subscription → member | member_id | SET NULL | 탈퇴해도 구독·결제 이력 보존 |
| subscription → subscription_plan | plan_id | RESTRICT | 구독 중인 플랜 삭제 방지 |
| watch_history → subscription | sub_id | CASCADE | 구독 삭제 시 시청 이력도 제거 |
| watch_history → content | content_id | SET NULL | 콘텐츠 삭제 후에도 시청시간 통계 보존 |
| billing → subscription | sub_id | RESTRICT | 결제 이력은 절대 삭제 불가 |
| price_history → subscription_plan | plan_id | RESTRICT | 가격 이력 보존 |
| member_profile_history → member | member_id | CASCADE | 회원 탈퇴 시 인적사항 이력도 삭제 |

---

# 3. Java 코드 구조

## 3.1 전체 구조

```
src/
├── Main.java                       # 메인 진입점 — while-switch 텍스트 메뉴 [REQ15]
├── config/
│   └── DBConfig.java               # DB 접속 정보 (URL, USER, PASS)
├── util/
│   └── DBUtil.java                 # JDBC 연결 싱글톤 관리
└── menu/
    ├── MenuHelper.java             # 공통 UX 헬퍼 (장르/회원/플랜 목록 출력)
    ├── InsertContentMenu.java      # INSERT① 콘텐츠 등록 [REQ5]
    ├── InsertSubscriptionMenu.java # INSERT② 구독 등록 [REQ5]
    ├── SelectGenreStatsMenu.java   # SELECT① 장르별 시청 통계 [REQ6]
    ├── SelectMemberBillingMenu.java# SELECT② 회원별 결제 이력 [REQ6]
    ├── SelectMonthlyRevenueMenu.java # SELECT③ 월별 구독 매출 [REQ7]
    ├── SelectGenreWatchMenu.java   # SELECT④ 장르별 평균 시청시간 [REQ7]
    ├── UpdatePlanPriceMenu.java    # UPDATE① 플랜 가격 변경 [REQ8][REQ12]
    ├── UpdateSubscriptionPlanMenu.java # UPDATE② 구독 플랜 변경 [REQ8]
    ├── DeleteMemberMenu.java       # DELETE① 회원 탈퇴 [REQ9]
    ├── DeleteContentMenu.java      # DELETE② 콘텐츠 삭제 [REQ9]
    ├── AnalyzePriceHistoryMenu.java # 분석① 가격 변동 분석 [REQ13]
    └── AnalyzeMemberProfileMenu.java # 분석② 인적사항 분석 [REQ14]
```

## 3.2 클래스별 역할 및 주요 메서드

### Main.java
텍스트 기반 UI의 진입점. `while(true)` 루프와 `switch` 문으로 12개 메뉴를 라우팅한다.

| 메서드 | 역할 |
|--------|------|
| `main(String[] args)` | DB 연결, 메뉴 루프, 각 메뉴 인스턴스 `run()` 호출 |
| `printMenu()` | 12개 메뉴 항목 출력 |
| `getIntInput(String prompt)` | `nextLine()`으로 정수 입력 받기 (Scanner 버퍼 충돌 방지) |

### DBUtil.java
싱글톤 패턴으로 JDBC `Connection` 객체를 관리한다. 연결이 끊긴 경우 자동으로 재연결하며, `close()` 메서드로 프로그램 종료 시 연결을 해제한다.

### MenuHelper.java
중복 코드를 제거하기 위한 공통 헬퍼 클래스. DB에서 목록을 조회하여 사용자에게 선택지를 번호로 제시한다.

| 메서드 | 역할 |
|--------|------|
| `selectGenre(Scanner)` | 장르 번호 선택 → 장르명 반환 |
| `selectOptionalGenre(Scanner)` | 장르 선택 (0 입력 시 null 반환) |
| `printMemberList()` | 전체 회원 목록 출력 |
| `printContentList()` | 전체 콘텐츠 목록 출력 |
| `printSubscriptionList()` | 전체 구독 목록 출력 |
| `printBillingYears()` | 결제 데이터 있는 연도 목록 출력 |

### 각 메뉴 클래스 (InsertContentMenu 등)
모두 동일한 인터페이스를 가진다.

```java
public void run(Scanner scanner) { ... }
```

`run()` 메서드는 사용자 입력 → DB 조작 → 결과 출력의 흐름으로 동작하며, 모든 사용자 입력은 `PreparedStatement`로 처리한다 [REQ10].

### 트랜잭션 처리 패턴 [REQ12]

`UpdatePlanPriceMenu`는 다음과 같은 트랜잭션 패턴을 사용한다:

```java
conn.setAutoCommit(false);
try {
    // 1. subscription_plan UPDATE
    // 2. price_history INSERT
    conn.commit();
} catch (SQLException e) {
    conn.rollback();
} finally {
    conn.setAutoCommit(true);
}
```

---

# 4. 실행 결과 스크린샷

## 4.1 INSERT① — 콘텐츠 등록 [REQ5]

담당: 최보경 | 클래스: `InsertContentMenu`

![INSERT① 콘텐츠 등록 — 입력 화면](../Screenshots/ChoiBokyung_menu1_insert_content_01.png)

![INSERT① 콘텐츠 등록 — 등록 완료](../Screenshots/ChoiBokyung_menu1_insert_content_02.png)

![INSERT① 콘텐츠 등록 — DB 확인](../Screenshots/ChoiBokyung_menu1_insert_content_03.png)

\bigskip

## 4.2 INSERT② — 구독 등록 [REQ5]

담당: 이태영 | 클래스: `InsertSubscriptionMenu`

![INSERT② 구독 등록 — 플랜 목록 및 입력](../Screenshots/TaeYoungLee_menu2_insertsub_1.png)

![INSERT② 구독 등록 — 등록 완료](../Screenshots/TaeYoungLee_menu2_insertsub_2.png)

![INSERT② 구독 등록 — DB 확인](../Screenshots/TaeYoungLee_menu2_insertsub_3.png)

![INSERT② 구독 등록 — 오류 처리 (존재하지 않는 회원)](../Screenshots/TaeYoungLee_menu2_insertsub_4.png)

\bigskip

## 4.3 SELECT① — 장르별 시청 통계 조회 [REQ6]

담당: 곽성은 | 클래스: `SelectGenreStatsMenu` | VIEW: `genre_watch_stats_view`

![SELECT① 장르별 시청 통계 — 장르 선택 목록](../Screenshots/SungeunKwak_menu3_select_genre_stats_01.png)

![SELECT① 장르별 시청 통계 — 조회 결과](../Screenshots/SungeunKwak_menu3_select_genre_stats_02.png)

![SELECT① MySQL — genre_watch_stats_view 내용](../Screenshots/SungeunKwak_menu3_genre_watch_stats_view.png)

\bigskip

## 4.4 SELECT② — 회원별 구독·결제 이력 조회 [REQ6]

담당: 하지수 | 클래스: `SelectMemberBillingMenu` | VIEW: `member_billing_summary_view`

![SELECT② 회원별 결제 이력 — 입력 및 결과](../Screenshots/Hajisoo_menu4_member_billing_history_01.png)

![SELECT② 회원별 결제 이력 — MySQL 확인](../Screenshots/Hajisoo_menu4_member_billing_history_02.png)

\bigskip

## 4.5 SELECT③ — 월별 구독 매출 및 ARPU [REQ7]

담당: 곽성은 | 클래스: `SelectMonthlyRevenueMenu`

![SELECT③ 월별 구독 매출 — 연도 입력 및 결과](../Screenshots/SungeunKwak_menu5_select_monthly_revenue_01.png)

![SELECT③ MySQL — billing 테이블 확인](../Screenshots/SungeunKwak_menu5_billing.png)

\bigskip

## 4.6 SELECT④ — 장르별 평균 시청시간 [REQ7]

담당: 최보경 | 클래스: `SelectGenreWatchMenu`

![SELECT④ 장르별 평균 시청시간 — 입력 및 결과](../Screenshots/ChoiBokyung_menu6_select_genre_watch_01.png)

![SELECT④ 장르별 평균 시청시간 — 추가 조회](../Screenshots/ChoiBokyung_menu6_select_genre_watch_02.png)

\bigskip

## 4.7 UPDATE① — 플랜 가격 변경 [REQ8][REQ12]

담당: 조수민 | 클래스: `UpdatePlanPriceMenu` | 트랜잭션: `setAutoCommit(false)` → `commit()`

![UPDATE① 플랜 가격 변경 — 변경 전 price_history](../Screenshots/ChoSumin_menu7_update_plan_price_01.png)

![UPDATE① 플랜 가격 변경 — 가격 변경 실행](../Screenshots/ChoSumin_menu7_update_plan_price_02.png)

![UPDATE① 플랜 가격 변경 — 변경 후 DB 확인](../Screenshots/ChoSumin_menu7_update_plan_price_03.png)

\bigskip

## 4.8 UPDATE② — 구독 플랜 변경 [REQ8]

담당: 이태영 | 클래스: `UpdateSubscriptionPlanMenu`

![UPDATE② 구독 플랜 변경 — 구독 목록 및 입력](../Screenshots/TaeYoungLee_menu8_updatesub_1.png)

![UPDATE② 구독 플랜 변경 — 변경 완료](../Screenshots/TaeYoungLee_menu8_updatesub_2.png)

![UPDATE② 구독 플랜 변경 — DB 변경 전](../Screenshots/TaeYoungLee_menu8_updatesub_3.png)

![UPDATE② 구독 플랜 변경 — DB 변경 후](../Screenshots/TaeYoungLee_menu8_updatesub_4.png)

![UPDATE② 구독 플랜 변경 — 오류 처리](../Screenshots/TaeYoungLee_menu8_updatesub_5.png)

\bigskip

## 4.9 DELETE① — 회원 탈퇴 [REQ9]

담당: 박나림 | 클래스: `DeleteMemberMenu`

![DELETE① 회원 탈퇴 — 회원 목록 및 선택](../Screenshots/ParkNarim_menu9_delete_member_01.png)

![DELETE① 회원 탈퇴 — 확인 프롬프트 및 완료](../Screenshots/ParkNarim_menu9_delete_member_02.png)

![DELETE① 회원 탈퇴 — DB 확인 (subscription.member_id SET NULL)](../Screenshots/ParkNarim_menu9_delete_member_03.png)

\bigskip

## 4.10 DELETE② — 콘텐츠 삭제 [REQ9]

담당: 최보경 | 클래스: `DeleteContentMenu`

![DELETE② 콘텐츠 삭제 — 콘텐츠 목록 및 선택](../Screenshots/ChoiBokyung_menu10_delete_content_01.png)

![DELETE② 콘텐츠 삭제 — 확인 및 삭제 완료](../Screenshots/ChoiBokyung_menu10_delete_content_02.png)

![DELETE② 콘텐츠 삭제 — DB 확인](../Screenshots/ChoiBokyung_menu10_delete_content_03.png)

\bigskip

## 4.11 분석① — 플랜 가격 변동 전후 매출 비교 [REQ13]

담당: 신우림 | 클래스: `AnalyzePriceHistoryMenu`

![분석① MySQL — price_history 가격 변동 이력](../Screenshots/WoorimShin_menu7_price_history_01.png)

![분석① 가격 변동 전후 매출 비교 — 분석 결과](../Screenshots/WoorimShin_menu11_analyze_price_history_01.png)

\bigskip

## 4.12 분석② — 회원 인적사항 변경 전후 매출 분석 [REQ14]

담당: 박나림 | 클래스: `AnalyzeMemberProfileMenu`

![분석② 인적사항 변경 분석 — 회원 목록 및 입력](../Screenshots/ParkNarim_menu14_analyze_member_profile_01.png)

![분석② 인적사항 변경 분석 — 변경 이력 및 매출 비교 결과](../Screenshots/ParkNarim_menu14_analyze_member_profile_02.png)

---

# 5. 요구사항 충족 설명

## REQ1 — 스키마 확장 및 변경

HW2-1의 6개 기본 테이블(`customer`, `product`, `store`, `sales`, `market_basket`, `total_sales`)을 스트리밍 도메인에 맞게 재설계하여 8개 테이블로 확장하였다. 단순 이름 변경에 그치지 않고 도메인에 필수적인 컬럼을 추가하였으며 (예: `subscription.region_snapshot`, `billing.applied_price`), REQ13·REQ14를 위한 이력 테이블 2개(`price_history`, `member_profile_history`)를 신규 추가하였다.

## REQ2 — 7개 이상 테이블 + 뷰 2개

테이블 8개(REQ2 기준 7개 이상 충족)와 뷰 2개(`genre_watch_stats_view`, `member_billing_summary_view`)를 구현하였다. 각 팀원은 최소 1개 이상의 테이블을 담당하였으며, 담당 테이블의 SQL(createschema, initdata)과 관련 Java 메뉴를 직접 구현하였다.

## REQ3 — PK, FK, 비PK 인덱스

- **Primary Key**: 8개 테이블 전체에 `AUTO_INCREMENT` PK 부여
- **Foreign Key**: 7개 참조 관계 정의 (ON DELETE 정책 명시)
- **비PK 인덱스**: 7개 인덱스 생성 (빈번한 조회 컬럼 대상 — `member_id`, `plan_id`, `content_id`, `billing_date` 등)

## REQ4 — 각 테이블 10~100 튜플

`initdata.sql` 기준 각 테이블의 초기 데이터 건수:

| 테이블 | 튜플 수 |
|--------|--------|
| content | 30 |
| subscription_plan | 10 |
| member | 20 |
| subscription | 30 |
| watch_history | 50 |
| billing | 30 |
| price_history | 15 |
| member_profile_history | 10 |

모든 테이블이 10~100 범위를 충족한다.

## REQ5 — INSERT 메뉴 2개

- **INSERT①** (`InsertContentMenu`): 제목, 장르, 출시일, 단가, 설명을 사용자로부터 입력받아 `content` 테이블에 삽입. 제목 필수 검증, 날짜 형식 검증, 가격 0 이상 검증 포함.
- **INSERT②** (`InsertSubscriptionMenu`): 회원 ID, 플랜 ID 입력 후 FK 존재 여부 확인 → `subscription` 테이블에 삽입. 회원의 현재 `region`을 `region_snapshot`으로 자동 저장.

## REQ6 — SELECT + JOIN + VIEW 메뉴 2개

- **SELECT①** (`SelectGenreStatsMenu`): 장르 입력 → `genre_watch_stats_view`와 `content` 테이블을 `JOIN`하는 단일 쿼리로 장르별 콘텐츠 목록 및 시청 통계 출력.
  ```sql
  SELECT c.content_id, c.title, c.genre, g.content_count, g.subscriber_count, ...
  FROM content c
  JOIN genre_watch_stats_view g ON c.genre = g.genre
  WHERE c.genre = ?
  ```
- **SELECT②** (`SelectMemberBillingMenu`): 회원 ID 입력 → `member_billing_summary_view`와 `member` 테이블을 `JOIN`하는 단일 쿼리로 구독·결제 이력 출력.
  ```sql
  SELECT v.member_id, v.name, v.sub_id, v.plan_name, v.total_billed, ...
  FROM member_billing_summary_view v
  JOIN member m ON v.member_id = m.member_id
  WHERE v.member_id = ?
  ```

두 쿼리 모두 사용자 입력, JOIN, VIEW를 하나의 쿼리 안에 포함한다.

## REQ7 — SELECT + 집계 + GROUP BY 메뉴 2개

- **SELECT③** (`SelectMonthlyRevenueMenu`): 연도 입력 → `SUM`, `COUNT`, `ROUND` 집계함수와 `GROUP BY MONTH(billing_date)`로 월별 매출 및 ARPU 계산.
- **SELECT④** (`SelectGenreWatchMenu`): 장르 입력 → `COUNT(DISTINCT)`, `ROUND(AVG(...))` 집계함수와 `GROUP BY c.genre`로 장르별 평균 시청시간 및 콘텐츠 수 계산.

## REQ8 — UPDATE 메뉴 2개

- **UPDATE①** (`UpdatePlanPriceMenu`): 플랜 ID, 새 가격 입력 → `subscription_plan.current_price` 업데이트. 동시에 `price_history`에 변경 이력 INSERT (REQ13 연계).
- **UPDATE②** (`UpdateSubscriptionPlanMenu`): 구독 ID, 새 플랜 ID 입력 → `subscription.plan_id` 업데이트.

## REQ9 — DELETE 메뉴 2개

- **DELETE①** (`DeleteMemberMenu`): 회원 ID 입력 → 대상 회원 정보 출력 후 y/n 확인 → `member` 삭제. FK 정책에 의해 `member_profile_history`는 CASCADE 삭제, `subscription.member_id`는 SET NULL.
- **DELETE②** (`DeleteContentMenu`): 콘텐츠 ID 입력 → 대상 정보 출력 후 y/n 확인 → `content` 삭제. `watch_history.content_id`는 SET NULL으로 시청 이력 보존.

## REQ10 — PreparedStatement 사용

모든 메뉴 클래스에서 사용자 입력값을 SQL에 삽입할 때 예외 없이 `PreparedStatement`의 `setInt()`, `setString()`, `setDate()` 등을 사용한다. 문자열 연결(String concatenation)로 SQL을 구성하는 코드는 존재하지 않는다.

## REQ11 — 뷰 + 인덱스

`createschema.sql`에 뷰 2개(`genre_watch_stats_view`, `member_billing_summary_view`)와 비PK 인덱스 7개를 함께 정의하였다. 인덱스는 JOIN 키, 조회 빈도가 높은 컬럼(`member_id`, `plan_id`, `content_id`, `billing_date` 등)에 집중 적용되었다.

## REQ12 — 트랜잭션 처리

`UpdatePlanPriceMenu`는 `conn.setAutoCommit(false)`로 트랜잭션을 시작하고, `subscription_plan` UPDATE와 `price_history` INSERT 두 작업이 모두 성공해야 `commit()`을 호출한다. 예외 발생 시 `rollback()`으로 원자적으로 취소하며, `finally` 블록에서 `setAutoCommit(true)`로 복구한다. `UpdateSubscriptionPlanMenu`와 `InsertSubscriptionMenu`도 동일한 패턴을 적용한다.

## REQ13 — 단가 변경 이력 관리 및 분석

**문제**: 구독 플랜 가격 변경 시 과거 결제 금액이 바뀌면 안 된다.

**해결**:
1. `billing.applied_price` 컬럼에 결제 당시 가격을 스냅샷으로 저장 → 이후 가격 변경과 무관하게 과거 결제액 보존
2. `price_history` 테이블에 `(old_price, new_price, valid_from, valid_to)` 형태로 기간별 가격 이력 관리
3. 플랜 가격 변경 시(`UpdatePlanPriceMenu`) 자동으로 `price_history`에 이력 INSERT

**분석 메뉴** (`AnalyzePriceHistoryMenu`): 플랜 ID 입력 → 가격 변동 구간별 결제 건수 및 매출 집계 출력. `billing.applied_price`를 `price_history`의 `valid_from`/`valid_to` 기간과 매핑하여 각 구간의 실제 매출을 정확히 계산한다.

## REQ14 — 인적사항 변경 이력 관리 및 분석

**문제**: 회원 지역 변경(서울→부산) 시 과거 "서울 지역 매출" 분석이 왜곡되면 안 된다.

**해결**:
1. `subscription.region_snapshot` 컬럼에 구독 시점의 회원 지역을 스냅샷 저장
2. `member_profile_history` 테이블에 변경 이력 로그 기록(`field_name`, `old_value`, `new_value`, `changed_at`)

**분석 메뉴** (`AnalyzeMemberProfileMenu`): 회원 ID 입력 → 인적사항 변경 이력 전체 출력 → 가장 최근 변경 시점을 기준으로 변경 전·후 구독 건수 및 총 매출 비교 출력.

## REQ15 — 텍스트 기반 UI

`Main.java`는 `while(true)` 루프와 `switch(choice)` 문으로 구성된 텍스트 기반 메뉴를 제공한다. 모든 입출력은 `System.in`/`System.out`(표준 스트림)을 통해 이루어지며, 그래픽 컴포넌트를 사용하지 않는다. 실행 결과는 정렬된 텍스트 표 형식으로 출력한다.

## REQ16 — SQL 스크립트 3종

`sql/` 폴더에 다음 3개 파일을 제공한다:

| 파일 | 역할 |
|------|------|
| `createschema.sql` | 테이블 8개, 뷰 2개, 비PK 인덱스 7개 생성 |
| `initdata.sql` | 각 테이블 초기 데이터 삽입 (10~100 튜플) |
| `dropschema.sql` | 전체 테이블 및 뷰 삭제 (외래키 순서 고려) |

3개 파일 모두 `USE DATABASE` 구문을 포함하지 않는다.

## REQ17 — Java 소스코드

`src/` 폴더에 총 16개 `.java` 파일을 제출한다. 모든 파일에 클래스 레벨 Javadoc 주석이 포함되어 담당 REQ 번호, 담당자, 주요 기능을 명시하고 있다.

## REQ18 — 실행 가능한 JAR 파일

`join-app.jar`는 MySQL Connector/J 8.0.33을 포함한 Fat JAR로 빌드되었다. 별도 classpath 설정 없이 `java -jar join-app.jar` 명령으로 바로 실행 가능하다.

## REQ19 — README

`README.md`에 실행 방법, 메인 클래스명(`Main`), DB 설정 방법, SQL 초기화 순서, 폴더 구조, 요구사항 체크리스트를 포함하였다.

## REQ20 — 보고서

본 보고서가 REQ20을 충족한다. ER 다이어그램, Relational Schema 다이어그램, Java 코드 구조 설명, 각 메뉴 실행 결과 스크린샷(PNG), REQ1~REQ20 충족 설명, 팀원별 담당이 포함되어 있다.

---

# 6. 팀원별 담당

| 이름 | 담당 테이블 | 담당 메뉴 | 기타 역할 |
|------|------------|---------|---------|
| **신우림** | `price_history` | 분석① — 플랜 가격 변동 전후 매출 비교 (REQ13) | 스키마 통합·검수, 뷰 2개·인덱스 설계, Main.java, SQL 3종 최종 정리, README, 보고서 취합, 제출 |
| **박나림** | `member`, `member_profile_history` | DELETE① 회원 탈퇴, 분석② 인적사항 변경 분석 (REQ14) | 회원 인적사항 이력 관리 구현, 발표자료 제작 |
| **최보경** | `content` | INSERT① 콘텐츠 등록, DELETE② 콘텐츠 삭제, SELECT④ 장르별 평균 시청시간 | 초기 데이터 50건(콘텐츠·시청이력), 데모 영상 제작 |
| **조수민** | `subscription_plan` | UPDATE① 플랜 가격 변경 (REQ12 트랜잭션) | REQ12 트랜잭션 처리 담당, 발표 |
| **이태영** | `subscription` | INSERT② 구독 등록, UPDATE② 구독 플랜 변경 | region_snapshot 스냅샷 처리, 발표 |
| **곽성은** | `watch_history` | SELECT① 장르별 시청 통계 (VIEW+JOIN), SELECT③ 월별 구독 매출 및 ARPU (GROUP BY) | billing 테이블 초기 데이터 지원, 발표자료 제작 |
| **하지수** | `billing` | SELECT② 회원별 구독·결제 이력 (VIEW+JOIN) | PreparedStatement 전체 검수, .jar 빌드, 데모 영상 제작 |

> 모든 팀원은 본인 담당 메뉴에 PreparedStatement를 직접 적용하였으며 [REQ10], 담당 테이블의 SQL(createschema, initdata)과 보고서용 실행 캡처를 각자 책임졌다.
