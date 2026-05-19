# StreamHub — 온라인 동영상 스트리밍 구독 관리 시스템
## Team Project Proposal (Draft)

> **제출 마감:** 2025-05-30 23:59 | **작성 기준일:** 2025-05-18

---

## 1. 프로젝트 개요 (Theme)

### 시스템 명칭
**StreamHub** — 온라인 동영상 스트리밍 구독 관리 시스템

### 주제 선정 배경
Netflix, Watcha 등 OTT(Over-The-Top) 플랫폼의 구독·콘텐츠 관리 시스템을 데이터베이스로 구현한다.  
HW2-1의 기본 스키마(`product`, `customer`, `store`, `sales`, `market_basket`, `total_sales`)를 스트리밍 도메인에 맞게 확장하여, 구독 회원·콘텐츠·플랜·결제·시청 이력을 통합 관리하는 시스템을 설계한다.

### 핵심 시나리오
1. 회원이 구독 플랜을 선택하고 결제하면 콘텐츠를 시청할 수 있다.
2. 시청 이력과 결제 이력이 누적되며 통계 분석에 활용된다.
3. 구독 플랜 가격 변동 시 **과거 결제 내역은 변경되지 않고** 이력으로 보존된다.
4. 회원의 지역·연령 등 인적사항 변경 시 **변경 전후 매출 분석**이 가능하도록 이력이 관리된다.

---

## 2. 데이터베이스 스키마 설계

### 테이블 구성 (8개 테이블)

| # | 테이블명 | HW2-1 대응 | 핵심 컬럼 |
|---|---------|-----------|---------|
| 1 | `member` | `customer` 확장 | member_id, name, email, birth_date, region |
| 2 | `content` | `product` 확장 | content_id, title, genre, release_date, unit_price |
| 3 | `subscription_plan` | `store` 대응 | plan_id, plan_name, current_price, max_devices |
| 4 | `subscription` | `sales` 확장 | sub_id, member_id, plan_id, start_date, region_snapshot |
| 5 | `watch_history` | `market_basket` 확장 | watch_id, sub_id, content_id, watched_at, watch_duration |
| 6 | `billing` | `total_sales` 대응 | billing_id, sub_id, billing_date, applied_price, total_amount |
| 7 | `price_history` | 신규 추가 | price_id, plan_id, old_price, new_price, valid_from, valid_to |
| 8 | `member_profile_history` | 신규 추가 | history_id, member_id, field_name, old_value, new_value, changed_at |

### [REQ13] 가격 변동 이력 관리 설계
- **문제:** 구독 플랜 가격(예: 9,900원 → 13,900원) 변경 시 기존 결제 금액이 바뀌면 안 됨
- **해결:**
  - `billing.applied_price` 컬럼에 **결제 당시 가격을 스냅샷으로 저장** → 이후 가격 변경과 무관하게 과거 결제액 보존
  - `price_history` 테이블에 `(plan_id, old_price, new_price, valid_from, valid_to)` 형태로 기간별 가격 이력 관리

### [REQ14] 고객 인적사항 변동 이력 관리 설계
- **문제:** 회원이 서울→부산으로 이사 시, 과거 "서울 지역 매출" 분석이 왜곡되면 안 됨
- **해결:**
  - `subscription.region_snapshot` 컬럼에 **구독 시점의 회원 지역을 스냅샷 저장** → 분석 시 현재 주소가 아닌 구독 당시 주소 기준 집계
  - `member_profile_history` 테이블에 변경 이력 로그 기록

### 뷰(View) 2개
| 뷰 이름 | 내용 |
|---------|------|
| `genre_watch_stats_view` | 장르별 평균 시청시간, 구독자 수, 콘텐츠 수 집계 |
| `member_billing_summary_view` | 회원별 구독 이력 및 누적 결제금액 요약 |

---

## 3. 기능 요구사항 (Application Menus)

| 메뉴 | 기능 | 담당 |
|------|------|------|
| INSERT ① | 콘텐츠 등록 (제목, 장르, 가격 입력) | 팀원3 |
| INSERT ② | 구독 등록 (회원 ID, 플랜 ID 입력) | 팀원5 |
| SELECT ① | 장르 입력 → 해당 장르 콘텐츠 시청 통계 조회 (VIEW + JOIN) | 팀원6 |
| SELECT ② | 회원 ID 입력 → 구독·결제 이력 조회 (VIEW + JOIN) | 팀원7 |
| SELECT ③ | 연도 입력 → 월별 구독 매출 합계 및 ARPU (GROUP BY) | 팀원6 |
| SELECT ④ | 장르 입력 → 장르별 평균 시청시간 및 콘텐츠 수 (GROUP BY) | 팀원3 |
| UPDATE ① | 플랜 가격 변경 (플랜 ID, 새 가격 입력) — 트랜잭션 처리 | 팀원4 |
| UPDATE ② | 구독 플랜 변경 (구독 ID, 새 플랜 ID 입력) | 팀원5 |
| DELETE ① | 회원 탈퇴 (회원 ID 입력) | 팀원2 |
| DELETE ② | 콘텐츠 삭제 (콘텐츠 ID 입력) | 팀원3 |
| 분석 ① | [REQ13] 플랜 ID 입력 → 가격 변동 전후 매출 비교 분석 | 팀장 |
| 분석 ② | [REQ14] 회원 ID 입력 → 인적사항 변경 전후 구독 매출 분석 | 팀원2 |

