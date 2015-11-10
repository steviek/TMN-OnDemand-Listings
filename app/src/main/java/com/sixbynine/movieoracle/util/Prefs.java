package com.sixbynine.movieoracle.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Prefs {

    private static ObjectMapper objectMapper;
    private static SharedPreferences prefs;

	private static void init(){
        if(prefs == null && MyApplication.getInstance() != null) {
            objectMapper = MyApplication.getInstance().getObjectMapper();
            prefs = MyApplication.getInstance().getSharedPreferences("movieoracle", Context.MODE_MULTI_PROCESS);
        }
	}
	
	public static void saveLastUpdateDate(String val){
        init();
        if(prefs != null) prefs.edit().putString(Keys.LAST_SAVE_DATE, val).apply();
	}
	
	public static String getLastUpdateDate(){
        init();
        if(prefs != null) return prefs.getString(Keys.LAST_SAVE_DATE, null);
        return null;
	}

    public static void putStringSet(String key, Set<String> stringSet){
        init();
        if(Build.VERSION.SDK_INT >= 11){
            prefs.edit().putStringSet(key, stringSet).apply();
        }else{
            StringBuilder saveString = new StringBuilder("");
            if(stringSet.size() > 0) {
                Iterator<String> iter = stringSet.iterator();
                saveString.append(iter.next());
                while (iter.hasNext()) {
                    saveString.append(";;;").append(iter.next());
                }
            }
            prefs.edit().putString(key, saveString.toString()).apply();
        }
    }

    public static Set<String> getStringSet(String key){
        return getStringSet(key, new HashSet<String>());
    }

    public static Set<String> getStringSet(String key, Set<String> fallback){
        init();
        if(Build.VERSION.SDK_INT >= 11){
            return prefs.getStringSet(key, fallback);
        }else{
            String rawString = prefs.getString(key, "");
            if(rawString.isEmpty()){
                return fallback;
            }else{
                String[] parts = rawString.split(";;;");
                Set<String> returnVal = new HashSet<>(parts.length);
                Collections.addAll(returnVal, parts);
                return returnVal;
            }
        }
    }

    public static void addTitleToIgnoreList(String title){
        init();
        if(prefs != null){
            Set<String> movieTitles = getStringSet(Keys.IGNORE_LIST);
            movieTitles.add(title);
            putStringSet(Keys.IGNORE_LIST, movieTitles);
        }
    }

    public static void saveIgnoreList(Set<String> list){
        init();
        if(prefs != null){
            putStringSet(Keys.IGNORE_LIST, list);
        }
    }

    public static Set<String> getIgnoreList(){
        init();
        if(prefs != null){
            return getStringSet(Keys.IGNORE_LIST);
        }
        return new HashSet<>();
    }

    public static void putCurrentResults(Map<String, RTMovieQueryResult> queryResultMap) {
        init();
        for (Map.Entry<String, RTMovieQueryResult> entry : queryResultMap.entrySet()) {
            putSummary(entry.getKey(), entry.getValue());
        }
        putStringSet("rt-movies", queryResultMap.keySet());
    }

    public static Map<String, RTMovieQueryResult> getCurrentResults() {
        init();
        Set<String> movies = getStringSet("rt-movies");
        Map<String, RTMovieQueryResult> queryResultMap = new HashMap<>();
        for (String movie : movies) {
            queryResultMap.put(movie, getSummary(movie));
        }
        return queryResultMap;
    }

    public static List<RTMovieQueryMovieSummary> getCurrentBestSummaries() {
        Map<String, RTMovieQueryResult> queryResultMap = getCurrentResults();
        List<RTMovieQueryMovieSummary> summaries = new ArrayList<>();
        for (Map.Entry<String, RTMovieQueryResult> entry : queryResultMap.entrySet()) {
            summaries.add(entry.getValue().getBestMatch(entry.getKey()));
        }
        return summaries;
    }

    public static RTMovieQueryResult getSummary(String title) {
        init();
        try {
            return objectMapper.readValue(prefs.getString("rt-movie-" + title, null), RTMovieQueryResult.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void putSummary(String title, RTMovieQueryResult queryResult) {
        init();
        prefs.edit().putString("rt-movie-" + title, MyApplication.getInstance().writeValueAsSring(queryResult)).apply();
    }

    public class Keys{
        public static final String IGNORE_LIST = "IGNORE_LIST";
        public static final String EXCLUDE_LIST = "EXCLUDE_LIST";
        private static final String LAST_SAVE_DATE = "LAST_SAVE_DATE";
        public static final String ALL_SUMMARIES = "ALL_SUMMARIES";
        public static final String CURRENT_SUMMARIES = "CURRENT_SUMMARIES_2";
    }

	
}
