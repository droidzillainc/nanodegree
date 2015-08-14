package com.andreicraciun.nanodegree.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by acraciun on 8/11/15.
 */
public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    public static final String ACTION_PLAY = "com.andreicraciun.nanodegree.spotifystreamer.PLAY";
    public static final String ACTION_PAUSE = "com.andreicraciun.nanodegree.spotifystreamer.PAUSE";
    public static final String ACTION_RESUME = "com.andreicraciun.nanodegree.spotifystreamer.RESUME";
    public static final String ACTION_SEEK = "com.andreicraciun.nanodegree.spotifystreamer.SEEK";

    public static final String ACTION_PLAYING_STATE_NOTIFICATION = "com.andreicraciun.nanodegree.spotifystreamer.ACTION_PLAYING_STATE_NOTIFICATION";

    private MediaPlayer mediaPlayer = null;

    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            play();
        }
        if (intent.getAction().equals(ACTION_PAUSE)) {
            pause();
        }
        if (intent.getAction().equals(ACTION_RESUME)) {
            resume();
        }
        if (intent.getAction().equals(ACTION_SEEK)) {
            seek(intent.getIntExtra("seekTo", mediaPlayer.getCurrentPosition()));
        }
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    private void play() {
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            final Track currentTrack = SpotifyManager.getInstance().getCurrentTrack();
            Uri myUri = Uri.parse(currentTrack.preview_url);

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(this, myUri);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.prepareAsync(); // prepare async to not block main thread

            shouldNotify = true;
            handler.post(runnable);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void pause() {
        mediaPlayer.pause();
        shouldNotify = false;
    }

    private void resume() {
        mediaPlayer.start();
        shouldNotify = true;
        handler.post(runnable);
    }

    private void seek(int seekTo) {
        mediaPlayer.seekTo(seekTo);
    }


    private boolean shouldNotify = false;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            notifySeekBar();
            if (shouldNotify) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void notifySeekBar() {
        Intent intent = new Intent(ACTION_PLAYING_STATE_NOTIFICATION);
        intent.putExtra("songLength", mediaPlayer.getDuration() / 1000);
        intent.putExtra("currentPosition", mediaPlayer.getCurrentPosition()/1000);
        intent.putExtra("onCompletion", false);
        sendBroadcast(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Intent intent = new Intent(ACTION_PLAYING_STATE_NOTIFICATION);
        intent.putExtra("songLength", mediaPlayer.getDuration() / 1000);
        intent.putExtra("currentPosition", mediaPlayer.getCurrentPosition()/1000);
        intent.putExtra("onCompletion", true);
        sendBroadcast(intent);
    }
}
