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
		
		//페이징의 기준이 되는 객체 criteria를 가지고 질문 게시글 리스트 불러오기
		List<Board> boardList = boardService.getBoardList(criteria);
		// 질문 게시글의 총 갯수를 구해서
		int total = boardService.getBoardTotal();
		//페이지메이커로 페이지네이션
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
		
		//검색 옵션에 따라 검색 결과가 다르게 (과목명, 문제번호, 게시글제목, 글쓴이)
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

		//작성자 id
		String username = principal.getName();
		
		//문제와 연동된 질문글일 시 문제 정보와 과목이름을 가져옴
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
		
		//유효성 검사를 통과하지 못했을 시 form으로 되돌아가기
		if (result.hasErrors()) {
			
			//문제와 연동 된 게시글일 경우
			if(boardForm.getQuestion_no()!=null) {
				Question question = questionService.getQuestionByQNo(boardForm.getQuestion_no());
				String subject_name = subjectService.getSubjectNameByQuestion(question.getSubject_code());
				
				model.addAttribute("question", question);
				model.addAttribute("subject_name", subject_name);
			}
			//form에 표시할 username
			model.addAttribute("username",boardForm.getUsername());
			return "board/write";
		}
		
		//질문게시글 DB에 들어갈 과목코드 가져오기
		String subject_code = subjectService.getSubjectCodeByQNo(boardForm.getQuestion_no());
		//질문게시글 DB에 들어갈 글쓴이정보 (user_no) 가져오기
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
		
		// 최초 표시할 댓글리스트 전달
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
		
		//게시글 수정 권한이 없는 유저가 주소창에 url로 직접 접근 시도 시 error 페이지 (alert 후 뒤로가기)
		if (!board.getUsername().equals(principal.getName()) && !principal.getName().equals("admin")) {
			return "error/403";
		}
		
		//문제와 연동된 질문글일 경우 문제 정보 가져와서 수정 페이지에 보여주기
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
		
		//유효성 검사를 통과하지 못했을 때
		if(result.hasErrors()) {
			Board board = boardService.getBoard(board_no);
			//문제와 연동 된 질문글일 경우 문제 정보 가져오기
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
	public String boardDelete(@PathVariable("board_no")Long board_no, Principal principal, Model model) {
		
		Board board = boardService.getBoard(board_no);
		//주소창에 url로 직접 접근 시 없는 게시글일 경우 alert
		if(board == null) {
			model.addAttribute("message","해당 게시글을 찾을 수 없습니다.");
			model.addAttribute("url","/board/list");
			return "alert";
		}
		//삭제 권한이 없는 유저가 주소창으로 직접 접근 시 에러페이지
		if(!principal.getName().equals(board.getUsername()) && !principal.getName().equals("admin")){
			return "error/403";
		}
		
		boardService.deleteBoard(board_no);
		model.addAttribute("message","질문글 삭제 완료");
		model.addAttribute("url","/board/list");
		return "alert";
	}
	
	
	//공지글 리스트 (비회원도 가능)
	@PreAuthorize("permitAll()")
	@GetMapping("/notice")
	public String noticeList(Criteria criteria, Model model) {
		
		//페이징 기준 객체인 criteria를 가지고 공지글 리스트를 가져옴
		List<Board> noticeList = boardService.getNoticeList(criteria);
		//공지글의 총 갯수를 구해서
		int total = boardService.getNoticeTotal();
		//criteria를 가지고 페이지네이션
		PageMaker pageMaker = new PageMaker(criteria, total);

		model.addAttribute("pmk", pageMaker);
		model.addAttribute("noticeList", noticeList);
		
		return "board/notice";
	}
	
	//공지글 검색 (비회원도 가능)
	@PreAuthorize("permitAll()")
	@GetMapping("/noticeSearch")
	public String noticeSearch(@RequestParam("search")String search,Criteria criteria, Model model) {
		
		//페이징 기준 객체인 criteria와 검색어를 가지고 검색된 공지글 리스트를 가져옴
		List<Board> noticeList = boardService.getNoticeByTitle(criteria, search);
		//검색 된 공지의 총 갯수를 구해서
		int total = boardService.getNoticeTotalBySearch(search);
		//페이지네이션
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
		
		//로그인 한 회원 id를 가져옴 (공지를 수정,삭제 할 권한이 있는 지 판단하기 위함)
		//조건문을 붙이지 않으면 null오류 발생
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
		
		//공지를 작성하는 사람의 id
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
		//게시글 DB에 저장될 user_no 가져오기
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
		// 주소창으로 직접 입력 시 없는 게시글 번호일 경우
		if (board==null) {
			return "error/500";
		}
		
		// 권한이 없는 유저가 직접 주소창에 입력 할 경우 (admin은 모든 공지를 수정/삭제 가능하지만, 선생님 계정은 본인이 쓴 공지만 수정/삭제 가능)
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
		
		//유효성 검사를 통과 못했을 시
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
		
		//주소창에 직접 url을 입력했을 때 해당 번호의 게시글이 없는 경우
		if (board==null) {
			return "error/500";
		}
		//권한이 없는 유저가 주소창으로 직접 입력해서 접근 한 경우
		if (!board.getUsername().equals(principal.getName()) && !principal.getName().equals("admin")) {
			return "error/403";
		}
		
		boardService.deleteBoard(board_no);
		model.addAttribute("message","공지 삭제 완료");
		model.addAttribute("url","/board/notice");
		return "alert";
	}
	
	
	
}
