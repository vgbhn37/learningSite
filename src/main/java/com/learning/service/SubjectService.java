package com.learning.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learning.domain.Subject;
import com.learning.form.SubjectForm;
import com.learning.mapper.SubjectMapper;

@Service
public class SubjectService {

	@Autowired
	private SubjectMapper subjectMapper;
	
	public List<Subject> getSubjectList() {
		return subjectMapper.getSubjectList();
	}

	public List<Subject> getSubjectListBySearch(String search) {
		return subjectMapper.getSubjectListBySearch(search);
	}
	
	public void createSubject(SubjectForm createSubjectForm) {

		Subject subject = new Subject();
		subject.setSubject_code(createSubjectForm.getSubject_code());
		subject.setSubject_name(createSubjectForm.getSubject_name().trim());

		subjectMapper.createSubject(subject);
	}
	
	public Subject getSubjectByCode(String subject_code) {
		return subjectMapper.getSubjectByCode(subject_code);
	}

	public Subject getSubjectByName(String subject_name) {
		return subjectMapper.getSubjectByName(subject_name);
	}
	
	public void updateSubject(SubjectForm updateSubjectForm) {

		Subject subject = new Subject();
		subject.setSubject_code(updateSubjectForm.getSubject_code());
		subject.setSubject_name(updateSubjectForm.getSubject_name());
		subjectMapper.updateSubject(subject);
	}

	public void deleteSubject(String subject_id) {
		subjectMapper.deleteSubject(subject_id);
	}
	
	public String getSubjectNameByQuestion(String subject_code) {
		
		return subjectMapper.getSubjectNameByQuestion(subject_code);
	}
	
	public String getSubjectCodeByQNo(Long question_no) {
		return subjectMapper.getSubjectCodeByQNo(question_no);
	}
}
