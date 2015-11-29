package com.sixbynine.movieoracle.datamodel.tmn;

import java.util.Set;

public final class TMNResources {

    private Set<String> movies;
    private Set<String> series;

    public TMNResources(Set<String> movies, Set<String> series) {
        this.movies = movies;
        this.series = series;
    }

    public Set<String> getMovies() {
        return movies;
    }

    public Set<String> getSeries() {
        return series;
    }
}
