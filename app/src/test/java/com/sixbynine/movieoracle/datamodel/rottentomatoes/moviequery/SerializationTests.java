package com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery;

import com.google.common.collect.ImmutableList;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class SerializationTests {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final RTMovieQueryAltIds ALT_IDS = new RTMovieQueryAltIds("some id");

    @Test
    public void testRTMovieQueryAltIds() throws Exception {
        String serialized = OBJECT_MAPPER.writeValueAsString(ALT_IDS);
        RTMovieQueryAltIds altIds = OBJECT_MAPPER.readValue(serialized, RTMovieQueryAltIds.class);
        assertThat(altIds.getImdbId()).isEqualTo(ALT_IDS.getImdbId());
    }

    private static final RTMovieQueryCastMember SEAN_BEAN
            = new RTMovieQueryCastMember("Sean Bean", ImmutableList.of("Ned Stark"));
    private static final List<RTMovieQueryCastMember> CAST = ImmutableList.of(SEAN_BEAN);

    @Test
    public void testRTMovieQueryCastMember() throws Exception {
        String serialized = OBJECT_MAPPER.writeValueAsString(SEAN_BEAN);
        RTMovieQueryCastMember seanBean = OBJECT_MAPPER.readValue(serialized, RTMovieQueryCastMember.class);
        assertThat(seanBean.getName()).isEqualTo("Sean Bean");
        assertThat(seanBean.getCharacters()).containsExactly("Ned Stark");
    }
}
