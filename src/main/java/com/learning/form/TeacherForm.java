package com.learning.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherForm {
	@Pattern(regexp="^[가-힣]*$" ,message = "선생님 권한 아이디는 한글만 가능합니다")
	@NotEmpty(message = "아이디는 필수입력사항입니다.")
	@Size(min = 2 , max = 12, message = "아이디는 2~12자 사이로 입력해주세요.")
	String username;
	
	@NotEmpty(message = "비밀번호는 필수입력사항입니다.")
	@Size(min = 8, max = 16, message = "비밀번호는 8~16자 사이로 입력해주세요.")
	String password;
	
	@NotEmpty(message = "비밀번호 확인칸을 입력해주세요.")
	String password2;
	
	@NotEmpty(message = "이메일은 필수입력사항입니다.")
	@Email
	String user_email;
	
	@NotEmpty(message = "전화번호는 필수입력사항입니다.")
	@Pattern(regexp = "^[0-9]*$" , message = "전화번호는 숫자만 입력해주세요.")
	String user_phone;
}
