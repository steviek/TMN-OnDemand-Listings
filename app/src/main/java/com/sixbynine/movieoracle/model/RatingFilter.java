package com.sixbynine.movieoracle.model;

import android.os.Parcel;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;

public final class RatingFilter implements Filter {

    private final int rating;

    public RatingFilter(int rating) {
        this.rating = rating;
    }

    @Override
    public boolean showSpinner() {
        return true;
    }

    @Override
    public boolean showFilter() {
        return true;
    }

    @Override
    public boolean apply(RTMovieQueryMovieSummary rtMovieQueryMovieSummary) {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rating);
    }

    public static final Creator<RatingFilter> CREATOR = new Creator<RatingFilter>() {
        @Override
        public RatingFilter createFromParcel(Parcel source) {
            return new RatingFilter(source.readInt());
        }

        @Override
        public RatingFilter[] newArray(int size) {
            return new RatingFilter[size];
        }
    };
}
