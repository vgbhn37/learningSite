package com.learning.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoneQuestion {
	private Long user_no;
	private Long question_no;
	private LocalDateTime reg_date;
	private String correct;
	private String user_select;
	
}
