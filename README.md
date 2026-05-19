# StreamHub — 온라인 동영상 스트리밍 구독 관리 시스템

이화여대 데이터베이스 팀 프로젝트 — Java + JDBC + MySQL HeatWave

---

## 프로젝트 정보

| 항목 | 내용 |
|------|------|
| 주제 | **StreamHub** — 온라인 동영상 스트리밍 구독 관리 시스템 |
| 제출 마감 | **2025-06-09 23:59** |
| 제안서 마감 | **2025-05-30 23:59** |
| 발표 | 2025-06-11 (수업 시간) |

---

## 개발 환경

| 항목 | 버전 |
|------|------|
| JDK | 17 |
| MySQL | Oracle MySQL HeatWave (Free tier) |
| JDBC Driver | mysql-connector-j-8.x.x.jar |
| IDE | IntelliJ IDEA / Eclipse |

---

## 시작하기

### 1. JDBC 드라이버

`lib/` 폴더에 `mysql-connector-j-8.x.x.jar` 파일을 넣고 IDE classpath에 추가하세요.

- 다운로드: <https://dev.mysql.com/downloads/connector/j/> (Platform Independent 선택)

### 2. DB 연결 설정

`src/util/DBUtil.java` 에서 본인 환경에 맞게 수정:

```java
private static final String DB_URL  = "jdbc:mysql://<HOST>:3306/<DB_NAME>?useSSL=true&serverTimezone=Asia/Seoul";
private static final String DB_USER = "your_mysql_user";
private static final String DB_PASS = "your_mysql_password";
```

> ⚠ DB 비밀번호는 절대 커밋하지 마세요. `DBUtil.java`는 로컬에서만 수정하세요.

### 3. DB 스키마 초기화

MySQL Workbench 또는 CLI에서 순서대로 실행:

```
1. sql/dropschema.sql    -- 기존 테이블 삭제 (⚠ 데이터 전부 삭제됨)
2. sql/createschema.sql  -- 테이블 / 뷰 / 인덱스 생성
3. sql/initdata.sql      -- 초기 데이터 삽입
```

> ⚠ `USE DATABASE` 구문은 SQL 파일에 포함하지 않습니다 (제출 요건).

### 4. 실행

```bash
java -jar join-app.jar
```

또는 IDE에서 `src/Main.java`를 직접 실행하세요.

---

## 폴더 구조

```
├── sql/
│   ├── createschema.sql   # 테이블, 뷰, 인덱스 생성 [REQ1][REQ2][REQ3][REQ11]
│   ├── initdata.sql       # 초기 데이터 삽입 (각 테이블 10~100 튜플) [REQ4]
│   └── dropschema.sql     # 전체 테이블 삭제
├── src/
│   ├── Main.java          # 메인 진입점 (텍스트 메뉴) [REQ15]
│   ├── util/
│   │   └── DBUtil.java    # DB 연결 싱글톤
│   └── menu/              # 각 메뉴 기능 클래스 (팀원별 구현)
├── lib/                   # JDBC .jar (각자 로컬에 추가)
├── docs/                  # 제안서, ERD, 설계 문서 [REQ20]
└── README.md              # [REQ19]
```

---

## 요구사항 체크리스트

