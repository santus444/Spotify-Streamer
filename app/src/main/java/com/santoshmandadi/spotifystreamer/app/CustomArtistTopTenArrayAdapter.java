package com.santoshmandadi.spotifystreamer.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by santosh on 6/13/15.
 */
public class CustomArtistTopTenArrayAdapter extends ArrayAdapter<ArtistTopTenObject> {
    List<ArtistTopTenObject> artistTopTenObjects;
    Context context;
    int resource, trackNameTextViewResouceId, albumNameTextViewResourceId, albumImageViewResourceId;

    public CustomArtistTopTenArrayAdapter(Context context, int resource, int imageViewResourceId, int textViewResourceId1, int textViewResourceId2, List<ArtistTopTenObject> objects) {
        super(context, resource, textViewResourceId1, objects);
        this.context = context;
        this.artistTopTenObjects = objects;
        this.resource = resource;
        this.trackNameTextViewResouceId = textViewResourceId1;
        this.albumNameTextViewResourceId = textViewResourceId2;
        this.albumImageViewResourceId = imageViewResourceId;

    }

    @Override
    public void clear() {
        super.clear();
        artistTopTenObjects.clear();
    }

    @Override
    public void add(ArtistTopTenObject object) {
        artistTopTenObjects.add(object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MyViewHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new MyViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (MyViewHolder) row.getTag();
        }
        holder.trackTitleTextView.setText(artistTopTenObjects.get(position).getTrackName());
        holder.albumTitleTextView.setText(artistTopTenObjects.get(position).getAlbumName());
        if (artistTopTenObjects.get(position).getImage() != "")
            Picasso.with(context).load(artistTopTenObjects.get(position).getImage()).into(holder.albumImageView);
        else
            holder.albumImageView.setImageResource(R.mipmap.ic_launcher);

        return row;
    }

    class MyViewHolder {
        ImageView albumImageView;
        TextView albumTitleTextView, trackTitleTextView;

        MyViewHolder(View v) {
            albumImageView = (ImageView) v.findViewById(albumImageViewResourceId);
            albumTitleTextView = (TextView) v.findViewById(trackNameTextViewResouceId);
            trackTitleTextView = (TextView) v.findViewById(albumNameTextViewResourceId);

        }
    }
}
