package com.sixbynine.movieoracle.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryResult;
import com.sixbynine.movieoracle.datamodel.tmn.TMNResources;
import com.sixbynine.movieoracle.datamodel.webresources.WebResources;
import com.sixbynine.movieoracle.events.RTMovieQueryResultMapLoadedEvent;
import com.sixbynine.movieoracle.events.TMNResourcesLoadedEvent;
import com.sixbynine.movieoracle.events.WebResourcesLoadedEvent;
import com.sixbynine.movieoracle.util.Prefs;
import com.sixbynine.movieoracle.util.TMNDateUtils;
import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public final class DataManager {

    private static volatile DataManager instance;

    private static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    private State state;
    private Map<String, RTMovieQueryResult> movieQueryResultMap;

    private DataManager() {
        state = State.INITIAL;
        MyApplication.getInstance().getBus().register(this);
    }

    private State loadDataIfNecessaryInner() {
        if (state == State.INITIAL) {
            if (onDemandListingsAreStale()) {
                if (hasInternet()) {
                    state = State.LOADING_WEB_RESOURCES;
                    WebResources webResources = WebResourcesManager.getWebResources();
                    if (webResources != null) {
                        getInstance().loadTMNData(webResources);
                    }
                } else {
                    state = State.ERROR_NO_INTERNET;
                }
            } else {
                state = State.LOADED;
            }
        }
        return state;
    }

    private boolean onDemandListingsAreStale() {
        String oldDate = Prefs.getLastUpdateDate();
        return oldDate == null || Integer.parseInt(getLastTuesday()) - Integer.parseInt(oldDate) > 0;
    }

    private String getDateStamp() {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    private String getLastTuesday() {
        return new SimpleDateFormat("yyyyMMdd").format(TMNDateUtils.getLastTuesday().getTime());
    }

    private void saveUpdateDate() {
        Prefs.saveLastUpdateDate(getDateStamp());
    }

    @Subscribe
    public void onWebResourcesLoaded(WebResourcesLoadedEvent webResourcesLoadedEvent) {
        loadTMNData(webResourcesLoadedEvent.getWebResources());
    }

    private void loadTMNData(WebResources webResources) {
        state = State.LOADING_TMN;
        TMNResources resources = TMNManager.loadData(webResources);
        if (resources != null) {
            loadRottenTomatesData(resources);
        }
    }

    @Subscribe
    public void onTMNResourcesLoaded(TMNResourcesLoadedEvent tmnResourcesLoadedEvent) {
        loadRottenTomatesData(tmnResourcesLoadedEvent.getResources());
    }

    private void loadRottenTomatesData(TMNResources tmnResources) {
        state = State.LOADING_ROTTEN_TOMATOES;
        Map<String, RTMovieQueryResult> movieQueryResultMap = RottenTomatoesManager.search(tmnResources);
        if (movieQueryResultMap != null) {
            displayData(movieQueryResultMap);
        }
    }

    @Subscribe
    public void onRTMovieQueryResultMapLoaded(RTMovieQueryResultMapLoadedEvent rtMovieQueryResultMapLoadedEvent) {
        displayData(rtMovieQueryResultMapLoadedEvent.getQueryResultMap());
    }

    private void displayData(Map<String, RTMovieQueryResult> movieQueryResultMap) {
        this.movieQueryResultMap = movieQueryResultMap;
        state = State.LOADED;
    }

    public static State loadDataIfNecessary() {
        // getInstance forces initialization
        return getInstance().loadDataIfNecessaryInner();
    }

    public static State getState() {
        return getInstance().state;
    }

    public static Map<String, RTMovieQueryResult> getMovieQueryResultMap() {
        return getInstance().movieQueryResultMap;
    }

    private static boolean hasInternet(){
        Context context = MyApplication.getInstance();
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    public enum State {
        INITIAL,
        LOADING_WEB_RESOURCES,
        LOADING_TMN,
        LOADING_ROTTEN_TOMATOES,
        LOADED,
        ERROR_NO_INTERNET,
        ERROR_UNKNOWN
    }
}
