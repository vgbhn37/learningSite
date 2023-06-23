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

		if (principal != null) {
			String username = principal.getName();
			Long user_no = userService.findUserNoById(username);
			String subject_name = userService.findUserHistory(user_no);
			List<Board> boardList = boardService.getMainBoardList(user_no);

			model.addAttribute("username", username);
			model.addAttribute("user_no", user_no);
			model.addAttribute("subject_name", subject_name);
			model.addAttribute("boardList", boardList);

		}
		List<Board> noticeList = boardService.getMainNoticeList();
		model.addAttribute("noticeList", noticeList);

		return "main";
	}
}
