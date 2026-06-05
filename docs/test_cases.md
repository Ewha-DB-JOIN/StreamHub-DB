# StreamHub 테스트 케이스 명세서

> **프로젝트**: StreamHub — 온라인 동영상 스트리밍 구독 관리 시스템  
> **실행 환경**: `java -jar join-app.jar` (DBConfig.java DB 접속 정보 설정 필요)  
> **사전 조건**: `dropschema.sql → createschema.sql → initdata.sql` 순서로 실행 완료

---

## TC-01 | INSERT① 콘텐츠 등록 (REQ5①)

**메뉴**: 1번 — InsertContentMenu  
**목적**: 사용자 입력으로 content 테이블에 신규 콘텐츠를 등록한다

| 구분 | TC-01-01 정상 등록 | TC-01-02 필수 필드 누락 | TC-01-03 잘못된 가격 형식 |
|------|------------------|----------------------|------------------------|
| 입력 | title: `테스트 영화`<br>genre: `Action`<br>release_date: `2024-01-01`<br>unit_price: `9.99`<br>description: `테스트` | title: (공백 입력) | unit_price: `abc` |
| 기대 결과 | "콘텐츠 등록이 완료되었습니다" 출력<br>DB에 1건 INSERT 확인 | 재입력 요청 메시지 출력 | 재입력 요청 메시지 출력 |
| 검증 SQL | `SELECT * FROM content ORDER BY content_id DESC LIMIT 1;` | — | — |
| 판정 기준 | 등록된 title, genre, unit_price 일치 | 프로그램 종료되지 않음 | 프로그램 종료되지 않음 |

---

## TC-02 | INSERT② 구독 등록 (REQ5②)

**메뉴**: 2번 — InsertSubscriptionMenu  
**목적**: member_id, plan_id 입력으로 subscription 테이블에 구독을 등록한다

| 구분 | TC-02-01 정상 등록 | TC-02-02 존재하지 않는 member_id | TC-02-03 존재하지 않는 plan_id |
|------|------------------|-------------------------------|------------------------------|
| 입력 | member_id: `1`<br>plan_id: `2` | member_id: `9999`<br>plan_id: `1` | member_id: `1`<br>plan_id: `9999` |
| 기대 결과 | "구독 등록이 완료되었습니다" 출력<br>region_snapshot 자동 세팅 | "회원의 지역 정보가 없습니다" 출력 | "존재하지 않는 plan_id입니다" 출력 |
| 검증 SQL | `SELECT * FROM subscription ORDER BY sub_id DESC LIMIT 1;` | — | — |
| 판정 기준 | status='active', region_snapshot=member.region | 트랜잭션 롤백, DB 변경 없음 | 트랜잭션 롤백, DB 변경 없음 |

---

## TC-03 | SELECT① 장르별 시청 통계 조회 (REQ6①)

**메뉴**: 3번 — SelectGenreStatsMenu  
**목적**: 장르명 입력으로 genre_watch_stats_view를 통해 시청 통계를 조회한다

| 구분 | TC-03-01 존재하는 장르 | TC-03-02 존재하지 않는 장르 | TC-03-03 대소문자 구분 |
|------|---------------------|--------------------------|----------------------|
| 입력 | `Action` | `Unknown` | `action` |
| 기대 결과 | 평균 시청시간, 구독자 수, 콘텐츠 수 출력 | "조회 결과가 없습니다" 출력 | 결과 있거나 없거나 (DB 설정에 따라) |
| 검증 SQL | `SELECT * FROM genre_watch_stats_view WHERE genre='Action';` | — | — |
| 판정 기준 | VIEW + JOIN 쿼리 결과와 일치 | 프로그램 정상 유지 | 정상 처리 |

---

## TC-04 | SELECT② 회원별 구독·결제 이력 조회 (REQ6②)

**메뉴**: 4번 — SelectMemberBillingMenu  
**목적**: member_id 입력으로 member_billing_summary_view를 통해 결제 이력을 조회한다

| 구분 | TC-04-01 정상 조회 | TC-04-02 결제 이력 없는 회원 | TC-04-03 존재하지 않는 회원 |
|------|------------------|---------------------------|--------------------------|
| 입력 | `1` | 결제 없는 member_id | `9999` |
| 기대 결과 | 구독 이력, 플랜명, 결제 날짜, 금액 출력 | "조회 결과가 없습니다" 출력 | "해당 회원이 존재하지 않습니다" 출력 |
| 검증 SQL | `SELECT * FROM member_billing_summary_view WHERE member_id=1;` | — | — |
| 판정 기준 | VIEW 결과와 출력 일치 | 정상 처리 | 정상 처리 |

