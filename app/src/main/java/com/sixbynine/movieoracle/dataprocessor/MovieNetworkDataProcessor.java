package com.sixbynine.movieoracle.dataprocessor;

import android.os.AsyncTask;
import android.util.Log;

import com.sixbynine.movieoracle.SplashActivityCallback;
import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.media.Series;
import com.sixbynine.movieoracle.util.Logger;
import com.sixbynine.movieoracle.util.Prefs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class MovieNetworkDataProcessor {
	private Catalogue catalogue;
	private List<String> movies;
	private TreeMap<String, List<String>> series;
	private boolean finishedLoading;
	private boolean loadError;
	private SplashActivityCallback cb;
	private boolean initialized;
	
	public MovieNetworkDataProcessor(SplashActivityCallback cb){
		this.cb = cb;
		initialized = false;
	}
	
	public Catalogue retrieveListings(){
		loadCatalogue();
		return catalogue.trimDuplicates();
	}
	
	private void loadCatalogue(){
		catalogue = new Catalogue();
		catalogue.addAll(Series.processSeries(series));
		catalogue.addAll(movies);
	}
	
	
	public List<String> getMovies(){
		if(movies == null) {
			Log.w(this.getClass().getName(), "Movies have not been loaded");
			return new ArrayList<String>();
		}
		return movies;
	}
	
	public TreeMap<String, List<String>> getSeries(){
		if(series == null) {
			Log.w(this.getClass().getName(), "Series have not been loaded");
			return new TreeMap<String, List<String>>();
		}
		return series;
	}
	
	public List<String> getAll(){
		List<String> result = new ArrayList<String>();
		result.addAll(movies);
		result.addAll(series.keySet());
		return result;
	}
	
	public void repopulate(){
		initialized = true;
		new DownloadListings().execute(Prefs.getTmnUrl() + "&date=" + getClosestDate());
	}
	
	public boolean isFinishedLoading(){
		return finishedLoading;
	}
	
	public boolean isInitialized(){
		return initialized;
	}
	
	public boolean loadedWithError(){
		return loadError;
	}
	
	
	public void populate(String url) throws Exception{
		movies = new ArrayList<String>();
		series = new TreeMap<String, List<String>>();
		
		String pageCode = DataProcessor.getHtml(url);
		Document doc = Jsoup.parse(pageCode);
		Elements shows = doc.select("td");
		for(Element show : shows){
			String title = show.text();

			if(title.contains("Making Of") || title.contains("Recap Show")) continue;

            boolean isSeries = title.indexOf("Ep.") > 0;
            Set<String> seriesSet = Prefs.getSeriesList();
            if(seriesSet != null){
                isSeries = isSeries || seriesSet.contains(title);
            }

            if(isSeries){
					String seriesName= title.substring(0, title.indexOf("Ep.")-2);
					if(!series.containsKey(seriesName)) series.put(seriesName, new ArrayList<String>());
					
					if(!series.get(seriesName).contains(title)) series.get(seriesName).add(title);
					if(movies.contains(seriesName)) movies.remove(title);
					Logger.i("Added " + title + " to series");
            }else{
                boolean exclude = false;
                Set<String> excludeList = Prefs.getExcludeList();
                if(excludeList != null) {
                    for (String exclusion : excludeList) {
                        if (!"".equals(exclusion) && title.startsWith(exclusion)) {
                            exclude = true;
                            break;
                        }
                    }
                }
                Set<String> ignoreList = Prefs.getIgnoreList();
                if(!exclude && ignoreList != null) {
                    for (String ignore : ignoreList) {
                        if (!"".equals(ignore) && title.startsWith(ignore)) {
                            exclude = true;
                            break;
                        }
                    }
                }
                if(exclude) continue;
                Logger.i("Added " + title + " to movies");
                movies.add(title);
            }
		}
		Logger.i("Finished loading data.");

	 }
	
	
	private class DownloadListings extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			finishedLoading = false;
			loadError = false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			finishedLoading = true;
			loadError = result;
			cb.notifyChange(SplashActivityCallback.MOVIE_NETWORK_INTERNET_FINISHED);
		}

		@Override
		protected Boolean doInBackground(String... urls) {
			try{
				populate(urls[0]);
				return true;
			}catch(Exception e){
				Logger.e(e.getMessage());
				return false;
			}	
		}
		
	}
	
	private String getClosestDate(){
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		if(c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
			return year + "-" + (month + 1) + "-" + day;
		}else if(c.get(Calendar.DAY_OF_WEEK) >= Calendar.TUESDAY){
			int[] date = getDateMinusDays(year, month, day, c.get(Calendar.DAY_OF_WEEK) - Calendar.TUESDAY);
			if(date != null){
				return date[0] + "-" + (date[1] + 1) + "-" + date[2];
			}else{
				return "";
			}
		}else{
			int[] date = getDateMinusDays(year, month, day, 7 + c.get(Calendar.DAY_OF_WEEK) - Calendar.TUESDAY);
			if(date != null){
				return date[0] + "-" + (date[1] + 1) + "-" + date[2];
			}else{
				return "";
			}
		}
	}
	
	public static int[] getDateMinusDays(int year, int month, int day, int subtract){
		if(subtract == 0){
			return new int[]{year, month, day};
		}else{
			if(day == 1){
				switch(month){
				case Calendar.JANUARY:
					return getDateMinusDays(year - 1, Calendar.DECEMBER, 31, subtract - 1);
				case Calendar.FEBRUARY:
					return getDateMinusDays(year, Calendar.JANUARY, 31, subtract - 1);
				case Calendar.MARCH:
					if(year % 4 == 0){
						return getDateMinusDays(year, Calendar.FEBRUARY, 29, subtract - 1);
					}else{
						return getDateMinusDays(year, Calendar.FEBRUARY, 28, subtract - 1);
					}
				case Calendar.APRIL:
					return getDateMinusDays(year, Calendar.MARCH, 31, subtract - 1);
				case Calendar.MAY:
					return getDateMinusDays(year, Calendar.APRIL, 30, subtract - 1);
				case Calendar.JUNE:
					return getDateMinusDays(year, Calendar.MAY, 31, subtract - 1);
				case Calendar.JULY:
					return getDateMinusDays(year, Calendar.JUNE, 30, subtract - 1);
				case Calendar.AUGUST:
					return getDateMinusDays(year, Calendar.JULY, 31, subtract - 1);
				case Calendar.SEPTEMBER:
					return getDateMinusDays(year, Calendar.AUGUST, 31, subtract - 1);
				case Calendar.OCTOBER:
					return getDateMinusDays(year, Calendar.SEPTEMBER, 30, subtract - 1);
				case Calendar.NOVEMBER:
					return getDateMinusDays(year, Calendar.OCTOBER, 31, subtract - 1);
				case Calendar.DECEMBER:
					return getDateMinusDays(year, Calendar.NOVEMBER, 30, subtract - 1);
				default:
					return null;
				}
			}else{
				return getDateMinusDays(year, month, day-1, subtract - 1);
			}
				
		}
	}
	
	
}
