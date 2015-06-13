package com.santoshmandadi.spotifystreamer.app;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;

import com.santoshmandadi.spotifystreamer.app.ArtistObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private CustomArtistArrayAdapter searchResultsAdapter;
    EditText searchArtist;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        searchArtist = (EditText)rootView.findViewById(R.id.editTextArtistName);
        searchArtist.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searchResultsAdapter.clear();
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = searchArtist.getText().toString().trim();
                if (text.length() > 2) {
                    FetchAlbums fetchAlbums = new FetchAlbums();
                    fetchAlbums.execute(text);
                }
            }
        });
        List<String> listOfArtists = new ArrayList<>();
        List<String> listOfArtistImages = new ArrayList<>();

      //  searchResultsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_results, R.id.list_item_artist_textview,listOfArtists);
        searchResultsAdapter = new CustomArtistArrayAdapter(getActivity(), R.layout.list_item_results, R.id.list_item_artist_textview, R.id.list_item_artist_imageview,listOfArtists, listOfArtistImages);
        ListView lv = (ListView)rootView.findViewById(R.id.listview_search);
        lv.setAdapter(searchResultsAdapter);
        return rootView;
    }



    public class FetchAlbums extends AsyncTask<String, Void , List<ArtistObject>>{

        @Override
        protected List<ArtistObject> doInBackground(String... params){
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            ArtistsPager artistsSearchResults = spotifyService.searchArtists(params[0]);
            Pager<Artist> artists = artistsSearchResults.artists;
            int count = 0;
            Log.d(LOG_TAG,"Total Number of results: " + artists.items.size());
            List<ArtistObject> artistsList = new ArrayList<>();
            for(Artist artist: artists.items){
                String image;
                try{
                    image = artist.images.get(0).url;
                }catch (IndexOutOfBoundsException e){
                     image = "http://cache.filehippo.com/img/ex/2762__Spotify_icon.png";
                }
                Log.d(LOG_TAG,"Artist Name: " + artist.name+" Image: "+image);
                artistsList.add(count, new ArtistObject(artist.name,image));
                count++;
            }

            return artistsList;
        }

        @Override
        protected void onPostExecute(List<ArtistObject> artistObjectList) {
           // super.onPostExecute(List<ArtistObject> artistObjectList);
            searchResultsAdapter.clear();
            for(ArtistObject  artist: artistObjectList){
                searchResultsAdapter.add(artist.getName(), artist.getImage());
                Log.e(LOG_TAG, artist.getName() + " Image: "+ artist.getImage());
            }
        }
    }


}
