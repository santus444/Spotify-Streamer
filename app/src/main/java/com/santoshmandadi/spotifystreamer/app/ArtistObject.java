package com.santoshmandadi.spotifystreamer.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by santosh on 6/10/15.
 */
public class ArtistObject implements Parcelable {
    private String name, image, id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(id);
    }

    private ArtistObject (Parcel in) {
        name = in.readString();
        image = in.readString();
        id = in.readString();
    }


    public ArtistObject(String name, String image, String id) {
        this.name = name;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static final Parcelable.Creator<ArtistObject> CREATOR
            = new Parcelable.Creator<ArtistObject>() {
        public ArtistObject createFromParcel(Parcel in) {
            return new ArtistObject(in);
        }

        public ArtistObject[] newArray(int size) {
            return new ArtistObject[size];
        }
    };
}
