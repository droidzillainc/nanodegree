package com.andreicraciun.nanodegree.spotifystreamer;

import com.andreicraciun.nanodegree.R;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
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

    private ArtistsPager currentArtists;
    private String lastPrefixSearched;
    private Track currentTrack;
    private AlbumSimple currentAlbum;
    private Tracks currentTracks;
    private int currentTrackIndex;
    private String lastTopTracksArtistSearched;

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

    public void searchArtists(final String prefix) {
        searchArtists(prefix, accessToken, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                lastPrefixSearched = prefix;
                currentArtists = artistsPager;
                if (listener != null) {
                    if (artistsPager.artists.items.size() == 0) {
                        listener.notifyNoArtistsFound(prefix);
                    }
                    listener.updateArtistsList(artistsPager);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (listener != null) {
                    listener.reloadSpotifyKey();
                }
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

    public void getTopTracks(final String artistSpotifyId, String locale) {
        getToptracks(accessToken, artistSpotifyId, locale, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                currentTracks = tracks;
                if (currentTracks.tracks.size() > 0) {
                    currentTrackIndex = 0;
                    currentTrack = currentTracks.tracks.get(0);
                    lastTopTracksArtistSearched = artistSpotifyId;
                }
                if (listener != null) {
                    if (currentTracks.tracks.size() == 0) {
                        listener.notifyNoTracksFound();
                    }
                    listener.updateTracksList(tracks);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (listener != null) {
                    listener.reloadSpotifyKey();
                }
            }
        });
    }

    private void getToptracks(final String accessToken, final String artistsSpotifyId, final String locale, final Callback<Tracks> callback) {
        new Thread() {
            public void run() {
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(accessToken);
                SpotifyService spotifyService = api.getService();
                Map<String, Object> options = new HashMap<String, Object>();
                options.put("country", locale);
                spotifyService.getArtistTopTrack(artistsSpotifyId, options, callback);
            }
        }.start();
    }

    public void setListener(SpotifyManagerListener listener) {
        this.listener = listener;
    }

    public void removeListener(SpotifyManagerListener listener) {
        if (this.listener == listener) {
            this.listener = null;
        }
    }

    public Track getCurrentTrack() {
        return currentTracks.tracks.get(currentTrackIndex);
    }

    public Tracks getCurrentTracks() {
        return currentTracks;
    }

    public void nextTrack() {
        currentTrackIndex++;
        if (currentTrackIndex >= currentTracks.tracks.size()) {
            currentTrackIndex = 0;
        }
    }

    public void previousTrack() {
        currentTrackIndex--;
        if (currentTrackIndex < 0) {
            currentTrackIndex = currentTracks.tracks.size() - 1;
        }
    }

    public String getLastTopTracksArtistSearched() {
        return lastTopTracksArtistSearched;
    }

    public ArtistsPager getCurrentArtists() {
        return currentArtists;
    }

    public String getLastPrefixSearched() {
        return lastPrefixSearched;
    }

    public void setCurrentTrackIndex(int currentTrackIndex) {
        this.currentTrackIndex = currentTrackIndex;
    }
}
