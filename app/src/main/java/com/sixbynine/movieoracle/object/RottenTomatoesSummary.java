package com.sixbynine.movieoracle.object;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sixbynine.movieoracle.model.Filter;
import com.sixbynine.movieoracle.model.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by steviekideckel on 10/30/14.
 */
public class RottenTomatoesSummary implements Parcelable{
    public static final String SELECT_ACTOR = "Select Actor";
    public static final String RATING_ALL = "All";
    public static final String RATING_FRESH = "Fresh";


    private String id;
    private String title;
    private String year;
    private String runtime;
    private String synopsis;
    private RottenTomatoesRatings ratings;
    private RottenTomatoesPosters posters;
    private RottenTomatoesLinks links;
    @SerializedName("abridged_cast")
    private ArrayList<RottenTomatoesActorBrief> cast;
    @SerializedName("alternate_ids")
    private RottenTomatoesAltId altIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public int getYearAsInt(){
        try{
            return Integer.parseInt(year);
        }catch(NumberFormatException e){
            return 0;
        }
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRuntime() {
        return runtime;
    }

    public int getRuntimeAsInt(){
        try{
            return Integer.parseInt(runtime);
        }catch(NumberFormatException e){
            return 0;
        }
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public RottenTomatoesRatings getRatings() {
        return ratings;
    }

    public void setRatings(RottenTomatoesRatings ratings) {
        this.ratings = ratings;
    }

    public RottenTomatoesPosters getPosters() {
        return posters;
    }

    public void setPosters(RottenTomatoesPosters posters) {
        this.posters = posters;
    }

    public ArrayList<RottenTomatoesActorBrief> getCast() {
        return cast;
    }

    public void setCast(ArrayList<RottenTomatoesActorBrief> cast) {
        this.cast = cast;
    }

    public RottenTomatoesAltId getAltIds() {
        return altIds;
    }

    public void setAltIds(RottenTomatoesAltId altIds) {
        this.altIds = altIds;
    }

    public RottenTomatoesLinks getLinks() {
        return links;
    }

    public void setLinks(RottenTomatoesLinks links) {
        this.links = links;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] s = new String[5];
        s[0] = title;
        s[1] = id;
        s[2] = year;
        s[3] = runtime;
        s[4] = synopsis;
        dest.writeStringArray(s);

        Bundle ratingsBundle = new Bundle();
        ratingsBundle.setClassLoader(RottenTomatoesRatings.class.getClassLoader());
        ratingsBundle.putParcelable("ratings", ratings);
        dest.writeBundle(ratingsBundle);

        Bundle posterBundle = new Bundle();
        posterBundle.setClassLoader(RottenTomatoesPosters.class.getClassLoader());
        posterBundle.putParcelable("posters", posters);
        dest.writeBundle(posterBundle);

        Bundle linksBundle = new Bundle();
        linksBundle.setClassLoader(RottenTomatoesLinks.class.getClassLoader());
        linksBundle.putParcelable("links", links);
        dest.writeBundle(linksBundle);

        Bundle castBundle = new Bundle();
        castBundle.setClassLoader(RottenTomatoesActorBrief.class.getClassLoader());
        castBundle.putParcelableArrayList("cast", cast);
        dest.writeBundle(castBundle);

        Bundle altBundle = new Bundle();
        altBundle.setClassLoader(RottenTomatoesAltId.class.getClassLoader());
        altBundle.putParcelable("alt", altIds);
        dest.writeBundle(altBundle);
    }

    public static final Creator<RottenTomatoesSummary> CREATOR = new Creator<RottenTomatoesSummary>() {
        @Override
        public RottenTomatoesSummary createFromParcel(Parcel source) {
            RottenTomatoesSummary summary = new RottenTomatoesSummary();
            String[] s = new String[5];
            source.readStringArray(s);
            summary.title = s[0];
            summary.id = s[1];
            summary.year = s[2];
            summary.runtime = s[3];
            summary.synopsis = s[4];

            Bundle ratingBundle = source.readBundle(RottenTomatoesRatings.class.getClassLoader());
            summary.ratings = ratingBundle.getParcelable("ratings");

            Bundle postersBundle = source.readBundle(RottenTomatoesPosters.class.getClassLoader());
            summary.posters = postersBundle.getParcelable("posters");

            Bundle linksBundle = source.readBundle(RottenTomatoesLinks.class.getClassLoader());
            summary.links = linksBundle.getParcelable("links");

            Bundle castBundle = source.readBundle(RottenTomatoesActorBrief.class.getClassLoader());
            summary.cast = castBundle.getParcelableArrayList("cast");

            Bundle altBundle = source.readBundle(RottenTomatoesAltId.class.getClassLoader());
            summary.altIds = altBundle.getParcelable("alt");

            return summary;
        }

        @Override
        public RottenTomatoesSummary[] newArray(int size) {
            return new RottenTomatoesSummary[size];
        }
    };

    public static List<String> getAllChoices(List<? extends RottenTomatoesSummary> list, Filter filter){
        List<String> choices = new ArrayList<String>();
        int len = list.size();
        switch(filter){
            case ACTOR:
                for(int i =0; i < len; i ++){
                    RottenTomatoesSummary summary = list.get(i);
                    if(summary.cast != null) {
                        int len2 = summary.cast.size();
                        for (int j = 0; j < len2; j ++){
                            RottenTomatoesActorBrief actorBrief = summary.cast.get(j);
                            if(!choices.contains(actorBrief.getName())) {
                                choices.add(actorBrief.getName());
                            }
                        }
                    }
                }
                Collections.sort(choices);
                choices.add(0,SELECT_ACTOR);
                break;
            case RATING:
                choices.add(RATING_ALL);
                choices.add(RATING_FRESH);
                break;
        }
        return choices;

    }

    public static List<RottenTomatoesSummary> sortAndFilterList(ArrayList<? extends RottenTomatoesSummary> list, Sort sort, Filter filter, Object filterParameter){
        List<RottenTomatoesSummary> result;
        if(filter == null || filterParameter == null){
            result = new ArrayList<RottenTomatoesSummary>(list);
        }else{
            result = Filter.filterList(list, filter.acceptor, filterParameter);
        }
        Collections.sort(result, sort.comparator);
        return result;
    }
}
