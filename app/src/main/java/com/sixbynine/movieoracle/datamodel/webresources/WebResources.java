package com.sixbynine.movieoracle.datamodel.webresources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class WebResources {

    private final String tmnUrl;
    private final String tmnUrlTemplate;
    private final int updateDay;
    private final List<String> excludePrefixes;
    private final List<String> populatedUrls;
    private final List<String> series;

    @JsonCreator
    public WebResources(
            @JsonProperty("tmn_url") String tmnUrl,
            @JsonProperty("tmn_url_template") String tmnUrlTemplate,
            @JsonProperty("update_day") int updateDay,
            @JsonProperty("exclude_prefixes") List<String> excludePrefixes,
            @JsonProperty("populated_urls") List<String> populatedUrls,
            @JsonProperty("series") List<String> series) {
        this.tmnUrl = tmnUrl;
        this.tmnUrlTemplate = tmnUrlTemplate;
        this.updateDay = updateDay;
        this.excludePrefixes = excludePrefixes;
        this.populatedUrls = populatedUrls;
        this.series = series;
    }

    public String getTmnUrl() {
        return tmnUrl;
    }

    public String getTmnUrlTemplate() {
        return tmnUrlTemplate;
    }

    public int getUpdateDay() {
        return updateDay;
    }

    public List<String> getExcludePrefixes() {
        return excludePrefixes;
    }

    public List<String> getPopulatedUrls() {
        return populatedUrls;
    }

    public List<String> getSeries() {
        return series;
    }
}
