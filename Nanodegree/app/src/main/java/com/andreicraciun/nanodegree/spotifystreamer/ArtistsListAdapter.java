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

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by acraciun on 6/15/15.
 */
public class ArtistsListAdapter extends BaseAdapter {

    private List<Artist> artists = new LinkedList<>();
    private LayoutInflater layoutInflater;
    private Activity artistListActivity;

    public ArtistsListAdapter(Activity artistListActivity, List<Artist> artists) {
        this.artistListActivity = artistListActivity;
        layoutInflater = (LayoutInflater) artistListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.artists = artists;
    }

    @Override
    public int getCount() {

        return artists.size();
    }

    @Override
    public Object getItem(int position) {
        return artists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return artists.get(position).id.hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.artists_list_item, parent, false);
        }

        TextView txtArtistName = (TextView) convertView.findViewById(R.id.txtArtistName);
        txtArtistName.setText(artists.get(position).name);

        List<Image> images = artists.get(position).images;
        if (images.size() > 1) {
            ImageView imageArtist = (ImageView) convertView.findViewById(R.id.imageArtist);
            // url that picasso will load would not be null as images.size is 2 or above
            Picasso.with(artistListActivity).load(images.get(images.size() - 2).url).into(imageArtist);
        }


        return convertView;
    }
}
