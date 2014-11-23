package com.sixbynine.movieoracle.model;

import com.sixbynine.movieoracle.object.RottenTomatoesSummary;

import java.util.Comparator;

/**
 * Created by steviekideckel on 11/20/14.
 */
public enum Sort {
    ALPHABETICAL(0, "A-Z", new Comparator<RottenTomatoesSummary>() {
        @Override
        public int compare(RottenTomatoesSummary lhs, RottenTomatoesSummary rhs) {
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    }),
    RATING(1, "Rating", new Comparator<RottenTomatoesSummary>() {
        @Override
        public int compare(RottenTomatoesSummary lhs, RottenTomatoesSummary rhs) {
            return rhs.getRatings().compareTo(lhs.getRatings());
        }
    }),
    YEAR(2, "Year", new Comparator<RottenTomatoesSummary>() {
        @Override
        public int compare(RottenTomatoesSummary lhs, RottenTomatoesSummary rhs) {
            return rhs.getYearAsInt() - lhs.getYearAsInt();
        }
    });
    public int id;
    public String name;
    public Comparator<RottenTomatoesSummary> comparator;

    Sort(int id, String name, Comparator<RottenTomatoesSummary> comparator){
        this.id = id;
        this.name = name;
        this.comparator = comparator;
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
