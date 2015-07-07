package com.andreicraciun.nanodegree.spotifystreamer;

import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Spotify;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by acraciun on 6/15/15.
 */
public class SpotifyManager {

    private Artist currentArtist;
    private Track currentTrack;
    private AlbumSimple currentAlbum;
    private Tracks currentTracks;

    private static SpotifyManager instance = null;

    private String accessToken;

    private SpotifyManagerListener listener;

    private SpotifyManager() {

    }

    public static SpotifyManager getInstance () {
        if (instance == null) {
            instance = new SpotifyManager();
        }
        return instance;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void searchArtists(String prefix) {
        searchArtists(prefix, accessToken, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                if (listener != null) {
                    listener.updateArtistsList(artistsPager);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("nanodegree", "Error searching for artists:" + error );
            }
        });
    }

    private void searchArtists(final String prefix, final String accessToken, final Callback<ArtistsPager> callback) {
        new Thread() {
            public void run() {
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(accessToken);
                SpotifyService spotifyService = api.getService();
                spotifyService.searchArtists(prefix, callback);
            }
        }.start();
    }

    public void getTopTracks(String artistSpotifyId, String locale) {
        getToptracks(accessToken, artistSpotifyId, locale, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                currentTracks = tracks;
                if (listener != null) {
                    listener.updateTracksList(tracks);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("nanodegree", "Error getting top tracks:" + error );
            }
        });
    }

    private void getToptracks(final String accessToken, final String artistsSpotifyId, final String locale, final Callback<Tracks> callback) {
        new Thread() {
            public void run() {
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(accessToken);
                SpotifyService spotifyService = api.getService();
                Log.e("caca", "id:" + artistsSpotifyId);
                Map<String, Object> options = new HashMap<String, Object>();
                options.put("country", locale);
                spotifyService.getArtistTopTrack(artistsSpotifyId, options, callback);
//                Log.e("caca", "toptracks:" + spotifyService.getArtistTopTrack(artistsSpotifyId).tracks.size());
            }
        }.start();
    }

    public SpotifyManagerListener getListener() {
        return listener;
    }

    public void setListener(SpotifyManagerListener listener) {
        this.listener = listener;
    }
}
