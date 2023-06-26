package com.learning.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.learning.domain.SiteUser;
import com.learning.form.UserForm;
import com.learning.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/signup")
	public String createUser(UserForm userForm) {
		return "user/signup_form";
	}

	@PostMapping("/signup")
	public String signup(@Valid UserForm userForm, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "user/signup_form";
		}
		// 패스워드와 확인이 같지 않을경우 에러메세지를 만들어 다시 되돌아감
		if (!userForm.getPassword().equals(userForm.getPassword2())) {
			result.rejectValue("user_password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			return "user/signup_form";
		}
		// 새로운 유저를 생성한다
		try {
			userService.createUser(userForm);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			result.reject("signupFailed", "이미 등록된 아이디나 이메일입니다.");
			return "user/signup_form"; // 되돌아감
		} catch (Exception e) {
			e.printStackTrace();
			result.reject("signupFailed", e.getMessage());
			return "user/signup_form"; // 되돌아감
		}
		Long user_no = userService.findUserNoById(userForm.getUsername());
		//최근 본 과목을 기록하기 위해 튜플 생성
		userService.createUserHistory(user_no);
		model.addAttribute("message", "회원가입에 성공하셨습니다");
		model.addAttribute("url", "/");
		return "alert"; // 회원가입 성공 메세지를 보내 alert 페이지로 (message에 따라 alert 내용을 다르게 설정)
	}

	@PreAuthorize("isAnonymous()")
	@GetMapping("/login")
	public String login() {
		return "user/login_form";
	}

	@GetMapping("/modify/{user_no}")
	public String modifyUser(@PathVariable("user_no") Long user_no, @ModelAttribute("userForm") UserForm userForm,
			Model model) {

		SiteUser user = userService.findUserByUserNo(user_no);
		model.addAttribute("user", user);
		return "user/modifyUser";
	}

	@PostMapping("/modify/{user_no}")
	public String modifyUser(@PathVariable("user_no") Long user_no, @Valid UserForm userForm, BindingResult result,
			Model model) {

		SiteUser modifyUser = userService.findUserByUserNo(user_no);
		//유효성 검사를 통과하지 못했을 시 Form으로 되돌아감
		if (result.hasErrors()) {
			model.addAttribute("user", modifyUser);
			return "user/modifyUser";
		}
		// 패스워드와 확인이 같지 않을경우 에러메세지를 만들어 다시 되돌아감
		if (!userForm.getPassword().equals(userForm.getPassword2())) {
			result.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			model.addAttribute("user", modifyUser);
			return "user/modifyUser";
		}
		// 유저 정보 바꾸기
		try {
			userService.modifyUser(modifyUser, userForm);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			result.reject("signupFailed", "이미 등록된 아이디나 이메일입니다.");
			return "user/modifyUser"; // 되돌아감
		} catch (Exception e) {
			e.printStackTrace();
			result.reject("signupFailed", e.getMessage());
			return "user/modifyUser"; // 되돌아감
		}

		model.addAttribute("message", "회원정보 수정완료");
		model.addAttribute("url", "/");

		return "alert";
	}

}
