package com.sixbynine.movieoracle.manager;

import com.google.common.base.Optional;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryResultMap;
import com.sixbynine.movieoracle.datamodel.tmn.TMNResources;
import com.sixbynine.movieoracle.datamodel.webresources.WebResources;
import com.sixbynine.movieoracle.events.RTMovieQueryResultMapLoadedEvent;
import com.sixbynine.movieoracle.events.TMNResourcesLoadedEvent;
import com.sixbynine.movieoracle.events.WebResourcesLoadedEvent;
import com.sixbynine.movieoracle.util.Logger;
import com.sixbynine.movieoracle.util.Prefs;
import com.sixbynine.movieoracle.util.TMNDateUtils;
import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private RTMovieQueryResultMap movieQueryResultMap;

    private DataManager() {
        state = State.INITIAL;
        MyApplication.getInstance().getBus().register(this);
    }

    private State loadDataIfNecessaryInner() {
        if (state == State.INITIAL) {
            if (onDemandListingsAreStale()) {
                if (hasInternet()) {
                    state = State.LOADING_WEB_RESOURCES;
                    Optional<WebResources> webResources = WebResourcesManager.getWebResources();
                    if (webResources.isPresent()) {
                        getInstance().loadTMNData(webResources.get());
                    }
                } else {
                    movieQueryResultMap = Prefs.getCurrentResults();
                    state = State.ERROR_NO_INTERNET;
                }
            } else {
                movieQueryResultMap = Prefs.getCurrentResults();
                state = State.LOADED;
            }
        }
        return state;
    }

    private boolean onDemandListingsAreStale() {
        String oldDate = Prefs.getLastUpdateDate();
        Logger.d("last update date: " + oldDate);
        return oldDate == null || Integer.parseInt(getLastTuesday()) - Integer.parseInt(oldDate) > 0;
    }

    private String getDateStamp() {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    private String getLastTuesday() {
        String lastTuesday = new SimpleDateFormat("yyyyMMdd").format(TMNDateUtils.getLastTuesday().getTime());
        Logger.d("lastTuesday: " + lastTuesday);
        return lastTuesday;
    }

    @Subscribe
    public void onWebResourcesLoaded(WebResourcesLoadedEvent webResourcesLoadedEvent) {
        loadTMNData(webResourcesLoadedEvent.getWebResources());
    }

    private void loadTMNData(WebResources webResources) {
        state = State.LOADING_TMN;
        Optional<TMNResources> resources = TMNManager.loadData(webResources);
        if (resources.isPresent()) {
            loadRottenTomatesData(resources.get());
        }
    }

    @Subscribe
    public void onTMNResourcesLoaded(TMNResourcesLoadedEvent tmnResourcesLoadedEvent) {
        loadRottenTomatesData(tmnResourcesLoadedEvent.getResources());
    }

    private void loadRottenTomatesData(TMNResources tmnResources) {
        state = State.LOADING_ROTTEN_TOMATOES;
        Optional<RTMovieQueryResultMap> movieQueryResultMap = RottenTomatoesManager.search(tmnResources);
        if (movieQueryResultMap.isPresent()) {
            onRTMovieQueryResultMapLoaded(new RTMovieQueryResultMapLoadedEvent(movieQueryResultMap.get()));
        }
    }

    @Subscribe
    public void onRTMovieQueryResultMapLoaded(RTMovieQueryResultMapLoadedEvent event) {
        this.movieQueryResultMap = event.getQueryResultMap();
        Prefs.saveLastUpdateDate(getDateStamp());
        state = State.LOADED;
    }

    public static State loadDataIfNecessary() {
        // getInstance forces initialization
        return getInstance().loadDataIfNecessaryInner();
    }

    public static RTMovieQueryResultMap getMovieQueryResultMap() {
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
    }
}
