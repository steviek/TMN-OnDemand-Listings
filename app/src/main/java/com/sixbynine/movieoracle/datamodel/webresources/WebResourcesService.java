package com.sixbynine.movieoracle.datamodel.webresources;

import retrofit.http.GET;

public interface WebResourcesService {

    @GET("/resources.json")
    WebResources getWebResources();
}
