package com.sixbynine.movieoracle.events;

import com.sixbynine.movieoracle.datamodel.webresources.WebResources;

public final class WebResourcesLoadedEvent {

    private final WebResources webResources;

    public WebResourcesLoadedEvent(WebResources webResources) {
        this.webResources = webResources;
    }

    public WebResources getWebResources() {
        return webResources;
    }
}
