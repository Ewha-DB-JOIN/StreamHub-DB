-- ============================================================
-- INIT DATA (샘플/초기 데이터)
-- 실행 순서: 1. dropschema.sql → 2. createschema.sql → 3. initdata.sql
-- ============================================================

-- TODO: 테스트용 초기 데이터를 여기에 작성하세요
-- 예시:
-- INSERT INTO users (username, password) VALUES ('admin', 'admin1234');
-- =====================================================
-- INIT DATA - content
-- 담당: 최보경
-- =====================================================

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
