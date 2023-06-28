package com.learning.model;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
	
	private Long question_no;
	private Long author_no;
	private String subject_code;
	private String question_content;
	private String question_a1;
	private String question_a2;
	private String question_a3;
	private String question_a4;
	private String question_a5;
	private String question_answer;
	private String question_explanation;
	private String question_pic;
	private LocalDateTime reg_date;
	private String subject_name;
	private String user_select;
    private MultipartFile pic_file;
    
}
