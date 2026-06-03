-- ============================================================
-- INITDATA.SQL  [REQ4]
-- 실행 순서: 1. dropschema.sql → 2. createschema.sql → 3. initdata.sql
-- ============================================================


-- ※ 각 담당자가 본인 테이블 INSERT 구문을 아래에 추가하세요
-- ※ FK 의존성 순서: member → content → subscription_plan
--                  → subscription → watch_history / billing / price_history / member_profile_history

-- ------------------------------------------------------------
-- [최보경] content
-- ------------------------------------------------------------
INSERT INTO content (title, genre, release_date, unit_price, description) VALUES
-- Drama
('Squid Game S1', 'Drama', '2021-09-17', 8.99, 'Korean survival drama with deadly childhood games'),
('Squid Game S2', 'Drama', '2024-12-26', 9.99, 'The second season of the global hit Squid Game'),
('The Glory', 'Drama', '2022-12-30', 8.99, 'A revenge story of a school violence victim'),
('Breaking Bad S1', 'Drama', '2008-01-20', 5.99, 'A chemistry teacher turns into a drug lord'),
('Game of Thrones S1', 'Fantasy', '2011-04-17', 7.99, 'Epic fantasy series about power and survival'),

-- Action
('Parasite', 'Drama', '2019-05-30', 8.99, 'Bong Joon-ho social thriller about class divide'),
('Train to Busan', 'Action', '2016-07-20', 7.99, 'Zombie outbreak on a Korean high-speed train'),
('Avengers: Endgame', 'Action', '2019-04-24', 9.99, 'The grand finale of the Marvel Avengers series'),
('Top Gun: Maverick', 'Action', '2022-06-22', 8.99, 'The legendary pilot Maverick returns'),
('The Roundup: Punishment', 'Action', '2024-04-24', 8.99, 'Fourth installment of the Korean action series'),

-- SciFi
('Interstellar', 'SciFi', '2014-11-06', 9.99, 'A journey through space and time to save humanity'),
('Inception', 'SciFi', '2010-07-16', 9.99, 'A thief enters dreams to plant an idea'),
('Dune: Part Two', 'SciFi', '2024-02-28', 9.99, 'Paul Atreides unites with the Fremen people'),
('The Matrix', 'SciFi', '1999-05-15', 7.99, 'A hacker discovers the truth about reality'),
('Alien: Romulus', 'SciFi', '2024-08-14', 9.99, 'Latest installment of the Alien franchise'),

-- Romance
('Crash Landing on You', 'Romance', '2019-12-14', 7.99, 'A Korean heiress crash-lands in North Korea'),
('Titanic', 'Romance', '1998-02-20', 7.99, 'A tragic love story aboard the doomed ship'),
('The Notebook', 'Romance', '2004-06-25', 6.99, 'A timeless love story spanning decades'),
('La La Land', 'Romance', '2016-12-07', 7.99, 'Two dreamers fall in love in Los Angeles'),
('Before Sunrise', 'Romance', '1995-01-27', 5.99, 'Two strangers share one unforgettable night'),

-- Horror / Thriller
('Exhuma', 'Horror', '2024-02-22', 8.99, 'Korean occult horror about disturbing a grave'),
('Get Out', 'Horror', '2017-02-24', 7.99, 'A social thriller about race and manipulation'),
('Signal', 'Thriller', '2016-01-22', 7.99, 'A walkie-talkie connects past and present detectives'),
('Knives Out', 'Thriller', '2019-11-27', 8.99, 'A clever whodunit full of twists'),
('12.12: The Day', 'Drama', '2023-11-22', 8.99, 'A dramatization of the 1979 Korean military coup'),

-- Animation
('Spirited Away', 'Animation', '2002-07-20', 7.99, 'A girl enters a mysterious spirit world'),
('Frozen', 'Animation', '2014-01-16', 6.99, 'Disney animated musical about sisterly love'),
('Inside Out 2', 'Animation', '2024-06-12', 8.99, 'Pixar sequel exploring teenage emotions'),
('Your Name', 'Animation', '2016-08-26', 7.99, 'Two teenagers mysteriously swap bodies'),
('The First Slam Dunk', 'Animation', '2023-01-04', 7.99, 'Theatrical film based on the Slam Dunk manga');

