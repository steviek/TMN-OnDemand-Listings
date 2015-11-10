package com.sixbynine.movieoracle.datamodel.rottentomatoes;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryResult;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Retrofit interface for the Rotten Tomatoes API.
 */
public interface RottenTomatoesService {

    @GET("/movies.json")
    RTMovieQueryResult searchMovies(@Query("q") String query, @Query("apikey") String apikey);

}
