package com.sixbynine.movieoracle.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steviekideckel on 10/30/14.
 */
public class RottenTomatoesAltId implements Parcelable{
    @SerializedName("imdb")
    private String imdbId;

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] s = new String[1];
        s[0] = imdbId;
        dest.writeStringArray(s);
    }

    public static final Creator<RottenTomatoesAltId> CREATOR = new Creator<RottenTomatoesAltId>() {
        @Override
        public RottenTomatoesAltId createFromParcel(Parcel source) {
            RottenTomatoesAltId id = new RottenTomatoesAltId();
            String[] s = new String[1];
            source.readStringArray(s);
            id.imdbId = s[0];
            return id;
        }

        @Override
        public RottenTomatoesAltId[] newArray(int size) {
            return new RottenTomatoesAltId[size];
        }
    };
}
