package com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public final class RTMovieQueryResult {

    private final int total;
    private final List<RTMovieQueryMovieSummary> movies;

    @JsonCreator
    public RTMovieQueryResult(
            @JsonProperty("total") int total,
            @JsonProperty("movies") List<RTMovieQueryMovieSummary> movies) {
        this.total = total;
        this.movies = movies;
    }

    public int getTotal() {
        return total;
    }

    public List<RTMovieQueryMovieSummary> getMovies() {
        return movies;
    }

    public RTMovieQueryMovieSummary getBestMatch(String title) {
        List<RTMovieQueryMovieSummary> exactMatches = new ArrayList<>();
        for (RTMovieQueryMovieSummary summary : movies) {
            if (summary.getTitle().equals(title)) {
                exactMatches.add(summary);
            }
        }

        if (exactMatches.isEmpty()) {
            if (movies.isEmpty()) {
                return null;
            } else {
                return movies.get(0);
            }
        } else {
            RTMovieQueryMovieSummary newest = null;
            for (RTMovieQueryMovieSummary summary : exactMatches) {
                if (newest == null || summary.getYear() > newest.getYear()) {
                    newest = summary;
                }
            }
            return newest;
        }
    }
}
