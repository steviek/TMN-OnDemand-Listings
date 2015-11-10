package com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class RTMovieQueryLinks {

    private final String self;
    private final String alternative;
    private final String cast;
    private final String clips;
    private final String reviews;
    private final String similar;

    @JsonCreator
    public RTMovieQueryLinks(
            @JsonProperty("self") String self,
            @JsonProperty("alternative") String alternative,
            @JsonProperty("cast") String cast,
            @JsonProperty("clips") String clips,
            @JsonProperty("reviews") String reviews,
            @JsonProperty("similar") String similar) {
        this.self = self;
        this.alternative = alternative;
        this.cast = cast;
        this.clips = clips;
        this.reviews = reviews;
        this.similar = similar;
    }

    public String getSelf() {
        return self;
    }

    public String getAlternative() {
        return alternative;
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
