package com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class RTMovieQueryCastMember {

    private final String name;
    private final List<String> characters;

    @JsonCreator
    public RTMovieQueryCastMember(@JsonProperty("name") String name, @JsonProperty("characters") List<String> characters) {
        this.name = name;
        this.characters = characters;
    }

    public String getName() {
        return name;
    }

    public List<String> getCharacters() {
        return characters;
    }
}
