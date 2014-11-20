package com.sixbynine.movieoracle.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steviekideckel on 10/30/14.
 */
public class RottenTomatoesRatings implements Parcelable, Comparable<RottenTomatoesRatings>{
    @SerializedName("critics_score")
    private int criticsScore;
    @SerializedName("audience_score")
    private int audienceScore;

    public int getCriticsScore() {
        return criticsScore;
    }

    public void setCriticsScore(int criticsScore) {
        this.criticsScore = criticsScore;
    }

    public int getAudienceScore() {
        return audienceScore;
    }

    public void setAudienceScore(int audienceScore) {
        this.audienceScore = audienceScore;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(criticsScore);
        dest.writeInt(audienceScore);
    }

    public static final Creator<RottenTomatoesRatings> CREATOR = new Creator<RottenTomatoesRatings>() {
        @Override
        public RottenTomatoesRatings createFromParcel(Parcel source) {
            RottenTomatoesRatings ratings = new RottenTomatoesRatings();
            ratings.criticsScore = source.readInt();
            ratings.audienceScore = source.readInt();
            return ratings;
        }

        @Override
        public RottenTomatoesRatings[] newArray(int size) {
            return new RottenTomatoesRatings[size];
        }
    };

    private int getAverage(){
        if(criticsScore == 0){
            return audienceScore;
        }else if(audienceScore == 0){
            return criticsScore;
        }else{
            return (audienceScore + criticsScore)/2;
        }
    }

    @Override
    public int compareTo(RottenTomatoesRatings another) {
        return getAverage() - another.getAverage();
    }
}
