package com.sixbynine.movieoracle.datamodel.webresources;

import retrofit.Call;
import retrofit.http.GET;

public interface WebResourcesService {

    @GET("/steviek/TMN-OnDemand-Listings/master/app/src/main/assets/resources.json")
    Call<WebResources> getWebResources();
}
