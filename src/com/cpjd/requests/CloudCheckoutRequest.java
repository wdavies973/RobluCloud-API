package com.cpjd.requests;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.cpjd.http.Request;
import com.cpjd.http.Request.RequestType;
import com.cpjd.models.CloudCheckout;

/**
 * CheckoutRequest manages the API calls represented under the "Checkouts" section of https://github.com/wdavies973/RobluCloud/wiki/Server-API.
 * 
 * These API calls will make up about 90% of all requests to the server.
 * 
 * Request overview:
 * -init()
 * -purge()
 * -pushMetaChanges()
 * -pushCheckouts()
 * -pullCheckouts()
 * -pullCheckoutsMeta()
 * -pullCompletedCheckouts()
 * 
 * @version 1
 * @since 1.0.0
 * @author Will Davies
 *
 */
public class CloudCheckoutRequest {

	private String teamCode;
	private Request r;
	
	private int teamNumber = -1;
	
	public CloudCheckoutRequest(Request r, String teamCode) {
		this.r = r;
		this.teamCode = teamCode;
	}
	
	public boolean init(int teamNumber, String activeEventName, String serializedForm, String serializedUI, String serializedCheckoutsArray, String tbaEventKey) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("code", teamCode);
			values.put("number", String.valueOf(teamNumber));
			values.put("active_event_name", activeEventName);
			values.put("form", serializedForm);
			values.put("ui", serializedUI);
			values.put("tbaKey", tbaEventKey);
			values.put("content", serializedCheckoutsArray);
			JSONObject response = (JSONObject) r.doRequest(RequestType.POST, "checkouts/init", values);
			return ((boolean)response.get("data"));
		} catch(Exception e) {
			System.err.println("Failed to complete init checkouts push to server. "+e.getMessage());
			return false;
		}
	}
	
	public boolean purge() {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("code", teamCode);
			JSONObject response = (JSONObject) r.doRequest(RequestType.GET, "checkouts/purge", values);
			return response.get("status").equals("success");
		} catch(Exception e) {
			System.err.println("Failed to purge event from the server");
			return false;
		}
	}
	
	public boolean pushCheckouts(String serializedCheckoutsArray) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("code", teamCode);
			values.put("content", serializedCheckoutsArray);
			String response = ((JSONObject) r.doRequest(RequestType.POST, "checkouts/pushCheckouts", values)).get("status").toString();
			return response.equals("success");
		} catch(Exception e) {
			System.err.println("Failed to push checkouts to the server: "+e.getMessage());
			return false;
		}
	}
	
	public CloudCheckout[] pullCheckouts(String syncIDs, boolean all) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("code", teamCode);
			if(syncIDs != null && !all) values.put("syncIDs", syncIDs);
			if(all) values.put("pullAll", String.valueOf(all));
			if(teamNumber != -1) values.put("teamNumber", String.valueOf(teamNumber));
			JSONArray checkouts = (JSONArray) ((JSONObject) r.doRequest(RequestType.GET, "checkouts/pullCheckouts", values)).get("data");
			CloudCheckout[] received = new CloudCheckout[checkouts.size()];
			for(int i = 0; i < checkouts.size(); i++) {
				JSONObject o = (JSONObject) checkouts.get(i);
				received[i] = new CloudCheckout(Long.parseLong(o.get("sync_id").toString()), o.get("content").toString());
			}
			return received;
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Failed to pull checkouts from the server");
			return null;
		}
	}
	
	public CloudCheckout[] pullCompletedCheckouts(String syncIDs) {
		try {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("code", teamCode);
			if(syncIDs != null) values.put("syncIDs", syncIDs);
			if(teamNumber != -1) values.put("teamNumber", String.valueOf(teamNumber));
			JSONArray checkouts = (JSONArray) ((JSONObject) r.doRequest(RequestType.GET, "checkouts/pullCompletedCheckouts", values)).get("data");
			CloudCheckout[] received = new CloudCheckout[checkouts.size()];
			for(int i = 0; i < checkouts.size(); i++) {
				JSONObject o = (JSONObject) checkouts.get(i);
				received[i] = new CloudCheckout(Long.parseLong(o.get("sync_id").toString()), o.get("content").toString());
			}
			return received;
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Failed to pull checkouts from the server");
			return null;
		}
	}

	public void setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
	}

	public void setTeamCode(String teamCode) {
		this.teamCode = teamCode;
	}
	
	
}
