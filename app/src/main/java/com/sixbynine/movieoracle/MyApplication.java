package com.sixbynine.movieoracle;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by steviekideckel on 10/23/14.
 */
public class MyApplication extends Application{

    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getInstance(){
        return sInstance;
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
