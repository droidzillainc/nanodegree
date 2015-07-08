package com.andreicraciun.nanodegree.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreicraciun.nanodegree.R;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by acraciun on 6/15/15.
 */
public class TracksListAdapter extends BaseAdapter {

    private List<Track> tracks = new LinkedList<>();
    private LayoutInflater layoutInflater;
    private Activity trackListActivity;

    public TracksListAdapter(Activity trackListActivity, List<Track> tracks) {
        this.trackListActivity = trackListActivity;
        layoutInflater = (LayoutInflater) trackListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tracks = tracks;
    }

    @Override
    public int getCount() {

        return tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tracks.get(position).id.hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.artists_list_item, parent, false);
        }

        TextView txtArtistName = (TextView) convertView.findViewById(R.id.txtArtistName);
        txtArtistName.setText(tracks.get(position).name);

        List<Image> images = tracks.get(position).album.images;
        if (images.size() > 1) {
            ImageView imageArtist = (ImageView) convertView.findViewById(R.id.imageArtist);
            String imageURL = images.get(images.size() - 2).url;
            if (imageURL != null) {
                Picasso.with(trackListActivity).load(imageURL).into(imageArtist);
            }
        }


        return convertView;
    }
}
