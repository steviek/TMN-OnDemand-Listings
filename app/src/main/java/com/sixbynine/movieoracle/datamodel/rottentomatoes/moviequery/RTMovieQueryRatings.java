package com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RTMovieQueryRatings {

    private final String criticsRating;
    private final int criticsScore;
    private final String audienceRating;
    private final int audienceScore;

    @JsonCreator
    public RTMovieQueryRatings(
            @JsonProperty("critics_rating") String criticsRating,
            @JsonProperty("critics_score") int criticsScore,
            @JsonProperty("audience_rating") String audienceRating,
            @JsonProperty("audience_score") int audienceScore) {
        this.criticsRating = criticsRating;
        this.criticsScore = criticsScore;
        this.audienceRating = audienceRating;
        this.audienceScore = audienceScore;
    }

    public String getCriticsRating() {
        return criticsRating;
    }

    public int getCriticsScore() {
        return criticsScore;
    }

    public String getAudienceRating() {
        return audienceRating;
    }

    public int getAudienceScore() {
        return audienceScore;
    }

    public int getAverage() {
        if (audienceScore > 0 && criticsScore > 0) {
            return (audienceScore + criticsScore) / 2;
        } else if (audienceScore > 0) {
            return audienceScore;
        } else {
            return criticsScore;
        }
    }
}
