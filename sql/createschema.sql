-- ============================================================
-- CREATESCHEMA.SQL  [REQ1][REQ2][REQ3][REQ11]
-- 실행 순서: 1. dropschema.sql → 2. createschema.sql → 3. initdata.sql
-- ============================================================

-- [REQ1][REQ2] 테이블 8개

CREATE TABLE member (
    member_id  INT          PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    birth_date DATE,
    region     VARCHAR(50),
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE content (
    content_id   INT           PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(200)  NOT NULL,
    genre        VARCHAR(50),
    release_date DATE,
    unit_price   DECIMAL(10,2) NOT NULL,
    description  TEXT
);

CREATE TABLE subscription_plan (
    plan_id       INT           PRIMARY KEY AUTO_INCREMENT,
    plan_name     VARCHAR(100)  NOT NULL UNIQUE,
    current_price DECIMAL(10,2) NOT NULL,
    max_devices   INT           NOT NULL,
    ads_included  BOOLEAN       NOT NULL,
    description   TEXT
);

-- member 탈퇴 시: member_id → SET NULL (subscription 보존 → billing 보존)
CREATE TABLE subscription (
    sub_id          INT  PRIMARY KEY AUTO_INCREMENT,
    member_id       INT,
    plan_id         INT  NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE,
    region_snapshot VARCHAR(50) NOT NULL,
    status          ENUM('active','paused','cancelled','expired') NOT NULL DEFAULT 'active',
    FOREIGN KEY (member_id) REFERENCES member(member_id)    ON DELETE SET NULL,
    FOREIGN KEY (plan_id)   REFERENCES subscription_plan(plan_id) ON DELETE RESTRICT
);

-- content 삭제 시: watch_history CASCADE
-- content_id가 CASCADE 아닌 이유 : 콘텐츠가 삭제돼도 "몇 분 시청했는지" 이력은 통계상 의미가 있으므로 content_id만 NULL 처리하고 행은 보존.
CREATE TABLE watch_history (
    watch_id       INT      PRIMARY KEY AUTO_INCREMENT,
    sub_id         INT      NOT NULL,
    content_id     INT,
    watched_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    watch_duration INT      NOT NULL,
    FOREIGN KEY (sub_id)     REFERENCES subscription(sub_id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES content(content_id)  ON DELETE SET NULL
);

-- billing은 절대 삭제 안 됨 → RESTRICT
CREATE TABLE billing (
    billing_id    INT           PRIMARY KEY AUTO_INCREMENT,
    sub_id        INT           NOT NULL,
    billing_date  DATE          NOT NULL,
    applied_price DECIMAL(10,2) NOT NULL,
    total_amount  DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sub_id) REFERENCES subscription(sub_id) ON DELETE RESTRICT
);

-- [REQ13] 플랜 가격 변동 이력
CREATE TABLE price_history (
    price_id   INT           PRIMARY KEY AUTO_INCREMENT,
    plan_id    INT           NOT NULL,
    old_price  DECIMAL(10,2) NOT NULL,
    new_price  DECIMAL(10,2) NOT NULL,
    valid_from DATETIME      NOT NULL,
    valid_to   DATETIME,
    changed_at DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plan_id) REFERENCES subscription_plan(plan_id) ON DELETE RESTRICT
);

-- [REQ14] 회원 인적사항 변동 이력 — member 탈퇴 시 이력도 함께 삭제
CREATE TABLE member_profile_history (
    history_id INT          PRIMARY KEY AUTO_INCREMENT,
    member_id  INT,
    field_name VARCHAR(50)  NOT NULL,
    old_value  VARCHAR(255) NOT NULL,
    new_value  VARCHAR(255) NOT NULL,
    changed_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ============================================================
-- [REQ3][REQ11] 비PK 인덱스
-- ============================================================

CREATE INDEX idx_subscription_member   ON subscription(member_id);
CREATE INDEX idx_subscription_plan     ON subscription(plan_id);
CREATE INDEX idx_watch_history_content ON watch_history(content_id);
CREATE INDEX idx_watch_history_sub     ON watch_history(sub_id);
CREATE INDEX idx_billing_sub           ON billing(sub_id);
CREATE INDEX idx_billing_date          ON billing(billing_date);
CREATE INDEX idx_price_history_plan    ON price_history(plan_id);

-- ============================================================
-- [REQ2][REQ11] 뷰 2개
-- ============================================================

-- VIEW 1: 장르별 시청 통계 (SELECT① 기반)
CREATE VIEW genre_watch_stats_view AS
SELECT
    c.genre,
    COUNT(DISTINCT c.content_id)  AS content_count,
    COUNT(DISTINCT wh.sub_id)     AS subscriber_count,
    ROUND(AVG(wh.watch_duration), 2) AS avg_watch_duration_min
FROM content c
LEFT JOIN watch_history wh ON c.content_id = wh.content_id
GROUP BY c.genre;

-- VIEW 2: 회원별 구독·결제 요약 (SELECT② 기반)
CREATE VIEW member_billing_summary_view AS
SELECT
    m.member_id,
    m.name,
    m.email,
    s.sub_id,
    sp.plan_name,
    s.start_date,
    s.end_date,
    s.status,
    s.region_snapshot,
    COALESCE(SUM(b.total_amount), 0) AS total_billed
FROM member m
JOIN subscription s          ON m.member_id = s.member_id
JOIN subscription_plan sp    ON s.plan_id   = sp.plan_id
LEFT JOIN billing b          ON s.sub_id    = b.sub_id
GROUP BY m.member_id, m.name, m.email,
         s.sub_id, sp.plan_name, s.start_date, s.end_date, s.status, s.region_snapshot;