package com.sixbynine.movieoracle.events;

import com.sixbynine.movieoracle.datamodel.tmn.TMNResources;

public final class TMNResourcesLoadedEvent {

    private TMNResources resources;

    public TMNResourcesLoadedEvent(TMNResources resources) {
        this.resources = resources;
    }

    public TMNResources getResources() {
        return resources;
    }
}
