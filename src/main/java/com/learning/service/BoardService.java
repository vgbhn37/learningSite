package com.learning.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learning.form.BoardForm;
import com.learning.mapper.BoardMapper;
import com.learning.model.Board;
import com.learning.model.Criteria;

@Service
public class BoardService {

	@Autowired
	private BoardMapper boardMapper;

	public void writeBoard(Long user_no, String subject_code, BoardForm boardForm) {

		Board board = new Board();

		board.setBoard_title(boardForm.getBoard_title());
		board.setBoard_content(boardForm.getBoard_content());
		board.setQuestion_no(boardForm.getQuestion_no());
		board.setUser_no(user_no);
		board.setSubject_code(subject_code);
		board.setBoard_classify("question");
		board.setReg_date(LocalDateTime.now());

		boardMapper.writeBoard(board);
	}
	
	public void writeNotice(Long user_no, BoardForm boardForm) {
		
		Board board = new Board();
		
		board.setBoard_title(boardForm.getBoard_title());
		board.setBoard_content(boardForm.getBoard_content());
		board.setUser_no(user_no);
		board.setBoard_classify("notice");
		board.setReg_date(LocalDateTime.now());

		boardMapper.writeBoard(board);
	}
	

	public List<Board> getBoardList(Criteria criteria) {
		return boardMapper.getBoardList(criteria);
	}

	public List<Board> getBoardListBySubject(Criteria criteria, String subject_name) {
		return boardMapper.getBoardListBySubject(criteria, subject_name);
	}

	public List<Board> getBoardListByQuestion(Criteria criteria, Long question_no) {
		return boardMapper.getBoardListByQuestion(criteria, question_no);
	}

	public List<Board> getBoardListByTitle(Criteria criteria, String board_title) {
		return boardMapper.getBoardListByTitle(criteria, board_title);
	}

	public List<Board> getBoardListByUser(Criteria criteria, String username) {
		return boardMapper.getBoardListByUser(criteria, username);
	}

	public int getBoardTotal() {
		return boardMapper.getBoardTotal();
	}

	public int getBoardTotalBySubject(String subject_name) {
		return boardMapper.getBoardTotalBySubject(subject_name);
	}

	public int getBoardTotalByQuestion(Long question_no) {
		return boardMapper.getBoardTotalByQuestion(question_no);
	}

	public int getBoardTotalByTitle(String board_title) {
		return boardMapper.getBoardTotalByTitle(board_title);
	}

	public int getBoardTotalByUser(String username) {
		return boardMapper.getBoardTotalByUser(username);
	}

	public Board getBoard(Long board_no) {
		return boardMapper.getBoard(board_no);
	}

	public void modifyBoard(Long board_no, BoardForm boardForm) {
		
		Board board = boardMapper.getBoard(board_no);
		board.setBoard_title(boardForm.getBoard_title());
		board.setBoard_content(boardForm.getBoard_content());
		
		boardMapper.modifyBoard(board);
	}

	public void deleteBoard(Long board_no) {
		boardMapper.deleteBoard(board_no);
	}
	
	public List<Board> getNoticeList(Criteria criteria) {
		return boardMapper.getNoticeList(criteria);
	}
	public int getNoticeTotal() {
		return boardMapper.getNoticeTotal();
	}
	
	public List<Board> getNoticeByTitle(Criteria criteria, String search){
		return boardMapper.getNoticeByTitle(criteria, search);
	}
	
	public int getNoticeTotalBySearch(String search) {
		return boardMapper.getNoticeTotalBySearch(search);
	}
	
	public List<Board> getMainBoardList(Long user_no) {
		return boardMapper.getMainBoardList(user_no);
	}
	
	public List<Board> getMainNoticeList(){
		return boardMapper.getMainNoticeList();
	}

}
