package com.santoshmandadi.spotifystreamer.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistDetailsActivityFragment extends Fragment {

    @InjectView(R.id.listview_topten) ListView lv;
    public String LOG_TAG = ArtistDetailsActivity.class.getSimpleName();
    private CustomArtistTopTenArrayAdapter artistTopTenArrayAdapter;
    ArrayList<ArtistTopTenObject> artistTopTenObjectList;

    public ArtistDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);
        ButterKnife.inject(this, rootView);
        if(savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            FetchArtistTopTen fetchArtistTopTen = new FetchArtistTopTen();
            fetchArtistTopTen.execute(getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT));
            artistTopTenObjectList = new ArrayList<>();
        }else{
            artistTopTenObjectList = savedInstanceState.getParcelableArrayList("key");
        }
        artistTopTenArrayAdapter = new CustomArtistTopTenArrayAdapter(getActivity(), R.layout.list_item_topten, R.id.list_item_toptenAlbumImage, R.id.track_name_textview, R.id.album_name_textview, artistTopTenObjectList);
        lv.setAdapter(artistTopTenArrayAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("key", artistTopTenObjectList);
    }

    private class FetchArtistTopTen extends AsyncTask<String, Void, List<ArtistTopTenObject>> {

        @Override
        protected List<ArtistTopTenObject> doInBackground(String... params) {
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            Map<String, Object> options = new HashMap<>();
            options.put("country", "US");
            Log.d(LOG_TAG, "Artist ID: " + params[0]);
            Tracks tracks = spotifyService.getArtistTopTrack(params[0], options);
            List<ArtistTopTenObject> artistTopTenObjectList = new ArrayList<>(10);
            List<Track> tracksList = tracks.tracks;
            int tracksCount = 0;
            for (Track track : tracksList) {
                if (tracksCount < 10) {
                    String image = "";
                    if (track.album.images.size() > 0) {
                        image = track.album.images.get(track.album.images.size() - 1).url;
                    }
                    artistTopTenObjectList.add(new ArtistTopTenObject(image, track.name, track.album.name));
                    tracksCount++;
                } else {
                    break;
                }
            }
            Log.d(LOG_TAG, "Artist Top Tracks count: " + artistTopTenObjectList.size());
            return artistTopTenObjectList;
        }

        @Override
        protected void onPostExecute(List<ArtistTopTenObject> artistTopTenObjects) {
            super.onPostExecute(artistTopTenObjects);
            artistTopTenArrayAdapter.clear();
            Log.d(LOG_TAG, "Artist Top Tracks count in onPoctExecute: " + artistTopTenObjects.size());

            for (ArtistTopTenObject artistTopTenObject : artistTopTenObjects) {
                artistTopTenArrayAdapter.add(artistTopTenObject);
            }
        }
    }

}


