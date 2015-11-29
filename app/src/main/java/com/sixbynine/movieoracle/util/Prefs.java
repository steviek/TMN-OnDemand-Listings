package com.sixbynine.movieoracle.util;

import com.google.common.base.Optional;

import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryResultMap;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryResult;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Prefs {

    private static ObjectMapper objectMapper;
    private static SharedPreferences prefs;

	private static void init(){
        if(prefs == null && MyApplication.getInstance() != null) {
            objectMapper = MyApplication.getInstance().getObjectMapper();
            prefs = MyApplication.getInstance().getSharedPreferences("movieoracle", 0);
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
        prefs.edit().putStringSet(key, stringSet).apply();
    }

    public static Set<String> getStringSet(String key){
        return getStringSet(key, new HashSet<String>());
    }

    public static Set<String> getStringSet(String key, Set<String> fallback){
        init();
        return prefs.getStringSet(key, fallback);
    }

    public static void addTitleToIgnoreList(String title){
        init();
        if(prefs != null){
            Set<String> movieTitles = getStringSet(Keys.IGNORE_LIST);
            movieTitles.add(title);
            putStringSet(Keys.IGNORE_LIST, movieTitles);
        }
    }

    public static Set<String> getIgnoreList(){
        init();
        return getStringSet(Keys.IGNORE_LIST);
    }

    public static RTMovieQueryResultMap getCurrentResults() {
        init();
        Set<String> movies = getStringSet("rt-movies");
        RTMovieQueryResultMap queryResultMap = new RTMovieQueryResultMap();
        for (String movie : movies) {
            Optional<RTMovieQueryResult> queryResult = getSummary(movie);
            if (queryResult.isPresent()) {
                queryResultMap.put(movie, queryResult.get());
            }
        }
        return queryResultMap;
    }

    public static Optional<RTMovieQueryResult> getSummary(String title) {
        init();
        String serialized = prefs.getString("rt-movie-" + title, null);
        if (serialized == null) {
            return Optional.absent();
        } else {
            try {
                return Optional.of(objectMapper.readValue(
                        prefs.getString("rt-movie-" + title, null),
                        RTMovieQueryResult.class));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static void putSummary(String title, RTMovieQueryResult queryResult) {
        init();
        prefs.edit().putString("rt-movie-" + title, MyApplication.getInstance().writeValueAsSring(queryResult)).apply();
    }

    public static void putCurrentTitles(Set<String> titles) {
        init();
        Set<String> oldTitles = getStringSet("rt-movies");
        oldTitles.removeAll(titles);
        SharedPreferences.Editor edit = prefs.edit();
        for (String oldTitle : oldTitles) {
            edit.remove("rt-movie-" + oldTitle);
        }
        edit.putStringSet("rt-movies", titles);
        edit.apply();
    }

    public class Keys{
        private static final String IGNORE_LIST = "IGNORE_LIST";
        private static final String LAST_SAVE_DATE = "LAST_SAVE_DATE";
    }

	
}
