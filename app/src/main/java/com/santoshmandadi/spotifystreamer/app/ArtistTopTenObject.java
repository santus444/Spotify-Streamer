package com.santoshmandadi.spotifystreamer.app;

/**
 * Created by santosh on 6/13/15.
 */
public class ArtistTopTenObject {
    String image;
    String trackName;
    String albumName;
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


}
