package com.santoshmandadi.spotifystreamer.app;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by santosh on 6/10/15.
 */
public class ArtistObject {
    private String name;
    private String image;

    public ArtistObject(String name, String image){
        this.name = name;
        this.image = image;
    }
    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return getName();
    }
}
