-- ============================================================
-- CREATE SCHEMA
-- 실행 순서: 1. dropschema.sql → 2. createschema.sql → 3. initdata.sql
-- ============================================================

-- TODO: 여기에 CREATE TABLE 문을 작성하세요
-- 예시:
-- CREATE TABLE users (
--     user_id   INT PRIMARY KEY AUTO_INCREMENT,
--     username  VARCHAR(50) NOT NULL UNIQUE,
--     password  VARCHAR(255) NOT NULL,
--     created_at DATETIME DEFAULT CURRENT_TIMESTAMP
-- );
-- =====================================================
-- CREATE TABLE - content
-- 담당: 최보경
-- =====================================================

CREATE TABLE content (
    content_id   INT            PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(200)   NOT NULL,
    genre        VARCHAR(50),
    release_date DATE,
    unit_price   DECIMAL(10,2)  NOT NULL,
    description  TEXT
);

-- REQ3: 비PK 인덱스 (장르별 조회 최적화 - SELECT④ 용)
CREATE INDEX idx_content_genre ON content (genre);
