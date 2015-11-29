package com.sixbynine.movieoracle.manager;

import com.google.common.base.Optional;

import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.datamodel.webresources.WebResources;
import com.sixbynine.movieoracle.datamodel.webresources.WebResourcesService;
import com.sixbynine.movieoracle.events.WebResourcesLoadedEvent;
import com.sixbynine.movieoracle.util.Logger;

import retrofit.Callback;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public final class WebResourcesManager {

    private static WebResources webResources;

    public static Optional<WebResources> getWebResources() {
        if (webResources == null) {
            new Retrofit.Builder()
                    .baseUrl("https://raw.githubusercontent.com")
                    .addConverterFactory(JacksonConverterFactory.create(MyApplication.getInstance().getObjectMapper()))
                    .build()
                    .create(WebResourcesService.class)
                    .getWebResources()
                    .enqueue(new Callback<WebResources>() {
                        @Override
                        public void onResponse(Response<WebResources> response, Retrofit retrofit) {
                            webResources = response.body();
                            MyApplication.getInstance().getBus().post(new WebResourcesLoadedEvent(webResources));
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Logger.e(throwable);
                        }
                    });
        }
        return Optional.fromNullable(webResources);
    }
}
