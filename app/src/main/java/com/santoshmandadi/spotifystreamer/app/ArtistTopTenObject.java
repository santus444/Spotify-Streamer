package com.santoshmandadi.spotifystreamer.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by santosh on 6/13/15.
 */
public class ArtistTopTenObject implements Parcelable {
    public static final Parcelable.Creator<ArtistTopTenObject> CREATOR
            = new Parcelable.Creator<ArtistTopTenObject>() {
        public ArtistTopTenObject createFromParcel(Parcel in) {
            return new ArtistTopTenObject(in);
        }

        public ArtistTopTenObject[] newArray(int size) {
            return new ArtistTopTenObject[size];
        }
    };
    String image, trackName, albumName;

    public ArtistTopTenObject(Parcel in) {
        image = in.readString();
        trackName = in.readString();
        albumName = in.readString();
    }

    public ArtistTopTenObject(String image, String trackName, String albumName) {
        this.image = image;
        this.trackName = trackName;
        this.albumName = albumName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getImage() {
        return image;
    }

    public String getTrackName() {
        return trackName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(trackName);
        dest.writeString(albumName);
    }
}
