package com.santoshmandadi.spotifystreamer.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by santosh on 7/31/15.
 */
public class ArtistTopTenViewHolder {
    public final ImageView iconView;
    public final TextView trackName;
    public final TextView albumName;

    public ArtistTopTenViewHolder(View view){
        iconView = (ImageView) view.findViewById(R.id.list_item_toptenAlbumImage);
        trackName = (TextView) view.findViewById(R.id.track_name_textview);
        albumName = (TextView) view.findViewById(R.id.album_name_textview);

    }
}
