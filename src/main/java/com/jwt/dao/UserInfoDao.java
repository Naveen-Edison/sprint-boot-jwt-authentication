package com.jwt.dao;

import com.jwt.model.User;
import com.jwt.model.UserInfo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoDao extends CrudRepository<UserInfo, Long> {

	UserInfo save(UserInfo userinfo);

	Optional<UserInfo> findByUserId(int id);
	
	Optional<User> findById(int id);

	UserInfo getByReferralCode(String referral);

	UserInfo findByMobile(String mobile);

	void save(Optional<UserInfo> userkyc);

	Iterable<UserInfo> findAllByOrderByIdDesc();

	


	
	



}
