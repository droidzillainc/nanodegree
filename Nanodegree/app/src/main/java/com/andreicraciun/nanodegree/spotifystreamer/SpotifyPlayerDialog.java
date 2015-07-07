package com.andreicraciun.nanodegree.spotifystreamer;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreicraciun.nanodegree.R;
import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by acraciun on 6/25/15.
 */
public class SpotifyPlayerDialog extends DialogFragment {

    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.player_dialog, container,
                false);


        reloadVeiw();
        return rootView;
    }

    private void reloadVeiw() {

        Track currentTrack = SpotifyManager.getInstance().getCurrentTrack();
        TextView txtArtistName = (TextView) rootView.findViewById(R.id.txtArtistName);
        txtArtistName.setText(currentTrack.artists.get(0).name);
        TextView txtAlbumName = (TextView) rootView.findViewById(R.id.txtAlbumName);
        txtAlbumName.setText(currentTrack.album.name);
        TextView txtTrackName = (TextView) rootView.findViewById(R.id.txtTrackName);
        txtTrackName.setText(currentTrack.name);

        ImageView imgTrack = (ImageView) rootView.findViewById(R.id.imgTrack);
        Picasso.with(getActivity()).load(currentTrack.album.images.get(0).url).into(imgTrack);

        ImageButton btnNext = (ImageButton) rootView.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotifyManager.getInstance().nextTrack();
                reloadVeiw();
            }
        });

        ImageButton btnPrevious = (ImageButton) rootView.findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotifyManager.getInstance().previousTrack();
                reloadVeiw();
            }
        });

    }
}
