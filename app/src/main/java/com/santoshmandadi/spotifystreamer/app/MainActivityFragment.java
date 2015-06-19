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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    @InjectView(R.id.editTextArtistName)
    SearchView searchArtist;
    @InjectView(R.id.listview_search)
    ListView lv;
    ProgressDialog progressDialog;
    ArrayList<ArtistObject> listOfArtistObjects = new ArrayList<>();

    private CustomArtistArrayAdapter searchResultsAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);
        if (savedInstanceState == null || !savedInstanceState.containsKey("keyArtist")) {
            searchArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchArtist.clearFocus();
                    FetchAlbums fetchAlbums = new FetchAlbums();
                    fetchAlbums.execute(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

        } else {
            listOfArtistObjects = savedInstanceState.getParcelableArrayList("keyArtist");
            searchArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchArtist.clearFocus();
                    FetchAlbums fetchAlbums = new FetchAlbums();
                    fetchAlbums.execute(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
        searchResultsAdapter = new CustomArtistArrayAdapter(getActivity(), R.layout.list_item_results, R.id.list_item_artist_textview, R.id.list_item_artist_imageview, listOfArtistObjects);
        lv.setAdapter(searchResultsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artistId = searchResultsAdapter.getItem(position).getId();
                Intent intent = new Intent(getActivity(), ArtistDetailsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, artistId);
                intent.putExtra("artist", searchResultsAdapter.getItem(position).getName());
                startActivity(intent);
                progressDialog = ProgressDialog.show(getActivity(), "Wait", "Searching.....");
                Log.d(LOG_TAG,"Called new activity Intent");
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("keyArtist", listOfArtistObjects);
    }

    @Override
    public void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }

    private class FetchAlbums extends AsyncTask<String, Void, List<ArtistObject>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Wait", "Searching.....");
        }

        @Override
        protected List<ArtistObject> doInBackground(String... params) {
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            List<ArtistObject> artistsList = new ArrayList<>();
            try {
                ArtistsPager artistsSearchResults = spotifyService.searchArtists(params[0]);

                Pager<Artist> artists = artistsSearchResults.artists;
                int count = 0;
                Log.d(LOG_TAG, "Total Number of results: " + artists.items.size());
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
                }
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                Log.e(LOG_TAG, "RetrofitError: " + spotifyError.getErrorDetails() + error.getKind());
                return null;
            }

            return artistsList;
        }

        @Override
        protected void onPostExecute(List<ArtistObject> artistObjectList) {
            searchResultsAdapter.clear();
            progressDialog.dismiss();
            if (artistObjectList != null) {
                if (artistObjectList.size() > 0) {
                    for (ArtistObject artist : artistObjectList) {
                        searchResultsAdapter.add(artist);
                    }
                } else {
                    Toast.makeText(getActivity(), "Sorry, Your search did not yield any results !", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Sorry, Count not connect to server", Toast.LENGTH_SHORT).show();
            }
        }


    }

}
