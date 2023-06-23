package com.learning.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.learning.domain.Criteria;
import com.learning.domain.SiteUser;

@Mapper
public interface UserMapper {
	public void createUser(SiteUser joinUser);
	public void createUserHistory(Long user_no);
	public void modifyUser(SiteUser modifyUser);
	public void deleteUser(Long user_no);
	public SiteUser findUserById(String username);
	public SiteUser findUserByUserNo(Long user_no);
	public Long findUserNoById(String username);
	public List<SiteUser> getUserListWithPage(Criteria criteria);
	public List<SiteUser> getUserListBySearchWithPage(@Param("criteria") Criteria creteria,String search);
	public List<SiteUser> getManagerList();
	public int getUserTotal();
	public int getUserTotalBySearch(String search);
	public void updateUserHistory(String subject_code, Long user_no);
	public String findUserHistory(Long user_no);
}
