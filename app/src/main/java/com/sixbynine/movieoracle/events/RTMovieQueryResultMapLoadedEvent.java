package com.sixbynine.movieoracle.events;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryResult;

import java.util.Map;

public final class RTMovieQueryResultMapLoadedEvent {

    private final Map<String, RTMovieQueryResult> queryResultMap;

    public RTMovieQueryResultMapLoadedEvent(Map<String, RTMovieQueryResult> queryResultMap) {
        this.queryResultMap = queryResultMap;
    }

    public Map<String, RTMovieQueryResult> getQueryResultMap() {
        return queryResultMap;
    }
}
