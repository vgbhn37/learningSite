package com.learning.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.learning.domain.Board;
import com.learning.service.BoardService;
import com.learning.service.UserService;

@Controller
public class HomeController {

	@Autowired
	UserService userService;

	@Autowired
	BoardService boardService;

	@RequestMapping("/")
	public String home(Model model, Principal principal) {

		//로그인 했을 시
		if (principal != null) {
			//메인 화면에 보여줄 유저id ( username님! 반갑습니다!)
			String username = principal.getName();
			Long user_no = userService.findUserNoById(username);
			//유저가 최근에 학습한 과목 표시
			String subject_name = userService.findUserHistory(user_no);
			//유저가 질문한 게시글
			List<Board> boardList = boardService.getMainBoardList(user_no);

			model.addAttribute("username", username);
			model.addAttribute("user_no", user_no);
			model.addAttribute("subject_name", subject_name);
			model.addAttribute("boardList", boardList);

		}
		//로그인 여부에 관계 없이 공지글은 메인화면에 띄움
		List<Board> noticeList = boardService.getMainNoticeList();
		model.addAttribute("noticeList", noticeList);

		return "main";
	}
}
