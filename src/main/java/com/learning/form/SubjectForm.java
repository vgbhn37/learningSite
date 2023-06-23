package com.learning.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectForm {
	
	@NotEmpty(message = "과목코드를 설정해주세요.")
	@Size(min = 4 , max = 4 , message = "과목코드는 4자리 숫자로 설정해주세요.")
	@Pattern(regexp ="^[0-9]*$", message = "과목코드는 4자리 숫자로 설정해주세요.")
	String subject_code;
	
	@Size(max = 15 , message = "과목명은 15자 이하로 설정해주세요.")
	@NotEmpty(message = "과목이름을 설정해주세요.")
	String subject_name;

}
