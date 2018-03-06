package com.cpjd.models;

public class CloudTeam {

	/*
	 * General team info
	 */
	
	private String code;
	
	private String officialTeamName;
	
	private String ownerEmail;
	
	private String secret;
	
	/*
	 * Meta-data level items
	 */
	
	private long number;
	
	private String activeEventName;
	
	private String tbaKey;

	private boolean active;
	
	private long syncID;
	
	/*
	 * Content level items
	 */
	private String form;
	
	private String ui;

	private boolean optedIn;
	
	
	/*
	 * Getters and setters
	 */

	public void setOptedIn(boolean optedIn) {
		this.optedIn = optedIn;
	}
	
	public boolean isOptedIn() {
		return optedIn;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return "Team [code=" + code + ", officialTeamName=" + officialTeamName + ", ownerEmail=" + ownerEmail
				+ ", secret=" + secret + ", number=" + number + ", activeEventName=" + activeEventName
				+ ", active=" + active + ", form=" + form + ", ui=" + ui + "]";
	}
	public String getSecret() {
		return secret;
	}
	
	public long getSyncID() {
		return syncID;
	}

	public void setSyncID(long syncID) {
		this.syncID = syncID;
	}

	public String getTbaKey() {
		return tbaKey;
	}
	public void setTbaKey(String tbaKey) {
		this.tbaKey = tbaKey;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setOfficialTeamName(String officialTeamName) {
		this.officialTeamName = officialTeamName;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public void setActiveEventName(String activeEventName) {
		this.activeEventName = activeEventName;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public void setUi(String ui) {
		this.ui = ui;
	}

	public String getCode() {
		return code;
	}

	public String getOfficialTeamName() {
		return officialTeamName;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public long getNumber() {
		return number;
	}

	public String getActiveEventName() {
		return activeEventName;
	}
	public boolean isActive() {
		return active;
	}

	public String getForm() {
		return form;
	}

	public String getUi() {
		return ui;
	}
	
	
}