package com.learning.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

	private Long answer_no;
	private Long board_no;
	private Long user_no;
	private String answer_content;
	private String username;
	private LocalDateTime reg_date;
	
	
}
