package com.sixbynine.movieoracle.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public final class Filters {

    private static final Filter NO_FILTER = new NoFilter();

    private Filters() {}

    public static Filter create(Filter.Type type, String parameter) {
        Preconditions.checkNotNull(type);
        switch (type) {
            case NONE:
                return NO_FILTER;
            case RATING:
                return new RatingFilter(parameter);
            case ACTOR:
                return new ActorFilter(parameter);
            case TITLE:
                return new TitleFilter(parameter);
            default:
                throw new IllegalArgumentException("Unexpected type: " + type);
        }
    }

    public static int indexOf(final Filter.Type filterType) {
        Preconditions.checkNotNull(filterType);
        return Iterables.indexOf(ImmutableList.copyOf(Filter.Type.values()), new Predicate<Filter.Type>() {
            @Override
            public boolean apply(Filter.Type type) {
                return type == filterType;
            }
        });
    }

    public static boolean hasOptions(Filter.Type filterType) {
        return filterType != Filter.Type.NONE;
    }
}
