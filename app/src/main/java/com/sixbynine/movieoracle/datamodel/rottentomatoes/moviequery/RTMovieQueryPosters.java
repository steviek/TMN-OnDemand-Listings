package com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RTMovieQueryPosters {

    private final String thumbnail;
    private final String profile;
    private final String detailed;
    private final String original;

    @JsonCreator
    public RTMovieQueryPosters(
            @JsonProperty("thumbnail") String thumbnail,
            @JsonProperty("profile") String profile,
            @JsonProperty("detailed") String detailed,
            @JsonProperty("original") String original) {
        this.thumbnail = thumbnail;
        this.profile = profile;
        this.detailed = detailed;
        this.original = original;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getProfile() {
        return profile;
    }

    public String getDetailed() {
        return detailed;
    }

    public String getOriginal() {
        return original;
    }
}