---

## TC-05 | SELECT③ 월별 구독 매출 및 ARPU (REQ7③)

**메뉴**: 5번 — SelectMonthlyRevenueMenu  
**목적**: 연도 입력으로 월별 총 매출 및 ARPU(평균 매출)를 GROUP BY로 집계한다

| 구분 | TC-05-01 데이터 있는 연도 | TC-05-02 데이터 없는 연도 | TC-05-03 숫자 아닌 입력 |
|------|------------------------|------------------------|----------------------|
| 입력 | `2026` | `2020` | `abc` |
| 기대 결과 | 월별 매출 합계, ARPU 표 출력 | "조회 결과가 없습니다" 출력 | 재입력 요청 |
| 검증 SQL | `SELECT MONTH(billing_date), SUM(total_amount) FROM billing WHERE YEAR(billing_date)=2026 GROUP BY MONTH(billing_date);` | — | — |
| 판정 기준 | DB 집계값과 출력값 일치 | 정상 처리 | 정상 처리 |

---

## TC-06 | SELECT④ 장르별 평균 시청시간 (REQ7④)

**메뉴**: 6번 — SelectGenreWatchMenu  
**목적**: 장르명 입력으로 watch_history + content JOIN 후 GROUP BY 집계한다

| 구분 | TC-06-01 존재하는 장르 | TC-06-02 시청 이력 없는 장르 | TC-06-03 전체 장르 조회 |
|------|---------------------|--------------------------|----------------------|
| 입력 | `Drama` | `Animation` (이력 없는 경우) | (전체 조회 지원 시) |
| 기대 결과 | 평균 시청시간(AVG), 콘텐츠 수(COUNT) 출력 | "조회 결과가 없습니다" 출력 | 전체 장르 통계 출력 |
| 검증 SQL | `SELECT AVG(watch_duration), COUNT(DISTINCT content_id) FROM watch_history wh JOIN content c ON wh.content_id=c.content_id WHERE c.genre='Drama';` | — | — |
| 판정 기준 | DB 집계값과 출력값 일치 | 정상 처리 | 정상 처리 |

---

## TC-07 | UPDATE① 플랜 가격 변경 + 트랜잭션 (REQ8①, REQ12)

**메뉴**: 7번 — UpdatePlanPriceMenu  
**목적**: plan_id와 새 가격 입력으로 subscription_plan 업데이트 및 price_history 이력을 트랜잭션으로 처리한다

| 구분 | TC-07-01 정상 변경 | TC-07-02 존재하지 않는 plan_id | TC-07-03 트랜잭션 롤백 확인 |
|------|------------------|------------------------------|--------------------------|
| 입력 | plan_id: `1`<br>새 가격: `12000` | plan_id: `9999`<br>새 가격: `10000` | plan_id: `9999` (강제 롤백) |
| 기대 결과 | "성공: 플랜 가격 변경 완료" 출력<br>subscription_plan UPDATE<br>price_history INSERT | "존재하지 않는 플랜 ID" 출력<br>"트랜잭션 롤백 발생" 출력 | "데이터베이스에 아무런 변경 사항도 반영되지 않음" 출력 |
| 검증 SQL | `SELECT current_price FROM subscription_plan WHERE plan_id=1;`<br>`SELECT * FROM price_history ORDER BY history_id DESC LIMIT 1;` | `SELECT COUNT(*) FROM price_history;` (변화 없어야 함) | 동일 |
| 판정 기준 | 두 테이블 모두 변경됨 (원자성) | price_history 건수 변화 없음 | DB 변경 없음 확인 |

---

## TC-08 | UPDATE② 구독 플랜 변경 (REQ8②)

**메뉴**: 8번 — UpdateSubscriptionPlanMenu  
**목적**: sub_id와 새 plan_id 입력으로 subscription.plan_id를 업데이트한다

