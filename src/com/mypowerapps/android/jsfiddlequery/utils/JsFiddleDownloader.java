package com.mypowerapps.android.jsfiddlequery.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.mypowerapps.android.jsfiddlequery.ui.JsFiddleQueryActivity;
import com.mypowerapps.android.jsfiddlequery.utils.Constants;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class JsFiddleDownloader extends AsyncTask<String, Void, String> {

	private WeakReference<JsFiddleQueryActivity> activity;
	private DownloadFinishedListener callback;
	
	public JsFiddleDownloader(JsFiddleQueryActivity activity, DownloadFinishedListener callback) {
	     this.activity = new WeakReference<JsFiddleQueryActivity>(activity);
	     this.callback = callback;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String data = null;
		HttpURLConnection httpUrlConnection = null;
		
		try {	
				String username = params[0];
			
				String url = Constants.URL_USERQUERY_START + username + Constants.URL_USERQUERY_PARAMS;				
				httpUrlConnection = (HttpURLConnection) new URL(url).openConnection();
				httpUrlConnection .setRequestProperty("Connection", "keep-alive");
			
				if(httpUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream inputStream = httpUrlConnection.getInputStream();				
				
					String filename = Constants.FILENAME;
					saveStream(inputStream, filename);
					data = Helper.readStream(null, filename, activity.get());	
				}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		    Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
		} catch (IOException e) {
			e.printStackTrace();
		    Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
		} catch (Exception e) {
			e.printStackTrace();
		    Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
		}			
		finally {
			if (httpUrlConnection != null)
				httpUrlConnection.disconnect();
		}
		
		return data;
	}

	@Override
	protected void onPostExecute(String result) {
		callback.notifyDataRefreshed(result);
	}
	
	private void saveStream(InputStream inputStream, String filename) {
		FileOutputStream outputStream = null;
		
		try {
			 int read = 0;
		     int bufferSize = 1024;
		     outputStream = activity.get().openFileOutput(filename, Context.MODE_PRIVATE);
		     
		     byte[] buffer = new byte[bufferSize];
		     while ((read = inputStream.read(buffer)) > 0 ) {
		    	 outputStream.write(buffer, 0, read);
		     } 
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
				}
			}
			
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
				}
			}
		}
	}
}
