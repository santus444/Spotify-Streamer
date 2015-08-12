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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.santoshmandadi.spotifystreamer.app.data.SpotifyContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    //CustomArtistArrayAdapter mSearchResultsAdapter;

    public FetchArtistsTask(Context context) {
        mContext = context;
    }

    private boolean DEBUG = true;



    /**
     * Helper method to handle insertion of a new artist in the spotify database.
     *
     * @param artistId Artist ID used to request top tracks from the server.
     * @param artistName A human-readable artist name, e.g "Cold Play"
     * @param imageUrl Artist image URL
     * @return the row ID of the added artist.
     */
    long addLocation(String artistId, String artistName, double imageUrl) {
        long artistTableId = 0;
        // Students: First, check if the artist with this artist id exists in the db
        Cursor cursor = mContext.getContentResolver().query(
                SpotifyContract.ArtistsEntry.CONTENT_URI,
                new String[]{SpotifyContract.ArtistsEntry._ID},
                SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID + " = ? ",
                new String[]{artistId}, null);
        Uri uri;
        // If it exists, return the current ID
        if(cursor.moveToFirst()) {
            int artistIdIndex = cursor.getColumnIndex(SpotifyContract.ArtistsEntry._ID);
            artistTableId = cursor.getLong(artistIdIndex);
            // Otherwise, insert it using the content resolver and the base URI
        }else{
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
    }

    @Override
    protected Void doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String artistQuery = params[0];

        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        List<ArtistObject> artistsList = new ArrayList<>();

        try {
           // if(new Util().isNetworkAvailable(MainActivity.this)) {

                ArtistsPager artistsSearchResults = spotifyService.searchArtists(params[0]);

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
            deletedInArtists = mContext.getContentResolver().delete(SpotifyContract.ArtistsEntry.CONTENT_URI, null,null);
            deletedInTopTracks = mContext.getContentResolver().delete(SpotifyContract.TopTracksEntry.CONTENT_URI, null,null);

            // add to database
            if ( cVVector.size() > 0 ) {
                // Student: call bulkInsert to add the weatherEntries to the database here
                inserted = mContext.getContentResolver().bulkInsert(SpotifyContract.ArtistsEntry.CONTENT_URI,cVVector.toArray(new ContentValues[cVVector.size()]));
            }

            Log.d(LOG_TAG, "FetchArtistsTask Complete. " + deletedInArtists + " Deleted in artists");
            Log.d(LOG_TAG, "FetchArtistsTask Complete. " + deletedInTopTracks + " Deleted in top tracksFe");
            Log.d(LOG_TAG, "FetchArtistsTask Complete. " + inserted + " Inserted");

//            String sortOrder = SpotifyContract.ArtistsEntry.COLUMN_ARTIST_NAME + " ASC";
//            Uri artistsTableUri = SpotifyContract.ArtistsEntry.CONTENT_URI;
//            Cursor cur = mContext.getContentResolver().query(artistsTableUri,
//                    null, null, null, sortOrder);
//
//            cVVector = new Vector<ContentValues>(cur.getCount());
//            if (cur.moveToFirst()) {
//                do {
//                    ContentValues cv = new ContentValues();
//                    DatabaseUtils.cursorRowToContentValues(cur, cv);
//                    cVVector.add(cv);
//                } while (cur.moveToNext());
//            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");


//            }else
//            {
//                return null;
//            }
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            Log.e(LOG_TAG, "RetrofitError: " + spotifyError.getErrorDetails() + error.getKind());
            return null;
        }


        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }
//    @Override
//    protected void onPostExecute(List<ArtistObject> artistObjectList) {
//        mSearchResultsAdapter.clear();
//        progressDialog.dismiss();
//        if (artistObjectList != null) {
//            if (artistObjectList.size() > 0) {
//                for (ArtistObject artist : artistObjectList) {
//                    mSearchResultsAdapter.add(artist);
//                }
//            } else {
//                Toast.makeText(mContext, R.string.no_results_message, Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(mContext, R.string.no_network_message, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    List<ArtistObject> convertContentValuesToListArrayOfArtistObjects(Vector<ContentValues> cvv) {
//        // return strings to keep UI functional for now
//        List<ArtistObject> resultStrs = new ArrayList<>(cvv.size());
//        for ( int i = 0; i < cvv.size(); i++ ) {
//            ContentValues artistDetails = cvv.elementAt(i);
//            resultStrs.add(new ArtistObject(artistDetails.getAsString(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_NAME), artistDetails.getAsString(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_IMAGE),artistDetails.getAsString(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID)));
//        }
//        return resultStrs;
//    }
//    @Override
//    protected void onPostExecute(String[] result) {
//        if (result != null && mForecastAdapter != null) {
//            mForecastAdapter.clear();
//            for(String dayForecastStr : result) {
//                mForecastAdapter.add(dayForecastStr);
//            }
//            // New data is back from the server.  Hooray!
//        }
//    }
}