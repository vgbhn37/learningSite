package com.learning.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learning.form.AnswerForm;
import com.learning.mapper.AnswerMapper;
import com.learning.model.Answer;

@Service
public class AnswerService {

	@Autowired
	AnswerMapper answerMapper;
	
	public List<Answer> getAnswerList(Long board_no) {
		return answerMapper.getAnswerList(board_no);
	}
	
	public void writeAnswer(AnswerForm answerForm, Long user_no, Long board_no) {
		
		Answer answer = new Answer();
		answer.setAnswer_content(answerForm.getAnswer_content());
		answer.setBoard_no(board_no);
		answer.setUser_no(user_no);
		answer.setReg_date(LocalDateTime.now());
		answerMapper.writeAnswer(answer);
	}
	
	public Answer getAnswerByNo(Long answer_no) {
		return answerMapper.getAnswerByNo(answer_no);
	}
	
	public void deleteAnswer(Long answer_no) {
		answerMapper.deleteAnswer(answer_no);
	}
	
	
	public void modifyAnswer(AnswerForm answerForm, Long board_no) {
		Answer answer = getAnswerByNo(board_no);
		answer.setAnswer_content(answerForm.getAnswer_content());
		
		answerMapper.modifyAnswer(answer);
	}
}
