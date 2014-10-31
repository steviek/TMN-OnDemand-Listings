package com.sixbynine.movieoracle.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.sixbynine.movieoracle.MyApplication;

import java.util.HashSet;
import java.util.Set;

public class Prefs {
	private static SharedPreferences prefs;

	private static void init(){
        if(prefs == null && MyApplication.getInstance() != null) {
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

    public static void addTitleToIgnoreList(String title){
        init();
        if(prefs != null){
            Set<String> movieTitles = prefs.getStringSet(Keys.IGNORE_LIST, new HashSet<String>());
            movieTitles.add(title);
            prefs.edit().putStringSet(Keys.IGNORE_LIST, movieTitles).apply();
        }
    }

    public static Set<String> getIgnoreList(){
        init();
        if(prefs != null){
            return prefs.getStringSet(Keys.IGNORE_LIST, new HashSet<String>());
        }
        return new HashSet<String>();
    }

    public static void saveExcludeList(Set<String> list){
        init();
        if(prefs != null){
            prefs.edit().putStringSet(Keys.EXCLUDE_LIST, list).apply();
        }
    }

    public static void saveSeriesList(Set<String> list){
        init();
        if(prefs != null){
            prefs.edit().putStringSet(Keys.SERIES_LIST, list).apply();
        }
    }

    public static void savePopulatedList(Set<String> list){
        init();
        if(prefs != null){
            prefs.edit().putStringSet(Keys.POPULATED_LIST, list).apply();
        }
    }

    public static Set<String> getExcludeList(){
        init();
        if(prefs != null){
            return prefs.getStringSet(Keys.EXCLUDE_LIST, new HashSet<String>());
        }
        return new HashSet<String>();
    }

    public static Set<String> getSeriesList(){
        init();
        if(prefs != null){
            return prefs.getStringSet(Keys.SERIES_LIST, new HashSet<String>());
        }
        return new HashSet<String>();
    }

    public static Set<String> getPopulatedList(){
        init();
        if(prefs != null){
            return prefs.getStringSet(Keys.POPULATED_LIST, new HashSet<String>());
        }
        return null;
    }

    public static void saveTmnUrl(String url){
        init();
        if(prefs != null){
            prefs.edit().putString(Keys.TMN_URL, url).apply();
        }
    }

    public static String getTmnUrl(){
        init();
        if(prefs != null){
            return prefs.getString(Keys.TMN_URL, "http://www.themovienetwork.ca/ondemand/print?network=tmn");
        }
        return "http://www.themovienetwork.ca/ondemand/print?network=tmn";
    }

    public class Keys{
        public static final String IGNORE_LIST = "IGNORE_LIST";
        public static final String EXCLUDE_LIST = "EXCLUDE_LIST";
        public static final String SERIES_LIST = "SERIES_LIST";
        public static final String POPULATED_LIST = "POPULATED_LIST";
        public static final String TMN_URL = "tmn-url";
        private static final String LAST_SAVE_DATE = "LAST_SAVE_DATE";
    }

	
}
