package com.sixbynine.movieoracle.model;

import android.os.Parcel;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryMovieSummaryWithTitle;

final class NoFilter implements Filter {

    @Override
    public boolean showFilter() {
        return false;
    }

    @Override
    public boolean showSpinner() {
        return false;
    }

    @Override
    public Type getType() {
        return Type.NONE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public boolean apply(RTMovieQueryMovieSummaryWithTitle summary) {
        return true;
    }

    public static final Creator<NoFilter> CREATOR = new Creator<NoFilter>() {
        @Override
        public NoFilter createFromParcel(Parcel source) {
            return new NoFilter();
        }

        @Override
        public NoFilter[] newArray(int size) {
            return new NoFilter[size];
        }
    };
}
