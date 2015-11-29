package com.sixbynine.movieoracle.events;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryResultMap;

public final class RTMovieQueryResultMapLoadedEvent {

    private final RTMovieQueryResultMap queryResultMap;

    public RTMovieQueryResultMapLoadedEvent(RTMovieQueryResultMap queryResultMap) {
        this.queryResultMap = queryResultMap;
    }

    public RTMovieQueryResultMap getQueryResultMap() {
        return queryResultMap;
    }
}
