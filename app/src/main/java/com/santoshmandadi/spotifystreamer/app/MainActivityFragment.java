package com.santoshmandadi.spotifystreamer.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;

import com.santoshmandadi.spotifystreamer.app.data.SpotifyContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final int ARTISTS_LOADER = 0;
    private static final String[] ARTISTS_COLUMNS = {
            SpotifyContract.ArtistsEntry.TABLE_NAME + "." + SpotifyContract.ArtistsEntry._ID,
            SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID,
            SpotifyContract.ArtistsEntry.COLUMN_ARTIST_NAME,
            SpotifyContract.ArtistsEntry.COLUMN_ARTIST_IMAGE};

    @InjectView(R.id.editTextArtistName)
    SearchView searchArtist;
    @InjectView(R.id.listview_search)
    ListView lv;
    ProgressDialog progressDialog;
    ArrayList<ArtistObject> listOfArtistObjects = new ArrayList<>();

    private ArtistsAdapter searchResultsAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);
        if (savedInstanceState == null || !savedInstanceState.containsKey("keyArtist")) {
            searchArtist.setIconifiedByDefault(false);
            this.setSearch();
        } else {
            listOfArtistObjects = savedInstanceState.getParcelableArrayList("keyArtist");
            //not setting iconifiedByDefault as false as it will be annoying if the user has not selected and we
            //keep bringing up the keyboard when they rotate screen
           this.setSearch();
        }
       // searchResultsAdapter = new CustomArtistArrayAdapter(getActivity(), R.layout.list_item_results, R.id.list_item_artist_textview, R.id.list_item_artist_imageview, listOfArtistObjects);
        searchResultsAdapter = new ArtistsAdapter(getActivity(), null, 0);
        lv.setAdapter(searchResultsAdapter);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String artistId = searchResultsAdapter.getItem(position).getId();
//                Intent intent = new Intent(getActivity(), ArtistDetailsActivity.class);
//                intent.putExtra(Intent.EXTRA_TEXT, artistId);
//                intent.putExtra("artist", searchResultsAdapter.getItem(position).getName());
//                startActivity(intent);
//                progressDialog = ProgressDialog.show(getActivity(), "Wait", "Searching.....");
//                Log.d(LOG_TAG,"Called new activity Intent");
//            }
//        });
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
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

//    private class FetchAlbums extends AsyncTask<String, Void, List<ArtistObject>> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = ProgressDialog.show(getActivity(), "Wait", "Searching.....");
//        }
//
//        @Override
//        protected List<ArtistObject> doInBackground(String... params) {
//            SpotifyApi spotifyApi = new SpotifyApi();
//            SpotifyService spotifyService = spotifyApi.getService();
//            List<ArtistObject> artistsList = new ArrayList<>();
//            try {
//                if(new Util().isNetworkAvailable(getActivity())) {
//
//                    ArtistsPager artistsSearchResults = spotifyService.searchArtists(params[0]);
//
//                Pager<Artist> artists = artistsSearchResults.artists;
//                int count = 0;
//                Log.d(LOG_TAG, "Total Number of results: " + artists.items.size());
//                for (Artist artist : artists.items) {
//                    String image;
//                    int size = artist.images.size();
//                    Log.d(LOG_TAG, "Artist Name: " + artist.name + " Image: " + size);
//
//                    if (size > 0) {
//                        image = artist.images.get(size - 1).url;
//                    } else {
//                        image = "";
//                    }
//                    artistsList.add(count, new ArtistObject(artist.name, image, artist.id));
//                    count++;
//                }
//                }else
//                {
//                    return null;
//                }
//            } catch (RetrofitError error) {
//                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
//                Log.e(LOG_TAG, "RetrofitError: " + spotifyError.getErrorDetails() + error.getKind());
//                return null;
//            }
//
//            return artistsList;
//        }
//
//        @Override
//        protected void onPostExecute(List<ArtistObject> artistObjectList) {
//            searchResultsAdapter.clear();
//            progressDialog.dismiss();
//            if (artistObjectList != null) {
//                if (artistObjectList.size() > 0) {
//                    for (ArtistObject artist : artistObjectList) {
//                        searchResultsAdapter.add(artist);
//                    }
//                } else {
//                    Toast.makeText(getActivity(), R.string.no_results_message, Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(getActivity(), R.string.no_network_message, Toast.LENGTH_SHORT).show();
//            }
//        }
//

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ARTISTS_LOADER,null, this);

    }
//
//    }

    private void setSearch(){
        searchArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchArtist.clearFocus();
                FetchArtistsTask fetchArtistsTask = new FetchArtistsTask(getActivity());
                fetchArtistsTask.execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = SpotifyContract.ArtistsEntry.COLUMN_ARTIST_NAME + " ASC";
        return new CursorLoader(getActivity(), SpotifyContract.ArtistsEntry.CONTENT_URI, ARTISTS_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        searchResultsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        searchResultsAdapter.swapCursor(null);
    }
}
