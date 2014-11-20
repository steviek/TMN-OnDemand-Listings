package com.sixbynine.movieoracle.model;

/**
 * Created by steviekideckel on 11/20/14.
 */
public enum Sort {
    ALPHABETICAL(0, "A-Z"),
    RATING(1, "Rating"),
    YEAR(2, "Year");
    public int id;
    public String name;

    Sort(int id, String name){
        this.id = id;
        this.name = name;
    }

    public static Sort fromId(int id){
        for(Sort filter : values()){
            if(filter.id == id){
                return filter;
            }
        }
        return null;
    }
}
