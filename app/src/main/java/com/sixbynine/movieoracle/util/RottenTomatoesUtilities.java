package com.sixbynine.movieoracle.util;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryPosters;

public class RottenTomatoesUtilities {

    private RottenTomatoesUtilities() {}

    public static String getPosterUrl(RTMovieQueryPosters posters) {
        return posters.getOriginal();
    }
}
