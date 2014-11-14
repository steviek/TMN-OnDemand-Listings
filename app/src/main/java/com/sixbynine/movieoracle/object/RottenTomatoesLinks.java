package com.sixbynine.movieoracle.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by steviekideckel on 11/13/14.
 */
public class RottenTomatoesLinks implements Parcelable{

    private String self;
    private String alternate;
    private String cast;
    private String reviews;
    private String similar;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getSimilar() {
        return similar;
    }

    public void setSimilar(String similar) {
        this.similar = similar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] s = new String[5];
        s[0] = self;
        s[1] = alternate;
        s[2] = cast;
        s[3] = reviews;
        s[4] = similar;
        dest.writeStringArray(s);
    }

    public static final Creator<RottenTomatoesLinks> CREATOR = new Creator<RottenTomatoesLinks>() {
        @Override
        public RottenTomatoesLinks createFromParcel(Parcel source) {
            String[] s = new String[5];
            source.readStringArray(s);
            RottenTomatoesLinks links = new RottenTomatoesLinks();
            links.self = s[0];
            links.alternate = s[1];
            links.cast = s[2];
            links.reviews = s[3];
            links.similar = s[4];
            return links;
        }

        @Override
        public RottenTomatoesLinks[] newArray(int size) {
            return new RottenTomatoesLinks[size];
        }
    };
}
