package com.santoshmandadi.spotifystreamer.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santoshmandadi.spotifystreamer.app.data.SpotifyContract;
import com.squareup.picasso.Picasso;

/**
 * {@link TopTenAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class TopTenAdapter extends CursorAdapter {

    public TopTenAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_topten, parent, false);
        ArtistTopTenViewHolder artistTopTenViewHolder = new ArtistTopTenViewHolder(view);
        view.setTag(artistTopTenViewHolder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        ArtistTopTenViewHolder artistTopTenViewHolder = (ArtistTopTenViewHolder) view.getTag();
        String albumImageUrl = cursor.getString(cursor.getColumnIndex(SpotifyContract.TopTracksEntry.COLUMN_SMALL_ALBUM_IMAGE));
        if (!albumImageUrl.equalsIgnoreCase(""))
            Picasso.with(context).load(albumImageUrl).into(artistTopTenViewHolder.iconView);
        else
            artistTopTenViewHolder.iconView.setImageResource(R.mipmap.ic_launcher);
        String albumName = cursor.getString(cursor.getColumnIndex(SpotifyContract.TopTracksEntry.COLUMN_ALBUM_NAME));
        artistTopTenViewHolder.albumName.setText(albumName);

        String trackName = cursor.getString(cursor.getColumnIndex(SpotifyContract.TopTracksEntry.COLUMN_TRACK_NAME));
        artistTopTenViewHolder.trackName.setText(trackName);

    }
}