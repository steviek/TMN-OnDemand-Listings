package com.sixbynine.movieoracle.manager;

import android.os.AsyncTask;

import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.datamodel.webresources.WebResources;
import com.sixbynine.movieoracle.datamodel.webresources.WebResourcesService;
import com.sixbynine.movieoracle.events.WebResourcesLoadedEvent;

import retrofit.Retrofit;

public final class WebResourcesManager {

    private static WebResources webResources;

    public static WebResources getWebResources() {
        if (webResources == null) {
            new LoadWebResourcesTask().execute();
        }
        return webResources;
    }

    private static final class LoadWebResourcesTask extends AsyncTask<Void, Void, WebResources> {

        @Override
        protected WebResources doInBackground(Void... params) {
            return new Retrofit.Builder()
                    .baseUrl("https://raw.githubusercontent.com/steviek/TMN-OnDemand-Listings/" +
                            "master/app/src/main/assets")
                    .build()
                    .create(WebResourcesService.class)
                    .getWebResources();
        }

        @Override
        protected void onPostExecute(WebResources webResources) {
            WebResourcesManager.webResources = webResources;
            MyApplication.getInstance().getBus().post(new WebResourcesLoadedEvent(webResources));
        }
    }
}
