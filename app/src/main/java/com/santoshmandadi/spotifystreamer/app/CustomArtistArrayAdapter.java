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
public class CustomArtistArrayAdapter extends ArrayAdapter<String> {

    Context context;
    int resource, textViewResourceID, imageViewResourceID;
    List<String> listOfArtists;
    List<String> listOfArtistImages;
    public CustomArtistArrayAdapter(Context context, int resource, int textViewResourceID,int imageViewResourceID, List<String> listOfArtists, List<String> listOfArtistImages) {
        super(context, resource, textViewResourceID, listOfArtists);
        this.context = context;
        this.resource = resource;
        this.textViewResourceID = textViewResourceID;
        this.imageViewResourceID = imageViewResourceID;
        this.listOfArtistImages = listOfArtistImages;
        this.listOfArtists = listOfArtists;
    }

    @Override
    public void clear() {
        super.clear();
        listOfArtists.clear();
        listOfArtistImages.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
        }
        TextView textView = (TextView)row.findViewById(textViewResourceID);
        ImageView imageView = (ImageView)row.findViewById(imageViewResourceID);

        textView.setText(listOfArtists.get(position));
        Picasso.with(context).load(listOfArtistImages.get(position)).into(imageView);

        return row;
    }

    public void add(String artistName, String artistImageResourceID) {
            listOfArtists.add(artistName);
            listOfArtistImages.add(artistImageResourceID);
    }
}
