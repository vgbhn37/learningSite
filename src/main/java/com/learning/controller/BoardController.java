package com.learning.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.learning.domain.Answer;
import com.learning.domain.Board;
import com.learning.domain.Criteria;
import com.learning.domain.PageMaker;
import com.learning.domain.Question;
import com.learning.form.AnswerForm;
import com.learning.form.BoardForm;
import com.learning.service.AnswerService;
import com.learning.service.BoardService;
import com.learning.service.QuestionService;
import com.learning.service.SubjectService;
import com.learning.service.UserService;

@Controller
@RequestMapping("/board")
@PreAuthorize("isAuthenticated()")
public class BoardController {

	@Autowired
	BoardService boardService;

	@Autowired
	QuestionService questionService;

	@Autowired
	SubjectService subjectService;

	@Autowired
	UserService userService;
	
	@Autowired
	AnswerService answerService;

	//질문 게시판 리스트
	@GetMapping("/list")
	public String boardList(Criteria criteria, Model model) {

		List<Board> boardList = boardService.getBoardList(criteria);
		int total = boardService.getBoardTotal();
		PageMaker pageMaker = new PageMaker(criteria, total);

		model.addAttribute("pmk", pageMaker);
		model.addAttribute("boardList", boardList);
		return "board/list";
	}
	
	//질문 게시판 검색
	@GetMapping("/listSearch")
	public String boardSearchedList(@RequestParam("option") String option,
			@RequestParam("search") String search, Criteria criteria, Model model) {
		 
		int total = 0;
		List<Board> boardList = null;
			if (option.equals("subject")) {
				boardList = boardService.getBoardListBySubject(criteria, search);
				total = boardService.getBoardTotalBySubject(search);
			} else if (option.equals("question_no")) {
				boardList = boardService.getBoardListByQuestion(criteria, Long.parseLong(search));
				total = boardService.getBoardTotalByQuestion(Long.parseLong(search));
			} else if (option.equals("title")) {
				boardList = boardService.getBoardListByTitle(criteria, search);
				total = boardService.getBoardTotalByTitle(search);
			} else if (option.equals("user")) {
				boardList = boardService.getBoardListByUser(criteria, search);
				total = boardService.getBoardTotalByUser(search);
			}
			
			PageMaker pageMaker = new PageMaker(criteria, total);

			model.addAttribute("pmk", pageMaker);
			model.addAttribute("boardList", boardList);
			return "board/list";
		
		
	}
	
	//질문 게시글 작성 페이지
	@GetMapping("/write")
	public String boardWrite(@RequestParam(value = "question_no", required = false) Long question_no,
			@ModelAttribute("boardForm") BoardForm boardForm, Principal principal, Model model) {

		String username = principal.getName();
		if (question_no != null) {
			Question question = questionService.getQuestionByQNo(question_no);
			String subject_name = subjectService.getSubjectNameByQuestion(question.getSubject_code());

			model.addAttribute("question", question);
			model.addAttribute("subject_name", subject_name);
		}

		model.addAttribute("username", username);
		return "board/write";
	}
	
	//질문 게시글 작성
	@PostMapping("/write")
	public String boardWrite(@Valid BoardForm boardForm, BindingResult result, Model model) {

		if (result.hasErrors()) {
			return "board/write";
		}

		String subject_code = subjectService.getSubjectCodeByQNo(boardForm.getQuestion_no());
		Long user_no = userService.findUserNoById(boardForm.getUsername());
		boardService.writeBoard(user_no, subject_code, boardForm);

		model.addAttribute("message", "질문글 게시 성공");
		model.addAttribute("url", "/board/list");
		return "alert";
	}
	
	//게시글 상세 읽기
	@GetMapping("/read/{board_no}")
	public String boardRead(@PathVariable("board_no")Long board_no, @ModelAttribute("answerForm")AnswerForm answerForm,
							Principal principal, Model model) {
		
		//해당 board_no에 있는 게시글 찾아오기
		Board board = boardService.getBoard(board_no);
		
		//board_no에 맞는 게시글 없으면 alert
		if(board==null) {
			model.addAttribute("message","해당 게시글을 찾을 수 없습니다.");
			model.addAttribute("url","/board/list");
			return "alert";
		}
		
		//문제와 연결된 질문글이면 문제를 같이 전달
		if(board.getQuestion_no()!=null) {
			Question question = questionService.getQuestionByQNo(board.getQuestion_no());
			model.addAttribute("question",question);
		}
		// 현재 로그인 되어있는 유저의 id 전달
		String loginUser = principal.getName();
		
		// 최초 표시할 댓글리스트 전탈
		List<Answer> answerList = answerService.getAnswerList(board_no);
		
		model.addAttribute("answerList",answerList);
		model.addAttribute("loginUser",loginUser);
		model.addAttribute("board",board);
		return "board/read";
	}
	
	//게시글 수정 페이지
	@GetMapping("/modify/{board_no}")
	public String boardModify(@PathVariable("board_no")Long board_no, @ModelAttribute("boardForm")BoardForm boardForm, Principal principal, Model model) {
		
		Board board = boardService.getBoard(board_no);
		
		if(board==null) {
			model.addAttribute("message","해당 게시글을 찾을 수 없습니다.");
			model.addAttribute("url","/board/list");
			return "alert";
		}
		
		if (!board.getUsername().equals(principal.getName()) && !principal.getName().equals("admin")) {
			return "error/403";
		}
		
		if(board.getQuestion_no()!=null) {
			Question question = questionService.getQuestionByQNo(board.getQuestion_no());
			model.addAttribute("question",question);
		}
		
		
		model.addAttribute("board",board);
		
		return "board/modify";
	}
	
