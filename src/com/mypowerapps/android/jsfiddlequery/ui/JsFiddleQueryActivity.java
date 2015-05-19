package com.mypowerapps.android.jsfiddlequery.ui;


import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import com.mypowerapps.android.jsfiddlequery.R;
import com.mypowerapps.android.jsfiddlequery.utils.Constants;
import com.mypowerapps.android.jsfiddlequery.utils.Helper;
import com.mypowerapps.android.jsfiddlequery.utils.JsFiddleDownloader;
import com.mypowerapps.android.jsfiddlequery.utils.DownloadFinishedListener;
import com.mypowerapps.android.jsfiddlequery.utils.PreferencesActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class JsFiddleQueryActivity extends Activity {
    private ListView listView;
    private FiddleAdapter adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsfiddlequery);
        
        if(getFileStreamPath(Constants.FILENAME).exists()) {  
        	ArrayList<Fiddle> fiddles = getFiddles(null);
        	populateAdapter(fiddles);
        	
    	    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    	    	@Override
    	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	    		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	    	    if (networkInfo != null && networkInfo.isConnected()) {
    	    	    	Fiddle selectedFiddle = (Fiddle) adapter.getItem(position);
    		    		Uri url = Uri.parse(selectedFiddle.getUrl());
    		            Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
    		            
    		            String header = getResources().getString(R.string.default_browse);
    		            Intent chooserIntent = Intent.createChooser(browserIntent, header);	            
    		            if (browserIntent.resolveActivity(getPackageManager()) != null) {
    		    			startActivity(chooserIntent);
    		    		}    	    			
    	    	    } else {
    	    	    	String msg = getResources().getString(R.string.error_noconnection);
    	    	    	Toast.makeText(JsFiddleQueryActivity.this, msg, Toast.LENGTH_LONG).show();
    	    	    }
    	        }
    	    });
        } else {
        	AlertDialog dialog = new AlertDialog.Builder(this)
												.setMessage(R.string.confirm_nolocalcopy)
												.setCancelable(false)
												.setPositiveButton(R.string.dialog_okay, null)
												.create();	
        	dialog.show();
        }
    }
    
    private ArrayList<Fiddle> getFiddles(String fiddleData) {       	
    	try {
    		if(fiddleData == null) {
    			fiddleData = getData();
    		}
    		
    		JSONObject jsonResponse = new JSONObject(fiddleData);
			return Fiddle.fromJson(jsonResponse);
    	}
    	catch(JSONException e) {
    		Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
    	}
    	
    	return null;
    }
    
    private String getData() {
    	String filename = Constants.FILENAME;
		return Helper.readStream(null, filename, JsFiddleQueryActivity.this);
    }
    
    protected void populateAdapter(ArrayList<Fiddle> fiddles) {
    	adapter = new FiddleAdapter(this, fiddles);
	    listView = (ListView) findViewById(R.id.listview);
	    listView.setAdapter(adapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jsfiddlequery, menu);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.menuitem_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            
            // add option if expand search view by default
            if(getFileStreamPath(Constants.FILENAME).exists()) { 
            	searchView.setIconifiedByDefault(false);
            	searchView.clearFocus();
            	
            	MenuItem searchMenuItem = menu.findItem(R.id.menuitem_search);
                searchMenuItem.expandActionView();                
                
                if(adapter != null){
                	adapter.getFilter().filter("");
                } 
            }
            else {
            	searchView.setIconifiedByDefault(true);
            	
            	if(adapter != null){
                	adapter.getFilter().filter("");
                }
            }
            
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { 
                @Override 
                public boolean onQueryTextChange(String query) {
                	if (!TextUtils.isEmpty(query)) {
                		if(adapter != null) {
                			adapter.getFilter().filter(query);
                		} else {
                			adapter.getFilter().filter("");
                		}
                	}
                	
                    return true; 
                }

				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				} 
            });
            
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {				
				@Override
				public boolean onClose() {
					if(adapter != null) {
                		adapter.getFilter().filter("");
                	}
					return false;
				}
			});
        }
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menuitem_refresh) {
        	
        	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	    if (networkInfo != null && networkInfo.isConnected()) {    	    	
    	    	if (getFileStreamPath(Constants.FILENAME).exists()) {
    	    		
    	    		AlertDialog dialog = new AlertDialog.Builder(this)
    	    				.setMessage(R.string.confirm_refresh)
    	    				.setCancelable(false)
    	    				.setNegativeButton(R.string.dialog_cancel, null)
							.setPositiveButton(R.string.dialog_yes,
									new DialogInterface.OnClickListener() {
										public void onClick(final DialogInterface dialog, int id) {
											downloadFiddles();
								}
							}).create();
    	    		
    	    		dialog.show();
    			} else {
    				downloadFiddles();
    			}
    	    } else {
    	    	String msg = getResources().getString(R.string.error_noconnection);
    	    	Toast.makeText(JsFiddleQueryActivity.this, msg, Toast.LENGTH_LONG).show();
    	    }
        	
            return true;
        }
        else if (id == R.id.menuitem_settings) {
        	startActivity(new Intent(JsFiddleQueryActivity.this, PreferencesActivity.class));
        	
            return true;
        }
        /*else if (id == R.id.menuitem_help) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
    
    private void downloadFiddles() {
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String defaultValue = getResources().getString(R.string.default_username);
		String username = sharedPref.getString(Constants.PREFERENCE_USERNAME, defaultValue);
	
		if(username.compareTo(defaultValue) == 0) {
			String msg = getResources().getString(R.string.error_username);
			Toast.makeText(JsFiddleQueryActivity.this, msg, Toast.LENGTH_LONG).show();
		} else {
			JsFiddleDownloader downloader = new JsFiddleDownloader(this, new DownloadFinishedListener()
	    	{
	    		@Override
	            public void notifyDataRefreshed(String fiddleData) {
	    			if(fiddleData != null) {
	    				String notificationText = getResources().getString(R.string.status_download);
	    				Toast.makeText(JsFiddleQueryActivity.this, notificationText, Toast.LENGTH_SHORT).show();
	    				
	    				ArrayList<Fiddle> fiddles = getFiddles(fiddleData);
	    	        	populateAdapter(fiddles);
	    	        	adapter.getFilter().filter("");
	    			}
	            }
	    	});
			
	    	downloader.execute(username);
		} 
    }
}
