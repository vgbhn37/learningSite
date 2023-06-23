package com.learning.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.learning.domain.Subject;

@Mapper
public interface SubjectMapper {
	public List<Subject> getSubjectList();
	public void createSubject(Subject subject);
	public Subject getSubjectByCode(String subject_code);
	public Subject getSubjectByName(String subject_name);
	public List<Subject> getSubjectListBySearch(String search);
	public void updateSubject(Subject subject);
	public void deleteSubject(String subject_code);
	public String getSubjectNameByQuestion(String subject_code);
	public String getSubjectCodeByQNo(Long question_no);
}
