package com.cpjd.models;

public class CloudCheckout {

	private long syncID;
	
	private String content;

	public CloudCheckout(long syncID, String content) {
		super();
		this.syncID = syncID;
		this.content = content;
	}

	
	public long getSyncID() {
		return syncID;
	}


	public void setSyncID(long syncID) {
		this.syncID = syncID;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
	
}
