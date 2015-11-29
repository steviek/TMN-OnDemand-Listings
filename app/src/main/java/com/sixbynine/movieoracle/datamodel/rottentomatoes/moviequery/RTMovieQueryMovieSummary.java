package com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryMovieSummaryWithTitle;
import com.sixbynine.movieoracle.model.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RTMovieQueryMovieSummary {

    private final String id;
    private final String title;
    private final int year;
    private final String mpaaRating;
    private final int runtime;
    private final String criticsConsensus;
    private final RTMovieQueryRatings ratings;
    private final String synopsis;
    private final RTMovieQueryPosters posters;
    private final List<RTMovieQueryCastMember> cast;
    private final RTMovieQueryAltIds altIds;
    private final RTMovieQueryLinks links;

    @JsonCreator
    public RTMovieQueryMovieSummary(
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("year") int year,
            @JsonProperty("mpaa_rating") String mpaaRating,
            @JsonProperty("runtime") int runtime,
            @JsonProperty("critics_consensus") String criticsConsensus,
            @JsonProperty("ratings") RTMovieQueryRatings ratings,
            @JsonProperty("synopsis") String synopsis,
            @JsonProperty("posters") RTMovieQueryPosters posters,
            @JsonProperty("abridged_cast") List<RTMovieQueryCastMember> cast,
            @JsonProperty("alternate_ids") RTMovieQueryAltIds altIds,
            @JsonProperty("links") RTMovieQueryLinks links) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.mpaaRating = mpaaRating;
        this.runtime = runtime;
        this.criticsConsensus = criticsConsensus;
        this.ratings = ratings;
        this.synopsis = synopsis;
        this.posters = posters;
        this.cast = cast;
        this.altIds = altIds;
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getMpaaRating() {
        return mpaaRating;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getCriticsConsensus() {
        return criticsConsensus;
    }

    public RTMovieQueryRatings getRatings() {
        return ratings;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public RTMovieQueryPosters getPosters() {
        return posters;
    }

    public List<RTMovieQueryCastMember> getCast() {
        return cast;
    }

    public RTMovieQueryAltIds getAltIds() {
        return altIds;
    }

    public RTMovieQueryLinks getLinks() {
        return links;
    }

    public static List<String> getAllChoices(List<RTMovieQueryMovieSummaryWithTitle> summaries, Filter filter) {
        List<String> choices = new ArrayList<>();
        switch (filter.getType()) {
            case ACTOR:
                for (RTMovieQueryMovieSummaryWithTitle summary : summaries) {
                    if (summary.getSummary().cast != null) {
                        int len2 = summary.getSummary().cast.size();
                        for (int j = 0; j < len2; j++) {
                            RTMovieQueryCastMember actorBrief = summary.getSummary().cast.get(j);
                            if (!choices.contains(actorBrief.getName())) {
                                choices.add(actorBrief.getName());
                            }
                        }
                    }
                }
                Collections.sort(choices);
                choices.add(0, Filter.SELECT_ACTOR);
                break;
            case RATING:
                choices.add(Filter.RATING_ALL);
                choices.add(Filter.RATING_FRESH);
                break;
        }
        return choices;
    }
}
