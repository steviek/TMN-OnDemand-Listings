package com.sixbynine.movieoracle.media;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Parcel;
import android.os.Parcelable;



public class Series extends Media implements Parcelable{
	/**
	 * 
	 */
	ArrayList<Season> seasons;
	

	public Series(String title) {
		super(title);
		seasons = new ArrayList<Season>();
	}
	
	protected Series(Parcel in){
		super(in);
		seasons = in.readArrayList(Season.class.getClassLoader());
	}
	
	
	public void addEpisode(int seasonNum, String episode){
		Season season = getSeason(seasonNum);
		if(season == null){
			season = new Season(title, seasonNum);
			seasons.add(season);
		}
		season.addEpisode(episode);
	}
	
	public ArrayList<Season> getSeasons(){
		return seasons;
	}
	
	public void setSeasons(ArrayList<Season> seasons){
		this.seasons = seasons;
	}
	
	/*public void addSeasonsFrom(Series other){
		for(Season season : other.getSeasons()){
			seasons.add(season);
		}
	}*/
	
	public String getEpisode(int season, int episode){
		return seasons.get(season-1).getEpisode(episode-1);
	}
	
	public String getEpisode(int n){
		int season = 0;
		while(n >= seasons.get(season).numEpisodes()){
			n -= seasons.get(season).numEpisodes();
			season ++;
		}
		return seasons.get(season).getEpisode(n);
	}
	
	private Season getSeason(int id){
		
		for(Season season : seasons){
			if(season.getId() == id) return season;
		}
		return null;
	}
	
	public String getEpisodesString(){
		String result = "";
		if(seasons == null || seasons.size() == 0) return result;
		for(Season season : seasons){
			for(String episode : season.getEpisodes()){
				result += episode + ";";
			}
		}
		return result.substring(0, result.length()-1);
	}
	
	

	public static Catalogue processSeries(Map<String, List<String>> map){
			if(map == null){
				return new Catalogue();
			}
			 Catalogue result = new Catalogue();
			 Iterator<Entry<String, List<String>>> it = map.entrySet().iterator();
			 while(it.hasNext()){
				 Entry<String, List<String>> serie = it.next();
				 String seriesName = serie.getKey();
				 List<String> episodes = serie.getValue();
				 int seasonNumber = digitIn(seriesName);
				 if(seasonNumber != -1){
					 seriesName = seriesName.replaceAll(" " + seasonNumber, "");
				 }else{
					 seasonNumber = 1;
				 }
				 Series series = (Series) result.getByTitle(seriesName);
				 if(series == null){
					 series = new Series(seriesName);
					 result.add(series);
				 }
				 for(String episode : episodes){
					 series.addEpisode(seasonNumber, episode);
				 }
				 
			 }
			 return result;	 
		 
	}
	
	 private static int digitIn(String s){
		 for(char c : s.toCharArray()){
			 if(c >= '1' && c <= '9'){
				 return Integer.parseInt(c + "");
			 }
		 }
		 return -1;
	 }
	 
	 @Override
	public String toString(){
		 String result = super.toString() + ":";
		 for(Season season : seasons){
			 result += "\n" + season.toString();
		 }
		 return result;
	 }
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeList(seasons);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Series> CREATOR = new Parcelable.Creator<Series>() {
        public Series createFromParcel(Parcel in) {
            return new Series(in); 
        }

        public Series[] newArray(int size) {
            return new Series[size];
        }
    };
}
