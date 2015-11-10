package com.sixbynine.movieoracle.model;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;

import java.util.Comparator;

public enum Sorting implements Comparator<RTMovieQueryMovieSummary> {

    ALPHABETICAL("A-Z", new Comparator<RTMovieQueryMovieSummary>() {
        @Override
        public int compare(RTMovieQueryMovieSummary lhs, RTMovieQueryMovieSummary rhs) {
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    }),
    RATING("Rating", new Comparator<RTMovieQueryMovieSummary>() {
        @Override
        public int compare(RTMovieQueryMovieSummary lhs, RTMovieQueryMovieSummary rhs) {
            return rhs.getRatings().getAverage() - lhs.getRatings().getAverage();
        }
    }),
    YEAR("Year", new Comparator<RTMovieQueryMovieSummary>() {
        @Override
        public int compare(RTMovieQueryMovieSummary lhs, RTMovieQueryMovieSummary rhs) {
            return rhs.getYear() - lhs.getYear();
        }
    });

    private final String name;
    private final Comparator<RTMovieQueryMovieSummary> comparator;

    Sorting(String name, Comparator<RTMovieQueryMovieSummary> comparator) {
        this.name = name;
        this.comparator = comparator;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compare(RTMovieQueryMovieSummary lhs, RTMovieQueryMovieSummary rhs) {
        return comparator.compare(lhs, rhs);
    }
}
