package com.sixbynine.movieoracle.events;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryResult;

public final class RTMovieQueryResultLoadedEvent {

    private String title;
    private RTMovieQueryResult queryResult;
    private int index;
    private int total;

    public RTMovieQueryResultLoadedEvent(String title, RTMovieQueryResult queryResult, int index, int total) {
        this.title = title;
        this.queryResult = queryResult;
        this.index = index;
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public RTMovieQueryResult getQueryResult() {
        return queryResult;
    }

    public int getIndex() {
        return index;
    }

    public int getTotal() {
        return total;
    }
}
