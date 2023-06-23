package com.learning;

import lombok.Getter;

@Getter
public enum UserRole {
	ADMIN("ROLE_ADMIN"), USER("ROLE_USER"), TEACHER("ROLE_TEACHER");

	UserRole(String value) {
		this.value = value;
	}

	private String value;

}