---

## 4. 팀원 역할 분담

| 이름 | 담당 테이블 | 담당 메뉴 | 기타 |
|------|------------|---------|------|
| **팀장 신우림** | `price_history` | 분석① (REQ13) | 스키마 통합·검수, 뷰 2개·인덱스 작성, Main.java 메뉴 틀, SQL 3종 최종 정리·제출, README, 보고서 취합 |
| 박나림 | `member` + `member_profile_history` | DELETE① (회원 탈퇴), 분석② (REQ14) | 회원 인적사항 이력 관리 구현, 발표자료 제작 |
| 최보경 | `content` | INSERT① (콘텐츠 등록), DELETE② (콘텐츠 삭제), SELECT④ (장르별 집계) | 초기 데이터 50건, 데모 영상 제작 |
| 조수민 | `subscription_plan` | UPDATE① (가격 변경, 트랜잭션) | REQ12 트랜잭션 처리 담당, 발표 |
| 이태영 | `subscription` | INSERT② (구독 등록), UPDATE② (플랜 변경) | region_snapshot 스냅샷 처리, 발표 |
| 곽성은 | `watch_history` | SELECT① (장르별 시청 통계, VIEW+JOIN), SELECT③ (월별 매출 집계) | billing 테이블 초기 데이터 지원, 발표자료 제작 |
| 하지수 | `billing` | SELECT② (회원별 결제 이력, VIEW+JOIN) | PreparedStatement 전체 검수, .jar 빌드, 데모 영상 제작 |

> ※ 모든 팀원은 본인 담당 메뉴에 PreparedStatement를 직접 적용 (REQ10)  
> ※ 본인 담당 테이블의 SQL(createschema, initdata) 및 보고서 캡처를 각자 책임

---

## 5. 개발 일정

| 기간 | 마일스톤 | 담당 |
|------|---------|------|
| 5/18 ~ 5/23 | ERD 초안 확정 (dbdiagram.io), 테이블별 컬럼 상세 확정 | 전원 |
| 5/24 ~ 5/29 | 제안서 작성 및 검토 | 전원 |
| **5/30** | **제안서 제출 (cybercampus)** | 팀장 |
| 6/01 ~ 6/05 | SQL 작성 (createschema.sql, initdata.sql — 각자 담당 테이블) | 전원 |
| 6/01 ~ 6/07 | Java 메뉴 구현 (각자 담당 REQ) | 전원 |
| 6/07 | develop 브랜치 통합 및 통합 테스트 | 팀장 |
| 6/08 | .jar 빌드, 보고서 초안 완성 | 팀원7, 팀장 |
| **6/09** | **최종 제출 (cybercampus)** | 팀장 |
| 6/10 | 발표 자료 및 데모 영상 준비 | 전원 |
| **6/11** | **발표 및 데모** | 전원 |

---

## 6. 사용 도구 및 기술 스택

| 분류 | 도구 |
|------|------|
| **언어** | Java 17 |
| **DB** | Oracle MySQL HeatWave (Free tier) |
| **DB 연결** | JDBC (mysql-connector-j-8.x.x) |
| **IDE** | IntelliJ IDEA / Eclipse |
| **형상 관리** | GitHub (`Ewha-DB-JOIN/database-join`) |
| **협업·이슈 관리** | GitHub Projects (칸반 보드), GitHub Issues (REQ1~20) |
| **ERD 설계** | dbdiagram.io |
| **문서 작성** | Google Docs (제안서·보고서), Markdown (README) |

---

## 7. 요구사항 충족 계획 요약

| REQ | 충족 방법 |
|-----|---------|
| REQ1 | HW2-1 6개 테이블 → 8개 테이블로 도메인 확장 |
| REQ2 | 테이블 8개 + 뷰 2개 (`genre_watch_stats_view`, `member_billing_summary_view`) |
| REQ3 | 각 테이블 PK·FK 설정, 비PK 인덱스 추가 (`watch_history.content_id`, `billing.billing_date` 등) |
| REQ4 | 각 팀원이 담당 테이블에 10~100 튜플 삽입 |
| REQ5 | INSERT ①② |
| REQ6 | SELECT ①② (VIEW+JOIN+사용자입력) |
| REQ7 | SELECT ③④ (집계+GROUP BY+사용자입력) |
| REQ8 | UPDATE ①② |
| REQ9 | DELETE ①② |
| REQ10 | 전원 PreparedStatement 적용 |
| REQ11 | 뷰 2개 + 비PK 인덱스 createschema.sql에 포함 |
| REQ12 | UPDATE① (가격 변경) 트랜잭션 처리 |
| REQ13 | `price_history` + `billing.applied_price` 스냅샷 + 분석① 메뉴 |
| REQ14 | `member_profile_history` + `subscription.region_snapshot` + 분석② 메뉴 |
| REQ15 | `while-switch` 기반 텍스트 메뉴 (`Main.java`) |
| REQ16 | `createschema.sql`, `initdata.sql`, `dropschema.sql` 3종 제출 |
| REQ17 | 전원 .java 소스코드 제출 |
| REQ18 | 실행 가능한 `.jar` 파일 제출 |
| REQ19 | `README.md` (실행 방법, 메인 클래스명 포함) |
| REQ20 | ERD, 코드 구조 설명, 실행 캡처, REQ 충족 설명 포함 보고서 |
