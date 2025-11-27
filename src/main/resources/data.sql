SET FOREIGN_KEY_CHECKS = 0;
-- 1. 기존 데이터 초기화 (테스트 반복을 위해)
DELETE FROM answer;
DELETE FROM question;
DELETE FROM app_user;
DELETE FROM balance_game_selection;
DELETE FROM balance_game;
DELETE FROM quiz_selection;
DELETE FROM quiz;

-- Auto Increment 카운터 재설정 (ID 충돌 방지)
ALTER TABLE app_user AUTO_INCREMENT = 1;
ALTER TABLE question AUTO_INCREMENT = 1;
ALTER TABLE answer AUTO_INCREMENT = 1;
ALTER TABLE balance_game AUTO_INCREMENT = 1;
ALTER TABLE balance_game_selection AUTO_INCREMENT = 1;
ALTER TABLE quiz AUTO_INCREMENT = 1;
ALTER TABLE quiz_selection AUTO_INCREMENT = 1;

-- 2. 테스트 사용자 삽입
INSERT INTO app_user (email, name, password) VALUES
('parent_user_1@dadam.com', '부모님', 'test_password'),
('child_user_2@dadam.com', '자녀1', 'test_password'),
('child_user_3@dadam.com', '자녀2', 'test_password');

-- 3. 테스트 질문 삽입
INSERT INTO question (content, category, created_at) VALUES
('가족과 함께한 가장 즐거웠던 여행은 무엇인가요?', 'TRAVEL', NOW()),
('요즘 가족 구성원 각자가 빠져 있는 취미는?', 'HOBBY', NOW()),
('서로에게 가장 고마웠던 순간 하나씩 이야기해 볼까요?', 'MEMORY', NOW());

-- 4. 테스트 답변 삽입 (User ID 1, 2, 3은 위 INSERT로 자동 할당됨)
-- Question ID 1에 대한 답변
INSERT INTO answer (question_id, user_id, content, created_at) VALUES
(1, 2, '작년에 갔던 해외여행! 공항에서 길 잃어버릴 뻔한 게 짜릿했어요.', NOW()),
(1, 3, '그냥 집 근처 공원에서 텐트 치고 놀았던 주말이 제일 편하고 즐거웠어요.', NOW() + INTERVAL 1 MINUTE);

-- Question ID 2에 대한 답변
INSERT INTO answer (question_id, user_id, content, created_at) VALUES
(2, 1, '요즘 저는 주말에 새로운 레시피로 요리하는 것에 푹 빠져 있어요.', NOW()),
(2, 2, '저는 게임이요! 특히 다같이 할 수 있는 보드 게임을 다시 모으고 있어요!!', NOW() + INTERVAL 1 MINUTE),
(2, 3, '저는 그림 그리기요. 가족들 몰래 방에서 열심히 그리고 있어요.', NOW() + INTERVAL 2 MINUTE);

-- Question ID 3에 대한 답변
INSERT INTO answer (question_id, user_id, content, created_at) VALUES
(3, 1, '힘들 때 말없이 어깨를 토닥여준 순간이 가장 고마웠어요.', NOW()),
(3, 2, '생일날 깜짝 이벤트 해줬을 때요! 평생 잊지 못할 거예요.', NOW() + INTERVAL 1 MINUTE),
(3, 3, '제가 잘못했을 때 혼내지 않고 차분히 이야기해 줬던 그날이 기억에 남아요.', NOW() + INTERVAL 2 MINUTE);

INSERT INTO balance_game (question_content, option_a, option_b, created_at) VALUES
('Q1: 모든 구성원 참여 완료 - 밸런스 게임 Q1', 'A: 민트초코', 'B: 반민트초코', NOW()),
('Q2: 나만 참여 안 함 - 밸런스 게임 Q2', 'A: 평생 한 가지 음식만 먹기', 'B: 평생 한 가지 색깔만 보기', NOW() + INTERVAL 1 HOUR);

INSERT INTO balance_game_selection (balance_game_id, user_id, selected_option, created_at) VALUES
(1, 1, 'A', NOW()),       -- 나(User 1) 참여
(1, 2, 'B', NOW() + INTERVAL 1 MINUTE),
(1, 3, 'A', NOW() + INTERVAL 2 MINUTE);

INSERT INTO balance_game_selection (balance_game_id, user_id, selected_option, created_at) VALUES
(2, 2, 'B', NOW() + INTERVAL 3 MINUTE),
(2, 3, 'A', NOW() + INTERVAL 4 MINUTE);

-- 퀴즈 1 (ID=1): 모두 참여 (정답: A)
INSERT INTO quiz (id, question_content, option_a, option_b, option_c, option_d, correct_answer, created_at)
VALUES (1, '신조어 "억텐"의 정확한 의미는?', '억지로 밝은 척 하는 텐션', '1억을 버는 텐트 판매 사업', '억 소리 나게 대단한 텐션', '억압된 텐션을 폭발시킴', 'A', NOW());

-- 퀴즈 2 (ID=2): 나(ID=1) 제외 모두 참여 (정답: D)
INSERT INTO quiz (id, question_content, option_a, option_b, option_c, option_d, correct_answer, created_at)
VALUES (2, '신조어 "군싹"의 의미는?', '군대에서 사용하던 싹수', '군것질거리 싹쓸이', '군침이 싹 도네', '군인을 싹둑 자르는 행동', 'C', NOW());

-- 퀴즈 1 참여 (모두 참여, ID=1이 정답)
INSERT INTO quiz_selection (quiz_id, user_id, selected_option, is_correct, created_at)
VALUES  -- User 1: 정답
(1, 2, 'B', 0, NOW()),  -- User 2: 오답
(1, 3, 'A', 1, NOW());  -- User 3: 정답

-- 퀴즈 2 참여 (나(ID=1) 제외 모두 참여, ID=2가 정답)
INSERT INTO quiz_selection (quiz_id, user_id, selected_option, is_correct, created_at)
VALUES
(2, 1, 'A', 0, NOW()),
(2, 2, 'D', 0, NOW()),  -- User 2: 오답
(2, 3, 'C', 1, NOW());  -- User 3: 정답

SET FOREIGN_KEY_CHECKS = 1;