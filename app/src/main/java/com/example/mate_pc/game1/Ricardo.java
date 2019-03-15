package com.example.mate_pc.game1;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class Ricardo extends AppCompatActivity {

    Button pauseButton;
    VideoView videoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricardo);


        videoview = (VideoView) findViewById(R.id.video);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ricardo_milos);
    //    videoview.setRotation(90f);
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
