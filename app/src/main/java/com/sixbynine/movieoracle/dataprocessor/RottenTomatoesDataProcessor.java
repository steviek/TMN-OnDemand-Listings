package com.sixbynine.movieoracle.dataprocessor;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sixbynine.movieoracle.BuildConfig;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.media.Movie;
import com.sixbynine.movieoracle.media.Rating;
import com.sixbynine.movieoracle.media.Series;
import com.sixbynine.movieoracle.util.Keys;
import com.sixbynine.movieoracle.util.URIHelper;

public class RottenTomatoesDataProcessor extends DataProcessor{

	
	@Override
	public Media retrieve(Resources res, String title, boolean movie) throws IOException{
		 Media media = movie? new Movie(title) : new Series(title);
		 URIHelper uriHelper = URIHelper.getInstance(res);
		 
		 String url = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?" +
				 "apikey=" + Keys.RT_API_KEY +
		 		"&q=" + uriHelper.getAsURI(title) + "&page_limit=3";
		 URL website = new URL(url);
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
        try{
	     JSONObject obj = new JSONObject(json_data);
	     JSONArray mediaArray = obj.getJSONArray("movies");
	     
	     
	     if (mediaArray.length() <= 0) return media;
	     JSONObject mediaData = null;
	     
	     JSONObject movie1 = mediaArray.getJSONObject(0);
	     mediaData = movie1;

	    	 JSONObject movie2 = mediaArray.getJSONObject(1);
	    	 JSONObject movie3 = mediaArray.getJSONObject(2);
	    	 
	    	 boolean equal1 = movie1.getString("title").equals(title);
	    	 boolean equal2 = movie2.getString("title").equals(title);
	    	 boolean equal3 = movie3.getString("title").equals(title);
	    	 
	    	 String year1 = movie1.getString("year");
	    	 String year2 = movie2.getString("year");
	    	 String year3 = movie3.getString("year");
	    	 
	    	 if(equal1){
	    		 if(equal2){
	    			 if(equal3){
	    				 if(year1.compareTo(year2) >= 0){
	    	    			 if(year1.compareTo(year3) >= 0){
	    	    				 mediaData = movie1;
	    	    			 }else{
	    	    				 mediaData = movie3;
	    	    			 }
	    	    			 
	    	    		 }else{
	    	    			 if(year2.compareTo(year3) >= 0){
	    	    				 mediaData = movie2;
	    	    			 }else{
	    	    				 mediaData = movie3;
	    	    			 }
	    	    		 }
	    			 }else{
	    				 if(year1.compareTo(year2) >= 0){
	    					 mediaData = movie1;
	    				 }else{
	    					 mediaData = movie2;
	    				 }
	    			 }
	    		 }else{
	    			 if(equal3){
	    				 if(year1.compareTo(year3) >= 0){
    	    				 mediaData = movie1;
    	    			 }else{
    	    				 mediaData = movie3;
    	    			 }
	    			 }else{
	    				 mediaData = movie1;
	    			 }
	    		 }
	    	 }else{
	    		 if(equal2){
	    			 if(equal3){
	    				 if(year2.compareTo(year3) >= 0){
    	    				 mediaData = movie2;
    	    			 }else{
    	    				 mediaData = movie3;
    	    			 }
	    			 }else{
	    				 mediaData = movie2;
	    			 }
	    		 }else{
	    			 if(equal3){
	    				 mediaData = movie3;
	    			 }else{
	    				 mediaData = movie1;
	    			 }
	    		 }
	    	 }
	    		 
	    	 
	    	
	    	 

	     String rtID = getJSONValueString(mediaData, "id");
	     media.addId(DataProcessor.ROTTEN_TOMATOES, rtID);

	    	 JSONObject otherIDs = mediaData.getJSONObject("alternate_ids");
	    	 String imdb = getJSONValueString(otherIDs, "imdb");
	    	 if(!imdb.equals("")) media.addId(DataProcessor.IMDB, imdb);
	     }catch(Exception e){}
	     
	     return updateMedia(media, DataProcessor.ROTTEN_TOMATOES);
	     
	 }
	
	public static Media updateMedia(Media media, String site) throws IOException{
		 if(media.getId(site) == null) return null;
		 if(site.equals(DataProcessor.ROTTEN_TOMATOES)){
			 String url = "http://api.rottentomatoes.com/api/public/v1.0/movies/"
					 + media.getId(site) + ".json?apikey=" + Keys.RT_API_KEY;

			 URL website = new URL(url);
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

             try{
		     JSONObject mediaData = new JSONObject(json_data);
		     
		 
		     media.setYear(getJSONValueInt(mediaData, "year") + "");
		     media.setParentalAdvisory(getJSONValueString(mediaData, "mpaa_rating"));
		     media.setRuntime(getJSONValueInt(mediaData, "runtime") + "");
		     media.setSynopsis(getJSONValueString(mediaData, "synopsis"));
		     

			     JSONObject ratings = mediaData.getJSONObject("ratings");
				 String critics_rating = getJSONValueString(ratings, "critics_rating");
				 int critics_score = getJSONValueInt(ratings, "critics_score");
				 if(critics_rating != null && !critics_rating.equals("") && critics_score > 0){
					 media.addRating(Rating.CRITIC, critics_score, critics_rating);
				 }
				 
				 String audience_rating = getJSONValueString(ratings, "audience_rating");
				 int audience_score = getJSONValueInt(ratings, "audience_score");
				 if(audience_rating != null && !audience_rating.equals("") && audience_score > 0){
					 media.addRating(Rating.AUDIENCE, audience_score, audience_rating);
				 }

			     JSONArray genres = mediaData.getJSONArray("genres");
			     for(int i = 0; i < genres.length(); i ++){
			    	 String genre = genres.getString(i);
			    	 media.addGenre(genre);
			     }

			     JSONArray cast = mediaData.getJSONArray("abridged_cast");
			     for(int i = 0; i < cast.length(); i ++){
			    	 JSONObject castMember = cast.getJSONObject(i);
			    	 String name = getJSONValueString(castMember, "name");
			    	 if(!name.equals("")){
			    		 media.addCastMember(name);
			    	 }
			     }

			     JSONArray directors = mediaData.getJSONArray("abridged_directors");
			     for(int i = 0; i < directors.length(); i ++){
			    	 JSONObject director = directors.getJSONObject(i);
			    	 String name = getJSONValueString(director, "name");
			    	 if(!name.equals("")){
			    		 media.addDirector(name);
			    	 }
			     }
		     }catch(Exception e){
                 if(BuildConfig.DEBUG) Log.e("TMN", e.toString());
             }
		    	 
		     
			 return media;
		 }
		 return media;
	 }
	 
	 private static String  getJSONValueString(JSONObject obj, String key){
		 String value = "";
		 try{
			  value = obj.getString(key);
		 }catch(Exception e){
			 
		 }
		 return value;
	 }
	 
	 
	 private static int  getJSONValueInt(JSONObject obj, String key){
		 int value = 0;
		 try{
			  value = obj.getInt(key);
		 }catch(Exception e){
			 
		 }
		 return value;
	 }
	 
}
