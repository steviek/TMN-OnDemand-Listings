package com.sixbynine.movieoracle.model;

import android.os.Parcel;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;

public final class TitleFilter implements Filter {

    private String title;

    public TitleFilter(String title) {
        this.title = title;
    }

    @Override
    public boolean showFilter() {
        return true;
    }

    @Override
    public boolean showSpinner() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
    }

    @Override
    public boolean apply(RTMovieQueryMovieSummary summary) {
        return title == null || summary.getTitle().toUpperCase().contains(title.toUpperCase());
    }

    public static final Creator<TitleFilter> CREATOR = new Creator<TitleFilter>() {
        @Override
        public TitleFilter createFromParcel(Parcel source) {
            return new TitleFilter(source.readString());
        }

        @Override
        public TitleFilter[] newArray(int size) {
            return new TitleFilter[size];
        }
    };
}
