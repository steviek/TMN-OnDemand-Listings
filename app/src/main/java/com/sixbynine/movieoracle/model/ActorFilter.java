package com.sixbynine.movieoracle.model;

import android.os.Parcel;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryMovieSummaryWithTitle;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryCastMember;

final class ActorFilter implements Filter {

    private String name;

    ActorFilter(String name) {
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
    public Type getType() {
        return Type.ACTOR;
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
    public boolean apply(RTMovieQueryMovieSummaryWithTitle summary) {
        for(int i = summary.getSummary().getCast().size()-1; i >= 0; i --){
            RTMovieQueryCastMember actor = summary.getSummary().getCast().get(i);
            if(name.equals(Filter.SELECT_ACTOR) || actor.getName().equals(name)){
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
