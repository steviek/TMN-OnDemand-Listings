package com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery;

import com.google.common.base.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryMovieSummaryWithTitle;
import com.sixbynine.movieoracle.util.Logger;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    private Optional<RTMovieQueryMovieSummaryWithTitle> bestMatch = null;

    public Optional<RTMovieQueryMovieSummaryWithTitle> getBestMatch(String title) {
        if (bestMatch != null) {
            return bestMatch;
        }

        List<RTMovieQueryMovieSummary> exactMatches = new ArrayList<>();
        for (RTMovieQueryMovieSummary summary : movies) {
            if (summary.getTitle().equals(title)) {
                exactMatches.add(summary);
            }
        }

        if (exactMatches.isEmpty()) {
            if (movies.isEmpty()) {
                Logger.w("returning absent best match for " + title);
                bestMatch = Optional.absent();
            } else {
                bestMatch = Optional.of(new RTMovieQueryMovieSummaryWithTitle(movies.get(0), title));
            }
        } else {
            RTMovieQueryMovieSummary newest = null;
            for (RTMovieQueryMovieSummary summary : exactMatches) {
                if (newest == null || summary.getYear() > newest.getYear()) {
                    newest = summary;
                }
            }
            bestMatch = Optional.of(new RTMovieQueryMovieSummaryWithTitle(newest, title));
        }
        return bestMatch;
    }
}