| REQ | 내용 | 담당 | 상태 |
|-----|------|------|------|
| REQ1 | HW2-1 스키마 확장 및 변경 | 신우림 | ⬜ |
| REQ2 | 테이블 7개 이상 + 뷰 2개 | 신우림 | ⬜ |
| REQ3 | FK, PK, 인덱스 포함 | 신우림 | ⬜ |
| REQ4 | 각 테이블 10~100 튜플 | 전원 (각자 담당 테이블) | ⬜ |
| REQ5 | INSERT 메뉴 2개 (사용자 입력) | 최보경①, 이태영② | ⬜ |
| REQ6 | SELECT 메뉴 2개 (사용자 입력 + JOIN + VIEW) | 곽성은①, 하지수② | ⬜ |
| REQ7 | SELECT 메뉴 2개 (사용자 입력 + 집계 + GROUP BY) | 곽성은③, 최보경④ | ⬜ |
| REQ8 | UPDATE 메뉴 2개 (사용자 입력) | 조수민①, 이태영② | ⬜ |
| REQ9 | DELETE 메뉴 2개 (사용자 입력) | 박나림①, 최보경② | ⬜ |
| REQ10 | PreparedStatement 사용 (사용자 입력 시) | 전원 | ⬜ |
| REQ11 | 스키마에 뷰 + 인덱스 포함 | 신우림 | ⬜ |
| REQ12 | UPDATE 트랜잭션 처리 | 조수민 | ⬜ |
| REQ13 | 상품 단가 변경 시 과거 매출 유지 + 분석 메뉴 | 신우림 | ⬜ |
| REQ14 | 고객 정보 변경 시 변경 전후 매출 분석 메뉴 | 박나림 | ⬜ |
| REQ15 | 텍스트 기반 UI | 신우림 | ⬜ |
| REQ16 | SQL 스크립트 3종 제출 | 신우림 (각자 담당 테이블 SQL 작성 후 통합) | ⬜ |
| REQ17 | Java 소스코드 (.java) 제출 | 전원 | ⬜ |
| REQ18 | 실행 가능한 .jar 파일 제출 | 하지수 | ⬜ |
| REQ19 | README (실행 방법, 메인 클래스명) | 신우림 | ⬜ |
| REQ20 | 보고서 (ER다이어그램, 코드 설명, 실행 캡처, REQ 충족 설명) | 신우림 (취합), 전원 (캡처) | ⬜ |

---

## 이슈(Issue) & 프로젝트 관리

### 이슈 기반 작업 관리

모든 작업은 **GitHub Issue** 단위로 관리합니다. REQ1~REQ20에 해당하는 이슈가 이미 생성되어 있습니다.

