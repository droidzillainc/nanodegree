package com.andreicraciun.nanodegree.spotifystreamer;

import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by acraciun on 7/5/15.
 */
public interface SpotifyManagerListener {

    void updateTracksList(Tracks tracks);

    void updateArtistsList(ArtistsPager artistsPager);
}
