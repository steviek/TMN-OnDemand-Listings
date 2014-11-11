package com.sixbynine.movieoracle.manager;

import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.rt.RottenTomatoesRestClient;
import com.sixbynine.movieoracle.util.Prefs;

import java.util.ArrayList;

/**
 * Created by steviekideckel on 11/2/14.
 */
public class RottenTomatoesManager extends Manager{
    private static RottenTomatoesManager sInstance;
    private ArrayList<RottenTomatoesSummary> mSummaries;

    public static RottenTomatoesManager getInstance(){
        if(sInstance == null){
            synchronized (Manager.class){
                if(sInstance == null){
                    sInstance = new RottenTomatoesManager();
                }
            }
        }
        return sInstance;
    }

    public ArrayList<RottenTomatoesSummary> getSummaries(){
        return mSummaries;
    }

    public void loadListings(Catalogue catalogue){
        if(mSummaries != null){
            publish(UpdateEvent.RT_LISTINGS_LOADED, mSummaries);
        }else{
            RottenTomatoesRestClient.getMovies(new RottenTomatoesRestClient.Callback() {
                @Override
                public void onListingsLoaded(ArrayList<RottenTomatoesSummary> movies) {
                    mSummaries = movies;
                    Prefs.saveCurrentSummaries(movies);
                    publish(UpdateEvent.RT_LISTINGS_LOADED, movies);
                }

                @Override
                public void onListingsFailure() {
                    publish(UpdateEvent.RT_LISTINGS_FAILURE);
                }

                @Override
                public void onListingLoaded(String title, int soFar, int total) {
                    publish(UpdateEvent.RT_LISTING_LOADED, title, soFar, total);
                }
            }, catalogue);
        }
    }

}
