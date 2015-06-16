package com.santoshmandadi.spotifystreamer.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Image;
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
        searchArtist.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String text = searchArtist.getText().toString().trim();
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    FetchAlbums fetchAlbums = new FetchAlbums();
                    fetchAlbums.execute(text);
                return false;
            }
                return false;
        }});

        List<ArtistObject> listOfArtistObjects = new ArrayList<>();
        searchResultsAdapter = new CustomArtistArrayAdapter(getActivity(), R.layout.list_item_results, R.id.list_item_artist_textview, R.id.list_item_artist_imageview,listOfArtistObjects);
        ListView lv = (ListView)rootView.findViewById(R.id.listview_search);
        lv.setAdapter(searchResultsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artistId = searchResultsAdapter.getItem(position).getId();
                Intent intent = new Intent(getActivity(),ArtistDetailsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, artistId);
                intent.putExtra("artist",searchResultsAdapter.getItem(position).getName());
                startActivity(intent);
            }
        });
        return rootView;
    }



    private class FetchAlbums extends AsyncTask<String, Void , List<ArtistObject>>{

        @Override
        protected List<ArtistObject> doInBackground(String... params){
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            ArtistsPager artistsSearchResults = spotifyService.searchArtists(params[0]);
            Pager<Artist> artists = artistsSearchResults.artists;
            int count = 0;
            Log.d(LOG_TAG, "Total Number of results: " + artists.items.size());
            List<ArtistObject> artistsList = new ArrayList<>();
            for(Artist artist: artists.items){
                String image;
                int size = artist.images.size();
                Log.d(LOG_TAG, "Artist Name: " + artist.name + " Image: " + size);

                if(size>0) {
                    image = artist.images.get(size-1).url;
                }else {
                    image = "";
                }
                //Log.d(LOG_TAG,"Artist Name: " + artist.name+" Image: "+image);
                artistsList.add(count, new ArtistObject(artist.name,image, artist.id));
                count++;
            }

            return artistsList;
        }

        @Override
        protected void onPostExecute(List<ArtistObject> artistObjectList) {
           // super.onPostExecute(List<ArtistObject> artistObjectList);
            searchResultsAdapter.clear();
            if(artistObjectList.size()>0) {
                for (ArtistObject artist : artistObjectList) {
                    searchResultsAdapter.add(artist);
                    // Log.e(LOG_TAG, artist.getName() + " Image: "+ artist.getImage());
                }
            }else{

            }
        }
    }


}