- 이슈 확인: [Issues 탭](https://github.com/Ewha-DB-JOIN/database-join/issues)
- 프로젝트 보드: [DB-Team-Project-Management](https://github.com/orgs/Ewha-DB-JOIN/projects/1)

### 작업 시작 전 필수 절차

1. 담당 이슈를 열고 본인을 **Assignee**로 지정
2. 프로젝트 보드에서 해당 이슈를 **In Progress**로 변경
3. 이슈 번호를 확인하고 브랜치 생성 (`feature/req##-기능명`)

### 프로젝트 보드 필드

| 필드 | 용도 | 값 예시 |
|------|------|---------|
| **Status** | 진행 상태 | Todo / In Progress / Review / Done |
| **Assignees** | 담당자 | 팀원 GitHub 계정 |
| **Requirement ID** | REQ 번호 | REQ5, REQ13 |
| **Category** | 작업 분류 | DB 설계 / Java 구현 / 문서화 / 테스트 |
| **Due Date** | 마감일 | 2025-06-09 |

### 자동화 워크플로우

아래 동작은 자동으로 처리됩니다:

| 이벤트 | 자동 처리 |
|--------|----------|
| 이슈가 프로젝트에 추가됨 | Status → `Todo` 자동 설정 |
| 이슈가 Close됨 | Status → `Done` 자동 이동 |
| PR이 Merge됨 | 연결된 이슈 자동 Close |

> PR 본문에 `Closes #이슈번호` 를 포함하면 PR merge 시 이슈가 자동으로 닫힙니다.
> 예: `Closes #5` → REQ5 이슈 자동 Close

---

## 브랜치 전략

```
main       ← 최종 제출용 (PR + PM 승인 필수, 직접 push 금지)
  └── develop   ← 기능 통합 브랜치
        └── feature/req##-기능명   ← 개인 작업 브랜치
```

**작업 흐름:**

```bash
# 1. develop 최신화
git checkout develop && git pull origin develop

# 2. 내 브랜치 생성
git checkout -b feature/req05-insert-product

# 3. 작업 후 커밋
git add src/menu/InsertMenu.java
git commit -m "feat: [REQ5] 상품 INSERT 메뉴 구현"

# 4. GitHub에서 develop으로 PR 생성
git push origin feature/req05-insert-product
```

## 커밋 메시지 규칙

### 형식

```
<타입>: [REQ##] <변경 내용>
```

### 타입 목록

| 타입 | 사용 상황 | 예시 |
|------|----------|------|
| `feat` | 새 기능 구현 | `feat: [REQ5] 상품 INSERT 메뉴 구현` |
| `fix` | 버그 수정 | `fix: [REQ10] PreparedStatement 누락 수정` |
| `sql` | SQL 파일 변경 | `sql: [REQ2] SalesView 뷰 추가` |
| `docs` | 문서 작업 | `docs: ERD 다이어그램 업데이트` |
| `refactor` | 코드 구조 개선 (기능 변경 없음) | `refactor: DBUtil 예외처리 정리` |
| `chore` | 빌드/설정 변경 | `chore: .gitignore에 .env 추가` |

### 규칙

- REQ 번호가 있는 작업은 반드시 `[REQ##]` 포함 (예: `[REQ5]`, `[REQ13]`)
- 한 커밋에 하나의 논리적 변경만 포함
- 메시지는 한국어 또는 영어 통일 (팀 내 결정)
- `main` 브랜치에 직접 커밋 금지 — 반드시 PR 사용

### 예시

```bash
feat: [REQ5] 주문 INSERT 메뉴 구현
feat: [REQ6] 고객별 매출 조회 메뉴 (JOIN + VIEW)
feat: [REQ13] 단가 변경 전후 매출 분석 메뉴
fix:  [REQ12] UPDATE 트랜잭션 롤백 누락 수정
sql:  [REQ11] 상품 인덱스 및 SalesSummaryView 추가
docs: 제안서 초안 추가
```

## PR(Pull Request) 규칙

### PR 제목 형식

```
[브랜치명] 작업 내용 요약
```

예시:

```
[feature/req05-insert] REQ5 상품 INSERT 메뉴 구현
[develop] 초기 프로젝트 세팅 merge
```

### PR 본문 템플릿

```
## 작업 내용
- 구현하거나 변경한 내용을 bullet point로 작성

## 관련 REQ
- [REQ##] 내용

## 이슈 연결 (자동 Close)
Closes #이슈번호

## 체크리스트
- [ ] 로컬에서 정상 실행 확인
- [ ] SQL 파일 변경 시 dropschema → createschema → initdata 순서 확인
- [ ] DB 비밀번호 등 개인 정보 커밋 안 됨 확인
- [ ] 프로젝트 보드 Status → Review 로 변경
```

### 규칙

- `feature/*` → `develop` : 본인 작업 완료 후 PR, 팀원 1명 이상 리뷰
- `develop` → `main` : PM(본인) 최종 확인 후 merge, 제출 직전에만 사용

---

## 팀원 및 담당

| 이름 | 담당 REQ | 담당 테이블 |
|------|----------|------------|
| 신우림 | REQ1, REQ2, REQ3, REQ11, REQ13 | `price_history` — 스키마 통합·검수, 뷰/인덱스, 보고서 취합 |
| 박나림 | REQ9①, REQ14 | `member`, `member_profile_history` — 발표자료 제작 |
| 최보경 | REQ5①, REQ7④, REQ9② | `content` — 데모 영상 제작 |
| 조수민 | REQ8①, REQ12 | `subscription_plan` — 발표 |
| 이태영 | REQ5②, REQ8② | `subscription` — 발표 |
| 곽성은 | REQ6①, REQ7③ | `watch_history` — 발표자료 제작 |
| 하지수 | REQ6②, REQ17, REQ18 | `billing` — .jar 빌드, 데모 영상 제작 |

---

## 주요 일정

| 날짜 | 마일스톤 | 담당 |
|------|---------|------|
| 5/18 ~ 5/23 | ERD 초안 확정, 테이블별 컬럼 상세 확정 | ALL |
| 5/24 ~ 5/29 | 제안서 작성 및 검토 | ALL |
| **2025-05-30** | **제안서 제출** | 신우림 |
| 6/01 ~ 6/05 | SQL 작성 (createschema, initdata — 각자 담당 테이블) | ALL |
| 6/01 ~ 6/07 | Java 메뉴 구현 (각자 담당 REQ) | ALL |
| 6/07 | develop 브랜치 통합 및 통합 테스트 | 신우림 |
| 6/08 | .jar 빌드, 보고서 초안 완성 | 하지수, 신우림 |
| **2025-06-09** | **최종 제출 (SQL, .java, .jar, README, 보고서)** | 신우림 |
| 6/10 | 발표자료 및 데모 영상 준비 | 박나림, 최보경, 곽성은, 하지수 |
| **2025-06-11** | **발표 및 데모** | 이태영, 조수민 |
