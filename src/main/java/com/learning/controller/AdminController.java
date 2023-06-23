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

import com.learning.domain.Criteria;
import com.learning.domain.PageMaker;
import com.learning.domain.Question;
import com.learning.domain.SiteUser;
import com.learning.domain.Subject;
import com.learning.form.QuestionForm;
import com.learning.form.SubjectForm;
import com.learning.form.TeacherForm;
import com.learning.form.UserForm;
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
	@PreAuthorize("hasRole('ROLE_TEACHER')")
	@GetMapping("/createQuestion/{subject_code}")
	public String createQuestion(@PathVariable("subject_code") String subject_code, 
			@ModelAttribute("questionForm") QuestionForm questionForm, Model model) {
		model.addAttribute("subject_code", subject_code);
		return "admin/createQuestionForm";
	}

	// 문제 만들기 process
	@PreAuthorize("hasRole('ROLE_TEACHER')")
	@PostMapping("/createQuestion")
	public String createQuestion(@Valid QuestionForm questionForm, BindingResult result, Model model,
			Principal principal) {
		if (result.hasErrors()) {
	
			return "admin/createQuestionForm";
		}
		
		//question DB에 들어갈 user_id
		Long author_id = userService.findUserNoById(principal.getName());
		//questionForm을 Service로 넘겨 service에서 처리
		questionService.createQuestion(questionForm, author_id);
		
		model.addAttribute("message", "문제 업로드 완료");
		model.addAttribute("url","/admin/createQuestion/" + questionForm.getSubject_code());
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

		Subject subjectByCode = subjectService.getSubjectByCode(subjectForm.getSubject_code());
		Subject subjectByName = subjectService.getSubjectByName(subjectForm.getSubject_name());

		if (subjectByName != null) {
			result.reject("createSubjectFailed", "이미 등록된 과목이름입니다.");
			return "admin/createSubjectForm"; // 되돌아감
		} else if (subjectByCode != null) {
			result.reject("createSubjectFailed", "이미 등록된 과목코드입니다.");
			return "admin/createSubjectForm"; // 되돌아감
		}

		subjectService.createSubject(subjectForm);
		model.addAttribute("message", "과목 생성 완료");
		model.addAttribute("url","/admin/subject");
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
		List<Subject> sList = subjectService.getSubjectListBySearch(search);
		model.addAttribute("sList", sList);
		return "admin/subjectList";
	}
	
	//과목 수정 페이지
	@GetMapping("/modifySubject/{subject_code}")
	public String modifySubject(@PathVariable("subject_code") String subject_code,
			@ModelAttribute("subjectForm") SubjectForm subjectForm, Model model) {
		Subject subject = subjectService.getSubjectByCode(subject_code);
		model.addAttribute("subject", subject);
		return "admin/modifySubjectForm";
	}
	
	//과목 수정 process
	@PostMapping("/modifySubject/{subject_code}")
	public String modifySubject(@PathVariable("subject_code") String subject_code, @Valid SubjectForm subjectForm,
			BindingResult result, Model model) {

		Subject subjectByCode = subjectService.getSubjectByCode(subject_code);
		Subject subjectByName = subjectService.getSubjectByName(subjectForm.getSubject_name());

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
		model.addAttribute("url","/admin/subject");
		return "alert";
	}
	
	//과목 삭제
	@GetMapping("/deleteSubject/{subject_code}")
	public String deleteSubject(@PathVariable("subject_code") String subject_code, Model model) {
		
		Subject subjectByCode = subjectService.getSubjectByCode(subject_code);
		List<Question> QList = questionService.getQuestionListBySubject(subject_code);
		
		if(subjectByCode==null) {
			model.addAttribute("message", "잘못된 과목코드입니다");
			model.addAttribute("url","/admin/subject");
			return "alert";
		} else if (QList.size()>0) {
			model.addAttribute("message", "문제가 등록되어 있는 과목은 삭제할 수 없습니다.");
			model.addAttribute("url","/admin/subject");
			return "alert";
		}
		
		subjectService.deleteSubject(subject_code);
		model.addAttribute("message", "과목 삭제 완료");
		model.addAttribute("url","/admin/subject");
		return "alert";
	}
	
	//문제 관리 페이지
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/question")
	public String question(Model model) {
		List<Subject> sList = subjectService.getSubjectList();
		model.addAttribute("sList", sList);
		return "admin/selectSubject";
	}
	
	//문제 관리 페이지에서 과목 검색후 그에 맞는 과목리스트 출력(LIKE)
	@GetMapping("/questionSearch")
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	public String question(@RequestParam(value = "search") String search, Model model) {
		List<Subject> sList = subjectService.getSubjectListBySearch(search);
		model.addAttribute("sList", sList);
		return "admin/selectSubject";
	}
	
	//선택한 과목에 해당하는 문제 리스트 출력
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/questionList/{subject_code}")
	public String questionList(@PathVariable("subject_code") String subject_code, 
			Criteria criteria, Model model) {
		
		List<Question> qList = questionService.getQuestionListBySubjectWithPage(criteria, subject_code);
		int total = questionService.getQuestionTotal(subject_code);
		Subject subject = subjectService.getSubjectByCode(subject_code); 
		PageMaker pageMaker = new PageMaker(criteria,total);
		model.addAttribute("pmk", pageMaker);
		model.addAttribute("qList",qList);
		model.addAttribute("subject", subject);
		return "admin/questionList";
	}
	
	//문제 상세보기(수정, 삭제 가능)
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/manageQuestion/{question_no}")
	public String manageQuestion(@PathVariable("question_no")Long question_no, Principal principal, Model model) {
		Question question = questionService.getQuestionByQNo(question_no);
		String username = questionService.getUsernameByQuestion(question.getAuthor_no());
		String loginUser = principal.getName();
		
		model.addAttribute("loginUser", loginUser);
		model.addAttribute("author_name", username);
		model.addAttribute("question", question);
		return "admin/manageQuestion";
	}
	
	//문제 수정
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/modifyQuestion/{question_no}")
	public String modifyQuestion(@PathVariable("question_no")Long question_no, @ModelAttribute("questionForm") QuestionForm questionForm,
									Model model) {
		Question question = questionService.getQuestionByQNo(question_no);
		model.addAttribute("question",question);
		return "admin/modifyQuestionForm";
	}
	
	//문제 수정 process
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@PostMapping("/modifyQuestion/{question_no}")
	public String modifyQuestion(@PathVariable("question_no")Long question_no, @Valid QuestionForm questionForm, BindingResult result, Model model) {
		Question question = questionService.getQuestionByQNo(question_no);
		
		if (result.hasErrors()) {
			model.addAttribute("question", question);
			return "admin/modifyQuestionForm";
		}
		
		questionService.updateQuestion(question, questionForm);
		model.addAttribute("message", "문제 수정 완료");
		model.addAttribute("url","/admin/questionList/"+ questionForm.getSubject_code());
		return "alert";
	}
	
	//문제 삭제
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/deleteQuestion/{question_no}")
	public String deleteQuestion(@PathVariable("question_no")Long question_no, Model model) {
		Question question = questionService.getQuestionByQNo(question_no);
		if(question==null) {
			model.addAttribute("message", "없는 문제번호입니다.");
			model.addAttribute("url","/admin/question");
			return "alert";
		}
		model.addAttribute("message", "문제 삭제 완료");
		model.addAttribute("url","/admin/questionList/"+ question.getSubject_code());
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
        //패스워드와 확인이 같지 않을경우 에러메세지를 만들어 다시 되돌아감
        if (!teacherForm.getPassword().equals(teacherForm.getPassword2())) {
            result.rejectValue("user_password2", "passwordInCorrect", 
                    "2개의 패스워드가 일치하지 않습니다.");
            return "admin/createTeacherForm";
        }
        //새로운 선생님계정 생성한다
        try { userService.createTeacher(teacherForm);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			result.reject("signupFailed", "이미 등록된 아이디나 이메일입니다.");
			return "admin/createTeacherForm"; //되돌아감
		} catch (Exception e) {
			e.printStackTrace();
			result.reject("signupFailed", e.getMessage());
			return "admin/createTeacherForm"; //되돌아감
		}
        Long user_no = userService.findUserNoById(teacherForm.getUsername());
   	    userService.createUserHistory(user_no);
        model.addAttribute("message","선생님계정 만들기에 성공하셨습니다");
        model.addAttribute("url","/admin/manager");
        return "alert"; //회원가입 성공 메세지를 보내 alert 페이지로 (message에 따라 alert 내용을 다르게 설정)
	}
	
	//회원 리스트
	@GetMapping("/user")
	public String userList(Criteria criteria, Model model) {
		List<SiteUser> userList = userService.getUserListWithPage(criteria);
		int total = userService.getUserTotal();
		PageMaker pageMaker = new PageMaker(criteria,total);
		
		model.addAttribute("pmk",pageMaker);
		model.addAttribute("userList",userList);
		return "admin/userList";
	}
	
	//회원 검색후 결과
	@GetMapping("/userSearch")
	public String searchUserList(@RequestParam("search")String search, Criteria criteria, Model model) {
		
		List<SiteUser> userList = userService.getUserListBySearchWithPage(criteria, search);
		int total = userService.getUserTotalBySearch(search);
		PageMaker pageMaker = new PageMaker(criteria,total);
		
		model.addAttribute("pmk",pageMaker);
		model.addAttribute("userList", userList);
		
		return "admin/userList";
	}
	
	//유저 및 관리자 정보 수정
	@GetMapping("/modifyUser/{user_no}")
	public String modifyUser(@PathVariable("user_no")Long user_no, @ModelAttribute("userForm") UserForm userForm, Model model) {
		
		SiteUser user = userService.findUserByUserNo(user_no);
		model.addAttribute("user",user);
		return "admin/modifyUserForm";
	}
	
	//유저 정보 수정 process
	@PostMapping("/modifyUser/{user_no}")
	public String modifyUser(@PathVariable("user_no")Long user_no, @Valid UserForm userForm, BindingResult result, Model model) {
		
		SiteUser modifyUser = userService.findUserByUserNo(user_no);
		
		if (result.hasErrors()) {
			model.addAttribute("user",modifyUser);
			return "admin/modifyUserForm";
		}
        //패스워드와 확인이 같지 않을경우 에러메세지를 만들어 다시 되돌아감
        if (!userForm.getPassword().equals(userForm.getPassword2())) {
            result.rejectValue("password2", "passwordInCorrect", 
                    "2개의 패스워드가 일치하지 않습니다.");
    		model.addAttribute("user",modifyUser);
            return "admin/modifyUserForm";
        }
        //유저 정보 바꾸기
        try { userService.modifyUser(modifyUser,userForm);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			result.reject("signupFailed", "이미 등록된 아이디나 이메일입니다.");
			return "admin/modifyUserForm"; //되돌아감
		} catch (Exception e) {
			e.printStackTrace();
			result.reject("signupFailed", e.getMessage());
			return "admin/modifyUserForm"; //되돌아감
		}
        
        if(modifyUser.getUsername().equals("admin")) {
        	   model.addAttribute("message","관리자정보 수정완료");
               model.addAttribute("url","/admin/manager");
        } else {
        	   model.addAttribute("message","회원정보 수정완료");
               model.addAttribute("url","/admin/user");
        }
     
		return "alert";
	}
	
	//회원 및 선생님 계정 삭제
	@GetMapping("/deleteUser/{user_no}")
	public String deleteUser(@PathVariable("user_no")Long user_no, Model model) {
		
		SiteUser user = userService.findUserByUserNo(user_no);
		if(user==null) {
			model.addAttribute("message", "없는 유저번호입니다.");
			model.addAttribute("url","/admin/user");
			return "alert";
		}
		
		if(user.getUsername().matches("^[가-힣]*$")) {
			model.addAttribute("message", "선생님 계정 삭제 완료");
			model.addAttribute("url","/admin/manager");		
		} else {
			model.addAttribute("message", "회원 삭제 완료");
			model.addAttribute("url","/admin/user");
			
		}
		
		userService.deleteUser(user_no);
		return "alert";
	}
	
	//운영진 관리 페이지
	@GetMapping("/manager")
	public String manager(Model model){
		
		List<SiteUser> managerList = userService.getManagerList();
		model.addAttribute("managerList", managerList);
		return "admin/managerList";
	}
	
	//선생님 정보 수정
	@GetMapping("/modifyTeacher/{user_no}")
	public String modifyTeacher(@PathVariable("user_no")Long user_no, @ModelAttribute("teacherForm")TeacherForm teacherForm, Model model) {
		SiteUser teacher = userService.findUserByUserNo(user_no);
		model.addAttribute("user",teacher);
		
		return "admin/modifyTeacherForm";
	}
	
	//선생님 정보 수정 process
	@PostMapping("/modifyTeacher/{user_no}")
	public String modifyTeacher(@PathVariable("user_no")Long user_no, @Valid TeacherForm teacherForm, BindingResult result, Model model) {
		
		SiteUser teacher = userService.findUserByUserNo(user_no);
		if (result.hasErrors()) {
			return "admin/modifyTeacherForm";
		}
        //패스워드와 확인이 같지 않을경우 에러메세지를 만들어 다시 되돌아감
        if (!teacherForm.getPassword().equals(teacherForm.getPassword2())) {
            result.rejectValue("user_password2", "passwordInCorrect", 
                    "2개의 패스워드가 일치하지 않습니다.");
            return "admin/modifyTeacherForm";
        }
        //선생님 정보 바꾸기
        try { userService.modifyTeacher(teacher,teacherForm);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			result.reject("signupFailed", "이미 등록된 아이디나 이메일입니다.");
			return "admin/modifyTeacherForm"; //되돌아감
		} catch (Exception e) {
			e.printStackTrace();
			result.reject("signupFailed", e.getMessage());
			return "admin/modifyTeacherForm"; //되돌아감
		}
		
		
		model.addAttribute("message","정보 수정 성공");
		model.addAttribute("url","/admin/manager");
		return "alert";
	}
	
	
	
	
	
	

}
