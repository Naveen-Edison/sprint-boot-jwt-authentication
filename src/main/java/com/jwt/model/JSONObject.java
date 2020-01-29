package com.jwt.model;

public class JSONObject {
	
	private String title;
	
	private String body;
	
	private String notificationType;
	
	private String data;
	
	private String to;
	
	

	@Override
	public String toString() {
		return "JSONObject [data=" + data + ", to=" + to + "]";
	}

	public String getTitle() {
		return title;
	}

//	@Override
//	public String toString1() {
//		return "JSONObject [title=" + title + ", body=" + body + ", notificationType=" + notificationType + "]";
//	}

	public String getBody() {
		return body;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public String getData() {
		return data;
	}

	public String getTo() {
		return to;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void put(String string, JSONObject msg) {
		// TODO Auto-generated method stub
		
	}

	public void put(String string, String string2) {
		// TODO Auto-generated method stub
		
	}
	
	

}
