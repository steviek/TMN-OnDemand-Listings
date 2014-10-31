package com.sixbynine.movieoracle.object;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by steviekideckel on 10/30/14.
 */
public class RottenTomatoesSummary implements Parcelable{
    private long id;
    private String title;
    private int year;
    private int runtime;
    private RottenTomatoesRatings ratings;
    private RottenTomatoesPosters posters;
    @SerializedName("abridged_cast")
    private ArrayList<RottenTomatoesActorBrief> cast;
    @SerializedName("alternate_ids")
    private RottenTomatoesAltId altIds;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        long[] l = new long[1];
        l[0] = id;
        dest.writeLongArray(l);

        String[] s = new String[1];
        s[0] = title;
        dest.writeStringArray(s);

        int[] i = new int[2];
        i[0] = year;
        i[1] = runtime;
        dest.writeIntArray(i);

        Bundle b = new Bundle();
        b.putParcelable("ratings", ratings);
        b.putParcelable("posters", posters);
        b.putParcelableArrayList("cast", cast);
        b.putParcelable("alt", altIds);
        dest.writeBundle(b);
    }

    public static final Creator<RottenTomatoesSummary> CREATOR = new Creator<RottenTomatoesSummary>() {
        @Override
        public RottenTomatoesSummary createFromParcel(Parcel source) {
            RottenTomatoesSummary summary = new RottenTomatoesSummary();
            long[] l = new long[1];
            source.readLongArray(l);
            summary.id = l[0];

            String[] s = new String[1];
            source.readStringArray(s);
            summary.title = s[0];

            int[] i = new int[2];
            source.readIntArray(i);
            summary.year = i[0];
            summary.runtime = i[1];

            Bundle b = source.readBundle();
            summary.ratings = b.getParcelable("ratings");
            summary.posters = b.getParcelable("posters");
            summary.cast = b.getParcelableArrayList("cast");
            summary.altIds = b.getParcelable("alt");
            return summary;
        }

        @Override
        public RottenTomatoesSummary[] newArray(int size) {
            return new RottenTomatoesSummary[size];
        }
    };
}
