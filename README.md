# learningSite
자바 스프링부트를 이용한 학습보조사이트 프로젝트
 - 개발환경 : java11, MySQL, SpringBoot, SpringSecurity, MyBatis, Thymeleaf, Bootstrap, javascript
 - 주요기능 : <br>
      - 페이지 전체를 관리 하는 관리자 기능<br>
      - 문제 제작, 수정, 삭제와 공지를 작성할 수 있는 'TEACHER_ROLE'<br>
      - 제작된 문제를 학습, 결과에 따라 오답노트 제공<br>
      - 오답문제를 질문할 수 있는 질문 게시판<br>

- --------------------------------
- SQL query (MySQL)

CREATE TABLE IF NOT EXISTS subject( subject_code VARCHAR(10) PRIMARY KEY, subject_name VARCHAR(30) NOT NULL);

CREATE TABLE IF NOT EXISTS user( user_no BIGINT PRIMARY KEY AUTO_INCREMENT, user_id VARCHAR(20) UNIQUE NOT NULL, user_password VARCHAR(200) NOT NULL, user_email VARCHAR(30) UNIQUE NOT NULL, user_phone VARCHAR(20) NOT NULL);

CREATE TABLE IF NOT EXISTS question( question_no BIGINT PRIMARY KEY AUTO_INCREMENT, author_no BIGINT, subject_code VARCHAR(10), question_content VARCHAR(300) NOT NULL, question_a1 VARCHAR(100), question_a2 VARCHAR(100), question_a3 VARCHAR(100), question_a4 VARCHAR(100), question_a5 VARCHAR(100), question_answer VARCHAR(100) NOT NULL, question_explanation VARCHAR(400), question_pic VARCHAR(50), reg_date TIMESTAMP, FOREIGN KEY (subject_code) REFERENCES subject(subject_code) ON UPDATE CASCADE, FOREIGN KEY (author_no) REFERENCES user(user_no) ON UPDATE CASCADE );

CREATE TABLE IF NOT EXISTS done_question( user_no BIGINT, question_no BIGINT, reg_date TIMESTAMP, correct CHAR(1) NOT NULL, user_select CHAR(2) NOT NULL, CONSTRAINT PRIMARY KEY(user_no, question_no), FOREIGN KEY (user_no) REFERENCES user(user_no) ON DELETE CASCADE, FOREIGN KEY (question_no) REFERENCES question(question_no) ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS board( board_no BIGINT PRIMARY KEY AUTO_INCREMENT, board_title VARCHAR(100) NOT NULL, board_content TEXT NOT NULL, user_no BIGINT, question_no BIGINT, subject_code VARCHAR(10), file_name VARCHAR(256), board_classify VARCHAR(20), reg_date TIMESTAMP, FOREIGN KEY (user_no) REFERENCES user(user_no) ON DELETE CASCADE, FOREIGN KEY (question_no) REFERENCES question(question_no) ON DELETE CASCADE, FOREIGN KEY (subject_code) REFERENCES subject(subject_code) ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS answer( answer_no BIGINT PRIMARY KEY AUTO_INCREMENT, board_no BIGINT, user_no BIGINT, answer_content TEXT NOT NULL, reg_date TIMESTAMP, FOREIGN KEY (board_no) REFERENCES board(board_no) ON DELETE CASCADE, FOREIGN KEY (user_no) REFERENCES user(user_no) ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS recently_visited( user_no BIGINT PRIMARY KEY, subject_code VARCHAR(10), FOREIGN KEY (user_no) REFERENCES user(user_no) ON DELETE CASCADE, FOREIGN KEY (subject_code) REFERENCES subject(subject_code) ON DELETE CASCADE);


