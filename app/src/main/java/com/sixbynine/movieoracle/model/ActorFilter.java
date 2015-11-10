package com.sixbynine.movieoracle.model;

import android.os.Parcel;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryCastMember;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;

public final class ActorFilter implements Filter {

    private String name;

    public ActorFilter(String name) {
        this.name = name;
    }

    @Override
    public boolean showFilter() {
        return true;
    }

    @Override
    public boolean showSpinner() {
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public boolean apply(RTMovieQueryMovieSummary summary) {
        for(int i = summary.getCast().size()-1; i >= 0; i --){
            RTMovieQueryCastMember actor = summary.getCast().get(i);
            if(actor.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public static final Creator<ActorFilter> CREATOR = new Creator<ActorFilter>() {
        @Override
        public ActorFilter createFromParcel(Parcel source) {
            return new ActorFilter(source.readString());
        }

        @Override
        public ActorFilter[] newArray(int size) {
            return new ActorFilter[size];
        }
    };
}
