package com.jwt.model;

public class AuthToken {

    private String token;
    private String username;
    private int userId;
    private int fingerStatus;
    private int pinstatus;
    private String pin;
    private int status;

    public AuthToken(){

    }

    public AuthToken(String token, String username, int userId, int fingerStatus,int pinStatus,String pin,int status){
        this.token = token;
        this.username = username;
        this.userId = userId;
        this.fingerStatus = fingerStatus;
        this.pinstatus = pinStatus;
        this.pin = pin;
        this.status = status;
    }

    public AuthToken(String token){
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getFingerStatus() {
		return fingerStatus;
	}

	public void setFingerStatus(int fingerStatus) {
		this.fingerStatus = fingerStatus;
	}

	public int getPinstatus() {
		return pinstatus;
	}

	public void setPinstatus(int pinstatus) {
		this.pinstatus = pinstatus;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
    
    
}

