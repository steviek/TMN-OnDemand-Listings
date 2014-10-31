package com.sixbynine.movieoracle.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by steviekideckel on 10/30/14.
 */
public class RottenTomatoesActorBrief implements Parcelable{
    private String name;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] s = new String[2];
        s[0] = name;
        s[1] = id;
        dest.writeStringArray(s);
    }

    public static final Creator<RottenTomatoesActorBrief> CREATOR = new Creator<RottenTomatoesActorBrief>() {
        @Override
        public RottenTomatoesActorBrief createFromParcel(Parcel source) {
            RottenTomatoesActorBrief a = new RottenTomatoesActorBrief();
            String[] s = new String[2];
            source.readStringArray(s);
            a.name = s[0];
            a.id = s[1];
            return a;
        }

        @Override
        public RottenTomatoesActorBrief[] newArray(int size) {
            return new RottenTomatoesActorBrief[size];
        }
    };
}
