package com.example.mate_pc.game1.graphical_stuff;
/**
 *  Handles something surprising.
 */
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import com.example.mate_pc.game1.R;

public class Ricardo extends AppCompatActivity {

    //Button pauseButton;
    VideoView videoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricardo);


        videoview = findViewById(R.id.video);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ricardo_milos);
        videoview.setVideoURI(uri);
        videoview.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoview.stopPlayback();
        videoview.pause();
    }
}
