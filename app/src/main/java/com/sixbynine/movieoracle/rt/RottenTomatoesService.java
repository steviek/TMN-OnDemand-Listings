package com.sixbynine.movieoracle.rt;

import com.sixbynine.movieoracle.object.RottenTomatoesSummaryContainer;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by steviekideckel on 10/30/14.
 */
public interface RottenTomatoesService {
    @GET("/movies.json")
    RottenTomatoesSummaryContainer listMovies(@QueryMap Map<String, String> options);
}
