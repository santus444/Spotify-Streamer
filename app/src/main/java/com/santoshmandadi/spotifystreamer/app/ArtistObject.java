package com.santoshmandadi.spotifystreamer.app;

/**
 * Created by santosh on 6/10/15.
 */
public class ArtistObject {
    private String name, image, id;

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
}
