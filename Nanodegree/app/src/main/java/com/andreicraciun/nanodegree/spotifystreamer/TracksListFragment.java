package com.andreicraciun.nanodegree.spotifystreamer;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.andreicraciun.nanodegree.R;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * Created by acraciun on 7/5/15.
 */
public class TracksListFragment extends Fragment {

    public static final String SPOTIFY_ARTIST_ID_KEY = "SPOTIFY_ARTIST_ID_KEY";
    private ListView tracksList;

    private String currentSpotifyArtistId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(SPOTIFY_ARTIST_ID_KEY)) {
            currentSpotifyArtistId = getArguments().getString(SPOTIFY_ARTIST_ID_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView =  inflater.inflate(R.layout.tracks_list, container, false);
        tracksList = (ListView) mainView.findViewById(R.id.listTracks);
        tracksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Track track = ((Track)(((TracksListAdapter)(tracksList.getAdapter())).getItem(position)));
                SpotifyManager.getInstance().setCurrentTrackIndex(position);
                SpotifyPlayerDialog spotifyPlayerDialog = new SpotifyPlayerDialog();
                spotifyPlayerDialog.show(getFragmentManager(), "SPD");
                spotifyPlayerDialog.play(getActivity());
            }
        });

        if (!currentSpotifyArtistId.equals(SpotifyManager.getInstance().getLastTopTracksArtistSearched())) {
            SpotifyManager.getInstance().getTopTracks(currentSpotifyArtistId, getResources().getConfiguration().locale.getCountry());
        }
        else {
            updateTracksList(SpotifyManager.getInstance().getCurrentTracks());
        }
        return mainView;
    }

    public void updateTracksList(final Tracks tracks) {
        tracksList.post(new Runnable() {
            @Override
            public void run() {
                tracksList.setAdapter(new TracksListAdapter(getActivity(), tracks.tracks));
            }
        });
    }

}
