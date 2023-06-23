package com.learning.form;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardForm {
	
	@NotEmpty(message = "게시글 제목을 입력해주세요.")
	private String board_title;
	
	private Long question_no;
	
	private String username;
	
	@NotEmpty(message = "게시글 내용을 입력해주세요.")
	private String board_content;
}
