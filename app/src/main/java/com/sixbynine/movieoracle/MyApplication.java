package com.sixbynine.movieoracle;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

import com.crittercism.app.Crittercism;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.otto.Bus;

public final class MyApplication extends Application{

    private static MyApplication sInstance;

    private Bus mBus;
    private ObjectMapper mObjectMapper;

    @Override
    public void onCreate() {
        super.onCreate();
        Crittercism.initialize(getApplicationContext(), "5471a99308ebc1199c000002");
        sInstance = this;
        mBus = new Bus();
        mObjectMapper = new ObjectMapper();
    }

    public static MyApplication getInstance(){
        return sInstance;
    }

    public Bus getBus() {
        return mBus;
    }

    public ObjectMapper getObjectMapper() {
        return mObjectMapper;
    }

    public String writeValueAsSring(Object object) {
        try {
            return mObjectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Intent getStoreIntent(){
        Intent intent;
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.sixbynine.movieoracle"));
        } catch (android.content.ActivityNotFoundException anfe) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.sixbynine.movieoracle"));
        }
        return intent;
    }

}
