package com.sixbynine.movieoracle.datamodel.rottentomatoes;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryResult;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Retrofit interface for the Rotten Tomatoes API.
 */
public interface RottenTomatoesService {

    @GET("/api/public/v1.0/movies.json")
    Call<RTMovieQueryResult> searchMovies(@Query("q") String query, @Query("apikey") String apikey);

}
