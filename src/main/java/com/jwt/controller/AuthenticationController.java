package com.jwt.controller;

import com.jwt.config.JwtTokenUtil;
import com.jwt.dao.UserDao;
import com.jwt.dao.UserInfoDao;
import com.jwt.model.*;
import com.jwt.service.AndroidPushNotificationsService;
import com.jwt.service.EmailService;
import com.jwt.service.SendService;
import com.jwt.service.SettingService;
import com.jwt.service.UserInfoService;
import com.jwt.service.UserService;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/oauth")

public class AuthenticationController  {
	

	private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

	private static final Object USER = null;
	
	private final String TOPIC = "jwt";

	@Autowired
	private AndroidPushNotificationsService androidPushNotificationsService;

    @Autowired
    private AuthenticationManager authenticationManager;
    
	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserDao userDao;
    

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ApiResponse login(@Valid @RequestBody  LoginUser loginUser, HttpServletRequest request){
    	try{
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
        final User user = userService.findOne(loginUser.getUsername());
        if(user.getStatus() != 1) {
       	 return new ApiResponse<>(500, " Kindly Contact Admin, Your Account is blocked !",null);
       }
        if(user.getVerified() != 1) {
        	
        	String appUrl = request.getScheme() + "://" + request.getServerName();
   		 	Mail mail = new Mail();
   	        mail.setFrom("support@jwt.com");
   	        mail.setTo(user.getEmail());
   	        String url = "http://jwt.demo.com/emailverification/"+user.getEmailToken();
   	        mail.setMessage(url);
   	        mail.setName(user.getUsername());
   	        mail.setSubject("Email Verification");
   	        emailService.registeremail(mail);
   	        
   	        
        	 return new ApiResponse<>(500, "Kindly verify your email address , email already send to your email !",null);
        }
        final String token = jwtTokenUtil.generateToken(user);
        
        if(loginUser.getDeviceId() != null && loginUser.getDeviceType() != null && loginUser.getDeviceToken() != null) {
        	
        	user.setDeviceId(loginUser.getDeviceId());
        	user.setDeviceType(loginUser.getDeviceType());
        	user.setDeviceToken(loginUser.getDeviceToken());
            userDao.save(user);
        	
        }
        
        return new ApiResponse<>(200, "success",new AuthToken(token, user.getUsername(),user.getId(),user.getFingerStatus(),user.getPinStatus(),user.getPin(),user.getStatus()));
                
	    } catch (Exception e) {
	        
	    	 return new ApiResponse<>(500, "Invalid credentials",null);
	    }
    }
    
    

  	 
  	@RequestMapping(value = "/register", method = RequestMethod.POST)
 	public ApiResponse<AuthToken> registerUser( @RequestBody UserDto user, HttpServletRequest request){
  		try {
  		if(user.getEmail() != null) {
			User useremail = userDao.findByEmail(user.getEmail());
			if(useremail != null) {
				return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Email Address Already Found  !",1);
			}
		}
  		
  		if(user.getMobile() != null) {
			User usermobile = userDao.findByMobile(user.getMobile());
			if(usermobile != null) {
				return new ApiResponse<>(200, "Mobile Already Found  !",1);
			}
		}
		
		if(user.getUsername() != null) {
			User username = userDao.findByUsername(user.getUsername());
			if(username != null) {
				return new ApiResponse<>(200, "User Name Already Found  !",1);
			}
		}

		User userinfo = userService.save(user);
		String appUrl = request.getScheme() + "://" + request.getServerName();
		 Mail mail = new Mail();
	        mail.setFrom("support@jwt.com");
	        mail.setTo(user.getEmail());
	        String url = "http://jwt.demo.com/emailverification/"+userinfo.getEmailToken();
	        mail.setMessage(url);
	        mail.setName(user.getUsername());
	        mail.setSubject("Email Verification");
	        emailService.registeremail(mail);
		return new ApiResponse<>(HttpStatus.OK.value(), "User Registered successfully , Email verification mail send to the email address !",null);
  		} catch (Exception e) { 
	    	 return new ApiResponse<>(500, "Something went wrong",null);
	    }
	}

	
}
