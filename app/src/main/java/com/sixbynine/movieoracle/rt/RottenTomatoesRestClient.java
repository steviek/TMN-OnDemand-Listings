package com.sixbynine.movieoracle.rt;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.object.RottenTomatoesSummaryContainer;
import com.sixbynine.movieoracle.util.Keys;
import com.sixbynine.movieoracle.util.Logger;
import com.sixbynine.movieoracle.util.Prefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit.RestAdapter;

/**
 * Created by steviekideckel on 10/30/14.
 */
public class RottenTomatoesRestClient {

    public interface Callback {
        public void onListingsLoaded(ArrayList<RottenTomatoesSummary> movies);
        public void onListingsFailure();
        public void onListingLoaded(String title, int soFar, int total);
    }

    public static void getMovies(Callback callback, Catalogue titles) {
        new LoadListingsTask(callback, titles).execute();
    }

    private static class LoadListingsTask extends AsyncTask<Void, Void, ArrayList<RottenTomatoesSummary>> {
        private Callback mCallback;
        private Catalogue mTitles;

        public LoadListingsTask(Callback callback, Catalogue titles) {
            mCallback = callback;
            mTitles = titles;
        }

        @Override
        protected ArrayList<RottenTomatoesSummary> doInBackground(Void... args) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://api.rottentomatoes.com/api/public/v1.0")
                    .build();

            RottenTomatoesService service = restAdapter.create(RottenTomatoesService.class);

            final Set<String> ignoreList = Prefs.getMutableIgnoreList();

            final ArrayList<RottenTomatoesSummary> previouslyLoadedList = Prefs.getAllSummaries();
            final Map<String, RottenTomatoesSummary> previouslyLoaded = Prefs.getAllSummariesMap();

            final ArrayList<RottenTomatoesSummary> summaryList = new ArrayList<RottenTomatoesSummary>();
            int size = mTitles.size();
            for (int i = 0; i < size; i ++) {
                if(isCancelled()){
                    break;
                }
                final String title = mTitles.get(i).getTitle();
                if(ignoreList.contains(title)){
                    continue;
                }

                RottenTomatoesSummary summary = null;
                if(previouslyLoaded.get(title) == null) {
                    try {
                        String url = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?" +
                                "apikey=" + Keys.RT_API_KEY +
                                "&q=" + URLEncoder.encode(title, "utf-8") + "&page_limit=3";
                        URL website = new URL(url);
                        URLConnection connection = website.openConnection();
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()));

                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null)
                            response.append(inputLine);

                        in.close();
                        String json_data = response.toString();
                        Gson gson = new Gson();
                        RottenTomatoesSummaryContainer summaryContainer = gson.fromJson(json_data, RottenTomatoesSummaryContainer.class);
                        summary = pickBest(title, summaryContainer);
                        previouslyLoadedList.add(summary);
                    } catch (UnsupportedEncodingException e) {
                        Logger.e(e.toString());
                    } catch (IOException e) {
                        Logger.e(e.toString());
                    }
                }else{
                    summary = previouslyLoaded.get(title);
                }

                if(summary == null){
                    ignoreList.add(title);
                    Logger.w("Couldn't do gson for " + title);
                }else{
                    Logger.d("Could do gson for " + title);
                    summaryList.add(summary);
                }
                mCallback.onListingLoaded(title, i + 1, size);
            }
            Prefs.saveIgnoreList(ignoreList);
            Prefs.saveAllSummaries(previouslyLoadedList);
            return summaryList;
        }

        @Override
        protected void onPostExecute(ArrayList<RottenTomatoesSummary> rottenTomatoesSummaries) {
            super.onPostExecute(rottenTomatoesSummaries);
            if(rottenTomatoesSummaries == null){
                mCallback.onListingsFailure();
            }else{
                mCallback.onListingsLoaded(rottenTomatoesSummaries);
            }
        }

        private RottenTomatoesSummary pickBest(final String title, RottenTomatoesSummaryContainer container) {
            final List<RottenTomatoesSummary> movies = container.getMovies();
            if (movies == null || movies.size() == 0) {
                return null;
            }else{
                Collections.sort(movies, new Comparator<RottenTomatoesSummary>() {
                    @Override
                    public int compare(RottenTomatoesSummary lhs, RottenTomatoesSummary rhs) {
                        boolean e1 = title.equals(lhs.getTitle());
                        boolean e2 = title.equals(rhs.getTitle());
                        if(e1 == e2){
                            return rhs.getYearAsInt() - lhs.getYearAsInt(); //newer movies come first
                        }else if(e1){
                            return -1;
                        }else{
                            return 1;
                        }
                    }
                });
                return movies.get(0);
            }
        }


    }
}
