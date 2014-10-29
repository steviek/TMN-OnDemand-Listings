package com.sixbynine.movieoracle.dataprocessor;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WebResources {
	private static final String WEB_ADDRESS = "https://raw.githubusercontent.com/steviek/TMN-OnDemand-Listings/master/app/src/main/assets/resources.json";
	public static String sTMNURL;
	public static String[] sExcludePrefixes;
	
	public interface Callback{
		public void onSuccess();
		public void onFailure();
	}
	
	public static void loadResources(Callback callback){
		new DownloadResources().execute(callback);
	}
	
	private static class DownloadResources extends AsyncTask<Callback, Void, Boolean>{

		private Callback callback;
		
		@Override
		protected Boolean doInBackground(Callback... args) {
			callback = args[0];
			try{
			 URL website = new URL(WEB_ADDRESS);
		     URLConnection connection = website.openConnection();
		     BufferedReader in = new BufferedReader(
		                             new InputStreamReader(
		                                 connection.getInputStream()));

		     StringBuilder response = new StringBuilder();
		     String inputLine;
		     while ((inputLine = in.readLine()) != null) 
		         response.append(inputLine);

		     in.close();
		     String json_data = response.toString();
		     
		     JSONObject obj = new JSONObject(json_data);
		     sTMNURL = obj.getString("tmn_url");
		     JSONArray arr = obj.getJSONArray("exclude_prefixes");
		     sExcludePrefixes = new String[arr.length()];
		     for(int i = 0; i < sExcludePrefixes.length; i ++){
		    	 sExcludePrefixes[i] = arr.optString(i, "");
		     }
		     
		     
			}catch(Exception e){
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result == null || result.booleanValue() == false){
				callback.onFailure();
			}else{
				callback.onSuccess();
			}
			
		}
		
		
		
	}
}
