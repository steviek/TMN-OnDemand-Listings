package com.sixbynine.movieoracle.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by steviekideckel on 10/30/14.
 */
public class RottenTomatoesPosters implements Parcelable{
    private String thumbnail;
    private String profile;
    private String detailed;
    private String original;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDetailed() {
        return detailed;
    }

    public void setDetailed(String detailed) {
        this.detailed = detailed;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] strings = new String[4];
        strings[0] = thumbnail;
        strings[1] = profile;
        strings[2] = detailed;
        strings[3] = original;
        dest.writeStringArray(strings);
    }

    public static final Creator<RottenTomatoesPosters> CREATOR = new Creator<RottenTomatoesPosters>() {
        @Override
        public RottenTomatoesPosters createFromParcel(Parcel source) {
            RottenTomatoesPosters p = new RottenTomatoesPosters();
            String[] s = new String[4];
            source.readStringArray(s);
            p.thumbnail = s[0];
            p.profile = s[1];
            p.detailed = s[2];
            p.original = s[3];
            return p;
        }

        @Override
        public RottenTomatoesPosters[] newArray(int size) {
            return new RottenTomatoesPosters[size];
        }
    };
}
