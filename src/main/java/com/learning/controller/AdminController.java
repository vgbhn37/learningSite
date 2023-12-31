package com.learning.controller;

import java.security.Principal;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.learning.form.QuestionForm;
import com.learning.form.SubjectForm;
import com.learning.form.TeacherForm;
import com.learning.form.UserForm;
import com.learning.model.Criteria;
import com.learning.model.PageMaker;
import com.learning.model.Question;
import com.learning.model.SiteUser;
import com.learning.model.Subject;
import com.learning.service.QuestionService;
import com.learning.service.SubjectService;
import com.learning.service.UserService;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	SubjectService subjectService;

	@Autowired
	UserService userService;

	@Autowired
	QuestionService questionService;

	// 문제 만들기 페이지
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/createQuestion/{subject_code}")
	public String createQuestion(@PathVariable("subject_code") String subject_code,
			@ModelAttribute("questionForm") QuestionForm questionForm, Model model) {
		model.addAttribute("subject_code", subject_code);
		return "admin/createQuestionForm";
	}

	// 문제 만들기 process
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@PostMapping("/createQuestion")
	public String createQuestion(@Valid QuestionForm questionForm, BindingResult result, Model model,
			Principal principal) {
		if (result.hasErrors()) {

			return "admin/createQuestionForm";
		}

		// question DB에 들어갈 user_id
		Long author_id = userService.findUserNoById(principal.getName());
		// questionForm을 Service로 넘겨 service에서 처리
		questionService.createQuestion(questionForm, author_id);

		model.addAttribute("message", "문제 업로드 완료");
		model.addAttribute("url", "/admin/createQuestion/" + questionForm.getSubject_code());
		return "alert";
	}

	// 과목 만들기 페이지
	@GetMapping("/createSubject")
	public String createSubject(SubjectForm subjectForm) {
		return "admin/createSubjectForm";
	}

	// 과목 만들기 process
	@PostMapping("/createSubject")
	public String createSubject(@Valid SubjectForm subjectForm, BindingResult result, Model model) {

		if (result.hasErrors()) {
			return "admin/createSubjectForm";
		}

		// Form에 입력된 값으로 이미 등록된 과목이 있는지 검색
		Subject subjectByCode = subjectService.getSubjectByCode(subjectForm.getSubject_code());
		Subject subjectByName = subjectService.getSubjectByName(subjectForm.getSubject_name());

		// 만약 입력된 값에 해당하는 과목이 있으면 에러메세지 출력 후 Form으로 되돌아감
		if (subjectByName != null) {
			result.reject("createSubjectFailed", "이미 등록된 과목이름입니다.");
			return "admin/createSubjectForm"; // 되돌아감
		} else if (subjectByCode != null) {
			result.reject("createSubjectFailed", "이미 등록된 과목코드입니다.");
			return "admin/createSubjectForm"; // 되돌아감
		}

		subjectService.createSubject(subjectForm);
		model.addAttribute("message", "과목 생성 완료");
		model.addAttribute("url", "/admin/subject");
		return "alert";

	}

	// 과목관리 페이지
	@GetMapping("/subject")
	public String subjectList(Model model) {
		List<Subject> sList = subjectService.getSubjectList();
		model.addAttribute("sList", sList);
		return "admin/subjectList";
	}

	// 과목관리 페이지에서 과목명으로 과목 검색(LIKE 연산자 활용)
	@GetMapping("/subjectSearch")
	public String searchSubject(@RequestParam(value = "search") String search, Model model) {
		// 검색어를 search 파라미터로 받아서 활용
		List<Subject> sList = subjectService.getSubjectListBySearch(search);
		model.addAttribute("sList", sList);
		return "admin/subjectList";
	}

	// 과목 수정 페이지
	@GetMapping("/modifySubject/{subject_code}")
	public String modifySubject(@PathVariable("subject_code") String subject_code,
			@ModelAttribute("subjectForm") SubjectForm subjectForm, Model model) {
		Subject subject = subjectService.getSubjectByCode(subject_code);
		model.addAttribute("subject", subject);
		return "admin/modifySubjectForm";
	}

	// 과목 수정 process
	@PostMapping("/modifySubject/{subject_code}")
	public String modifySubject(@PathVariable("subject_code") String subject_code, @Valid SubjectForm subjectForm,
			BindingResult result, Model model) {

		// Form에 입력된 값으로 이미 등록된 과목이 있는지 검색
		Subject subjectByCode = subjectService.getSubjectByCode(subject_code);
		Subject subjectByName = subjectService.getSubjectByName(subjectForm.getSubject_name());

		// 이미 등록된 과목이 있거나 유효성 검사를 통과 못할 시 Form으로 되돌아감
		if (result.hasErrors()) {
			model.addAttribute("subject", subjectByCode);
			return "admin/modifySubjectForm";
		} else if (subjectByCode.getSubject_name().equals(subjectForm.getSubject_name())) {
			result.reject("createSubjectFailed", "이전에 사용 중인 과목이름입니다. 새로운 과목이름을 등록해주세요");
			model.addAttribute("subject", subjectByCode);
			return "admin/modifySubjectForm";
		} else if (subjectByName != null) {
			result.reject("createSubjectFailed", "이미 등록된 과목이름입니다.");
			model.addAttribute("subject", subjectByCode);
			return "admin/modifySubjectForm";
		}
		subjectService.updateSubject(subjectForm);
		model.addAttribute("message", "과목 수정 완료");
		model.addAttribute("url", "/admin/subject");
		return "alert";
	}

	// 과목 삭제
	@GetMapping("/deleteSubject/{subject_code}")
	public String deleteSubject(@PathVariable("subject_code") String subject_code, Model model) {

		Subject subjectByCode = subjectService.getSubjectByCode(subject_code);
		List<Question> QList = questionService.getQuestionListBySubject(subject_code);

		// 주소창에 url로 직접 접근 시, 없는 과목코드를 주소창에 입력했으면 에러페이지(alert창 띄운 뒤 뒤로가기로 설정)
		if (subjectByCode == null) {
			return "error/500";
			// 문제가 이미 등록되어 있는 과목의 경우 삭제가 안되도록 함(DB 외래키 설정 - on delete cascade 설정을 해두지않음)
		} else if (QList.size() > 0) {
			model.addAttribute("message", "문제가 등록되어 있는 과목은 삭제할 수 없습니다.");
			model.addAttribute("url", "/admin/subject");
			return "alert";
		}

		subjectService.deleteSubject(subject_code);
		model.addAttribute("message", "과목 삭제 완료");
		model.addAttribute("url", "/admin/subject");
		return "alert";
	}

	// 문제 관리 페이지
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/question")
	public String question(Model model) {
		List<Subject> sList = subjectService.getSubjectList();
		model.addAttribute("sList", sList);
		return "admin/selectSubject";
	}

	// 문제 관리 페이지에서 과목 검색후 그에 맞는 과목리스트 출력(LIKE)
	@GetMapping("/searchSub")
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	public String searchSub(@RequestParam(value = "search") String search, Model model) {
		List<Subject> sList = subjectService.getSubjectListBySearch(search);
		model.addAttribute("sList", sList);
		return "admin/selectSubject";
	}

	// 선택한 과목에 해당하는 문제 리스트 출력
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/questionList/{subject_code}")
	public String questionList(@PathVariable("subject_code") String subject_code, Criteria criteria, Model model) {

		// 페이징의 기준이 되는 critria객체와 subject_code를 받아 해당 과목의 문제 리스트를 1페이지에 10개씩 보여주기
		List<Question> qList = questionService.getQuestionListBySubjectWithPage(criteria, subject_code);
		int total = questionService.getQuestionTotal(subject_code);
		Subject subject = subjectService.getSubjectByCode(subject_code);

		// 해당 과목 문제의 총 갯수와 페이징 기준 criteria로 페이지를 만듦
		PageMaker pageMaker = new PageMaker(criteria, total);

		model.addAttribute("pmk", pageMaker);
		model.addAttribute("qList", qList);
		model.addAttribute("subject", subject);
		return "admin/questionList";
	}

	// 문제 관리 페이지에서 문제 검색후 그에 맞는 문제리스트 출력(LIKE)
	@GetMapping("/searchQuestion/{subject_code}")
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	public String searchQuestion(@PathVariable("subject_code") String subject_code,
			@RequestParam(value = "search") String search, Model model) {
		List<Question> qList = questionService.getQuestionListByContent(search, subject_code);
		Subject subject = subjectService.getSubjectByCode(subject_code);
		model.addAttribute("qList", qList);
		model.addAttribute("subject", subject);
		return "admin/questionList";
	}

	// 문제 상세보기(수정, 삭제 가능)
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/manageQuestion/{question_no}")
	public String manageQuestion(@PathVariable("question_no") Long question_no, Principal principal, Model model) {
		Question question = questionService.getQuestionByQNo(question_no);
		// 해당 문제의 제작자 이름을 페이지에 표시하기 위함
		String username = questionService.getUsernameByQuestion(question.getAuthor_no());
		// 현재 로그인 한 유저의 정보를 보내 admin이나 해당 문제를 제작한 사람이 아니면 문제 수정 및 삭제를 못하게 함
		String loginUser = principal.getName();

		model.addAttribute("loginUser", loginUser);
		model.addAttribute("author_name", username);
		model.addAttribute("question", question);
		return "admin/manageQuestion";
	}

	// 문제 수정
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/modifyQuestion/{question_no}")
	public String modifyQuestion(@PathVariable("question_no") Long question_no,
			@ModelAttribute("questionForm") QuestionForm questionForm, Model model) {
		Question question = questionService.getQuestionByQNo(question_no);
		model.addAttribute("question", question);
		return "admin/modifyQuestionForm";
	}

	// 문제 수정 process
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@PostMapping("/modifyQuestion/{question_no}")
	public String modifyQuestion(@PathVariable("question_no") Long question_no, @Valid QuestionForm questionForm,
			BindingResult result, Model model) {
		Question question = questionService.getQuestionByQNo(question_no);

		if (result.hasErrors()) {
			model.addAttribute("question", question);
			return "admin/modifyQuestionForm";
		}

		questionService.updateQuestion(question, questionForm);
		model.addAttribute("message", "문제 수정 완료");
		model.addAttribute("url", "/admin/questionList/" + questionForm.getSubject_code());
		return "alert";
	}

	// 문제 삭제
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/deleteQuestion/{question_no}")
	public String deleteQuestion(@PathVariable("question_no") Long question_no, Model model) {

		Question question = questionService.getQuestionByQNo(question_no);
		// 주소창에 직접 url 입력 시, 이미 없는 문제번호라면 에러페이지 (alert 후 뒤로가기)
		if (question == null) {
			return "error/500";
		}

		model.addAttribute("message", "문제 삭제 완료");
		model.addAttribute("url", "/admin/questionList/" + question.getSubject_code());
		questionService.deleteQuestion(question_no);

		return "alert";
	}

	@GetMapping("/createTeacher")
	public String createTeacher(TeacherForm teacherForm) {
		return "admin/createTeacherForm";
	}

	@PostMapping("/createTeacher")
	public String signup(@Valid TeacherForm teacherForm, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "admin/createTeacherForm";
		}
		// 패스워드와 확인이 같지 않을경우 에러메세지를 만들어 다시 되돌아감
		if (!teacherForm.getPassword().equals(teacherForm.getPassword2())) {
			result.rejectValue("user_password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			return "admin/createTeacherForm";
		}
		// 새로운 선생님계정 생성한다
		try {
			userService.createTeacher(teacherForm);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			result.reject("signupFailed", "이미 등록된 아이디나 이메일입니다.");
			return "admin/createTeacherForm"; // 되돌아감
		} catch (Exception e) {
			e.printStackTrace();
			result.reject("signupFailed", e.getMessage());
			return "admin/createTeacherForm"; // 되돌아감
		}
		Long user_no = userService.findUserNoById(teacherForm.getUsername());
		userService.createUserHistory(user_no);
		model.addAttribute("message", "선생님계정 만들기에 성공하셨습니다");
		model.addAttribute("url", "/admin/manager");
		return "alert"; // 회원가입 성공 메세지를 보내 alert 페이지로
	}

	// 회원 리스트
	@GetMapping("/user")
	public String userList(Criteria criteria, Model model) {

		// 페이징의 기준이 되는 criteria 객체를 가지고 전체 회원리스트를 불러옴 (관리자 계정과 선생님 계정 제외)
		List<SiteUser> userList = userService.getUserListWithPage(criteria);
		int total = userService.getUserTotal();
		// 전체 일반 유저 수와 criteria를 가지고 페이지메이커를 만들어 페이지네이션
		PageMaker pageMaker = new PageMaker(criteria, total);

		model.addAttribute("pmk", pageMaker);
		model.addAttribute("userList", userList);
		return "admin/userList";
	}

	// 회원 검색후 결과
	@GetMapping("/userSearch")
	public String searchUserList(@RequestParam("search") String search, Criteria criteria, Model model) {

		// 페이징의 기준이 되는 criteria 객체와 검색어를 가져와 검색어에 해당하는 유저리스트 가졍기
		List<SiteUser> userList = userService.getUserListBySearchWithPage(criteria, search);
		int total = userService.getUserTotalBySearch(search);
		// 검색된 유저의 총 인원 수와 criteria를 가지고 페이지네이션
		PageMaker pageMaker = new PageMaker(criteria, total);

		model.addAttribute("pmk", pageMaker);
		model.addAttribute("userList", userList);

		return "admin/userList";
	}

	// 유저 및 관리자 정보 수정
	@GetMapping("/modifyUser/{user_no}")
	public String modifyUser(@PathVariable("user_no") Long user_no, @ModelAttribute("userForm") UserForm userForm,
			Model model) {

		SiteUser user = userService.findUserByUserNo(user_no);
		model.addAttribute("user", user);
		return "admin/modifyUserForm";
	}

	// 유저 정보 수정 process
	@PostMapping("/modifyUser/{user_no}")
	public String modifyUser(@PathVariable("user_no") Long user_no, @Valid UserForm userForm, BindingResult result,
			Model model) {

		SiteUser modifyUser = userService.findUserByUserNo(user_no);

		if (result.hasErrors()) {
			model.addAttribute("user", modifyUser);
			return "admin/modifyUserForm";
		}
		// 패스워드와 확인이 같지 않을경우 에러메세지를 만들어 다시 되돌아감
		if (!userForm.getPassword().equals(userForm.getPassword2())) {
			result.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			model.addAttribute("user", modifyUser);
			return "admin/modifyUserForm";
		}
		// 유저 정보 바꾸기
		try {
			userService.modifyUser(modifyUser, userForm);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			result.reject("signupFailed", "이미 등록된 아이디나 이메일입니다.");
			return "admin/modifyUserForm"; // 되돌아감
		} catch (Exception e) {
			e.printStackTrace();
			result.reject("signupFailed", e.getMessage());
			return "admin/modifyUserForm"; // 되돌아감
		}

		// admin일 경우 관리자정보 수정 메세지를 띄우게 함
		if (modifyUser.getUsername().equals("admin")) {
			model.addAttribute("message", "관리자정보 수정완료");
			model.addAttribute("url", "/admin/manager");
		} else {
			model.addAttribute("message", "회원정보 수정완료");
			model.addAttribute("url", "/admin/user");
		}

		return "alert";
	}

	// 회원 및 선생님 계정 삭제
	@GetMapping("/deleteUser/{user_no}")
	public String deleteUser(@PathVariable("user_no") Long user_no, Model model) {

		SiteUser user = userService.findUserByUserNo(user_no);
		// 주소 창에 직접 url 입력 시 없는 유저 번호면 메세지 alert
		if (user == null) {
			model.addAttribute("message", "없는 유저번호입니다.");
			model.addAttribute("url", "/admin/user");
			return "alert";
		}

		// 선생님 계정은 한글로만 작성 가능하게 해두었으므로, 삭제하려는 계정의 id가 한글일 경우 선생님 계정 삭제 메세지 alert
		if (user.getUsername().matches("^[가-힣]*$")) {
			model.addAttribute("message", "선생님 계정 삭제 완료");
			model.addAttribute("url", "/admin/manager");
		} else {
			model.addAttribute("message", "회원 삭제 완료");
			model.addAttribute("url", "/admin/user");

		}

		userService.deleteUser(user_no);
		return "alert";
	}

	// 운영진 관리 페이지
	@GetMapping("/manager")
	public String manager(Model model) {

		List<SiteUser> managerList = userService.getManagerList();
		model.addAttribute("managerList", managerList);
		return "admin/managerList";
	}

	// 선생님 정보 수정
	@GetMapping("/modifyTeacher/{user_no}")
	public String modifyTeacher(@PathVariable("user_no") Long user_no,
			@ModelAttribute("teacherForm") TeacherForm teacherForm, Model model) {
		SiteUser teacher = userService.findUserByUserNo(user_no);
		model.addAttribute("user", teacher);

		return "admin/modifyTeacherForm";
	}

	// 선생님 정보 수정 process
	@PostMapping("/modifyTeacher/{user_no}")
	public String modifyTeacher(@PathVariable("user_no") Long user_no, @Valid TeacherForm teacherForm,
			BindingResult result, Model model) {

		SiteUser teacher = userService.findUserByUserNo(user_no);
		if (result.hasErrors()) {
			return "admin/modifyTeacherForm";
		}
		// 패스워드와 확인이 같지 않을경우 에러메세지를 만들어 다시 되돌아감
		if (!teacherForm.getPassword().equals(teacherForm.getPassword2())) {
			result.rejectValue("user_password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			return "admin/modifyTeacherForm";
		}
		// 선생님 정보 바꾸기
		try {
			userService.modifyTeacher(teacher, teacherForm);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			result.reject("signupFailed", "이미 등록된 아이디나 이메일입니다.");
			return "admin/modifyTeacherForm"; // 되돌아감
		} catch (Exception e) {
			e.printStackTrace();
			result.reject("signupFailed", e.getMessage());
			return "admin/modifyTeacherForm"; // 되돌아감
		}

		model.addAttribute("message", "정보 수정 성공");
		model.addAttribute("url", "/admin/manager");
		return "alert";
	}

}
