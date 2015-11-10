package com.sixbynine.movieoracle.manager;

import android.os.AsyncTask;

import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.RottenTomatoesService;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryResult;
import com.sixbynine.movieoracle.datamodel.tmn.TMNResources;
import com.sixbynine.movieoracle.events.RTMovieQueryResultLoadedEvent;
import com.sixbynine.movieoracle.events.RTMovieQueryResultMapLoadedEvent;
import com.sixbynine.movieoracle.util.Keys;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import retrofit.Retrofit;

public final class RottenTomatoesManager {

    private static final RottenTomatoesService ROTTEN_TOMATOES = new Retrofit.Builder()
            .baseUrl("http://api.rottentomatoes.com/api/public/v1.0")
            .build()
            .create(RottenTomatoesService.class);

    private static Map<String, RTMovieQueryResult> movieQueryResultMap;

    public static Map<String, RTMovieQueryResult> search(TMNResources tmnResources) {
        if (movieQueryResultMap == null) {
            new SearchMoviesAsyncTask(tmnResources).execute();
        }
        return movieQueryResultMap;
    }

    private static class SearchMoviesAsyncTask extends AsyncTask<Void, Void, Void> {

        private final TMNResources resources;

        public SearchMoviesAsyncTask(TMNResources resources) {
            this.resources = resources;
        }

        @Override
        protected Void doInBackground(Void... params) {
            movieQueryResultMap = new HashMap<>();

            Set<String> movies = resources.getMovies();
            Set<String> series = resources.getSeries();

            int i = 1;
            int total = resources.getMovies().size() + resources.getSeries().size();

            for (String m : movies) {
                addIfNecessary(m, i++, total);
            }

            for (String s : series) {
                i++;
                addIfNecessary(s, i++, total);
            }

            return null;
        }

        private static void addIfNecessary(String name, int num, int total) {
            if (movieQueryResultMap.get(name) == null) {
                RTMovieQueryResult queryResult = ROTTEN_TOMATOES.searchMovies(name, Keys.RT_API_KEY);
                movieQueryResultMap.put(name, queryResult);
                MyApplication.getInstance()
                        .getBus()
                        .post(new RTMovieQueryResultLoadedEvent(name, queryResult, num, total));
            }
        }

        @Override
        protected void onPostExecute(Void queryResult) {
            MyApplication.getInstance().getBus().post(new RTMovieQueryResultMapLoadedEvent(movieQueryResultMap));
        }
    }
}
