package com.santoshmandadi.spotifystreamer.app;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.santoshmandadi.spotifystreamer.app.data.SpotifyContract;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String[] ARTISTS_COLUMNS = {
            SpotifyContract.ArtistsEntry.TABLE_NAME + "." + SpotifyContract.ArtistsEntry._ID,
            SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID,
            SpotifyContract.ArtistsEntry.COLUMN_ARTIST_NAME,
            SpotifyContract.ArtistsEntry.COLUMN_ARTIST_IMAGE};
    public static final int COL_ARTISTS_TABLE_ID = 0;
    public static final int COL_ARTIST_ID = 1;
    public static final int COL_ARTIST_NAME = 2;
    public static final int COL_ARTIST_IMAGE = 3;
    private static final int ARTISTS_LOADER = 0;
    final String LOG_TAG = MainActivityFragment.class.getSimpleName();
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
        searchResultsAdapter = new ArtistsAdapter(getActivity(), null, 0);
        lv.setAdapter(searchResultsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    FetchArtistTopTenTask fetchArtistTopTenTask = new FetchArtistTopTenTask(getActivity());
                    fetchArtistTopTenTask.execute(cursor.getString(cursor.getColumnIndex(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID)));
                    ((Callback) getActivity())
                            .onItemSelected(SpotifyContract.TopTracksEntry.buildTopTracksUriWithArtistId(
                                    cursor.getString(cursor.getColumnIndex(SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID))
                            ));
                    Log.d(LOG_TAG, "Called new activity Intent : ");
                }


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
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ARTISTS_LOADER, null, this);

    }
//
//    }

    private void setSearch() {
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

    public interface Callback {
        /**
         * ArtistDetailsActivityFragmentCallBack  for when an item has been selected.
         */
        public void onItemSelected(Uri contentUri);
    }
}