| 구분 | TC-08-01 정상 변경 | TC-08-02 존재하지 않는 sub_id | TC-08-03 존재하지 않는 plan_id |
|------|------------------|------------------------------|-------------------------------|
| 입력 | sub_id: `1`<br>새 plan_id: `3` | sub_id: `9999`<br>새 plan_id: `1` | sub_id: `1`<br>새 plan_id: `9999` |
| 기대 결과 | "구독 플랜 변경이 완료되었습니다" 출력 | "존재하지 않는 구독 아이디입니다" 출력 | "존재하지 않는 플랜 아이디입니다" 출력 |
| 검증 SQL | `SELECT plan_id FROM subscription WHERE sub_id=1;` | — | — |
| 판정 기준 | plan_id=3으로 변경됨 | DB 변경 없음 | DB 변경 없음 |

---

## TC-09 | DELETE① 회원 탈퇴 (REQ9①)

**메뉴**: 9번 — DeleteMemberMenu  
**목적**: member_id 입력으로 회원을 삭제하고 FK 정책(CASCADE/SET NULL)이 정상 적용되는지 확인한다

| 구분 | TC-09-01 정상 탈퇴 (y 확인) | TC-09-02 탈퇴 취소 (n 입력) | TC-09-03 존재하지 않는 회원 |
|------|--------------------------|--------------------------|--------------------------|
| 입력 | member_id: `3`, 확인: `y` | member_id: `3`, 확인: `n` | member_id: `9999` |
| 기대 결과 | "회원 탈퇴 처리가 완료되었습니다" 출력<br>member_profile_history CASCADE 삭제<br>subscription.member_id SET NULL | "탈퇴가 취소되었습니다" 출력 | "해당 ID의 회원이 존재하지 않습니다" 출력 |
| 검증 SQL | `SELECT * FROM member WHERE member_id=3;` (없어야 함)<br>`SELECT member_id FROM subscription WHERE member_id=3;` (NULL이어야 함)<br>`SELECT * FROM billing WHERE sub_id IN (SELECT sub_id FROM subscription WHERE member_id IS NULL);` (보존되어야 함) | `SELECT * FROM member WHERE member_id=3;` (있어야 함) | — |
| 판정 기준 | member 삭제, subscription 보존(member_id=NULL), billing 보존 | DB 변경 없음 | 정상 처리 |

---

## TC-10 | DELETE② 콘텐츠 삭제 (REQ9②)

**메뉴**: 10번 — DeleteContentMenu  
**목적**: content_id 입력으로 콘텐츠를 삭제하고 watch_history.content_id가 SET NULL 처리되는지 확인한다

| 구분 | TC-10-01 정상 삭제 (y 확인) | TC-10-02 삭제 취소 (n 입력) | TC-10-03 존재하지 않는 콘텐츠 |
|------|--------------------------|--------------------------|------------------------------|
| 입력 | content_id: `5`, 확인: `y` | content_id: `5`, 확인: `n` | content_id: `9999` |
| 기대 결과 | "콘텐츠 삭제가 완료되었습니다" 출력 | "삭제가 취소되었습니다" 출력 | "해당 ID의 콘텐츠가 존재하지 않습니다" 출력 |
| 검증 SQL | `SELECT * FROM content WHERE content_id=5;` (없어야 함)<br>`SELECT content_id FROM watch_history WHERE content_id IS NULL;` (NULL 확인) | `SELECT * FROM content WHERE content_id=5;` (있어야 함) | — |
| 판정 기준 | content 삭제, watch_history.content_id=NULL | DB 변경 없음 | 정상 처리 |

---

## TC-11 | 분석① 플랜 가격 변동 전후 매출 비교 (REQ13)

**메뉴**: 11번 — AnalyzePriceHistoryMenu  
**목적**: plan_id 입력으로 가격 변동 이력 및 구간별 billing 매출을 비교 분석한다

| 구분 | TC-11-01 이력 있는 플랜 | TC-11-02 이력 없는 플랜 | TC-11-03 존재하지 않는 plan_id |
|------|----------------------|----------------------|-------------------------------|
| 입력 | `1` | 이력 없는 plan_id | `9999` |
| 기대 결과 | 구간별 (이전 가격, 변경 가격, 결제 건수, 구간 총 매출) 표 출력<br>전체 누적 매출 출력 | "가격 변동 이력이 없습니다" 출력 | "존재하지 않는 플랜 ID입니다" 출력 |
| 검증 SQL | `SELECT * FROM price_history WHERE plan_id=1 ORDER BY valid_from;`<br>`SELECT SUM(total_amount) FROM billing b JOIN subscription s ON b.sub_id=s.sub_id WHERE s.plan_id=1;` | — | — |
| 판정 기준 | price_history 구간과 billing.applied_price 기준 집계 일치 | 정상 처리 | 정상 처리 |

