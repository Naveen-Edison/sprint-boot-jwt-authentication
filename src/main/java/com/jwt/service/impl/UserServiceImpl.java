package com.jwt.service.impl;

import com.jwt.controller.AuthenticationController;
import com.jwt.dao.BankDetailDao;
import com.jwt.dao.BankRequestDao;
import com.jwt.dao.MyOrderDao;
import com.jwt.dao.NotificationDao;
import com.jwt.dao.PlanUsedDao;
import com.jwt.dao.SendTransactionDao;
import com.jwt.dao.TreeViewDAO;
import com.jwt.dao.TreeViewDao;
import com.jwt.dao.UserDao;
import com.jwt.dao.UserInfoDao;
import com.jwt.dao.WalletDao;
import com.jwt.dao.WalletTransactionDao;
import com.jwt.model.AuthToken;
import com.jwt.model.BankDetail;
import com.jwt.model.BankRequest;
import com.jwt.model.Mail;
import com.jwt.model.MyOrder;
import com.jwt.model.Notification;
import com.jwt.model.PlanUsed;
import com.jwt.model.SendTransaction;
import com.jwt.model.TreeView;
import com.jwt.model.User;
import com.jwt.model.UserData;
import com.jwt.model.UserDto;
import com.jwt.model.UserInfo;
import com.jwt.model.Wallet;
import com.jwt.model.WalletTransaction;
import com.jwt.service.EmailService;
import com.jwt.service.UserInfoService;
import com.jwt.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.mail.MessagingException;
import javax.validation.Valid;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	@Autowired
	private UserDao userDao;

	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;

	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private BankRequestDao bankRequestDao;

	@Autowired
	private WalletDao walletDao;

	@Autowired
	private NotificationDao  notificationDao;
	
	@Autowired
	private PlanUsedDao planUsedDao;

	@Autowired
	private TreeViewDao treeViewDao;

	@Autowired
	private MyOrderDao myOrderDao;
	
	@Autowired 
	private TreeViewDAO treeViewDAO;

	@Autowired
	private WalletTransactionDao walletTransactionDao;

	@Autowired
	private SendTransactionDao sendTransactionDao;

	@Autowired
	private BankDetailDao bankDetailDao;

	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if (userDao.findByMobile(username) != null) {
			User user = userDao.findByMobile(username);
			log.info("user:" + user);
			if (user == null) {
				throw new UsernameNotFoundException("Invalid username or password.");
			}

			return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
					getAuthority());
		} else {
			User user = userDao.findByEmail(username);
			if (user == null) {
				throw new UsernameNotFoundException("Invalid username or password.");
			}

			return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
					getAuthority());
		}

