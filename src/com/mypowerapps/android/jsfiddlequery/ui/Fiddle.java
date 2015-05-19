package com.mypowerapps.android.jsfiddlequery.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.mypowerapps.android.jsfiddlequery.utils.Constants;

public class Fiddle {
	private String url, title, details, description;
	
	public String getUrl() {
		return this.url;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDetails() {
		return this.details;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	private Fiddle decodeJson(JSONObject jsonObject) {
		String url = jsonObject.optString(Constants.LISTITEM_URL);
		url = url.replace("//","http://");
    	String title = jsonObject.optString(Constants.LISTITEM_TITLE);
    	String framework = jsonObject.optString(Constants.LISTITEM_FRAMEWORK);
    	String version = jsonObject.optString(Constants.LISTITEM_VERSION);
    	String createdDateTime = jsonObject.optString(Constants.LISTITEM_CREATEDDATETIME);
    	String description = jsonObject.optString(Constants.LISTITEM_DESCRIPTION);
    	description = TextUtils.isEmpty(description) ? "No description set." : "[Description] " + description;

    	StringBuilder details = new StringBuilder();
    	details.append("(");
    	details.append(framework);
    	details.append(") rev");
    	details.append(version);
    	details.append(" ");
    	details.append(createdDateTime);
    	
    	Fiddle fiddle = new Fiddle();
    	fiddle.url = url;
    	fiddle.title = title;
    	fiddle.details = details.toString();
    	fiddle.description = description;
	    
	  	return fiddle;
	}
	
	public static ArrayList<Fiddle> fromJson(JSONObject jsonResponse) {
		String status = jsonResponse.optString(Constants.JSON_STATUS);
		Integer listCount = jsonResponse.optInt(Constants.JSON_LISTCOUNT);
		JSONArray list = jsonResponse.optJSONArray(Constants.JSON_LISTRESPONSE);
		//Boolean returnedItemsCountSameAsOverallResultSetCount = list.length() == listCount;    		
		
		ArrayList<Fiddle> fiddleList = new ArrayList<Fiddle>(listCount);
		
		try {
			if(status.compareTo("ok") == 0) {    		 
				for(int i = 0; i <= list.length() - 1; i++) {
					JSONObject content = list.getJSONObject(i);				
					fiddleList.add(new Fiddle().decodeJson(content));
				}
			}
		} catch (JSONException e) {
			Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
		}
		
		return fiddleList;
	}
}
