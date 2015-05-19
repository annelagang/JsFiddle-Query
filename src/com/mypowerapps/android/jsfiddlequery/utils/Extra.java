package com.mypowerapps.android.jsfiddlequery.utils;
/*package com.mypowerapps.android.jsfiddlequery.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mypowerapps.android.jsfiddlequery.R;
import com.mypowerapps.android.jsfiddlequery.utils.Constants;
import com.mypowerapps.android.jsfiddlequery.utils.JsFiddleDownloader;
import com.mypowerapps.android.jsfiddlequery.utils.DownloadFinishedListener;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class JsFiddleQueryActivity extends Activity {
    private ListView listView;
    List<Map<String,String>> fiddleList = new ArrayList<Map<String,String>>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsfiddlequery);
        
        listView = (ListView) findViewById(R.id.listview);
        
        //handleIntent(getIntent());
        
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    if (networkInfo != null && networkInfo.isConnected()) {
	    	JsFiddleDownloader downloader = new JsFiddleDownloader(this, new DownloadFinishedListener()
	    	{
	    		@Override
	            public void notifyDataRefreshed(String fiddle) {
	    			initList();	    			
	    			SimpleAdapter simpleAdapter = new SimpleAdapter(JsFiddleQueryActivity.this, fiddleList , android.R.layout.simple_list_item_1, new String[] {"title"}, new int[] {android.R.id.text1}); 
	    			listView.setAdapter(simpleAdapter);
	            }
	    	});
			
	    	downloader.execute();
			
	    } else {
	    	Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG).show();
	    }
	    
	    initList();	 
	    
	    String[] from = 
        	{ 
        		Constants.LISTITEM_TITLE, 
        		"details",
        		Constants.LISTITEM_DESCRIPTION
            };

        int[] to = { R.id.listview_title, 
        		     R.id.listview_details,
        		     R.id.listview_description };
	    
		SimpleAdapter simpleAdapter = new SimpleAdapter(JsFiddleQueryActivity.this, fiddleList, R.layout.listviewitem, from, to); 
		listView.setAdapter(simpleAdapter);
    }
   
    private void initList() {       	
    	try {
    		JSONObject jsonResponse = new JSONObject(GetData());
    		String status = jsonResponse.optString(Constants.JSON_STATUS);
    		Integer listCount = jsonResponse.optInt(Constants.JSON_LISTCOUNT);
    		JSONArray list = jsonResponse.optJSONArray(Constants.JSON_LISTRESPONSE);
    		//Boolean returnedItemsCountSameAsOverallResultSetCount = list.length() == listCount;    		
    		
    		if(status.compareTo("ok") == 0) {    		 
    			for(int i = 0; i <= list.length() - 1; i++){
    				JSONObject content = list.getJSONObject(i);				
    				fiddleList.add(getFiddleDetails(content));
    			}
    		}
    	}
    	catch(JSONException e) {
    		Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
    	}
    }
    
    private HashMap<String, String> getFiddleDetails(JSONObject fiddle) {    	 
        HashMap<String, String> fiddleDetails = new HashMap<String, String>();
        
        String url = fiddle.optString(Constants.LISTITEM_URL);
    	String title = fiddle.optString(Constants.LISTITEM_TITLE);
    	String framework = fiddle.optString(Constants.LISTITEM_FRAMEWORK);
    	String version = fiddle.optString(Constants.LISTITEM_VERSION);
    	String createdDateTime = fiddle.optString(Constants.LISTITEM_CREATEDDATETIME);
    	String description = "[Description] " + fiddle.optString(Constants.LISTITEM_DESCRIPTION);

    	StringBuilder details = new StringBuilder();
    	details.append("(");
    	details.append(framework);
    	details.append(") rev");
    	details.append(version);
    	details.append(" ");
    	details.append(createdDateTime);
    	
    	url = url.replace("//","");
    	
    	fiddleDetails.put(Constants.LISTITEM_TITLE, url);
    	fiddleDetails.put("details", details.toString());
    	fiddleDetails.put(Constants.LISTITEM_DESCRIPTION, description);
        
        return fiddleDetails;
    }
    
    private String GetData() {
    	BufferedReader jsonReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.list)));
		StringBuilder jsonBuilder = new StringBuilder();
		try {
			for (String line = null; (line = jsonReader.readLine()) != null;) {
				jsonBuilder.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String s = jsonBuilder.toString();
		s = s.replace("Apistart=0(","");
		s = s.replace(");","");
		
		return s;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }
    
    
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            //Intent wordIntent = new Intent(this, WordActivity.class);
            //wordIntent.setData(intent.getData());
            //startActivity(wordIntent);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //showResults(query);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jsfiddlequery, menu);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.menuitem_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
        
        MenuItem searchMenuItem = menu.findItem(R.id.menuitem_search);
        searchMenuItem.expandActionView();
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menuitem_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}*/