-- ------------------------------------------------------------
-- [신우림] price_history (REQ13용, 15 tuples)
-- plan_id 1~4 는 조수민 담당 subscription_plan 데이터 기준
-- ------------------------------------------------------------

INSERT INTO price_history (plan_id, old_price, new_price, valid_from, valid_to, changed_at) VALUES
-- plan 1 (광고형 베이직): 3회 변동
(1,  5500.00,  6900.00, '2022-01-01 00:00:00', '2023-03-01 00:00:00', '2022-01-01 09:00:00'),
(1,  6900.00,  7900.00, '2023-03-01 00:00:00', '2024-06-01 00:00:00', '2023-03-01 09:00:00'),
(1,  7900.00,  9900.00, '2024-06-01 00:00:00', NULL,                  '2024-06-01 09:00:00'),

-- plan 2 (스탠다드): 3회 변동
(2,  9900.00, 11900.00, '2022-06-01 00:00:00', '2023-06-01 00:00:00', '2022-06-01 09:00:00'),
(2, 11900.00, 13900.00, '2023-06-01 00:00:00', '2024-09-01 00:00:00', '2023-06-01 09:00:00'),
(2, 13900.00, 15900.00, '2024-09-01 00:00:00', NULL,                  '2024-09-01 09:00:00'),

-- plan 3 (프리미엄): 3회 변동
(3, 14900.00, 16900.00, '2022-03-01 00:00:00', '2023-01-01 00:00:00', '2022-03-01 09:00:00'),
(3, 16900.00, 17900.00, '2023-01-01 00:00:00', '2024-01-01 00:00:00', '2023-01-01 09:00:00'),
(3, 17900.00, 19900.00, '2024-01-01 00:00:00', NULL,                  '2024-01-01 09:00:00'),

-- plan 4 (패밀리): 3회 변동
(4, 17900.00, 19900.00, '2022-09-01 00:00:00', '2023-09-01 00:00:00', '2022-09-01 09:00:00'),
(4, 19900.00, 21900.00, '2023-09-01 00:00:00', '2025-01-01 00:00:00', '2023-09-01 09:00:00'),
(4, 21900.00, 24900.00, '2025-01-01 00:00:00', NULL,                  '2025-01-01 09:00:00'),

-- plan 1 추가 (분석 메뉴 다양성을 위한 초기 최저가 기록)
(1,  4900.00,  5500.00, '2021-01-01 00:00:00', '2022-01-01 00:00:00', '2021-01-01 09:00:00'),
(2,  8900.00,  9900.00, '2021-01-01 00:00:00', '2022-06-01 00:00:00', '2021-01-01 09:00:00'),
(3, 12900.00, 14900.00, '2021-01-01 00:00:00', '2022-03-01 00:00:00', '2021-01-01 09:00:00'),
(4, 15900.00, 17900.00, '2021-01-01 00:00:00', '2022-09-01 00:00:00', '2021-01-01 09:00:00');

-- ------------------------------------------------------------
-- [박나림] member, member_profile_history
-- [최보경] content
-- [조수민] subscription_plan
-- [이태영] subscription
-- [곽성은] watch_history  /  billing 지원
-- [하지수] billing
-- ------------------------------------------------------------

-- ------------------------------------------------------------
-- [박나림] member
-- ------------------------------------------------------------
INSERT INTO member (member_id, name, email, birth_date, region, created_at) VALUES
(1,  '김민준', 'minjun.kim@email.com',   '1990-03-15', 'Seoul',  '2023-01-10 09:00:00'),
(2,  '이서연', 'seoyeon.lee@email.com',  '1995-07-22', 'Busan',  '2023-02-05 10:00:00'),
(3,  '박지호', 'jiho.park@email.com',    '1988-11-30', 'Daegu',  '2023-03-12 11:00:00'),
(4,  '최수아', 'sua.choi@email.com',     '1992-05-18', 'Incheon','2023-04-20 09:30:00'),
(5,  '정도윤', 'doyun.jung@email.com',   '1998-09-05', 'Gwangju','2023-05-08 14:00:00'),
(6,  '강하은', 'haeun.kang@email.com',   '1993-01-25', 'Daejeon','2023-06-15 10:30:00'),
(7,  '윤시우', 'siu.yoon@email.com',     '1987-04-10', 'Seoul',  '2023-07-03 08:00:00'),
(8,  '임나연', 'nayeon.lim@email.com',   '1996-12-03', 'Busan',  '2023-08-19 13:00:00'),
(9,  '한준서', 'junseo.han@email.com',   '1991-08-27', 'Ulsan',  '2023-09-11 15:00:00'),
(10, '오채원', 'chaewon.oh@email.com',   '1999-02-14', 'Seoul',  '2023-10-07 11:00:00');                                                                                (1,  '김민준', 'minjun.kim@email.com',   '1990-03-15', 'Seoul',  '2023-01-10 09:00:00'),