---

## TC-12 | 분석② 회원 인적사항 변경 전후 매출 분석 (REQ14)

**메뉴**: 12번 — AnalyzeMemberProfileMenu  
**목적**: member_id 입력으로 인적사항 변경 이력 및 변경 전/후 구독 매출을 비교 분석한다

| 구분 | TC-12-01 변경 이력 있는 회원 | TC-12-02 변경 이력 없는 회원 | TC-12-03 존재하지 않는 회원 |
|------|--------------------------|--------------------------|--------------------------|
| 입력 | `1` | 이력 없는 member_id | `9999` |
| 기대 결과 | 변경 이력 표 출력 (field_name, old_value, new_value, changed_at)<br>변경 전/후 구독 건수 및 총 매출 출력 | "변경 이력이 없습니다" 출력 | "해당 ID의 회원이 존재하지 않습니다" 출력 |
| 검증 SQL | `SELECT * FROM member_profile_history WHERE member_id=1 ORDER BY changed_at;`<br>`SELECT COUNT(*), SUM(b.total_amount) FROM subscription s LEFT JOIN billing b ON s.sub_id=b.sub_id WHERE s.member_id=1;` | — | — |
| 판정 기준 | 가장 최근 변경 시점 기준 BEFORE/AFTER 구독 매출 분리 출력 | 정상 처리 | 정상 처리 |

---

## TC-13 | 공통 — 잘못된 메뉴 입력 처리 (REQ15)

**목적**: 메인 메뉴에서 유효하지 않은 입력 시 재입력 처리되는지 확인한다

| 구분 | TC-13-01 범위 밖 숫자 | TC-13-02 문자 입력 | TC-13-03 0 입력 (종료) |
|------|---------------------|------------------|----------------------|
| 입력 | `99` | `abc` | `0` |
| 기대 결과 | "잘못된 입력입니다" 출력 후 메뉴 재출력 | 동일 | "프로그램을 종료합니다" 출력 후 종료 |
| 판정 기준 | 프로그램 종료되지 않음 | 프로그램 종료되지 않음 | 정상 종료 (exit code 0) |

---

## TC-14 | 공통 — PreparedStatement SQL Injection 방어 (REQ10)

**목적**: 사용자 입력에 SQL 삽입 공격 문자열 입력 시 안전하게 처리되는지 확인한다

| 구분 | TC-14-01 SQL Injection 시도 |
|------|---------------------------|
| 입력 | member_id: `1 OR 1=1` |
| 기대 결과 | 숫자 파싱 실패 → "올바른 숫자를 입력해주세요" 출력 또는 정상 처리 |
| 판정 기준 | DB 비정상 접근 없음, 프로그램 정상 유지 |

---

## 테스트 실행 체크리스트

| TC | 메뉴 | 정상 | 예외① | 예외② | 비고 |
|----|------|------|-------|-------|------|
| TC-01 | INSERT① 콘텐츠 등록 | ☐ | ☐ | ☐ | |
| TC-02 | INSERT② 구독 등록 | ☐ | ☐ | ☐ | |
| TC-03 | SELECT① 장르 시청 통계 | ☐ | ☐ | ☐ | |
| TC-04 | SELECT② 회원별 결제 이력 | ☐ | ☐ | ☐ | |
| TC-05 | SELECT③ 월별 매출 ARPU | ☐ | ☐ | ☐ | |
| TC-06 | SELECT④ 장르별 시청시간 | ☐ | ☐ | ☐ | |
| TC-07 | UPDATE① 가격 변경 + 트랜잭션 | ☐ | ☐ | ☐ | 롤백 시나리오 포함 |
| TC-08 | UPDATE② 플랜 변경 | ☐ | ☐ | ☐ | |
| TC-09 | DELETE① 회원 탈퇴 | ☐ | ☐ | ☐ | CASCADE/SET NULL 확인 |
| TC-10 | DELETE② 콘텐츠 삭제 | ☐ | ☐ | ☐ | SET NULL 확인 |
| TC-11 | 분석① 가격 변동 매출 | ☐ | ☐ | ☐ | |
| TC-12 | 분석② 인적사항 변경 매출 | ☐ | ☐ | ☐ | |
| TC-13 | 공통 — 잘못된 입력 처리 | ☐ | ☐ | ☐ | |
| TC-14 | 공통 — SQL Injection 방어 | ☐ | — | — | |
