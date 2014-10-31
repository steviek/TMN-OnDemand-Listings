package com.sixbynine.movieoracle.dataprocessor;

import android.os.AsyncTask;

import com.sixbynine.movieoracle.util.Prefs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

public class WebResources {
	private static final String WEB_ADDRESS = "https://raw.githubusercontent.com/steviek/TMN-OnDemand-Listings/master/app/src/main/assets/resources.json";


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

		     Prefs.saveTmnUrl(obj.getString("tmn_url"));

		     JSONArray excludeArr = obj.getJSONArray("exclude_prefixes");
             HashSet<String> excludeList = new HashSet<String>(excludeArr.length());
		     for(int i = 0; i < excludeArr.length(); i ++){
                 excludeList.add(excludeArr.getString(i));
		     }
             Prefs.saveExcludeList(excludeList);

            JSONArray populatedArr = obj.getJSONArray("populated_urls");
            Set<String> populatedSet = new HashSet<String>(populatedArr.length());
            for(int i = 0; i < populatedArr.length(); i ++){
                populatedSet.add(populatedArr.getString(i));
            }
            Prefs.savePopulatedList(populatedSet);

            JSONArray seriesArr = obj.getJSONArray("series");
            HashSet<String> seriesList = new HashSet<String>(seriesArr.length());
            for(int i = 0; i < seriesArr.length(); i ++){
                seriesList.add(seriesArr.getString(i));
            }
            Prefs.saveSeriesList(seriesList);
		     
		     
			}catch(Exception e){
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result == null || !result){
				callback.onFailure();
			}else{
				callback.onSuccess();
			}
			
		}
		
		
		
	}
}
