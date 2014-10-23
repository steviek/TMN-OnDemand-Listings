package com.sixbynine.movieoracle.media;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Rating implements Comparable<Rating>, Parcelable{
	private String label;
	private double numericRating;
	private String stringRating;
	
	public static final String CRITIC = "Critics";
	public static final String AUDIENCE = "Audience";
	
	
	
	public Rating() {
		label = "";
		numericRating = 0.0;
		
		stringRating = "";
	}
	
	public Rating(String label, double rating, String stringRating){
		this.label = label;
		this.numericRating = rating;
		this.stringRating = stringRating;
	}
	
	protected Rating(Parcel in){
		label = in.readString();
		numericRating = in.readDouble();
		stringRating = in.readString();
	}

	
	public boolean isCriticRating(){
		return this.label.equals(CRITIC);
	}
	
	public boolean isAudienceRating(){
		return this.label.equals(AUDIENCE);
	}


	public String getLabel() {
		return label;
	}



	public void setLabel(String label) {
		this.label = label;
	}



	public double getRating() {
		return numericRating;
	}



	public void setRating(double rating) {
		this.numericRating = rating;
	}



	public String getStringRating() {
		return stringRating;
	}



	public void setStringRating(String stringRating) {
		this.stringRating = stringRating;
	}
	
	@Override
	public String toString(){
		return label + ": " + (stringRating == null || stringRating.equals("") ? "" : stringRating + " - ") + numericRating;
	}

	@Override
	public int compareTo(Rating arg0) {
		return Double.valueOf(numericRating).compareTo(Double.valueOf(arg0.getRating()));
	}
	
	public static Rating parseRating(String s){
		Rating r = new Rating();
		if(s.indexOf(":") < 0) return r;
		
		String label = s.substring(0, s.indexOf(":"));
		r.setLabel(label);
		
		s = s.substring(s.indexOf(":") + 1);
		if(s.indexOf("-") >= 0){
			String stringRating = s.substring(0, s.lastIndexOf("-"));
			r.setStringRating(stringRating);
			s = s.substring(s.lastIndexOf("-") + 1);
		}
			
		double numericRating = Double.parseDouble(s);
		r.setRating(numericRating);
		return r;
	}
	
	public static String getString(List<Rating> ratings){
		String result = "";
		if(ratings == null || ratings.size() == 0) return result;
		for(Rating rating : ratings){
			result += rating.toString() + ",";
		}
		return result.substring(0, result.length()-1);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(label);
		dest.writeDouble(numericRating);
		dest.writeString(stringRating);
	}
	
	public static final Parcelable.Creator<Rating> CREATOR = new Parcelable.Creator<Rating>() {
        public Rating createFromParcel(Parcel in) {
            return new Rating(in); 
        }

        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };

}
