package com.sixbynine.movieoracle.model;

import com.google.common.base.Predicate;

import android.os.Parcelable;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;

public interface Filter extends Predicate<RTMovieQueryMovieSummary>, Parcelable {
    boolean showFilter();
    boolean showSpinner();
}
