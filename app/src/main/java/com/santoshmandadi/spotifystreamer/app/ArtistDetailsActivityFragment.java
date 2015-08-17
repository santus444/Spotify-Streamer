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

import com.santoshmandadi.spotifystreamer.app.data.SpotifyContract;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistDetailsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String[] TRACKS_COLUMNS = {
            SpotifyContract.TopTracksEntry.TABLE_NAME + "." + SpotifyContract.ArtistsEntry._ID,
            SpotifyContract.TopTracksEntry.TABLE_NAME + "." + SpotifyContract.TopTracksEntry.COLUMN_ARTIST_ID,
            SpotifyContract.ArtistsEntry.COLUMN_ARTIST_NAME,
            SpotifyContract.TopTracksEntry.COLUMN_ALBUM_NAME,
            SpotifyContract.TopTracksEntry.COLUMN_SMALL_ALBUM_IMAGE,
            SpotifyContract.TopTracksEntry.COLUMN_TRACK_NAME,
            SpotifyContract.TopTracksEntry.COLUMN_TRACK_PREVIEW_URL
    };

    public static final int COL_ARTIST_TABLE_ID = 0;
    public static final int COL_ARTIST_ID = 1;
    public static final int COL_ARTIST_NAME = 2;
    public static final int COL_ALBUM_NAME = 3;
    public static final int COL_SMALL_ALBUM_IMAGE = 4;
    public static final int COL_TRACK_NAME = 5;
    public static final int COL_TRACK_PREVIEW_URL = 6;

    private static final int TOP_TEN_LOADER = 1;
    public String LOG_TAG = ArtistDetailsActivity.class.getSimpleName();
    @InjectView(R.id.listview_topten)
    ListView lv;
    ProgressDialog progressDialog;
    ArrayList<ArtistTopTenObject> artistTopTenObjectList = new ArrayList<>();
    private TopTenAdapter artistTopTenAdapter;
    Uri mUri;

    static final String DETAIL_URI = "URI";

    public ArtistDetailsActivityFragment() {
    }

    public static ArtistDetailsActivityFragment newInstance(String artistId) {
        ArtistDetailsActivityFragment fragment = new ArtistDetailsActivityFragment();
        Bundle args = new Bundle();
        args.putString("artistId", artistId);
        fragment.setArguments(args);
        return fragment;
    }

    public String getArtistId() {
        return getArguments().getString("artistId");
    }

    void onArtistChanged(String newArtistId) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            // String artistId = SpotifyContract.TopTracksEntry.getArtistIdFromTopTracksUri(uri);
            Uri updatedUri = SpotifyContract.TopTracksEntry.buildTopTracksUriWithArtistId(newArtistId);
            mUri = updatedUri;
            getLoaderManager().restartLoader(TOP_TEN_LOADER, null, this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(TOP_TEN_LOADER, null, this);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ArtistDetailsActivityFragment.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);
        ButterKnife.inject(this, rootView);

        artistTopTenAdapter = new TopTenAdapter(getActivity(), null, 0);
        lv.setAdapter(artistTopTenAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {

//                    Intent intent = new Intent(getActivity(), PlayerActivity.class)
//                            .setData(SpotifyContract.TopTracksEntry.buildTopTracksUriWithArtistId(
                    ((DetailsCallback) getActivity())
                            .onTrackItemSelected(SpotifyContract.TopTracksEntry.buildTrackUriWithArtistIdAndPosition(
                                    cursor.getString(COL_ARTIST_ID), position
                            ));
//                    intent.putExtra(Intent.EXTRA_TEXT, position);
//                    startActivity(intent);
                   // progressDialog = ProgressDialog.show(getActivity(), "Wait", "Searching.....");
                    Log.d(LOG_TAG, "Called new activity Intent");
                }


            }
        });

        return rootView;
    }


    @Override
    public void onStop() {
        super.onStop();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key", "test");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Intent intent = getActivity().getIntent();
//        if (intent == null || intent.getData() == null) {
//            return null;
//        } else if (intent != null || intent.getData() != null) {
//            FetchArtistTopTenTask fetchArtistTopTenTask = new FetchArtistTopTenTask(getActivity());
//            //fetchArtistTopTenTask.execute(getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT), getActivity().getIntent().getStringExtra("artist"));
//            fetchArtistTopTenTask.execute(SpotifyContract.TopTracksEntry.getArtistIdFromTopTracksUri(Uri.parse(getActivity().getIntent().getDataString())));
//
//        }
        if(null != mUri) {
            String sortOrder = SpotifyContract.TopTracksEntry.TABLE_NAME + "." + SpotifyContract.TopTracksEntry._ID + " ASC";
//            String selection = SpotifyContract.TopTracksEntry.TABLE_NAME + "." + SpotifyContract.TopTracksEntry.COLUMN_ARTIST_ID + " = ?";
//            String artistId = SpotifyContract.TopTracksEntry.getArtistIdFromTopTracksUri(mUri);
            return new CursorLoader(getActivity(), mUri, TRACKS_COLUMNS, null, null, sortOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        artistTopTenAdapter.swapCursor(data);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        artistTopTenAdapter.swapCursor(null);

    }


    public interface DetailsCallback {
        /**
         * ArtistDetailsActivityFragmentCallBack  for when an item has been selected.
         */
        public void onTrackItemSelected(Uri trackUri);
    }


}


