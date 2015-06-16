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
 * Created by santosh on 6/11/15.
 */
public class CustomArtistArrayAdapter extends ArrayAdapter<ArtistObject> {

    Context context;
    int resource, textViewResourceID, imageViewResourceID;
    List<ArtistObject> listOfArtistObjects;

    public CustomArtistArrayAdapter(Context context, int resource, int textViewResourceID, int imageViewResourceID, List<ArtistObject> listOfArtistObjects) {
        super(context, resource, textViewResourceID, listOfArtistObjects);
        this.context = context;
        this.resource = resource;
        this.textViewResourceID = textViewResourceID;
        this.imageViewResourceID = imageViewResourceID;
        this.listOfArtistObjects = listOfArtistObjects;
    }

    @Override
    public void clear() {
        super.clear();
        listOfArtistObjects.clear();
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
        holder.artistNameTextView.setText(listOfArtistObjects.get(position).getName());
        if (listOfArtistObjects.get(position).getImage() != "")
            Picasso.with(context).load(listOfArtistObjects.get(position).getImage()).into(holder.albumImageView);
        else
            holder.albumImageView.setImageResource(R.mipmap.ic_launcher);

        return row;
    }

    public void add(ArtistObject artist) {
        listOfArtistObjects.add(artist);
    }

    @Override
    public ArtistObject getItem(int position) {
        super.getItem(position);
        return listOfArtistObjects.get(position);
    }

    class MyViewHolder {
        ImageView albumImageView;
        TextView artistNameTextView;

        MyViewHolder(View v) {
            albumImageView = (ImageView) v.findViewById(imageViewResourceID);
            artistNameTextView = (TextView) v.findViewById(textViewResourceID);

        }
    }
}