-- ------------------------------------------------------------
-- [박나림] member_profile_history  [REQ14]
-- ------------------------------------------------------------
INSERT INTO member_profile_history (member_id, field_name, old_value, new_value, changed_at) VALUES
-- member 1: 서울 → 부산 → 인천 (2회 이사, 분석용으로 풍부하게)
(1, 'region', 'Seoul',   'Busan',    '2024-03-01 10:00:00'),
(1, 'region', 'Busan',   'Incheon',  '2025-01-15 09:00:00'),
-- member 2: 부산 → 서울
(2, 'region', 'Busan',   'Seoul',    '2024-06-10 14:00:00'),
-- member 3: 나이(생년월일 변경 오류 수정 케이스)
(3, 'birth_date', '1988-11-30', '1989-11-30', '2024-02-20 11:00:00'),
-- member 4: 인천 → 대구
(4, 'region', 'Incheon', 'Daegu',    '2024-08-05 16:00:00'),
-- member 5: 광주 → 서울
(5, 'region', 'Gwangju', 'Seoul',    '2024-04-18 10:30:00'),
-- member 6: 이메일 변경
(6, 'email', 'haeun.kang@email.com', 'haeun.kang@newmail.com', '2024-07-01 09:00:00'),
-- member 7: 서울 → 대전
(7, 'region', 'Seoul',   'Daejeon',  '2024-09-20 13:00:00'),
-- member 8: 부산 → 울산
(8, 'region', 'Busan',   'Ulsan',    '2025-02-10 10:00:00'),
-- member 9: 울산 → 부산
(9, 'region', 'Ulsan',   'Busan',    '2024-11-30 17:00:00'),
-- member 10: 서울 → 광주
(10, 'region', 'Seoul',  'Gwangju',  '2024-05-25 12:00:00');


-- [하지수] billing
INSERT INTO billing (sub_id, billing_date, applied_price, total_amount) VALUES
(1, '2026-01-01', 9900.00, 9900.00),
(2, '2026-01-02', 14900.00, 14900.00),
(3, '2026-01-03', 7900.00, 7900.00),
(4, '2026-01-04', 12900.00, 12900.00),
(5, '2026-01-05', 19900.00, 19900.00),
(6, '2026-01-06', 8900.00, 8900.00),
(7, '2026-01-07', 15900.00, 15900.00),
(8, '2026-01-08', 10900.00, 10900.00),
(9, '2026-01-09', 13900.00, 13900.00),
(10, '2026-01-10', 17900.00, 17900.00),

(1, '2026-02-01', 10900.00, 10900.00),
(2, '2026-02-02', 14900.00, 14900.00),
(3, '2026-02-03', 7900.00, 7900.00),
(4, '2026-02-04', 13900.00, 13900.00),
(5, '2026-02-05', 19900.00, 19900.00),
(6, '2026-02-06', 8900.00, 8900.00),
(7, '2026-02-07', 16900.00, 16900.00),
(8, '2026-02-08', 10900.00, 10900.00),
(9, '2026-02-09', 14900.00, 14900.00),
(10, '2026-02-10', 17900.00, 17900.00),

(1, '2026-03-01', 10900.00, 10900.00),
(2, '2026-03-02', 15900.00, 15900.00),
(3, '2026-03-03', 8900.00, 8900.00),
(4, '2026-03-04', 13900.00, 13900.00),
(5, '2026-03-05', 20900.00, 20900.00),
(6, '2026-03-06', 9900.00, 9900.00),
(7, '2026-03-07', 16900.00, 16900.00),
(8, '2026-03-08', 11900.00, 11900.00),
(9, '2026-03-09', 14900.00, 14900.00),
(10, '2026-03-10', 18900.00, 18900.00);
