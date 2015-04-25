package com.phy0312.childfun.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ddj on 15-3-29.
 */
public class PlaceItem implements Parcelable {

    public String id;

    public String name;

    public String desc;

    public double latitude;

    public double longitude;

    public float distance;

    public String iconUrl;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.desc);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeFloat(this.distance);
        dest.writeString(this.iconUrl);
    }

    public PlaceItem() {
    }

    private PlaceItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.desc = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.distance = in.readFloat();
        this.iconUrl = in.readString();
    }

    public static final Parcelable.Creator<PlaceItem> CREATOR = new Parcelable.Creator<PlaceItem>() {
        public PlaceItem createFromParcel(Parcel source) {
            return new PlaceItem(source);
        }

        public PlaceItem[] newArray(int size) {
            return new PlaceItem[size];
        }
    };
}
