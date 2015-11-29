package com.sixbynine.movieoracle.datamodel.rottentomatoes;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;

public class RTMovieQueryMovieSummaryWithTitle {

    private final RTMovieQueryMovieSummary summary;
    private final String title;

    public RTMovieQueryMovieSummaryWithTitle(RTMovieQueryMovieSummary summary, String title) {
        this.summary = summary;
        this.title = title;
    }

    public RTMovieQueryMovieSummary getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }
}
