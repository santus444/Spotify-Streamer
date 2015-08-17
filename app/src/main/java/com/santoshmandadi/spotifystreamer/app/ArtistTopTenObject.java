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
    String image, trackName, albumName, id, largeImage, previewUrl, artistName;

    public ArtistTopTenObject(Parcel in) {
        image = in.readString();
        trackName = in.readString();
        albumName = in.readString();
        id = in.readString();
        largeImage = in.readString();
        previewUrl = in.readString();
        artistName = in.readString();

    }

    public ArtistTopTenObject(String image, String trackName, String albumName, String id, String largeImage, String previewUrl, String artistName) {
        this.image = image;
        this.trackName = trackName;
        this.albumName = albumName;
        this.id = id;
        this.largeImage = largeImage;
        this.previewUrl = previewUrl;
        this.artistName = artistName;
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

    public String getId() {
        return id;
    }

    public String getLargeImage() {
        return largeImage;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getArtistName() {
        return artistName;
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
        dest.writeString(id);
        dest.writeString(largeImage);
        dest.writeString(previewUrl);
        dest.writeString(artistName);

    }
}
