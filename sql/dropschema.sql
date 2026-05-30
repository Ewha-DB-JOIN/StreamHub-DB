-- ============================================================
-- DROPSCHEMA.SQL
-- 실행 순서: 1. dropschema.sql → 2. createschema.sql → 3. initdata.sql
-- ⚠ 모든 테이블과 뷰가 삭제됩니다 (데이터 포함)
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP VIEW IF EXISTS member_billing_summary_view;
DROP VIEW IF EXISTS genre_watch_stats_view;

DROP TABLE IF EXISTS member_profile_history;
DROP TABLE IF EXISTS price_history;
DROP TABLE IF EXISTS billing;
DROP TABLE IF EXISTS watch_history;
DROP TABLE IF EXISTS subscription;
DROP TABLE IF EXISTS subscription_plan;
DROP TABLE IF EXISTS content;
DROP TABLE IF EXISTS member;

SET FOREIGN_KEY_CHECKS = 1;
