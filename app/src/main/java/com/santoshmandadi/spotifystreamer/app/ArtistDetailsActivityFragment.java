package com.santoshmandadi.spotifystreamer.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistDetailsActivityFragment extends Fragment {
    public String LOG_TAG = ArtistDetailsActivity.class.getSimpleName();
    @InjectView(R.id.listview_topten)
    ListView lv;
    ProgressDialog progressDialog;
    ArrayList<ArtistTopTenObject> artistTopTenObjectList = new ArrayList<>();
    private CustomArtistTopTenArrayAdapter artistTopTenArrayAdapter;

    public ArtistDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);
        ButterKnife.inject(this, rootView);
        artistTopTenArrayAdapter = new CustomArtistTopTenArrayAdapter(getActivity(), R.layout.list_item_topten, R.id.list_item_toptenAlbumImage, R.id.track_name_textview, R.id.album_name_textview, artistTopTenObjectList);
        lv.setAdapter(artistTopTenArrayAdapter);
        if (savedInstanceState == null || !savedInstanceState.containsKey("keyTracks")) {
            FetchArtistTopTen fetchArtistTopTen = new FetchArtistTopTen();
            fetchArtistTopTen.execute(getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT));

        } else {
            artistTopTenObjectList = savedInstanceState.getParcelableArrayList("keyTracks");
        }


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("keyTracks", artistTopTenObjectList);
    }

    private class FetchArtistTopTen extends AsyncTask<String, Void, List<ArtistTopTenObject>> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), "Wait", "Searching.....");
        }

        @Override
        protected List<ArtistTopTenObject> doInBackground(String... params) {
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            Map<String, Object> options = new HashMap<>();
            options.put("country", "US");
            Log.d(LOG_TAG, "Artist ID: " + params[0]);
            List<ArtistTopTenObject> artistTopTenObjectList = new ArrayList<>(10);
            try {
                Tracks tracks = spotifyService.getArtistTopTrack(params[0], options);
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

            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                Log.e(LOG_TAG, "RetrofitError: " + spotifyError.getErrorDetails() + error.getKind());
                return null;
            }

            return artistTopTenObjectList;
        }

        @Override
        protected void onPostExecute(List<ArtistTopTenObject> artistTopTenObjects) {
            artistTopTenArrayAdapter.clear();
            progressDialog.dismiss();
            Log.d(LOG_TAG, "Artist Top Tracks count in onPoctExecute: " + artistTopTenObjects.size());
            progressDialog.dismiss();
            if (artistTopTenObjects != null) {
                if (artistTopTenObjects.size() > 0) {
                    for (ArtistTopTenObject artistTopTenObject : artistTopTenObjects) {
                        artistTopTenArrayAdapter.add(artistTopTenObject);
                    }
                } else {
                    Toast.makeText(getActivity(), "This artist does not have any top tracks !", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Sorry, Count not connect to server", Toast.LENGTH_SHORT).show();
            }

        }
    }

}


