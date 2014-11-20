package com.sixbynine.movieoracle.model;

/**
 * Created by steviekideckel on 11/20/14.
 */
public enum Filter {
    NONE(0, "None"),
    RATING(1, "Rating"),
    ACTOR(2, "Actor"),
    TITLE(3, "Title");
    public int id;
    public String name;

    Filter(int id, String name){
        this.id = id;
        this.name = name;
    }

    public static Filter fromId(int id){
        for(Filter filter : values()){
            if(filter.id == id){
                return filter;
            }
        }
        return null;
    }
}
