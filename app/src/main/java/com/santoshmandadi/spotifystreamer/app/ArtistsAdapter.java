package com.santoshmandadi.spotifystreamer.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

/**
 * {@link ArtistsAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class ArtistsAdapter extends CursorAdapter {

    public ArtistsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_results, parent, false);
        MainActivityViewHolder mainActivityViewHolder = new MainActivityViewHolder(view);
        view.setTag(mainActivityViewHolder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        MainActivityViewHolder mainActivityViewHolder = (MainActivityViewHolder) view.getTag();
        String artistImageUrl = cursor.getString(MainActivityFragment.COL_ARTIST_IMAGE);
        if (!artistImageUrl.equalsIgnoreCase(""))
            Picasso.with(context).load(artistImageUrl).into(mainActivityViewHolder.iconView);
        else
            mainActivityViewHolder.iconView.setImageResource(R.mipmap.ic_launcher);
        String artistName = cursor.getString(MainActivityFragment.COL_ARTIST_NAME);
        mainActivityViewHolder.artistName.setText(artistName);


    }
}