package com.andreicraciun.nanodegree.spotifystreamer;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private enum PLAY_STATE {NOT_INITIALIZED, PLAYING, PAUSED};

    private PLAY_STATE play_state = PLAY_STATE.NOT_INITIALIZED;

    private Handler handler = new Handler();

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

        TextView txtDuration = (TextView) rootView.findViewById(R.id.txtEndTime);
        long seconds = ((currentTrack.duration_ms/1000)%60);
        long minutes = ((currentTrack.duration_ms/1000)/60);
        String duration = minutes +":"+ (seconds < 10 ? "0"+seconds:seconds);
        txtDuration.setText(duration);

        final SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.playSeekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean touching = false;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (touching) {
                    mediaPlayer.seekTo(progress * 1000);
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
        Picasso.with(getActivity()).load(currentTrack.album.images.get(0).url).into(imgTrack);

        final ImageButton btnPlayPause = (ImageButton) rootView.findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    switch (play_state)
                    {
                        case NOT_INITIALIZED:
                            Uri myUri = Uri.parse(currentTrack.preview_url);

                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(getActivity(), myUri);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            seekBar.setMax(mediaPlayer.getDuration()/1000);
                            play_state = PLAY_STATE.PLAYING;
                            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setProgress(mediaPlayer.getCurrentPosition()/1000);
                                    if (play_state == PLAY_STATE.PLAYING){
                                        handler.postDelayed(this, 1000);
                                    }
                                }
                            });
                            break;
                        case PLAYING:
                            mediaPlayer.pause();
                            play_state = PLAY_STATE.PAUSED;
                            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                            break;
                        case PAUSED:
                            mediaPlayer.start();
                            play_state = PLAY_STATE.PLAYING;
                            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                            break;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        ImageButton btnNext = (ImageButton) rootView.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                play_state = PLAY_STATE.NOT_INITIALIZED;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                seekBar.setProgress(0);

                SpotifyManager.getInstance().nextTrack();
                reloadVeiw();
            }
        });

        ImageButton btnPrevious = (ImageButton) rootView.findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                play_state = PLAY_STATE.NOT_INITIALIZED;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                seekBar.setProgress(0);

                SpotifyManager.getInstance().previousTrack();
                reloadVeiw();
            }
        });

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mediaPlayer!= null) {
            mediaPlayer.release();
        }
    }
}
