package com.sixbynine.movieoracle.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.SplashActivityCallback;
import com.sixbynine.movieoracle.dataprocessor.MovieNetworkDataProcessor;
import com.sixbynine.movieoracle.dataprocessor.WebResources;
import com.sixbynine.movieoracle.util.Prefs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by steviekideckel on 10/26/14.
 */
public class ListingsManager extends Manager{

    private static ListingsManager sInstance;
    private MovieNetworkDataProcessor mMovieNetworkDataProcessor;


    private ListingsManager(){

    }

    public static ListingsManager getInstance(){
        if(sInstance == null){
            synchronized (Manager.class){
                if(sInstance == null){
                    sInstance = new ListingsManager();
                }
            }
        }
        return sInstance;
    }

    public void loadListings(){
        if(onDemandListingsAreStale() && hasInternet()) {
            WebResources.loadResources(new WebResources.Callback() {

                @Override
                public void onSuccess() {
                    mMovieNetworkDataProcessor = new MovieNetworkDataProcessor(mSplashActivityCallback); // will initialize an object and fetches the data from the movie network site
                    mMovieNetworkDataProcessor.repopulate();
                }

                @Override
                public void onFailure() {
                    publish(UpdateEvent.FAILURE_LOADING_WEB_RESOURCES, null);
                }
            });
        }
    }

    private boolean hasInternet(){
        ConnectivityManager connMgr = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }

    private boolean onDemandListingsAreStale(){
        String oldDate = Prefs.getLastUpdateDate();
        if(oldDate == null){
            return true;
        }else{
            String currentDate = getDateStamp();
            return Integer.parseInt(currentDate) - Integer.parseInt(oldDate) > 0;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getDateStamp(){
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }


    private SplashActivityCallback mSplashActivityCallback = new SplashActivityCallback() {
        @Override
        public void notifyChange(int status) {
            switch(status){
                case SplashActivityCallback.MOVIE_NETWORK_INTERNET_FINISHED:

            }
        }

    };
}
