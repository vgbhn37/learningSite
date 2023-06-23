package com.learning.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
	
	
	@Size(min = 4, max = 4, message = "과목을 선택해주세요.")
	String subject_code;
	
	@NotEmpty(message = "문제를 작성해주세요.")
	@Size(max = 300, message = "문제는 300자 이하로 작성해주세요.")
	String question_content;
	
	@Size(max = 100, message = "보기는 100자 이하로 작성해주세요.")
	@NotEmpty(message = "보기 1번을 작성해주세요.")
	String question_a1;
	
	@NotEmpty(message = "보기 2번을 작성해주세요.")
	@Size(max = 100, message = "보기는 100자 이하로 작성해주세요.")
	String question_a2;
	
	@Size(max = 100, message = "보기는 100자 이하로 작성해주세요.")
	String question_a3;
	
	@Size(max = 100, message = "보기는 100자 이하로 작성해주세요.")
	String question_a4;
	
	@Size(max = 100, message = "보기는 100자 이하로 작성해주세요.")
	String question_a5;
	
	@NotEmpty(message = "문제의 답을 작성해주세요.")
	@Pattern(regexp="^[1-5]*$" ,message = "정답은 1~5 사이 숫자로만 입력해주세요 (정답번호)")
	String question_answer;
	
	@Size(max = 400, message = "해설은 400자 이하로 작성해주세요")
	String question_explanation;
	
	MultipartFile pic_file;
}
