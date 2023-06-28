package com.learning.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SiteUser  {
	private Long user_no;
	private String username;
	private String password;
	private String user_email;
	private String user_phone;
}