//		User user = userDao.findByEmail(username);
//		if(user == null){
//			throw new UsernameNotFoundException("Invalid username or password.");
//		}
//		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuthority());
	}

	private List<SimpleGrantedAuthority> getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("ADMIN"));
	}

	public List<User> findAll() {
		List<User> list = userDao.findAllByOrderByIdDesc();
		return list;
	}

	@Override
	public void delete(int id) {
		userDao.deleteById(id);
	}

	@Override
	public User findOne(String username) {
		if (userDao.findByMobile(username) != null) {
			User user = userDao.findByMobile(username);
			return user;
		} else {
			User user = userDao.findByEmail(username);
			return user;
		}
	}

	@Override
	public User findById(int id) {
		Optional<User> optionalUser = userDao.findById(id);
		return optionalUser.isPresent() ? optionalUser.get() : null;
	}

	@Override
	public UserDto update(UserDto userDto) {
		User user = findById(userDto.getId());
		if (user != null) {
			BeanUtils.copyProperties(userDto, user, "password");
			user.setUpdatedBy(user.getId());
			userDao.save(user);
		}
		return userDto;
	}

	@Override
	public User save(@Valid UserDto user) {

		User newUser = new User();
		newUser.setUsername(user.getUsername());
		newUser.setEmail(user.getEmail());
		newUser.setMobile(user.getMobile());
		newUser.setDeviceId(user.getDeviceId());
		newUser.setDeviceToken(user.getDeviceToken());
		newUser.setDeviceType(user.getDeviceType());
		newUser.setPin(user.getPin());

		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		String myUrl = bcryptEncoder.encode(user.getEmail());
		newUser.setEmailToken(myUrl.replaceAll("\\/",""));
	
		newUser.setRole(user.getRole());
		newUser.setStatus(1);

		Date date = new Date();
		long time = date.getTime();

		Timestamp ts = new Timestamp(time);
		newUser.setCreatedAt(ts);

		if (user.getReferralCode() != null && !user.getReferralCode().isEmpty()) {
			UserInfo info = userInfoService.getByReferralCode(user.getReferralCode());
			if (info != null) {
				newUser.setReferred(info.getUserId());
			}

		}

		User userone = userDao.save(newUser);
		String referralcode = generateRandomChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 10);

		UserInfo newUserInfo = new UserInfo();
		newUserInfo.setUserId(userone.getId());
		newUserInfo.setUsername(userone.getUsername());
		newUserInfo.setEmail(userone.getEmail());
		newUserInfo.setReferralCode("jwt" + userone.getId() + referralcode);
		newUserInfo.setMobile(userone.getMobile());
		newUserInfo.setActivity(1);
		newUserInfo.setCreatedBy(userone.getId());
		newUserInfo.setUpdatedBy(userone.getId());
		userInfoDao.save(newUserInfo);

		String address = generateRandomChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 10);
		Wallet newWallet = new Wallet();
		newWallet.setUserId(userone.getId());
		newWallet.setBalance(0);
		newWallet.setAddress("jwt" + userone.getId() + address);

		walletDao.save(newWallet);
		
		newUser.setWalletAddress(newWallet.getAddress());
		userDao.save(newUser);
		
		newUserInfo.setWalletAddress(newWallet.getAddress());
		userInfoDao.save(newUserInfo);

		return userone;
	}

	public static String generateRandomChars(String candidateChars, int length) {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
		}

		return sb.toString();
	}

	@Override
	public User refsave(User newUser) {
		// TODO Auto-generated method stub

		User newUser1 = new User();
		newUser1.setUsername(newUser.getUsername());
		newUser1.setEmail(newUser.getEmail());
		newUser1.setDeviceId(newUser.getDeviceId());
		newUser1.setDeviceToken(newUser.getDeviceToken());
		newUser1.setDeviceType(newUser.getDeviceType());
		newUser1.setPin(newUser.getPin());
		newUser1.setReferred(newUser.getReferred());
		newUser1.setPassword(bcryptEncoder.encode(newUser.getPassword()));
		newUser1.setEmailToken(bcryptEncoder.encode(newUser.getEmail()));
		

		return userDao.save(newUser1);
	}

	@Override
	public User findByEmail(String name) {

		User user = userDao.findByEmail(name);
		return user;

	}

	@Override
	public Object passupdate(User pass) {
		return userDao.save(pass);
	}

	@Override
	public User saveUser(User user) {
		return userDao.save(user);
	}

	@Override
	public Optional<User> findUserByResetToken(String resetToken) {
		return userDao.findByResetToken(resetToken);
	}

	@Override
	public User findByMobile(String username) {
		User user = userDao.findByMobile(username);
		return user;
	}

	@Override
	public User findByEmailOrMobile(String username) {
		if (userDao.findByMobile(username) != null) {
			User user = userDao.findByMobile(username);
			return user;
		} else {
			User user = userDao.findByEmail(username);
			return user;
		}

	}

	@Override
	public Object findByUserId(int id) {
			log.info("1");
		Optional<User> user = userDao.findById(id);
		Optional<UserInfo> userInfo = userInfoDao.findByUserId(id);
		Wallet wallet = walletDao.findByUserId(id);
		BankDetail bank = bankDetailDao.findByUserId(id);
		List<WalletTransaction> walletTransaction = walletTransactionDao.findAllByUserId(id);
		List<SendTransaction> sendTransaction = sendTransactionDao.findAllBySenderOrReceiver(id, id);
		List<MyOrder> myOrder = myOrderDao.findAllByUserId(id);
		List<PlanUsed> planUsed = planUsedDao.findAllByUserId(id);
		List<TreeView> directTeam = treeViewDao.findAllByReferredAndType(id, "DIRECT");
		List<TreeView> downTeam = treeViewDao.findAllByReferredAndType(id, "DOWN");
		List<BankRequest> bankRequest = bankRequestDao.findAllByUserId(id);
		List<User> referredPeople = userDao.findAllByReferred(user.get().getId());
		
		UserInfo referredBy = userService.findByReferredId(user.get().getReferred());
		
		
			

		return new UserData(user, userInfo, wallet, bank, walletTransaction, sendTransaction, myOrder, planUsed,
				directTeam, downTeam,bankRequest,referredPeople,referredBy);
	}
	
	@Override
	public UserInfo findByReferredId(int referred) {
		Optional<UserInfo> userInfo = userInfoDao.findByUserId(referred);
		return  userInfo.isPresent() ? userInfo.get() : null;
	}

	@Override
	public User emailverify(String token) {
		User user = userDao.findByEmailToken(token);
		if (user != null) {
			user.setVerified(1);
			userDao.save(user);
			return user;
		}
		return null;
	}

	@Override
	public UserInfo kycStatus(UserInfo user) {
		Optional<UserInfo> userkyc = userInfoDao.findByUserId(user.getUserId());
		if (userkyc.get() != null) {
			userkyc.get().setKyc(user.getKyc());
			if(user.getKyc() == 2) {
				userkyc.get().setKycReason(user.getKycReason());
			}
			userkyc.get().setUpdatedBy(user.getUpdatedBy());
			userInfoDao.save(userkyc.get());

			return userkyc.get();
		} else {
			return null;
		}

	}

	@Override
	public UserInfo useractivity(UserInfo user) {
		Optional<UserInfo> useract = userInfoDao.findByUserId(user.getUserId());
		if (useract.get() != null) {
			useract.get().setActivity(user.getActivity());
			useract.get().setUpdatedBy(user.getUserId());
			userInfoDao.save(useract.get());

			return useract.get();
		} else {
			return null;
		}
	}

	@Override
	public User userstatus(User user) {
		Optional<User> userinfo = userDao.findById(user.getId());
		if (userinfo.get() != null) {
			userinfo.get().setStatus(user.getStatus());
			userinfo.get().setUpdatedBy(user.getId());
			userDao.save(userinfo.get());

			return userinfo.get();
		} else {
			return null;
		}

	}
	
	

		@Override
		public Iterable<Notification> getallnotification() {
			
			Iterable<Notification> notify = notificationDao.findAllByOrderByIdDesc();
			
			return notify;
		}

		@Override
		public Iterable<Notification> usernotification(User user) {
			
				Iterable<Notification> notify = notificationDao.findAllByUserIdOrderByIdDesc(user.getId());
			
			return notify;
		}
		
		
		 //tree view
		@SuppressWarnings("unchecked")
		@Override
		public List<Object> fetchTreeData(Map<String, Object> passData){
			List<Object> returnMap = new ArrayList<Object>();
			Map<String, String> dataMap = (Map<String, String>) passData.get("data");
			returnMap = treeViewDAO.fetchTree(dataMap);
			if(returnMap.size()>0) {
				returnMap = formTreeData(returnMap,(Map<String, String>) passData.get("data"));
			}
			return returnMap;
		}
		
		
		@SuppressWarnings("unchecked")
		public List<Object> formTreeData(List<Object> passData,Map<String, String> params){
			List<Object> returnMap = new ArrayList<Object>();
			List<Object> firstLevelMap = new ArrayList<Object>();
			List<Object> secondLevelMap = new ArrayList<Object>();
			List<Object> thirdLevelMap = new ArrayList<Object>();
			List<Object> fourthLevelMap = new ArrayList<Object>();
			List<Object> finalList = new ArrayList<Object>();
			 
			List<Object> firstLevelChildMap = new ArrayList<Object>();
			List<Object> secondLevelChildMap = new ArrayList<Object>();
			List<Object> secondLevelEmptyChildMap = new ArrayList<Object>();
			List<Object> thirdLevelChildMap = new ArrayList<Object>();
			List<Object> fourthLevelChildMap = new ArrayList<Object>();
			 
			List<Object> removedData = new ArrayList<Object>();
			List<Object> secondlevelremovedData = new ArrayList<Object>();
			Map<String, Object> secondLevelAssignMap  = new HashMap<String, Object>();
			Map<String, Object> reassignMap  = new HashMap<String, Object>();
			Map<String, Object> firstreassignMap  = new HashMap<String, Object>();
			int firstlevelCount =0;
			int secondlevelCount =0;

			 
				firstLevelMap = findFIrstChild((String) params.get("user_id"),passData,"1");
				// System.out.println(firstLevelMap+"===>rammmmmm267677");
				firstlevelCount = firstLevelMap.size();
				if(firstlevelCount>0){
					removedData = removeUnwantedData(firstLevelMap,passData);
					// System.out.println(removedData+"=>3");
					for (int firstcounter = 0; firstcounter < firstLevelMap.size(); firstcounter++) { 	
						secondLevelMap = findChild((Map<String, Object>) firstLevelMap.get(firstcounter),removedData,"2");
						// System.out.println(secondLevelMap+"===>rammmm");
						secondlevelCount = firstLevelMap.size();
						// System.out.println(secondlevelCount+"===>rammmmmm1");
						 
						firstreassignMap  = new HashMap<String, Object>();
						firstreassignMap.putAll((Map<String, Object>) firstLevelMap.get(firstcounter));
						 
						if(secondlevelCount>0){
							//removedData = new ArrayList<Object>();
							secondLevelChildMap = new ArrayList<Object>();
							secondLevelEmptyChildMap = new ArrayList<Object>();
							for (int secondcounter = 0; secondcounter < secondLevelMap.size(); secondcounter++) { 	
								reassignMap  = new HashMap<String, Object>();
								reassignMap.putAll((Map<String, Object>) secondLevelMap.get(secondcounter));
								thirdLevelMap = findChild((Map<String, Object>) secondLevelMap.get(secondcounter),passData,"3");
								 
								secondLevelAssignMap  = new HashMap<String, Object>();
								secondLevelAssignMap.putAll(reassignMap);
								secondLevelAssignMap.put("children",thirdLevelMap);
								secondLevelChildMap.add(secondLevelAssignMap);
		
							}
							 
							
							secondLevelAssignMap  = new HashMap<String, Object>();
							secondLevelAssignMap.putAll((Map<String, Object>) firstLevelMap.get(firstcounter));
							secondLevelAssignMap.put("children",secondLevelChildMap);
							
							firstLevelChildMap.add(secondLevelAssignMap);
						}
						
					}
					 
				}

				 
			//	finalList.add(firstLevelChildMap.get(0));

				//firstLevelChildMap.remove(0);
				reassignMap  = new HashMap<String, Object>();
				//reassignMap.putAll((Map<String, Object>)finalList.get(0));
				reassignMap.put("children",firstLevelChildMap);
				finalList = new ArrayList<Object>();
				finalList.add(reassignMap);
			return finalList;
		}
		
		@SuppressWarnings("unchecked")
		public List<Object> findChild(Map<String,Object> userId,List<Object> passData,String level){
			List<Object> returnMap = new ArrayList<Object>();
			Map<String, String> dataMap = new HashMap<String, String>();
			for (int counter = 0; counter < passData.size(); counter++) { 	
				dataMap = new HashMap<String,String>();
				dataMap = (Map<String, String>) passData.get(counter);
			  if(((String) dataMap.get("referred")).equalsIgnoreCase((String) userId.get("user_id")) ){
				  dataMap.put("level", level);
				  returnMap.add(dataMap);
			  }
			}
			return returnMap;
		}
		
		@SuppressWarnings("unchecked")
		public List<Object> findFIrstChild(String userId,List<Object> passData,String level){
			List<Object> returnMap = new ArrayList<Object>();
			Map<String, String> dataMap = new HashMap<String, String>();
			for (int counter = 0; counter < passData.size(); counter++) { 	
				dataMap = new HashMap<String,String>();
				dataMap = (Map<String, String>) passData.get(counter);
			  if(((String) dataMap.get("referred")).equalsIgnoreCase(userId)){
				  dataMap.put("level", level);
				  returnMap.add(dataMap);
			  }
			}
			return returnMap;
		}
		
		@SuppressWarnings("unchecked")
		public List<Object> removeUnwantedData(List<Object> levelMap,List<Object> passData){
			List<Object> returnMap = new ArrayList<Object>();
			Map<String, String> dataMap = new HashMap<String, String>();
			Map<String, String> innerdataMap = new HashMap<String, String>();
			for (int counter = 0; counter < passData.size(); counter++) { 	
				dataMap = new HashMap<String,String>();
				boolean checkFlag = true;
				dataMap = (Map<String, String>) passData.get(counter);
				for (int innercounter = 0; innercounter < levelMap.size(); innercounter++) { 	
					innerdataMap = new HashMap<String,String>();
					innerdataMap = (Map<String, String>) levelMap.get(innercounter);
					  if(((String) dataMap.get("user_id")).equalsIgnoreCase((String) innerdataMap.get("user_id"))){
						  checkFlag = false;
					  }
				}
				
				if(checkFlag){
					returnMap.add(dataMap);
				}
			}
			return returnMap;
		}

		@Override
		public void resetmail(User user) {
			// TODO Auto-generated method stub
			
			
			Mail mail = new Mail();
	        mail.setFrom("support@jwt.com");
	        mail.setTo(user.getEmail());
	        String url = "http://jwt.demo.com/reset/"+user.getResetToken();
	        mail.setMessage(url);
	        mail.setName(user.getUsername());
	        mail.setSubject("Reset Passsword");
	        try {
				emailService.resetemail(mail);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public UserInfo kyccheck(int id) {
			Optional<UserInfo> user = userInfoDao.findByUserId(id);
			if(user.get().getKyc() != 1) {
				return null;
			}
			return user.get();
		}

		@Override
		public Notification deletenotification(int id) {
			notificationDao.deleteById(id);
			return null;
		}

		

}