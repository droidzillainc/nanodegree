package com.andreicraciun.nanodegree;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MyAppPortofolioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_app_portofolio);

        Button btnSpotifyStreamer = (Button) findViewById(R.id.btnSpotifyStreamer);
        btnSpotifyStreamer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayToast("This button will display the Spotify Streamer application");
            }
        });

        Button btnScoresApp = (Button) findViewById(R.id.btnScoresApp);
        btnScoresApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayToast("This button will display the Scores application");
            }
        });

        Button btnLibraryApp = (Button) findViewById(R.id.btnLibraryApp);
        btnLibraryApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayToast("This button will display the Library application");
            }
        });

        Button btnBuildItBigger = (Button) findViewById(R.id.btnBuildItBigger);
        btnBuildItBigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayToast("This button will display the Library application - bigger version :)");
            }
        });

        Button btnXyzReader = (Button) findViewById(R.id.btnXyzReader);
        btnXyzReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayToast("This button will display the XYZ Reader application");
            }
        });

        Button btnCapstone = (Button) findViewById(R.id.btnCapstone);
        btnCapstone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayToast("This button will display My app");
            }
        });

    }


    private void displayToast(CharSequence text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

}
