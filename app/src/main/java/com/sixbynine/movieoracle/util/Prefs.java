package com.sixbynine.movieoracle.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.Gson;
import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
        init();
        if(Build.VERSION.SDK_INT >= 11){
            return prefs.getStringSet(key, new HashSet<String>());
        }else{
            String rawString = prefs.getString(key, "");
            if(rawString.isEmpty()){
                return new HashSet<String>();
            }else{
                String[] parts = rawString.split(";;;");
                Set<String> returnVal = new HashSet<String>(parts.length);
                for(String s : parts){
                    returnVal.add(s);
                }
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
        return new HashSet<String>();
    }

    public static Set<String> getMutableIgnoreList(){
        Set<String> ignoreList = getIgnoreList();
        if(ignoreList == null){
            return null;
        }else{
            return new HashSet<String>(ignoreList);
        }
    }

    public static void saveExcludeList(Set<String> list){
        init();
        if(prefs != null){
            putStringSet(Keys.EXCLUDE_LIST, list);
        }
    }

    public static void saveSeriesList(Set<String> list){
        init();
        if(prefs != null){
            putStringSet(Keys.SERIES_LIST, list);
        }
    }

    public static void savePopulatedList(Set<String> list){
        init();
        if(prefs != null){
            putStringSet(Keys.POPULATED_LIST, list);
        }
    }

    public static Set<String> getExcludeList(){
        init();
        if(prefs != null){
            return getStringSet(Keys.EXCLUDE_LIST);
        }
        return new HashSet<String>();
    }

    public static Set<String> getSeriesList(){
        init();
        if(prefs != null){
            return getStringSet(Keys.SERIES_LIST);
        }
        return new HashSet<String>();
    }

    public static Set<String> getPopulatedList(){
        init();
        if(prefs != null){
            return getStringSet(Keys.POPULATED_LIST);
        }
        return null;
    }

    public static void saveUpdateDay(int day){
        init();
        if(prefs != null){
            prefs.edit().putInt(Keys.UPDATE_DAY, day).apply();
        }
    }

    public static int getUpdateDay(int day){
        init();
        if(prefs != null){
            return prefs.getInt(Keys.UPDATE_DAY, Calendar.TUESDAY);
        }
        return Calendar.TUESDAY;
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

    public static void saveAllSummaries(ArrayList<RottenTomatoesSummary> allSummaries){
        init();
        if(prefs != null){
            final int size = allSummaries.size();
            Set<String> stringSet = new HashSet<String>(size);
            Gson gson = new Gson();
            for(int i = 0; i < size; i ++){
                stringSet.add(gson.toJson(allSummaries.get(i)));
            }
            putStringSet(Keys.ALL_SUMMARIES, stringSet);
        }
    }

    public static ArrayList<RottenTomatoesSummary> getAllSummaries(){
        init();
        if(prefs != null){
            Set<String> stringSet = getStringSet(Keys.ALL_SUMMARIES);
            ArrayList<RottenTomatoesSummary> summaries = new ArrayList<RottenTomatoesSummary>(stringSet.size());
            Gson gson = new Gson();
            for(String s : stringSet){
                if(!s.equals("null"))
                    summaries.add(gson.fromJson(s, RottenTomatoesSummary.class));
            }
            return summaries;
        }
        return null;
    }

    public static void saveCurrentSummaries(ArrayList<RottenTomatoesSummary> currentSummaries){
        init();
        if(prefs != null){
            final int size = currentSummaries.size();
            Set<String> stringSet = new HashSet<String>(size);
            Gson gson = new Gson();
            for(int i = 0; i < size; i ++){
                stringSet.add(gson.toJson(currentSummaries.get(i)));
            }
            putStringSet(Keys.CURRENT_SUMMARIES, stringSet);
        }
    }

    public static ArrayList<RottenTomatoesSummary> getCurrentSummaries(){
        init();
        if(prefs != null){
            Set<String> stringSet = getStringSet(Keys.CURRENT_SUMMARIES);
            ArrayList<RottenTomatoesSummary> summaries = new ArrayList<RottenTomatoesSummary>(stringSet.size());
            Gson gson = new Gson();
            for(String s : stringSet){
                if(!s.equals("null"))
                    summaries.add(gson.fromJson(s, RottenTomatoesSummary.class));
            }
            return summaries;
        }
        return null;
    }

    public static Map<String, RottenTomatoesSummary> getAllSummariesMap(){
        init();
        if(prefs != null){
            Set<String> stringSet = getStringSet(Keys.ALL_SUMMARIES);
            Map<String, RottenTomatoesSummary> summaries = new HashMap<String, RottenTomatoesSummary>(stringSet.size());
            Gson gson = new Gson();
            for(String s : stringSet){
                if(!s.equals("null")){
                    RottenTomatoesSummary summary = gson.fromJson(s, RottenTomatoesSummary.class);
                    summaries.put(summary.getTitle(), summary);
                }
            }
            return summaries;
        }
        return null;
    }

    /*public static void setFilter(Filter filter){
        init();
        if(prefs != null){
            prefs.edit().putInt(Keys.FILTER, filter.id).apply();
        }
    }

    public static Filter getFilter(){
        init();
        if(prefs != null){
            return Filter.fromId(prefs.getInt(Keys.FILTER, Filter.NONE.id));
        }
        return Filter.NONE;
    }

    public static void setSort(Sort sort){
        init();
        if(prefs != null){
            prefs.edit().putInt(Keys.SORT, sort.id).apply();
        }
    }

    public static Sort getSort(){
        init();
        if(prefs != null){
            return Sort.fromId(prefs.getInt(Keys.SORT, Sort.ALPHABETICAL.id));
        }
        return Sort.ALPHABETICAL;
    }*/

    public class Keys{
        public static final String IGNORE_LIST = "IGNORE_LIST";
        public static final String EXCLUDE_LIST = "EXCLUDE_LIST";
        public static final String SERIES_LIST = "SERIES_LIST";
        public static final String POPULATED_LIST = "POPULATED_LIST";
        public static final String TMN_URL = "tmn-url";
        public static final String UPDATE_DAY = "update-day";
        private static final String LAST_SAVE_DATE = "LAST_SAVE_DATE";
        public static final String ALL_SUMMARIES = "ALL_SUMMARIES";
        public static final String CURRENT_SUMMARIES = "CURRENT_SUMMARIES";

        public static final String FILTER = "FILTER";
        public static final String SORT = "SORT";
    }

	
}
