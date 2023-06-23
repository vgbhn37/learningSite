package com.learning.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.learning.UserRole;
import com.learning.domain.SiteUser;


@Service
public class UserSecurityService implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SiteUser siteUser = userService.findUserById(username);
	
		if (siteUser == null) {
			throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
		}

		List<GrantedAuthority> authorities = new ArrayList<>();
		if ("admin".equals(username)) {
			authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
		} else if (username.matches("^[가-힣]*$")) {  
			authorities.add(new SimpleGrantedAuthority(UserRole.TEACHER.getValue()));
		} else {
			authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
		}

		User _siteUser = new User(siteUser.getUsername(), siteUser.getPassword(), authorities);

		return _siteUser;

	}
}
