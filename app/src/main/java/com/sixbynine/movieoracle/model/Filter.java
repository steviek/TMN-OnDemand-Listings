package com.sixbynine.movieoracle.model;

import com.google.common.base.Predicate;

import android.os.Parcelable;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryMovieSummaryWithTitle;

public interface Filter extends Predicate<RTMovieQueryMovieSummaryWithTitle>, Parcelable {

    String SELECT_ACTOR = "Select Actor";
    String RATING_ALL = "All";
    String RATING_FRESH = "Fresh";

    boolean showFilter();

    boolean showSpinner();

    Type getType();

    enum Type {
        NONE("None"),
        RATING("Rating"),
        ACTOR("Actor"),
        TITLE("Title");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Type fromName(String name) {
            for (Type type : values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unexpected name: " + name);
        }
    }
}
