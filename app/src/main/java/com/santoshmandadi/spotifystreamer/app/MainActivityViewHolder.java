package com.santoshmandadi.spotifystreamer.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by santosh on 7/31/15.
 */
public class MainActivityViewHolder {
    public final ImageView iconView;
    public final TextView artistName;

    public MainActivityViewHolder(View view) {
        iconView = (ImageView) view.findViewById(R.id.list_item_artist_imageview);
        artistName = (TextView) view.findViewById(R.id.list_item_artist_textview);

    }
}
