package com.sixbynine.movieoracle.manager;

import android.graphics.Bitmap;

import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.rt.RottenTomatoesRestClient;
import com.sixbynine.movieoracle.util.BitmapDownloader;
import com.sixbynine.movieoracle.util.Prefs;

import java.util.ArrayList;

/**
 * Created by steviekideckel on 11/2/14.
 */
public class RottenTomatoesManager extends Manager{
    private static RottenTomatoesManager sInstance;
    private ArrayList<RottenTomatoesSummary> mSummaries;
    private BitmapDownloader<RottenTomatoesSummary> mPosterDownloader;

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

    public RottenTomatoesManager(){
        mPosterDownloader = new BitmapDownloader<RottenTomatoesSummary>() {
            @Override
            protected String getUrl(RottenTomatoesSummary object) {
                String urldisplay = object.getPosters().getDetailed();
                return urldisplay.replace("tmb","det");
            }

            @Override
            protected void onSuccess(RottenTomatoesSummary rottenTomatoesSummary, Bitmap bmp) {
                publish(UpdateEvent.POSTER_LOADED, rottenTomatoesSummary, bmp);
            }

            @Override
            protected void onFailure(RottenTomatoesSummary rottenTomatoesSummary) {
                publish(UpdateEvent.POSTER_FAILED_TO_LOAD, rottenTomatoesSummary);
            }
        };
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

    public void getPoster(RottenTomatoesSummary summary){
        mPosterDownloader.loadImage(summary);
    }


}
