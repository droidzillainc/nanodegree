package com.andreicraciun.nanodegree.spotifystreamer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.andreicraciun.nanodegree.R;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

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
public class TrackListActivity extends FragmentActivity implements SpotifyManagerListener {

    private static final String TRACKS_LIST_FRAGMENT_TAG = "TRACKS_LIST_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks_list);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TracksListFragment.SPOTIFY_ARTIST_ID_KEY, getIntent().getStringExtra(ArtistListActivity.SPOTIFY_ARTIST_ID_KEY));
            TracksListFragment fragment = new TracksListFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.tracks_list_fragment_container, fragment, TRACKS_LIST_FRAGMENT_TAG)
                    .commit();
       }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            navigateUpTo(new Intent(this, ArtistListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("nanodegree", "TrackListActivity onStart" );
        SpotifyManager.getInstance().setListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("nanodegree", "TrackListActivity onStop" );
        SpotifyManager.getInstance().removeListener(this);
    }

    @Override
    public void updateTracksList(Tracks tracks) {
        TracksListFragment tracksListFragment = ((TracksListFragment) (getFragmentManager().findFragmentByTag(TRACKS_LIST_FRAGMENT_TAG)));
        if (tracksListFragment != null) {
            tracksListFragment.updateTracksList(tracks);
        }

    }

    @Override
    public void updateArtistsList(ArtistsPager artistsPager) {
        ArtistsListFragment artistsListFragment = ((ArtistsListFragment)(getFragmentManager().findFragmentById(R.id.artist_list_fragment)));
        if (artistsListFragment != null) {
            artistsListFragment.updateArtistsList(artistsPager);
        }
    }

    @Override
    public void reloadSpotifyKey() {

    }


}
