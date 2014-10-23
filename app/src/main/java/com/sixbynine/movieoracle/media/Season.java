package com.sixbynine.movieoracle.media;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Season extends Series implements Parcelable{
	private int id;
	private List<String> episodes;
	
	public Season(String series, int id){
		super(series);
		this.id = id;
		episodes = new ArrayList<String>();
	}
	
	public Season(String series, int id, List<String> episodes){
		super(series);
		this.id = id;
		this.episodes = episodes;
		
	}
	
	protected Season(Parcel in){
		super(in);
		id = in.readInt();
		episodes = in.createStringArrayList();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<String> getEpisodes() {
		return episodes;
	}
	public void setEpisodes(ArrayList<String> episodes) {
		this.episodes = episodes;
	}
	
	@Override
	public String getEpisode(int index){
		if(index < episodes.size()){
			return episodes.get(index);
		}
		else return null;
	}
	public void addEpisode(String episode){
		if(!episodes.contains(episode))
			episodes.add(episode);
	}
	
	public int numEpisodes(){
		return episodes.size();
	}
	
	@Override
	public String toString(){
		String result = title + " Season " + id + ":";
			for(String episode : episodes){
				result += "\n\t" + episode;
			}
		return result;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(id);
		dest.writeStringList(episodes);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Season> CREATOR = new Parcelable.Creator<Season>() {
        public Season createFromParcel(Parcel in) {
            return new Season(in); 
        }

        public Season[] newArray(int size) {
            return new Season[size];
        }
    };
	
}
