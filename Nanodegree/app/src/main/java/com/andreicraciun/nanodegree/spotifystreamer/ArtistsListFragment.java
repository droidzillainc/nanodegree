package com.andreicraciun.nanodegree.spotifystreamer;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.andreicraciun.nanodegree.R;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by acraciun on 7/5/15.
 */
public class ArtistsListFragment extends Fragment {

    private ListView artistsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView =  inflater.inflate(R.layout.artists_list, container, false);
        artistsList = (ListView) mainView.findViewById(R.id.listArtists);
        artistsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ArtistListActivity)getActivity()).artistSelected(((Artist) artistsList.getAdapter().getItem(position)).id);
            }
        });

        EditText txtSearch = (EditText) mainView.findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SpotifyManager.getInstance().searchArtists(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        return mainView;
    }

    public void updateArtistsList(final ArtistsPager artistsPager) {
        artistsList.post(new Runnable() {
            @Override
            public void run() {
                artistsList.setAdapter(new ArtistsListAdapter(getActivity(), artistsPager.artists.items));
            }
        });
    }
}
