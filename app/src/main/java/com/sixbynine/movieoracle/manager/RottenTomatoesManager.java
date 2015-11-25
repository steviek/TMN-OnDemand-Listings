package com.sixbynine.movieoracle.manager;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;

import android.os.AsyncTask;
import android.os.Handler;

import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryResultMap;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.RottenTomatoesService;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryResult;
import com.sixbynine.movieoracle.datamodel.tmn.TMNResources;
import com.sixbynine.movieoracle.events.RTMovieQueryResultLoadedEvent;
import com.sixbynine.movieoracle.events.RTMovieQueryResultMapLoadedEvent;
import com.sixbynine.movieoracle.util.Keys;
import com.sixbynine.movieoracle.util.Logger;
import com.sixbynine.movieoracle.util.Prefs;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Set;

import retrofit.Call;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public final class RottenTomatoesManager {

    private static final RottenTomatoesService ROTTEN_TOMATOES = new Retrofit.Builder()
            .baseUrl("http://api.rottentomatoes.com")
            .addConverterFactory(JacksonConverterFactory.create(MyApplication.getInstance().getObjectMapper()))
            .build()
            .create(RottenTomatoesService.class);

    private static RTMovieQueryResultMap movieQueryResultMap;

    public static Optional<RTMovieQueryResultMap> search(TMNResources tmnResources) {
        if (movieQueryResultMap == null) {
            new SearchMoviesAsyncTask(tmnResources).execute();
        }
        return Optional.fromNullable(movieQueryResultMap);
    }

    private static class SearchMoviesAsyncTask extends AsyncTask<Void, Void, Void> {

        private final TMNResources resources;
        private final Handler handler;

        public SearchMoviesAsyncTask(TMNResources resources) {
            this.resources = resources;
            this.handler = new Handler();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Set<String> movies = resources.getMovies();

            movieQueryResultMap = new RTMovieQueryResultMap();
            RTMovieQueryResultMap oldResults = Prefs.getCurrentResults();
            for (Map.Entry<String, RTMovieQueryResult> entry : oldResults.entrySet()) {
                if (movies.contains(entry.getKey())) {
                    movieQueryResultMap.put(entry);
                }
            }

            int i = 1;
            int total = resources.getMovies().size();

            for (String m : movies) {
                addIfNecessary(m, i++, total);
            }

            Prefs.putCurrentTitles(movieQueryResultMap.keySet());

            return null;
        }

        private void addIfNecessary(final String name, final int num, final int total) {
            try {
                if (movieQueryResultMap.get(name) == null) {
                    Call<RTMovieQueryResult> call = ROTTEN_TOMATOES.searchMovies(name, Keys.RT_API_KEY);
                    Response<RTMovieQueryResult> response = call.execute();

                    if (response.isSuccess()) {
                        final RTMovieQueryResult queryResult = response.body();

                        if (queryResult.getTotal() == 0) {
                            Prefs.addTitleToIgnoreList(name);
                            Logger.e("No results for: " + name);
                        } else {
                            Prefs.putSummary(name, queryResult);
                            movieQueryResultMap.put(name, queryResult);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MyApplication.getInstance()
                                            .getBus()
                                            .post(new RTMovieQueryResultLoadedEvent(name, queryResult, num, total));
                                }
                            });
                        }
                    } else {
                        Prefs.addTitleToIgnoreList(name);
                        Logger.e("Failed call: " + name);
                    }
                }
            } catch (SocketTimeoutException e) {
                Logger.e("Timeout: " + name);
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }

        @Override
        protected void onPostExecute(Void queryResult) {
            MyApplication.getInstance().getBus().post(new RTMovieQueryResultMapLoadedEvent(movieQueryResultMap));
        }
    }
}
