package com.learning.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.learning.domain.Question;
import com.learning.domain.Subject;
import com.learning.service.QuestionService;
import com.learning.service.SubjectService;
import com.learning.service.UserService;

@Controller
@RequestMapping("/question")
@PreAuthorize("isAuthenticated()")
public class QuestionController {

	@Autowired
	QuestionService questionService;

	@Autowired
	SubjectService subjectService;

	@Autowired
	UserService userService;

	// 과목 리스트를 보여준 뒤 과목 선택
	@GetMapping("/learning")
	public String displaySubjectList(Model model) {

		List<Subject> sList = subjectService.getSubjectList();
		model.addAttribute("sList", sList);
		return "question/questionMain";
	}

	// 과목 선택 페이지에서 과목명으로 과목 검색
	@GetMapping("/learningSearch")
	public String searchSubject(@RequestParam(value = "search") String search, Model model) {
		List<Subject> sList = subjectService.getSubjectListBySearch(search);
		model.addAttribute("sList", sList);
		return "question/questionMain";
	} 

	// 문제풀이
	@GetMapping("/test/{subject_code}")
	public String displayOneQuestion(@PathVariable("subject_code") String subject_code, Model model,
			Principal principal) {

		Long user_no = userService.findUserNoById(principal.getName());
		Question question = questionService.displayOneQuestion(user_no, subject_code);

		// 유저 방문 기록
		userService.updateUserHistory(subject_code, user_no);
		model.addAttribute("question", question);

		return "question/question";
	}

	// 문제의 정답 여부 판별 후 결과 저장
	@PostMapping("/judge/{questionNo}")
	public String judgeAnswer(@PathVariable("questionNo") Long question_no, @RequestParam String answer, Model model,
			Principal principal) {
		Question question = questionService.selectQuestionById(question_no);
		Long user_no = userService.findUserNoById(principal.getName());

		model.addAttribute("question", question);
		if (answer.equals(question.getQuestion_answer())) {
			model.addAttribute("result", "correct");
			questionService.saveResult(user_no, question_no, "o", answer);
			return "question/result";
		} else {
			model.addAttribute("result", "incorrect");
			questionService.saveResult(user_no, question_no, "x", answer);
			return "question/result";
		}
	}

	// 오답노트
	@GetMapping("/wrongnote/{subject_code}")
	public String wrongNote(@PathVariable("subject_code") String subject_code, Model model, Principal principal) {

		Long user_no = userService.findUserNoById(principal.getName());
		List<Question> wrongQList = questionService.getWrongQuestionList(user_no, subject_code);
		Subject subject = subjectService.getSubjectByCode(subject_code);
		// 유저 방문 기록
		userService.updateUserHistory(subject_code, user_no);
		model.addAttribute("subject_name", subject.getSubject_name());
		model.addAttribute("qList", wrongQList);

		return "question/wrong_note";
	}

	// 오답노트에서 학습 완료 후 복습으로 넘김
	@PostMapping("/wrongnote/{question_no}")
	public String wrongNote(@PathVariable("question_no") Long question_no, Model model, Principal principal) {

		Long user_no = userService.findUserNoById(principal.getName());
		String subject_code = questionService.getSubjectByQuestion(question_no);
		questionService.updateWrongNote(user_no, question_no);
		model.addAttribute("message", "오답노트에서 해당 문제를 삭제하였습니다. 오답노트에서 지운 문제들은 복습하기에서 확인하실 수 있습니다.");
		model.addAttribute("url", "/question/wrongnote/" + subject_code);

		return "alert";
	}

	// 정답인 문제들 복습
	@GetMapping("/review/{subject_code}")
	public String review(@PathVariable("subject_code") String subject_code, Model model, Principal principal) {
		Long user_no = userService.findUserNoById(principal.getName());
		List<Question> correctQList = questionService.getCorrectQuestionList(user_no, subject_code);
		Subject subject = subjectService.getSubjectByCode(subject_code);
		// 유저 방문 기록
		userService.updateUserHistory(subject_code, user_no);
		model.addAttribute("subject_name", subject.getSubject_name());
		model.addAttribute("qList", correctQList);

		return "question/review";
	}
	
	//학습기록 초기화
	@GetMapping("/initProgress/{subject_code}")
	public String initProgress(@PathVariable("subject_code") String subject_code, Model model, Principal principal) {

		Long user_no = userService.findUserNoById(principal.getName());
		questionService.initProgress(subject_code, user_no);

		model.addAttribute("message", "과목 진행도 초기화 완료!");
		model.addAttribute("url", "/question/learning");
		return "alert";
	}

}
