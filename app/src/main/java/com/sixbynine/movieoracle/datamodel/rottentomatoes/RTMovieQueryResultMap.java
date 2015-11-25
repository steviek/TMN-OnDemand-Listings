package com.sixbynine.movieoracle.datamodel.rottentomatoes;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingMap;

import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds a map from a TMN title to a RottenTomatoes query result.
 */
public final class RTMovieQueryResultMap extends ForwardingMap<String, RTMovieQueryResult> {

    private final Map<String, RTMovieQueryResult> delegate = new HashMap<>();
    private final Map<String, RTMovieQueryMovieSummaryWithTitle> summaryMap = new HashMap<>();
    private final List<RTMovieQueryMovieSummaryWithTitle> bestSummaries = new ArrayList<>();

    @Override
    protected Map<String, RTMovieQueryResult> delegate() {
        return delegate;
    }

    @Override
    public RTMovieQueryResult put(String key, RTMovieQueryResult value) {
        cacheTitleToBestSummaryMapping(key, value);
        return super.put(key, value);
    }

    public void put(Map.Entry<String, RTMovieQueryResult> entry) {
        Preconditions.checkNotNull(entry);
        put(entry.getKey(), entry.getValue());
    }

    @Override
    public void putAll(Map<? extends String, ? extends RTMovieQueryResult> map) {
        super.putAll(map);
        for (Map.Entry<? extends String, ? extends RTMovieQueryResult> entry : map.entrySet()) {
            cacheTitleToBestSummaryMapping(entry.getKey(), entry.getValue());
        }
    }

    private void cacheTitleToBestSummaryMapping(String key, RTMovieQueryResult value) {
        Optional<RTMovieQueryMovieSummaryWithTitle> bestMatch = value.getBestMatch(key);
        if (bestMatch.isPresent()) {
            summaryMap.put(bestMatch.get().getTitle(), bestMatch.get());
            bestSummaries.add(bestMatch.get());
        }
    }

    public List<RTMovieQueryMovieSummaryWithTitle> getBestSummaries() {
        return bestSummaries;
    }

    public RTMovieQueryMovieSummaryWithTitle getSummary(String title) {
        return summaryMap.get(title);
    }


}
