package com.learning.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.learning.form.QuestionForm;
import com.learning.mapper.QuestionMapper;
import com.learning.model.Criteria;
import com.learning.model.DoneQuestion;
import com.learning.model.Question;

@Service
public class QuestionService {

	@Autowired
	private QuestionMapper questionMapper;
	
	@Value("${file.question}")
	private String path_upload;
	
	public Question displayOneQuestion(Long user_no, String subject_code) {
		return questionMapper.displayOneQuestion(user_no, subject_code);
	}
	public List<Question> getQuestionList(){
		return questionMapper.getQuestionList();
	}
	public Question selectQuestionById(Long id) {
		return questionMapper.selectQuestionById(id);
	}
	public void saveResult(Long user_no, Long question_no, String correct, String user_select) {
		DoneQuestion done_q = new DoneQuestion();
		done_q.setUser_no(user_no);
		done_q.setQuestion_no(question_no);
		done_q.setCorrect(correct);
		done_q.setReg_date(LocalDateTime.now());
		done_q.setUser_select(user_select);
		questionMapper.saveResult(done_q);
	}
	
	public List<Question> getWrongQuestionList(Long user_no, String subject_code){
		return questionMapper.getWrongQuestionList(user_no, subject_code);
	}
	
	public List<Question> getCorrectQuestionList(Long user_no, String subject_code){
		return questionMapper.getCorrectQuestionList(user_no, subject_code);
	}
	
	public String getSubjectName(String subject_code) {
		return questionMapper.getSubjectName(subject_code);
	}
	
	public List<Question> getQuestionListBySubject(String subject_code){
		return questionMapper.getQuestionListBySubject(subject_code);
	}
	
	public List<Question> getQuestionListBySubjectWithPage(Criteria criteria, String subject_code) {
		return questionMapper.getQuestionListBySubjectWithPage(criteria, subject_code);
	}
	
	private String saveUploadFile(MultipartFile upload_file) {

		// 현재 시간(밀리세컨드)을 이용해서 파일의 이름이 중복되지 않게 설정
		String file_name = System.currentTimeMillis() + "_" + upload_file.getOriginalFilename();
		try {
			upload_file.transferTo(new File(path_upload + "/" + file_name));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file_name;
	}

	public void createQuestion(QuestionForm createQuestionForm, Long author_id) {

		Question question = new Question();
		MultipartFile upload_file = createQuestionForm.getPic_file();

		if (upload_file.getSize() > 0) {
			String file_name = saveUploadFile(upload_file);
			question.setQuestion_pic(file_name);
		}

		question.setAuthor_no(author_id);
		question.setSubject_code(createQuestionForm.getSubject_code());
		question.setQuestion_content(createQuestionForm.getQuestion_content());
		question.setQuestion_a1(createQuestionForm.getQuestion_a1());
		question.setQuestion_a2(createQuestionForm.getQuestion_a2());
		question.setQuestion_a3(createQuestionForm.getQuestion_a3());
		question.setQuestion_a4(createQuestionForm.getQuestion_a4());
		question.setQuestion_a5(createQuestionForm.getQuestion_a5());
		question.setQuestion_answer(createQuestionForm.getQuestion_answer());
		question.setQuestion_explanation(createQuestionForm.getQuestion_explanation());
		question.setReg_date(LocalDateTime.now());

		questionMapper.createQuestion(question);
	}
	
	public Question getQuestionByQNo(Long question_no) {
		return questionMapper.getQuestionByQNo(question_no);
	}

	public int getQuestionTotal(String subject_code) {
		return questionMapper.getQuestionTotal(subject_code);
	}

	public String getUsernameByQuestion(Long user_no) {
		return questionMapper.getUsernameByQuestion(user_no);
	}

	public void updateQuestion(Question question, QuestionForm questionForm) {
		
		MultipartFile upload_file = questionForm.getPic_file();

		if (upload_file.getSize() > 0) {
			String file_name = saveUploadFile(upload_file);
			question.setQuestion_pic(file_name);
		}

		question.setSubject_code(questionForm.getSubject_code());
		question.setQuestion_content(questionForm.getQuestion_content());
		question.setQuestion_a1(questionForm.getQuestion_a1());
		question.setQuestion_a2(questionForm.getQuestion_a2());
		question.setQuestion_a3(questionForm.getQuestion_a3());
		question.setQuestion_a4(questionForm.getQuestion_a4());
		question.setQuestion_a5(questionForm.getQuestion_a5());
		question.setQuestion_answer(questionForm.getQuestion_answer());
		question.setQuestion_explanation(questionForm.getQuestion_explanation());

		questionMapper.updateQuestion(question);
	}
	
	public void deleteQuestion(Long question_id) {
		questionMapper.deleteQuestion(question_id);
	}
	
	public void updateWrongNote(Long user_no, Long question_no) {
		questionMapper.updateWrongNote(user_no, question_no);
	}
	
	public void initProgress(String subject_code, Long user_no) {
		questionMapper.initProgress(subject_code, user_no);
	}
	
	public String getSubjectByQuestion(Long question_no) {
		return questionMapper.getSubjectByQuestion(question_no);
	}
	
	public List<Question> getQuestionListByContent(String search, String subject_code){
		return questionMapper.getQuestionListByContent(search, subject_code);
	}
}
