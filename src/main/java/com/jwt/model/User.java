package com.jwt.model;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "users")
public class User  implements UserDetails{
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	
	@NotNull(message = "Email is a required field")
	@Column(unique=true,length=225)
	
	private String email;
	
	
	@NotNull
    @Size(min=2, max=30 ,message="Name should have atleast 2 characters")
	@Column(unique=true,length=225)
	private String username;
	
	@NotNull(message = "Mobile is a required field")
	@Column(unique=true,length=225)
	private String mobile;

	
	 @NotNull
	 
	 @Column(length=225)
	private String password;
	
	 @Column(length=225)
	private String newPassword;

	 @Column(length=225)
	private String emailToken;
	
	private int referred;
	
	public int verified;

	public int pinStatus;
	public int fingerStatus;
	
	private String deviceId;
	
	private Timestamp createdAt;
	
	 @Column(length=225)
	private String deviceToken;
	
	private String deviceType;
	
	@Size(min=6 , message="Pin should have atleast 6 characters")
	private String pin;
	
	 @Column(length=225)
	private String resetToken;
	
	 @Column(length=225)
	private String referralCode;
	 
	 private String epin;
	 
		@Column
		@CreationTimestamp
		private LocalDateTime createDateTime;

		@Column
		@UpdateTimestamp
		private LocalDateTime updateDateTime;
	 
	 
	 private int planId;
	 
	 private int status;
	 
		private int createdBy;
		private int updatedBy;
		
		
		private String walletAddress;
		
		public String getWalletAddress() {
			return walletAddress;
		}
		public void setWalletAddress(String walletAddress) {
			this.walletAddress = walletAddress;
		}
		
		public int getCreatedBy() {
			return createdBy;
		}
		public void setCreatedBy(int createdBy) {
			this.createdBy = createdBy;
		}
		public int getUpdatedBy() {
			return updatedBy;
		}
		public void setUpdatedBy(int updatedBy) {
			this.updatedBy = updatedBy;
		}
	 public LocalDateTime getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(LocalDateTime createDateTime) {
		this.createDateTime = createDateTime;
	}

	public LocalDateTime getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(LocalDateTime updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPlanId() {
		return planId;
	}

	public void setPlanId(int planId) {
		this.planId = planId;
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	@JsonFormat(pattern = "dd/MM/yyyy HH:MM:SS")
		private Timestamp timeStamp;
	 
	 @Column(name = "role", nullable = false)
	    @Enumerated(EnumType.STRING)
	    private Role role;
	 
	 
	 public Role getRole() {
	        return role;
	    }

	    public void setRole(Role role) {
	        this.role = role;
	    }
	
	
	public String getEpin() {
		return epin;
	}

	public void setEpin(String epin) {
		this.epin = epin;
	}

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailToken() {
		return emailToken;
	}

	public void setEmailToken(String emailToken) {
		this.emailToken = emailToken;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public int getVerified() {
		return verified;
	}

	public void setVerified(int verified) {
		this.verified = verified;
	}

	public int getPinStatus() {
		return pinStatus;
	}

	public void setPinStatus(int pinStatus) {
		this.pinStatus = pinStatus;
	}

	public int getFingerStatus() {
		return fingerStatus;
	}

	public void setFingerStatus(int fingerStatus) {
		this.fingerStatus = fingerStatus;
	}

	public int getReferred() {
		return referred;
	}

	public void setReferred(int referred) {
		this.referred = referred;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	
	

	
	
}
