package com.learning.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.learning.model.Answer;

@Mapper
public interface AnswerMapper {

	public void writeAnswer(Answer answer);
	public List<Answer> getAnswerList(Long board_no);
	public int getAnswerTotal(Long board_no);
	public Answer getAnswerByNo(Long answer_no);
	public void deleteAnswer(Long answer_no);
	public void modifyAnswer(Answer answer);
	
}
