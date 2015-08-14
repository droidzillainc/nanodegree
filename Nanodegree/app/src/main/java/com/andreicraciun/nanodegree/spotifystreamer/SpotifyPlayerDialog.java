package com.andreicraciun.nanodegree.spotifystreamer;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andreicraciun.nanodegree.R;
import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by acraciun on 6/25/15.
 */
public class SpotifyPlayerDialog extends DialogFragment {

    private View rootView;

    private enum PLAY_STATE {NOT_INITIALIZED, PLAYING, PAUSED};

    private PLAY_STATE play_state = PLAY_STATE.NOT_INITIALIZED;

    private SeekBar seekBar;
    private TextView txtElapsed;
    private ImageButton btnPlayPause;


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

        final Track currentTrack = SpotifyManager.getInstance().getCurrentTrack();
        TextView txtArtistName = (TextView) rootView.findViewById(R.id.txtArtistName);
        txtArtistName.setText(currentTrack.artists.get(0).name);
        TextView txtAlbumName = (TextView) rootView.findViewById(R.id.txtAlbumName);
        txtAlbumName.setText(currentTrack.album.name);
        TextView txtTrackName = (TextView) rootView.findViewById(R.id.txtTrackName);
        txtTrackName.setText(currentTrack.name);

        txtElapsed = (TextView) rootView.findViewById(R.id.txtStartTime);

        TextView txtDuration = (TextView) rootView.findViewById(R.id.txtEndTime);
        long seconds = ((currentTrack.duration_ms/1000)%60);
        long minutes = ((currentTrack.duration_ms/1000)/60);
        String duration = minutes +":"+ (seconds < 10 ? "0"+seconds:seconds);
        txtDuration.setText(duration);

        seekBar = (SeekBar) rootView.findViewById(R.id.playSeekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean touching = false;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (touching) {
                    Intent seekIntent = new Intent();
                    seekIntent.putExtra("seekTo", progress * 1000);
                    seekIntent.setAction(PlayerService.ACTION_SEEK);
                    seekIntent.setPackage("com.andreicraciun.nanodegree");
                    getActivity().startService(seekIntent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                touching = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                touching = false;
            }
        });

        ImageView imgTrack = (ImageView) rootView.findViewById(R.id.imgTrack);
        Picasso.with(getActivity()).load(currentTrack.album.images.get(1).url).into(imgTrack);

        btnPlayPause = (ImageButton) rootView.findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(getActivity());
            }
        });

        ImageButton btnNext = (ImageButton) rootView.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seekBar.setProgress(0);

                SpotifyManager.getInstance().nextTrack();
                reloadVeiw();
                updateUlapsedTime(0);

                if (play_state == PLAY_STATE.PLAYING) {
                    Intent playIntent = new Intent();
                    playIntent.setAction(PlayerService.ACTION_PLAY);
                    playIntent.setPackage("com.andreicraciun.nanodegree");
                    getActivity().startService(playIntent);
                }
                else {
                    play_state = PLAY_STATE.NOT_INITIALIZED;
                }
            }
        });

        ImageButton btnPrevious = (ImageButton) rootView.findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seekBar.setProgress(0);

                SpotifyManager.getInstance().previousTrack();
                reloadVeiw();
                updateUlapsedTime(0);

                if (play_state == PLAY_STATE.PLAYING) {
                    Intent playIntent = new Intent();
                    playIntent.setAction(PlayerService.ACTION_PLAY);
                    playIntent.setPackage("com.andreicraciun.nanodegree");
                    getActivity().startService(playIntent);
                }
                else {
                    play_state = PLAY_STATE.NOT_INITIALIZED;
                }
            }
        });

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void play(Activity activity) {
        try {
            switch (play_state)
            {
                case NOT_INITIALIZED:
                    Intent playIntent = new Intent();
                    playIntent.setAction(PlayerService.ACTION_PLAY);
                    playIntent.setPackage("com.andreicraciun.nanodegree");

                    activity.startService(playIntent);
                    play_state = PLAY_STATE.PLAYING;
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    break;
                case PLAYING:
                    Intent pauseIntent = new Intent();
                    pauseIntent.setAction(PlayerService.ACTION_PAUSE);
                    pauseIntent.setPackage("com.andreicraciun.nanodegree");
                    activity.startService(pauseIntent);
                    play_state = PLAY_STATE.PAUSED;
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                    break;
                case PAUSED:
                    Intent resumeIntent = new Intent();
                    resumeIntent.setAction(PlayerService.ACTION_RESUME);
                    resumeIntent.setPackage("com.andreicraciun.nanodegree");
                    activity.startService(resumeIntent);

                    play_state = PLAY_STATE.PLAYING;
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    break;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private BroadcastReceiver playingStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int songLength = bundle.getInt("songLength");
                int currentPosition = bundle.getInt("currentPosition");
                boolean onCompletion = bundle.getBoolean("onCompletion");
                seekBar.setMax(songLength);
                seekBar.setProgress(currentPosition);
                updateUlapsedTime(currentPosition);
                if (onCompletion && currentPosition > 0) {
                    play_state = PLAY_STATE.NOT_INITIALIZED;
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.registerReceiver(playingStateReceiver, new IntentFilter(PlayerService.ACTION_PLAYING_STATE_NOTIFICATION));
    }

    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(playingStateReceiver);
        super.onDetach();
    }

    private void updateUlapsedTime(int elapsedSeconds) {
        long seconds = (elapsedSeconds % 60);
        long minutes = (elapsedSeconds / 60);
        String duration = minutes +":"+ (seconds < 10 ? "0"+seconds:seconds);
        txtElapsed.setText(duration);

    }
}