	//게시글 수정
	@PostMapping("/modify/{board_no}")
	public String boardModify(@PathVariable("board_no")Long board_no, @Valid BoardForm boardForm, BindingResult result, Model model) {
		
		if(result.hasErrors()) {
			Board board = boardService.getBoard(board_no);
			
			if(board.getQuestion_no()!=null) {
				Question question = questionService.getQuestionByQNo(board.getQuestion_no());
				model.addAttribute("question",question);
			}
			
			model.addAttribute("board",board);
			return "board/modify";
		}
		
		boardService.modifyBoard(board_no, boardForm);
		
		model.addAttribute("message","질문글 수정 완료");
		model.addAttribute("url","/board/read/"+board_no);
		return "alert";
	}
	
	//게시글 삭제
	@GetMapping("/delete/{board_no}")
	public String boardDelete(@PathVariable("board_no")Long board_no, Model model) {
		
		Board board = boardService.getBoard(board_no);
		if(board == null) {
			model.addAttribute("message","해당 게시글을 찾을 수 없습니다.");
			model.addAttribute("url","/board/list");
			return "alert";
		}
		
		boardService.deleteBoard(board_no);
		model.addAttribute("message","질문글 삭제 완료");
		model.addAttribute("url","/board/list");
		return "alert";
	}
	
	
	//공지글 리스트
	@PreAuthorize("permitAll()")
	@GetMapping("/notice")
	public String noticeList(Criteria criteria, Model model) {
		
		List<Board> noticeList = boardService.getNoticeList(criteria);
		int total = boardService.getNoticeTotal();
		PageMaker pageMaker = new PageMaker(criteria, total);

		model.addAttribute("pmk", pageMaker);
		model.addAttribute("noticeList", noticeList);
		
		return "board/notice";
	}
	
	//공지글 검색
	@PreAuthorize("permitAll()")
	@GetMapping("/noticeSearch")
	public String noticeSearch(@RequestParam("search")String search,Criteria criteria, Model model) {
		
		List<Board> noticeList = boardService.getNoticeByTitle(criteria, search);
		int total = boardService.getNoticeTotalBySearch(search);
		PageMaker pageMaker = new PageMaker(criteria, total);

		model.addAttribute("pmk", pageMaker);
		model.addAttribute("noticeList", noticeList);
		
		return "board/notice";
	}
	
	//공지글 상세정보
	@PreAuthorize("permitAll()")
	@GetMapping("/notice/{board_no}")
	public String noticeRead(@PathVariable("board_no")Long board_no, Principal principal ,Model model) {
		Board board = boardService.getBoard(board_no);
		if(board==null) {
			model.addAttribute("message","해당 게시글을 찾을 수 없습니다.");
			model.addAttribute("url","/board/list");
			return "alert";
		}
		if(principal!=null) {
			model.addAttribute("loginUser",principal.getName());
		}
		
		model.addAttribute("board", board);
		return "board/noticeRead";
	}
	
	
	//공지 작성 페이지
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/noticeWrite")
	public String noticeWrite(@ModelAttribute("boardForm")BoardForm boardForm, Principal principal, Model model) {
		
		String username = principal.getName();
		model.addAttribute("username",username);
		return "board/noticeWrite";
	}
	
	//공지 작성
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@PostMapping("/noticeWrite")
	public String noticeWrite(@Valid BoardForm boardForm, BindingResult result, Principal principal, Model model) {
		
		if(result.hasErrors()) {
			model.addAttribute("username",principal.getName());
			return "board/noticeWrite";
		}
		Long user_no = userService.findUserNoById(principal.getName());
		boardService.writeNotice(user_no, boardForm);
		model.addAttribute("message","공지 작성 완료");
		model.addAttribute("url","/board/notice");
		return "alert";
	}
	
	//공지 수정 페이지
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/noticeModify/{board_no}")
	public String noticeModify(@PathVariable("board_no")Long board_no, @ModelAttribute("boardForm")BoardForm boardForm, Principal principal, Model model) {
		
		Board board = boardService.getBoard(board_no);
		if (board==null) {
			return "error/403";
		}
		
		if (!board.getUsername().equals(principal.getName()) && !principal.getName().equals("admin")) {
			return "error/403";
		}
		
		model.addAttribute("board",board);
		return "board/noticeModify";
	}
	
	//공지 수정
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@PostMapping("/noticeModify/{board_no}")
	public String noticeModify(@PathVariable("board_no")Long board_no, @Valid BoardForm boardForm, BindingResult result, Model model) {
		
		if(result.hasErrors()) {
			Board board = boardService.getBoard(board_no);
			model.addAttribute("board",board);
			return "board/noticeModify";
		}
		
		boardService.modifyBoard(board_no, boardForm);
		model.addAttribute("message","공지 수정 완료");
		model.addAttribute("url","/board/notice");
		return "alert";
	}
	
	//공지 삭제
	@PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/noticeDelete/{board_no}")
	public String noticeDelete(@PathVariable("board_no")Long board_no, Principal principal, Model model) {
		
		Board board = boardService.getBoard(board_no);
		if (board==null) {
			return "error/403";
		}
		
		if (!board.getUsername().equals(principal.getName()) && !principal.getName().equals("admin")) {
			return "error/403";
		}
		
		boardService.deleteBoard(board_no);
		model.addAttribute("message","공지 삭제 완료");
		model.addAttribute("url","/board/notice");
		return "alert";
	}
	
	
	
}
