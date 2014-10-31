package com.sixbynine.movieoracle.object;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by steviekideckel on 10/30/14.
 */
public class RottenTomatoesSummaryContainer implements Parcelable{
    private ArrayList<RottenTomatoesSummary> movies;

    public ArrayList<RottenTomatoesSummary> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<RottenTomatoesSummary> movies) {
        this.movies = movies;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle b = new Bundle();
        b.putParcelableArrayList("movies", movies);
        dest.writeBundle(b);
    }

    public static final Creator<RottenTomatoesSummaryContainer> CREATOR = new Creator<RottenTomatoesSummaryContainer>() {
        @Override
        public RottenTomatoesSummaryContainer createFromParcel(Parcel source) {
            RottenTomatoesSummaryContainer c = new RottenTomatoesSummaryContainer();
            Bundle b = source.readBundle();
            c.movies = b.getParcelableArrayList("movies");
            return c;
        }

        @Override
        public RottenTomatoesSummaryContainer[] newArray(int size) {
            return new RottenTomatoesSummaryContainer[size];
        }
    };
}
