package com.mypowerapps.android.jsfiddlequery.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.content.Context;
import android.util.Log;

public final class Helper {
	
	public static String readStream(InputStream inputStream, String filename, Context context) {
		String queryResult = "";
		ByteArrayOutputStream outputStream = null;
		
		try {
			if(inputStream == null && filename != null) {
				inputStream = context.openFileInput(filename);
			}
			
			int read = 0;
			int bufferSize = 1024;
			outputStream = new ByteArrayOutputStream();
		     
			byte[] buffer = new byte[bufferSize];
			while ((read = inputStream.read(buffer)) > 0 ) {
				outputStream.write(buffer, 0, read);
			}
		     
			queryResult = new String(outputStream.toByteArray());	
		    queryResult = queryResult.replace("Apistart=0(","");
			queryResult = queryResult.replace(");","");
		     
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
		
		return queryResult;
	}
}
