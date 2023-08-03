package com.vivatelecoms.greenzone.model;

public class ChatMessagesDetails {
	
	private String id;
	private String msisdn;
	private String chatGroupId;
	private String status;
	private String chatDislikeCount;
	private String chatLikeCount;
	private String recordingDate;
	private String recordingPath;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getChatGroupId() {
		return chatGroupId;
	}
	public void setChatGroupId(String chatGroupId) {
		this.chatGroupId = chatGroupId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getChatLikeCount() {
		return chatLikeCount;
	}
	public void setChatLikeCount(String chatLikeCount) {
		this.chatLikeCount = chatLikeCount;
	}
	public String getChatDislikeCount() {
		return chatDislikeCount;
	}
	public void setChatDislikeCount(String chatDislikeCount) {
		this.chatDislikeCount = chatDislikeCount;
	}
	public String getRecordingDate() {
		return recordingDate;
	}
	public void setRecordingDate(String recordingDate) {
		this.recordingDate = recordingDate;
	}
	public String getRecordingPath() {
		return recordingPath;
	}
	public void setRecordingPath(String recordingPath) {
		this.recordingPath = recordingPath;
	}
	
	

}
