package com.cpjd.requests;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.cpjd.http.Request;
import com.cpjd.http.Request.RequestType;
import com.cpjd.models.CloudTeam;

/**
 * AdminRequest manages the API calls represented under the "Admin" section of https://github.com/wdavies973/RobluCloud/wiki/Server-API.
 * 
 * Request overview:
 * -getTeamAsAdmin()
 * -createTeam()
 * -deleteTeam()
 * -regenerateCode() - this can also be used to effectively lock a team out of their account
 * 
 * @version 1
 * @since 1.0.0
 * @author Will Davies
 *
 */
public class AdminRequest {

	/**
	 * This represents an administrator authentication token on the server. Since the following four methods can not be called
	 * by anyone but the server owner, an extra level of verification is required. 
	 */
	private String auth;
	
	/**
	 * This class requires access to the Request utility, which is used for making RESTful calls to the server.
	 */
	private Request r;
	
	/**
	 * Creates an AdminRequest object for making calls to admin level API methods.
	 * @param auth {@link AdminRequest#auth}
	 * @param r {@link AdminRequest#r}
	 */
	public AdminRequest(Request r, String auth) {
		this.auth = auth;
		this.r = r;
	}
	
	/**
	 * Returns a Team model containing all the information the server contains for the specified team.
	 * @param ownerEmail the email address of the team account owner (server requires this to be unique, so it can be used as a token)
	 * @return Team model containing: active boolean, team code, official_team_name, owner_email, number, active_event_name, and last_content_edit, null if the call was unsuccessful
	 */
	public CloudTeam getTeamAsAdmin(String ownerEmail) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("auth", auth);
			values.put("ownerEmail", ownerEmail);
			JSONObject response = (JSONObject) ((JSONObject) r.doRequest(RequestType.GET, "admin/getTeam", values)).get("data");
			CloudTeam t = new CloudTeam();
			t.setActive((boolean)response.get("active"));
			t.setCode((String)response.get("code"));
			t.setOfficialTeamName((response.get("official_team_name").toString()));
			t.setOwnerEmail(response.get("owner_email").toString());
			t.setNumber((long)response.get("number"));
			t.setSecret(response.get("secret").toString());
			t.setActiveEventName((String)response.get("active_event_name"));
			return t;
		} catch(Exception e) {
			System.err.println("Failed to pull team model from Roblu Cloud");
			return null;
		}
	}
	
	/**
	 * Creates a new team on the server
	 * @param officialName the team's official name
	 * @param ownerEmail the owner's email address
	 * @param secret a secret token used for support purposes
	 * @return the created team model, null if the call was unsuccessful
	 */
	public CloudTeam createTeam(String officialName, String ownerEmail, String code) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("auth", auth);
			values.put("officialName", officialName);
			values.put("ownerEmail", ownerEmail);
			values.put("code", code);
			JSONObject response = (JSONObject) ((JSONObject) r.doRequest(RequestType.GET, "admin/createTeam", values)).get("data");
			CloudTeam t = new CloudTeam();
			t.setCode(code);
			t.setOfficialTeamName((response.get("official_team_name").toString()));
			t.setOwnerEmail(response.get("owner_email").toString());
			t.setNumber((long)response.get("number"));
			t.setActiveEventName((String)response.get("active_event_name"));
			return t;
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Failed to create team on Roblu Cloud - either duplicate exists or server is unreachable.");
			return null;
		}
	}
	
	/**
	 * Deletes the team off the server - it's preferred to use regenerateToken() to lock a team out instead of deleting
	 * their team. 
	 * @param ownerEmail the owner's email address
	 * @param officialName the official team name
	 * @return the team model of the deleted team model, null if the call was unsuccessful
	 */
	public CloudTeam deleteTeam(String ownerEmail, String officialName) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("auth", auth);
			values.put("ownerEmail", ownerEmail);
			values.put("officialName", officialName); 
			JSONObject response = (JSONObject) ((JSONObject) r.doRequest(RequestType.GET, "admin/deleteTeam", values)).get("data");
			CloudTeam t = new CloudTeam();
			t.setOfficialTeamName((response.get("official_team_name").toString()));
			t.setOwnerEmail(response.get("owner_email").toString());
			return t;
		} catch(Exception e) {
			System.err.println("Failed to delete team model on Roblu Cloud - probably becuase it doesn't exist or server is unreachable.");
			return null;
		}
	}
	
	/**
	 * Force regenerates a team code
	 * @param ownerEmail the owner's email address
	 * @param officialName the team's official name
	 * @return the new team code String, null if unsuccessful
	 */
	public String regenerateCode(String ownerEmail, String officialName) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("auth", auth);
			values.put("ownerEmail", ownerEmail);
			values.put("officialName", officialName); 
			JSONArray response = (JSONArray) ((JSONObject) r.doRequest(RequestType.GET, "admin/regenerateCode", values)).get("data");
			JSONObject data = (JSONObject) response.get(0);
			CloudTeam t = new CloudTeam();
			t.setCode((String)data.get("code"));
			return t.getCode();
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Failed to pull team model from Roblu Cloud");
			return null;
		}
	}
	
	
}
