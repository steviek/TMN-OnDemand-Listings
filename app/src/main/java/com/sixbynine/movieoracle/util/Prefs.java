package com.sixbynine.movieoracle.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
	private static SharedPreferences prefs;
	private static final String FREQUENCY = "FREQUENCY";
	private static final String NUM_TOP_RATED = "NUM_TOP_RATED";
	private static final String LAST_SAVE_DATE = "LAST_SAVE_DATE";
	
	private static void getPrefs(Context context){
		prefs = context.getSharedPreferences("movieoracle", Context.MODE_MULTI_PROCESS);

	}
	
	public static void saveCheckingFrequency(Context context, int freq){
		getPrefs(context);
		prefs.edit().putInt(FREQUENCY, freq).commit();
	}
	
	public static int getCheckingFrequency(Context context, int defValue){
		getPrefs(context);
		return prefs.getInt(FREQUENCY, defValue);
	}
	
	public static void saveNumTopRated(Context context, int num){
		getPrefs(context);
		prefs.edit().putInt(NUM_TOP_RATED, num).commit();
	}
	
	public static int getNumTopRated(Context context, int defValue){
		getPrefs(context);
		return prefs.getInt(NUM_TOP_RATED, defValue);
	}
	
	public static void saveLastUpdateDate(Context context, String val){
		getPrefs(context);
		prefs.edit().putString(LAST_SAVE_DATE, val).commit();
	}
	
	public static String getLastUpdateDate(Context context){
		getPrefs(context);
		return prefs.getString(LAST_SAVE_DATE, null);
	}
	
	public static boolean did08092014BugFix(Context context){
		getPrefs(context);
		boolean did = prefs.getBoolean("bugfix-08092014", false);
		if(!did) prefs.edit().putBoolean("bugfix-08092014", true).commit();
		return did;
	}
	
	
}
