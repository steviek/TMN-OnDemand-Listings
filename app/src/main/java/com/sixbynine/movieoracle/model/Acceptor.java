package com.sixbynine.movieoracle.model;

/**
 * Created by steviekideckel on 11/22/14.
 */
public interface Acceptor<S, T>{
    public abstract boolean accepts(S s, T t);
}
