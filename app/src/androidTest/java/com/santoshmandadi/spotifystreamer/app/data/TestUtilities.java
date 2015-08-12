package com.santoshmandadi.spotifystreamer.app.data;

import android.content.ContentValues;

/**
 * Created by santosh on 7/28/15.
 */
public class TestUtilities {
    static int TEST_ARTIST_ID = 2;
    static ContentValues createArtistsContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID, TEST_ARTIST_ID);
        contentValues.put(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_NAME, "Rahman");
        contentValues.put(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_IMAGE, "test");
        return contentValues;
    }
}
