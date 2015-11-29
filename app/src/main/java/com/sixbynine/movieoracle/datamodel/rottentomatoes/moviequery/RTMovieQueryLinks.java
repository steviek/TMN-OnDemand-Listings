package com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RTMovieQueryLinks {

    private final String self;
    private final String alternate;
    private final String cast;
    private final String clips;
    private final String reviews;
    private final String similar;

    @JsonCreator
    public RTMovieQueryLinks(
            @JsonProperty("self") String self,
            @JsonProperty("alternate") String alternate,
            @JsonProperty("cast") String cast,
            @JsonProperty("clips") String clips,
            @JsonProperty("reviews") String reviews,
            @JsonProperty("similar") String similar) {
        this.self = self;
        this.alternate = alternate;
        this.cast = cast;
        this.clips = clips;
        this.reviews = reviews;
        this.similar = similar;
    }

    public String getSelf() {
        return self;
    }

    public String getAlternate() {
        return alternate;
    }

    public String getCast() {
        return cast;
    }

    public String getClips() {
        return clips;
    }

    public String getReviews() {
        return reviews;
    }

    public String getSimilar() {
        return similar;
    }
}
