package com.andreicraciun.nanodegree.spotifystreamer;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreicraciun.nanodegree.R;
import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by acraciun on 6/25/15.
 */
public class SpotifyPlayerDialog extends DialogFragment {

    private String artistName;
    private String albumName;
    private String trackName;
    private String imageUrl;
    private String trackUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistName = getArguments().getString("artistName");
        albumName = getArguments().getString("albumName");
        trackName = getArguments().getString("trackName");
        imageUrl = getArguments().getString("imageUrl");
        trackUrl = getArguments().getString("trackUrl");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.player_dialog, container,
                false);

        TextView txtArtistName = (TextView) rootView.findViewById(R.id.txtArtistName);
        txtArtistName.setText(artistName);
        TextView txtAlbumName = (TextView) rootView.findViewById(R.id.txtAlbumName);
        txtAlbumName.setText(albumName);
        TextView txtTrackName = (TextView) rootView.findViewById(R.id.txtTrackName);
        txtTrackName.setText(trackName);

        ImageView imgTrack = (ImageView) rootView.findViewById(R.id.imgTrack);
        Picasso.with(getActivity()).load(imageUrl).into(imgTrack);


        return rootView;
    }
}
