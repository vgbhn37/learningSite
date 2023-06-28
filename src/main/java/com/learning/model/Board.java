package com.learning.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Board {
	
	private Long board_no;
	private Long user_no;
	private Long question_no;
	private String subject_code;
	private String board_title;	
	private String username;
	private String board_content;
	private String file_name;
	private String subject_name;
	private String board_classify;
	private LocalDateTime reg_date;
	
}

