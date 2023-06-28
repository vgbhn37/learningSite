package com.learning.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.learning.form.AnswerForm;
import com.learning.model.Answer;
import com.learning.model.Board;
import com.learning.model.Question;
import com.learning.service.AnswerService;
import com.learning.service.BoardService;
import com.learning.service.QuestionService;
import com.learning.service.UserService;

@Controller
@RequestMapping("/answer")
public class AnswerController {

	@Autowired
	AnswerService answerService;

	@Autowired
	UserService userService;

	@Autowired
	BoardService boardService;

	@Autowired
	QuestionService questionService;
	
	//댓글 쓰기
	@PostMapping("/write/{board_no}")
	public String writeAnswer(@PathVariable("board_no") Long board_no, @Valid AnswerForm answerForm,
			BindingResult result, Principal principal, Model model) {
		
		//댓글이 비어 있을 때, 게시글에 담겨져있던 정보를 담아서 되돌아가기
		if (result.hasErrors()) {

			Board board = boardService.getBoard(board_no);
			List<Answer> answerList = answerService.getAnswerList(board_no);
			
			//문제와 연동된 질문 게시글일 경우 문제의 정보를 가져옴
			if (board.getQuestion_no() != null) {
				Question question = questionService.getQuestionByQNo(board.getQuestion_no());
				model.addAttribute("question", question);
			}

			model.addAttribute("answerList", answerList);
			model.addAttribute("board", board);	
			model.addAttribute("loginUser", principal.getName());
			return "/board/read";

		}
		
		//댓글 DB에 등록될 user_no
		Long user_no = userService.findUserNoById(principal.getName());
		answerService.writeAnswer(answerForm, user_no, board_no);
		
		//메세지와 이후 갈 주소를 담아 alert
		model.addAttribute("message", "댓글 작성 완료");
		model.addAttribute("url", "/board/read/" + board_no);

		return "alert";
	}

	//댓글 수정 페이지
	@GetMapping("/modify/{answer_no}")
	public String modifyAnswer(@PathVariable("answer_no") Long answer_no,
			@ModelAttribute("answerForm") AnswerForm answerForm, Principal principal, Model model) {
		Answer answer = answerService.getAnswerByNo(answer_no);
		
		// 없는 댓글 번호로 접근 시 에러페이지
		if (answer == null) {
			return "error/500";
		}
		// 권한이 없는 사람이 접근 시 에러페이지
		if (!answer.getUsername().equals(principal.getName()) && !principal.getName().equals("admin")) {
			return "error/403";
		}

		model.addAttribute("answer", answer);
		return "/answer/modify";
	}

	//댓글 수정
	@PostMapping("/modify/{answer_no}")
	public String modifyAnswer(@PathVariable("answer_no") Long answer_no, @Valid AnswerForm answerForm,
			BindingResult result, Model model) {

		Answer answer = answerService.getAnswerByNo(answer_no);
		if (result.hasErrors()) {
			model.addAttribute("answer", answer);
			return "/answer/modify";
		}

		answerService.modifyAnswer(answerForm, answer_no);

		//메세지와 이후 갈 주소를 담아 alert
		model.addAttribute("message", "댓글 수정 완료");
		model.addAttribute("url", "/board/read/" + answer.getBoard_no());
		return "alert";
	}

	//댓글 삭제
	@GetMapping("/delete/{answer_no}")
	public String deleteAnswer(@PathVariable("answer_no") Long answer_no, Principal principal, Model model) {

		Answer answer = answerService.getAnswerByNo(answer_no);
		
		// 없는 댓글 번호로 접근 시 에러페이지
		if (answer == null) {
			return "error/500";
		}
		// 권한이 없는 사람이 접근 시 에러페이지
		if (!answer.getUsername().equals(principal.getName()) && !principal.getName().equals("admin")) {
			return "error/403";
		}
		
		//메세지와 이후 갈 주소를 담아 alert
		model.addAttribute("message","댓글 삭제 완료");
		model.addAttribute("url","/board/read/" + answer.getBoard_no());
		answerService.deleteAnswer(answer_no);
		return "alert";
	}

}
