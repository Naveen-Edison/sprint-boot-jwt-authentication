package com.jwt.service;

import com.jwt.model.Notification;
import com.jwt.model.User;
import com.jwt.model.UserDto;
import com.jwt.model.UserInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    User save(UserDto user);
    List<User> findAll();
    void delete(int id);

    User findOne(String username);

    User findById(int id);

    UserDto update(UserDto userDto);
	User refsave(User newUser);
	User findByEmail(String name);
	Object passupdate(User pass);
	User saveUser(User user);
	
	Optional<User> findUserByResetToken(String resetToken);
	User findByMobile(String username);
	User findByEmailOrMobile(String username);
	Object findByUserId(int id);
	User emailverify(String token);
	UserInfo kycStatus(UserInfo user);
	UserInfo useractivity(UserInfo user);
	User userstatus(User user);
	
	 List<Object> fetchTreeData(Map<String, Object> passData);
	Iterable<Notification> getallnotification();
	Iterable<Notification> usernotification(User user);
	void resetmail(User user);
	UserInfo kyccheck(int id);
	UserInfo findByReferredId(int referred);
	Notification deletenotification(int id);
	
	
}
