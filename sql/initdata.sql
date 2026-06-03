-- ============================================================
-- INIT DATA (샘플/초기 데이터)
-- 실행 순서: 1. dropschema.sql → 2. createschema.sql → 3. initdata.sql
-- ============================================================

-- TODO: 테스트용 초기 데이터를 여기에 작성하세요
-- 예시:
-- INSERT INTO users (username, password) VALUES ('admin', 'admin1234');
INSERT INTO subscription
(member_id, plan_id, start_date, end_date, region_snapshot, status)
VALUES
(11, 1, '2026-01-11', NULL, 'Incheon', 'active'),
(12, 2, '2026-01-12', NULL, 'Daegu', 'active'),
(13, 3, '2026-01-13', NULL, 'Gwangju', 'active'),
(14, 4, '2026-01-14', NULL, 'Daejeon', 'active'),
(15, 1, '2026-01-15', NULL, 'Busan', 'active'),
(16, 2, '2026-01-16', NULL, 'Seoul', 'paused'),
(17, 3, '2026-01-17', NULL, 'Incheon', 'active'),
(18, 4, '2026-01-18', NULL, 'Daegu', 'active'),
(19, 1, '2026-01-19', NULL, 'Gwangju', 'cancelled'),
(20, 2, '2026-01-20', NULL, 'Daejeon', 'active'),
(21, 3, '2026-01-21', NULL, 'Seoul', 'active'),
(22, 4, '2026-01-22', NULL, 'Busan', 'paused'),
(23, 1, '2026-01-23', NULL, 'Incheon', 'active'),
(24, 2, '2026-01-24', NULL, 'Daegu', 'active'),
(25, 3, '2026-01-25', NULL, 'Gwangju', 'expired'),
(26, 4, '2026-01-26', NULL, 'Daejeon', 'active'),
(27, 1, '2026-01-27', NULL, 'Seoul', 'active'),
(28, 2, '2026-01-28', NULL, 'Busan', 'active'),
(29, 3, '2026-01-29', NULL, 'Incheon', 'paused'),
(30, 4, '2026-01-30', NULL, 'Daegu', 'active');