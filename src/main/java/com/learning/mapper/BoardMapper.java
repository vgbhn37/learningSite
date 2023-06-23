package com.learning.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.learning.domain.Board;
import com.learning.domain.Criteria;

@Mapper
public interface BoardMapper {

	public void writeBoard(Board board);
	public List<Board> getBoardList(Criteria criteria);
	public int getBoardTotal();
	public int getBoardTotalBySubject(String subject_name);
	public int getBoardTotalByQuestion(Long question_no);
	public int getBoardTotalByTitle(String board_title);
	public int getBoardTotalByUser(String username);
	public List<Board> getBoardListBySubject(Criteria criteria, String subject_name);
	public List<Board> getBoardListByQuestion(Criteria criteria, Long question_no);
	public List<Board> getBoardListByTitle(Criteria criteria, String board_title);
	public List<Board> getBoardListByUser(Criteria criteria, String username);
	public Board getBoard(Long board_no);
	public void modifyBoard(Board board);
	public void deleteBoard(Long board_no);
	public List<Board> getNoticeList(Criteria criteria);
	public int getNoticeTotal();
	public List<Board> getNoticeByTitle(Criteria criteria, String search);
	public int getNoticeTotalBySearch(String search);
	public List<Board> getMainBoardList(Long user_no);
	public List<Board> getMainNoticeList();
}
