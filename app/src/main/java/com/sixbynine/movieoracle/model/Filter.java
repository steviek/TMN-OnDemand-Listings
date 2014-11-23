package com.sixbynine.movieoracle.model;

import com.sixbynine.movieoracle.object.RottenTomatoesActorBrief;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.util.Logger;

import java.util.ArrayList;

/**
 * Created by steviekideckel on 11/20/14.
 */
public enum Filter {


    NONE(0, "None", null),
    RATING(1, "Rating", new Acceptor<Integer, RottenTomatoesSummary>() {
        @Override
        public boolean accepts(Integer i, RottenTomatoesSummary s) {
            return s.getRatings().getAverage() >= i;
        }
    }),
    ACTOR(2, "Actor", new Acceptor<String, RottenTomatoesSummary>(){
        @Override
        public boolean accepts(String s, RottenTomatoesSummary summary) {
            for(int i = summary.getCast().size()-1; i >= 0; i --){
                RottenTomatoesActorBrief actor = summary.getCast().get(i);
                if(actor.getName().equals(s)){
                    return true;
                }
            }
            return false;
        }
    }),
    TITLE(3, "Title", new Acceptor<String, RottenTomatoesSummary>(){
        @Override
        public boolean accepts(String s, RottenTomatoesSummary summary) {
            if(s == null) return true;
            return summary.getTitle().toUpperCase().contains(s.toUpperCase());
        }
    });
    public int id;
    public String name;
    public Acceptor acceptor;

    Filter(int id, String name, Acceptor acceptor){
        this.id = id;
        this.name = name;
        this.acceptor = acceptor;
    }

    public static Filter fromId(int id){
        for(Filter filter : values()){
            if(filter.id == id){
                return filter;
            }
        }
        return null;
    }



    /**
     *
     * @param arrayList the list to base the filter off of
     * @param acceptor
     * @param <T>
     * @return a new ArrayList that is filtered, preserving the ordering of the previous ArrayList
     */
    public static <S, T> ArrayList<T> filterList(final ArrayList<? extends T> arrayList, Acceptor<S, T> acceptor, S parameter){
        if(acceptor == null || parameter == null){
            Logger.w("Null acceptor or parameter in filter: acceptor=" + acceptor + ", parameter=" + parameter);
            return new ArrayList<T>(arrayList);
        }
        final ArrayList<T> result = new ArrayList<T>();
        int len = arrayList.size();
        for(int i = 0; i < len; i ++){
            T t = arrayList.get(i);
            if(acceptor.accepts(parameter, t)){
                result.add(t);
            }
        }
        return result;
    }


}
