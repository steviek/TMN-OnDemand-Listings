package com.sixbynine.movieoracle.media;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie extends Media implements Parcelable{
	
	
	public Movie(String title) {
		super(title);
	}
	
	protected Movie(Parcel in){
		super(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
	}
	
	public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in); 
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

	

}
