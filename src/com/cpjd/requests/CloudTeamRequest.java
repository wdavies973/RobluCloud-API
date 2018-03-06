package com.cpjd.requests;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.cpjd.http.Request;
import com.cpjd.http.Request.RequestType;
import com.cpjd.models.CloudTeam;

/**
 * TeamRequest manages the API calls represented under the "Team" section of https://github.com/wdavies973/RobluCloud/wiki/Server-API.
 * 
 * Request overview:
 * -getTeam()
 * -isActive()
 * -regenerateCode()
 * -pushForm()
 * -pushUI()  
 * 
 * @version 1
 * @since 1.0.0
 * @author Will Davies
 *
 */
public class CloudTeamRequest {

	private String code;
	
	private Request r;
	
	private int teamNumber = -1;
	
	public CloudTeamRequest(Request r, String teamCode) {
		this.r = r;
		this.code = teamCode;
	}
	            
	/**
	 * Returns a Team model containing all the information the server contains for the specified team.
	 * @param code the unique team code
	 * @param lastContentSync, if specified, the team will only be returned if it contains new data, use -1 to return the team anyway
	 * @return Team model containing: active boolean, team code, official_team_name, owner_email, number, active_event_name, and last_content_edit, null if the call was unsuccessful
	 */
	public CloudTeam getTeam(long lastSyncID) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("code", code);
			if(teamNumber != -1) values.put("teamNumber", String.valueOf(teamNumber));
			if(lastSyncID != -1) values.put("syncID", String.valueOf(lastSyncID));
			String test = ((JSONObject) r.doRequest(RequestType.GET, "teams/getTeam", values)).get("data").toString();
			if(test.startsWith("You are up to")) return null;
			JSONObject response = (JSONObject) ((JSONObject) r.doRequest(RequestType.GET, "teams/getTeam", values)).get("data");
			CloudTeam t = new CloudTeam();
			t.setSyncID((long)response.get(("sync_id")));
			t.setActive((boolean)response.get("active"));
			t.setCode(code);
			t.setOptedIn((boolean)response.get("opted_in"));
			t.setOfficialTeamName((response.get("official_team_name").toString()));
			t.setNumber((long)response.get("number"));
			t.setTbaKey(response.get("tba_event_key").toString());
			t.setActiveEventName((String)response.get("active_event_name"));
			t.setForm(response.get("form").toString());
			t.setUi(response.get("ui").toString());
			return t;
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Failed to pull team model from Roblu Cloud");
			return null;
		}
	}
	
	/**
	 * This method helps reduce data transfers by determining if an event is even being synced.
	 * If true, all other check methods should be called. 
	 * @return true if an event is available
	 */
	public boolean isActive() {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("code", code);
			if(teamNumber != -1) values.put("teamNumber", String.valueOf(teamNumber));
			JSONObject response = (JSONObject) r.doRequest(RequestType.GET, "teams/isActive", values);
			return ((boolean)response.get("data"));
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Failed to determine if active==true from Roblu Cloud");
			return false;
		}	
	}
	
	/**
	 * Generates a new team code for a team
	 * @return the new team code
	 */
	public CloudTeam regenerateCode() {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("code", code);
			JSONArray data = (JSONArray) ((JSONObject) r.doRequest(RequestType.GET, "teams/regenerateCode", values)).get("data");
			JSONObject response = (JSONObject) data.get(0);
			CloudTeam t = new CloudTeam();
			t.setActive((boolean)response.get("active"));
			t.setCode(response.get("code").toString());
			t.setOfficialTeamName((response.get("official_team_name").toString()));
			t.setNumber((long)response.get("number"));
			t.setActiveEventName((String)response.get("active_event_name"));
			
			// Update the request team code also
			this.code = t.getCode();
			
			return t;
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Failed to pull team model from Roblu Cloud");
			return null;
		}	
	}
	
	/**
	 * Changes whether this team's scouting data should be publicly readable
	 * @param optedIn the value to set
	 * @return true if the operation was successful
	 */
	public boolean setOptedIn(boolean optedIn) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("code", code);
			values.put("opted", String.valueOf(optedIn));
			JSONObject response = (JSONObject) r.doRequest(RequestType.GET, "teams/opt", values);
			return response.get("status").equals("success");
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Failed to push to form to Roblu Cloud");
			return false;
		}
	}
	
	/**
	 * Pushes the serialized form to the server
	 * @param serialized the serialized form string
	 * @return true if the operation was successful
	 */
	public boolean pushForm(String serialized) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("code", code);
			
			values.put("content", serialized);
			JSONObject response = (JSONObject) r.doRequest(RequestType.POST, "teams/pushForm", values);
			return response.get("status").equals("success");
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Failed to push to form to Roblu Cloud");
			return false;
		}
	}
	
	/**
	 * Pushes the serialized UI to the server
	 * @param serialized the serialized UI string
	 * @return true if the operation was successful
	 */
	public boolean pushUI(String serialized) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("code", code);
			values.put("content", serialized);
			JSONObject response = (JSONObject) r.doRequest(RequestType.POST, "teams/pushUI", values);
			return response.get("status").equals("success");
		} catch(Exception e) {
			System.err.println("Failed to push to form to Roblu Cloud");
			return false;
		}
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
	}
}
