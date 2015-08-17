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
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.santoshmandadi.spotifystreamer.app.data.SpotifyContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

public class FetchArtistTopTenTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchArtistTopTenTask.class.getSimpleName();

    private final Context mContext;
    private ProgressDialog progressDialog;
    private boolean noResults = false, noNetwork = false;
    private boolean DEBUG = true;

    public FetchArtistTopTenTask(Context context) {
        mContext = context;
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
        String artistId = params[0];
        String selection = SpotifyContract.TopTracksEntry.TABLE_NAME +
                "." + SpotifyContract.TopTracksEntry.COLUMN_ARTIST_ID + " = ? ";
        String sortOrder = SpotifyContract.TopTracksEntry.COLUMN_TRACK_NAME + " ASC";

        Cursor cursor = mContext.getContentResolver().query(SpotifyContract.TopTracksEntry.CONTENT_URI, null, selection, new String[]{artistId}, sortOrder);
        if (!cursor.moveToFirst()) {
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            Map<String, Object> options = new HashMap<>();
            options.put("country", "US");
            List<ArtistTopTenObject> artistTopTenObjectList = new ArrayList<>(10);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(10);
            try {

                Tracks tracks = spotifyService.getArtistTopTrack(params[0], options);
                List<Track> tracksList = tracks.tracks;
                int tracksCount = 0;
                for (Track track : tracksList) {
                    if (tracksCount < 10) {
                        String image = "", largeImage = "";
                        if (track.album.images.size() > 0) {
                            image = track.album.images.get(track.album.images.size() - 1).url;
                            largeImage = track.album.images.get(0).url;
                        }
                        // , largeImage, track.duration_ms, track.
                        Log.v(LOG_TAG, "Preview URL: " + track.preview_url);
                        Log.v(LOG_TAG, "Track Duration: " + track.duration_ms);

                        //    artistTopTenObjectList.add(new ArtistTopTenObject(image, track.name, track.album.name, track.id , largeImage, track.preview_url, params[1]));
                        tracksCount++;
                        ContentValues weatherValues = new ContentValues();

                        weatherValues.put(SpotifyContract.TopTracksEntry.COLUMN_ALBUM_NAME, track.album.name);
                        weatherValues.put(SpotifyContract.TopTracksEntry.COLUMN_ARTIST_ID, artistId);
                        weatherValues.put(SpotifyContract.TopTracksEntry.COLUMN_LARGE_ALBUM_IMAGE, largeImage);
                        weatherValues.put(SpotifyContract.TopTracksEntry.COLUMN_SMALL_ALBUM_IMAGE, image);
                        weatherValues.put(SpotifyContract.TopTracksEntry.COLUMN_TRACK_NAME, track.name);
                        weatherValues.put(SpotifyContract.TopTracksEntry.COLUMN_TRACK_PREVIEW_URL, track.preview_url);

                        cVVector.add(weatherValues);
                    } else {
                        break;
                    }
                }
                Log.d(LOG_TAG, "Artist Top Tracks count: " + artistTopTenObjectList.size());

                int inserted = 0;
                // add to database
                if (cVVector.size() > 0)
                    inserted = mContext.getContentResolver().bulkInsert(SpotifyContract.TopTracksEntry.CONTENT_URI, cVVector.toArray(new ContentValues[cVVector.size()]));
                else
                    noResults = true;


                if (inserted > 0)
                    Log.d(LOG_TAG, "FetchArtistTopTenTask Complete. " + inserted + " Inserted");


            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                Log.e(LOG_TAG, "RetrofitError: " + spotifyError.getErrorDetails() + error.getKind());
                noNetwork = true;
                return null;
            }
        }

        return null;
    }
}