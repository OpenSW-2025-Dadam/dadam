-- ⭐ 1. 모든 테이블 삭제 (외래 키 역순)
DROP TABLE IF EXISTS quiz_selection;
DROP TABLE IF EXISTS balance_game_selection;
DROP TABLE IF EXISTS answer;
DROP TABLE IF EXISTS balance_game;
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS quiz;
DROP TABLE IF EXISTS app_user;

-- =======================================================
-- 2. 핵심 테이블 생성 (AUTO_INCREMENT 값 제거)
-- =======================================================

CREATE TABLE `app_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_email` (`email`) -- 키 이름도 명시적으로 변경
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` enum('DAILY','ETC','FOOD','HOBBY','MEMORY','TRAVEL') NOT NULL,
  `content` varchar(500) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `balance_game` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_content` varchar(500) NOT NULL,
  `option_a` varchar(255) NOT NULL,
  `option_b` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =======================================================
-- 3. 외래 키를 참조하는 테이블 생성 (FK 이름 명시적으로 변경)
-- =======================================================

CREATE TABLE `answer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(1000) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `question_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_answer_question` (`question_id`),
  KEY `FK_answer_user` (`user_id`),
  -- ⭐ FK 이름 수정
  CONSTRAINT `FK_answer_user` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FK_answer_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `balance_game_selection` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `selected_option` varchar(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `balance_game_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_game_user` (`balance_game_id`,`user_id`),
  CONSTRAINT `FK_selection_game` FOREIGN KEY (`balance_game_id`) REFERENCES `balance_game` (`id`),
  CONSTRAINT `FK_selection_user` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `quiz` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_content` varchar(500) NOT NULL,
  `option_a` varchar(255) NOT NULL,
  `option_b` varchar(255) NOT NULL,
  `option_c` varchar(255) NOT NULL,
  `option_d` varchar(255) NOT NULL,
  `correct_answer` varchar(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `quiz_selection` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `selected_option` varchar(1) NOT NULL,
  `is_correct` tinyint(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `quiz_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_quiz_user` (`quiz_id`,`user_id`),
  CONSTRAINT `FK_selection_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`),
  CONSTRAINT `FK_selection_user_quiz` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;