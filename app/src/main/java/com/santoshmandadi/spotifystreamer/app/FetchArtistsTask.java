/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.santoshmandadi.spotifystreamer.app;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.santoshmandadi.spotifystreamer.app.data.SpotifyContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.RetrofitError;

public class FetchArtistsTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();

    private final Context mContext;
    private ProgressDialog progressDialog;
    private boolean noResults = false, noNetwork = false;
    //CustomArtistArrayAdapter mSearchResultsAdapter;
    private boolean DEBUG = true;

    public FetchArtistsTask(Context context) {
        mContext = context;
    }

    /**
     * Helper method to handle insertion of a new artist in the spotify database.
     *
     * @param artistId   Artist ID used to request top tracks from the server.
     * @param artistName A human-readable artist name, e.g "Cold Play"
     * @param imageUrl   Artist image URL
     * @return the row ID of the added artist.
     */
    long addLocation(String artistId, String artistName, double imageUrl) {
        long artistTableId = 0;
        Cursor cursor = mContext.getContentResolver().query(
                SpotifyContract.ArtistsEntry.CONTENT_URI,
                new String[]{SpotifyContract.ArtistsEntry._ID},
                SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID + " = ? ",
                new String[]{artistId}, null);
        Uri uri;
        // If it exists, return the current ID
        if (cursor.moveToFirst()) {
            int artistIdIndex = cursor.getColumnIndex(SpotifyContract.ArtistsEntry._ID);
            artistTableId = cursor.getLong(artistIdIndex);
            // Otherwise, insert it using the content resolver and the base URI
        } else {
            ContentValues values = new ContentValues();
            values.put(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID, artistId);
            values.put(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_NAME, artistName);
            values.put(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_IMAGE, imageUrl);
            uri = mContext.getContentResolver().insert(SpotifyContract.ArtistsEntry.CONTENT_URI, values);
            artistTableId = ContentUris.parseId(uri);
        }
        return artistTableId;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(mContext, "Wait", "Searching.....");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        if (noResults)
            Toast.makeText(mContext, R.string.no_results_message, Toast.LENGTH_SHORT).show();
        if (noNetwork)
            Toast.makeText(mContext, R.string.no_network_message, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        String artistQuery = params[0];

        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        List<ArtistObject> artistsList = new ArrayList<>();

        try {
            // if(new Util().isNetworkAvailable(MainActivity.this)) {

            ArtistsPager artistsSearchResults = spotifyService.searchArtists(artistQuery);

            Pager<Artist> artists = artistsSearchResults.artists;
            int count = 0;
            Log.d(LOG_TAG, "Total Number of results: " + artists.items.size());
            Vector<ContentValues> cVVector = new Vector<ContentValues>(artists.items.size());
            for (Artist artist : artists.items) {
                String image;
                int size = artist.images.size();
                Log.d(LOG_TAG, "Artist Name: " + artist.name + " Image: " + size);

                if (size > 0) {
                    image = artist.images.get(size - 1).url;
                } else {
                    image = "";
                }
                artistsList.add(count, new ArtistObject(artist.name, image, artist.id));
                count++;
                ContentValues weatherValues = new ContentValues();
                weatherValues.put(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID, artist.id);
                weatherValues.put(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_NAME, artist.name);
                weatherValues.put(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_IMAGE, image);

                cVVector.add(weatherValues);

            }
            int inserted = 0, deletedInArtists = 0, deletedInTopTracks = 0;
            deletedInArtists = mContext.getContentResolver().delete(SpotifyContract.ArtistsEntry.CONTENT_URI, null, null);
            deletedInTopTracks = mContext.getContentResolver().delete(SpotifyContract.TopTracksEntry.CONTENT_URI, null, null);

            // add to database
            if (cVVector.size() > 0)
                inserted = mContext.getContentResolver().bulkInsert(SpotifyContract.ArtistsEntry.CONTENT_URI, cVVector.toArray(new ContentValues[cVVector.size()]));
            else
                noResults = true;
            Log.d(LOG_TAG, "FetchArtistsTask Complete. " + deletedInArtists + " Deleted in artists");
            Log.d(LOG_TAG, "FetchArtistsTask Complete. " + deletedInTopTracks + " Deleted in top tracksFe");
            Log.d(LOG_TAG, "FetchArtistsTask Complete. " + inserted + " Inserted");


            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");

        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            Log.e(LOG_TAG, "RetrofitError: " + spotifyError.getErrorDetails() + error.getKind());
            noNetwork = true;
            return null;
        }


        return null;
    }
//
}