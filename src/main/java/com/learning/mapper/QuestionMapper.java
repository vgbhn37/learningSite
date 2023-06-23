package com.learning.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.learning.domain.Criteria;
import com.learning.domain.DoneQuestion;
import com.learning.domain.Question;

@Mapper
public interface QuestionMapper {
	
	public void createQuestion(Question question);
	public Question displayOneQuestion(Long user_no, String subject_code);
	public Question selectQuestionById(Long id);
	public List<Question> getQuestionList();
	public List<Question> getWrongQuestionList(Long user_no, String subject_code);
	public List<Question> getCorrectQuestionList(Long user_no, String subject_code);
	public List<Question> getQuestionListBySubject(String subject_code);
	void saveResult(DoneQuestion doneQuestion);
	String getSubjectName(String subjuct_code);
	public List<Question> getQuestionListBySubjectWithPage(@Param("criteria") Criteria criteria, String subject_code);
	public int getQuestionTotal(String subject_code);
	public Question getQuestionByQNo(Long question_no);
	public String getUsernameByQuestion(Long user_no);
	public String getSubjectByQuestion(Long question_no);
	public void updateQuestion(Question question);
	public void deleteQuestion(Long question_no);
	public void updateWrongNote(Long user_no, Long question_no);
	public void initProgress(String subject_code, Long user_no);
	
}
