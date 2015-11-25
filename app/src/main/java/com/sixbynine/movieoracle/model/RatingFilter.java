package com.sixbynine.movieoracle.model;

import android.os.Parcel;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryMovieSummaryWithTitle;

final class RatingFilter implements Filter {

    private final int rating;

    RatingFilter(String ratingType) {
        switch (ratingType) {
            case Filter.RATING_ALL:
                this.rating = 0;
                break;
            case Filter.RATING_FRESH:
                this.rating = 60;
                break;
            default:
                throw new IllegalArgumentException("Unexpected rating type: " + ratingType);
        }
    }

    private RatingFilter(int rating) {
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
    public Type getType() {
        return Type.RATING;
    }

    @Override
    public boolean apply(RTMovieQueryMovieSummaryWithTitle rtMovieQueryMovieSummary) {
        return rtMovieQueryMovieSummary.getSummary().getRatings().getAverage() >= rating;
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
