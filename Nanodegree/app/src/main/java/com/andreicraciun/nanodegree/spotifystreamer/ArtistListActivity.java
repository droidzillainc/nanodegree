package com.andreicraciun.nanodegree.spotifystreamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.andreicraciun.nanodegree.R;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by acraciun on 6/15/15.
 */
public class ArtistListActivity extends FragmentActivity implements SpotifyManagerListener {

    private static final String CLIENT_ID = "d168765098f543e59b75310eaaa07725";
    private static final String CLIENT_SECRET = "31db73c898f146d7b03ca1c3bdc942aa";

    public static final String SPOTIFY_ARTIST_ID_KEY = "SPOTIFY_ARTIST_ID_KEY";

    private static final int REQUEST_CODE = 8703;

    private static final String REDIRECT_URI = "rubixnanodegree://callback";
    private static final String TRACKS_LIST_FRAGMENT_TAG = "TRACKS_LIST_FRAGMENT_TAG";
    private static final String SPOTIFY_PREFS_NAME = "SPOTIFY_PREFS_NAME";
    private static final String SPOTIFY_TOKEN = "SPOTIFY_TOKEN";

    private ListView artistsList;

    private boolean onTablet;

    private boolean spotifyInitializationPending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);

        if (findViewById(R.id.tracks_list_fragment_container) != null) {
            onTablet = true;
        }


        artistsList = (ListView) findViewById(R.id.listArtists);
        artistsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                artistSelected(((Artist) artistsList.getAdapter().getItem(position)).id);
            }
        });
        initializeSpotify(false);
    }

    public void artistSelected(String id) {


        if (onTablet) {
            Bundle arguments = new Bundle();
            arguments.putString(TracksListFragment.SPOTIFY_ARTIST_ID_KEY, id);
            TracksListFragment fragment = new TracksListFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.tracks_list_fragment_container, fragment, TRACKS_LIST_FRAGMENT_TAG)
                    .commit();

        } else {
            Intent tracksIntent = new Intent(ArtistListActivity.this, TrackListActivity.class);
            tracksIntent.putExtra(SPOTIFY_ARTIST_ID_KEY, id);
            startActivity(tracksIntent);
        }

    }

    public void initializeSpotify(boolean force) {

        String token = null;
        if (!force) {
            SharedPreferences settings = getSharedPreferences(SPOTIFY_PREFS_NAME, 0);
            token = settings.getString(SPOTIFY_TOKEN, null);
        }
        if ((token == null) && (!spotifyInitializationPending)) {
            spotifyInitializationPending = true;
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

            builder.setScopes(new String[]{"streaming"});
            builder.setShowDialog(true);
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }
        else {
            SpotifyManager.getInstance().setAccessToken(token);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            spotifyInitializationPending = false;
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    SharedPreferences settings = getSharedPreferences(SPOTIFY_PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(SPOTIFY_TOKEN, response.getAccessToken());
                    editor.commit();
                    SpotifyManager.getInstance().setAccessToken(response.getAccessToken());
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.e("nanodegree", "Got some error");
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.e("nanodegree", "Got default");
            }
        }
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
        initializeSpotify(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        SpotifyManager.getInstance().setListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyManager.getInstance().removeListener(this);
    }
}
