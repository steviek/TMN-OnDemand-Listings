package com.sixbynine.movieoracle.model;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryMovieSummaryWithTitle;

import java.util.Comparator;

public enum Sorting implements Comparator<RTMovieQueryMovieSummaryWithTitle> {

    ALPHABETICAL("A-Z", new Comparator<RTMovieQueryMovieSummaryWithTitle>() {
        @Override
        public int compare(RTMovieQueryMovieSummaryWithTitle lhs, RTMovieQueryMovieSummaryWithTitle rhs) {
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    }),
    RATING("Rating", new Comparator<RTMovieQueryMovieSummaryWithTitle>() {
        @Override
        public int compare(RTMovieQueryMovieSummaryWithTitle lhs, RTMovieQueryMovieSummaryWithTitle rhs) {
            return rhs.getSummary().getRatings().getAverage() - lhs.getSummary().getRatings().getAverage();
        }
    }),
    YEAR("Year", new Comparator<RTMovieQueryMovieSummaryWithTitle>() {
        @Override
        public int compare(RTMovieQueryMovieSummaryWithTitle lhs, RTMovieQueryMovieSummaryWithTitle rhs) {
            return rhs.getSummary().getYear() - lhs.getSummary().getYear();
        }
    });

    private final String name;
    private final Comparator<RTMovieQueryMovieSummaryWithTitle> comparator;

    Sorting(String name, Comparator<RTMovieQueryMovieSummaryWithTitle> comparator) {
        this.name = name;
        this.comparator = comparator;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compare(RTMovieQueryMovieSummaryWithTitle lhs, RTMovieQueryMovieSummaryWithTitle rhs) {
        return comparator.compare(lhs, rhs);
    }

    public static Sorting fromName(String name) {
        for (Sorting sorting : values()) {
            if (sorting.name.equals(name)) {
                return sorting;
            }
        }
        throw new IllegalArgumentException("Unexpected name: " + name);
    }
}
