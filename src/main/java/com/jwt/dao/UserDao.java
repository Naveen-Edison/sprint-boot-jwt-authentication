package com.jwt.dao;

import com.jwt.model.Mlmearning;
import com.jwt.model.User;
import com.jwt.model.UserDto;
import com.jwt.model.UserInfo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<User, Integer> {

    User findByUsername(String username);
    User findByEmail(String username);
	User findById(String string);
	User save(UserDto user);
	Optional<User> findByResetToken(String resetToken);
	User findByMobile(String mobile);
	List<User> findByReferred(int id);
	List<User> findAllByReferred(int userId);
	List<User> findAllByRole(String role);
	User findByEpin(String epin);
	User findByEmailToken(String token);
	List<User> findAllByOrderByIdDesc();


}