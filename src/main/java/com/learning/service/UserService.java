package com.learning.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.learning.domain.Criteria;
import com.learning.domain.SiteUser;
import com.learning.form.TeacherForm;
import com.learning.form.UserForm;
import com.learning.mapper.UserMapper;

@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private PasswordEncoder passEncoder;

	public void createUser(UserForm userForm) {
		SiteUser joinUser = new SiteUser();
		joinUser.setUsername(userForm.getUsername());
		joinUser.setPassword(passEncoder.encode(userForm.getPassword()));
		joinUser.setUser_email(userForm.getUser_email());
		joinUser.setUser_phone(userForm.getUser_phone());
		userMapper.createUser(joinUser);
	}
	
	public void modifyUser(SiteUser modifyUser, UserForm userForm) {
		modifyUser.setUsername(userForm.getUsername());
		modifyUser.setPassword(passEncoder.encode(userForm.getPassword()));
		modifyUser.setUser_email(userForm.getUser_email());
		modifyUser.setUser_phone(userForm.getUser_phone());
		userMapper.modifyUser(modifyUser);
	}
	
	public void deleteUser(Long user_no) {
		
		userMapper.deleteUser(user_no);
	}
	
	public void createTeacher(TeacherForm teacherForm) {
		SiteUser joinUser = new SiteUser();
		joinUser.setUsername(teacherForm.getUsername());
		joinUser.setPassword(passEncoder.encode(teacherForm.getPassword()));
		joinUser.setUser_email(teacherForm.getUser_email());
		joinUser.setUser_phone(teacherForm.getUser_phone());
		userMapper.createUser(joinUser);
	}
	
	public void modifyTeacher(SiteUser teacher, TeacherForm teacherForm) {
		teacher.setUsername(teacherForm.getUsername());
		teacher.setPassword(passEncoder.encode(teacherForm.getPassword()));
		teacher.setUser_email(teacherForm.getUser_email());
		teacher.setUser_phone(teacherForm.getUser_phone());
		userMapper.modifyUser(teacher);
	}

	public void createUserHistory(Long user_no) {
		userMapper.createUserHistory(user_no);
	}

	public SiteUser findUserById(String username) {
		return userMapper.findUserById(username);
	}
	
	public SiteUser findUserByUserNo(Long user_no) {
		return userMapper.findUserByUserNo(user_no);
	}

	public Long findUserNoById(String username) {
		return userMapper.findUserNoById(username);
	}
	
	public List<SiteUser> getUserListWithPage(Criteria criteria){
		return userMapper.getUserListWithPage(criteria);
	}
	
	public List<SiteUser> getUserListBySearchWithPage(Criteria criteria, String search){
		return userMapper.getUserListBySearchWithPage(criteria, search);
	}
	
	public List<SiteUser> getManagerList(){
		return userMapper.getManagerList();
	}
	
	public int getUserTotal() {
		return userMapper.getUserTotal();
	}
	
	public int getUserTotalBySearch(String search) {
		return userMapper.getUserTotalBySearch(search);
	}
	
	public void updateUserHistory(String subject_code, Long user_no) {
		userMapper.updateUserHistory(subject_code, user_no);
	}
	
	public String findUserHistory(Long user_no) {
		return userMapper.findUserHistory(user_no);
	}
	
	
}
