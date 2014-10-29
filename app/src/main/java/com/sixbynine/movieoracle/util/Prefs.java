package com.sixbynine.movieoracle.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.sixbynine.movieoracle.MyApplication;

import java.util.HashSet;
import java.util.Set;

public class Prefs {
	private static SharedPreferences prefs;
	private static final String FREQUENCY = "FREQUENCY";
	private static final String NUM_TOP_RATED = "NUM_TOP_RATED";
	private static final String LAST_SAVE_DATE = "LAST_SAVE_DATE";
	
	private static void init(){
        if(prefs == null && MyApplication.getInstance() != null) {
            prefs = MyApplication.getInstance().getSharedPreferences("movieoracle", Context.MODE_MULTI_PROCESS);
        }
	}
	
	public static void saveLastUpdateDate(String val){
        init();
        if(prefs != null) prefs.edit().putString(LAST_SAVE_DATE, val).apply();
	}
	
	public static String getLastUpdateDate(){
        init();
        if(prefs != null) return prefs.getString(LAST_SAVE_DATE, null);
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



    public class Keys{
        public static final String IGNORE_LIST = "IGNORE_LIST";
    }

	
}
