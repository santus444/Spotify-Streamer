package com.santoshmandadi.spotifystreamer.app;

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

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.AlbumsPager;
import kaaes.spotify.webapi.android.models.Pager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArrayAdapter<String> searchResultsAdapter;
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
      //  searchResultsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_results, R.id.list_item_artist_textview,listOfArtists);
        searchResultsAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_results, R.id.list_item_artist_textview,listOfArtists);
        ListView lv = (ListView)rootView.findViewById(R.id.listview_search);
        lv.setAdapter(searchResultsAdapter);
        return rootView;
    }



    public class FetchAlbums extends AsyncTask<String, Void , String[]>{

        @Override
        protected String[] doInBackground(String... params){
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            AlbumsPager artistAlbums = spotifyService.searchAlbums(params[0]);
            Pager<AlbumSimple> albums = artistAlbums.albums;
            String[] albumNames = new String[albums.items.size()];
            int count = 0;
            for(AlbumSimple albumSimple: albums.items){
                albumNames[count] = albumSimple.name;
                count++;
            }

            return albumNames;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            searchResultsAdapter.clear();
            for(String album: strings){
                searchResultsAdapter.add(album);
                Log.e(LOG_TAG, album);
            }
        }
    }


}
