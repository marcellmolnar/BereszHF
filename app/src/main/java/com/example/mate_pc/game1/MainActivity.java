package com.example.mate_pc.game1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.content.Intent;
import android.media.MediaPlayer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    GameSurface gameSurface;

    private MediaPlayer mediaPlayer;    //mediaplayer for background music

    Button shootButton;
    Button left;
    Button right;
    Button up;
    Button settingsBtn;
    Button playButton;

    LinearLayout joystickSurface;

    private final static int INTERVAL = 200;
    Handler mHandler = new Handler();

    boolean isJoystick = false;
    String controlSettingsKey = "controlSettings_isJoystick";




    @SuppressLint("ClickableViewAccessibility") // Silencing "performClick" warning. Delete line to see affect.
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout surface = findViewById(R.id.gameSurface);
        gameSurface = new GameSurface(this);
        surface.addView(gameSurface);

        joystickSurface = findViewById(R.id.joystickSurface);
        joystickSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gameSurface.onJoystickTouchListener(motionEvent);
                return true;
            }
        });

        settingsBtn = findViewById(R.id.settings_Btn);
        settingsBtn.setBackground(getDrawable(R.drawable.settings_icon));

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyOtherActivity.class);
                startActivityForResult(intent,121);
            }
        });

        playButton = findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playintent = new Intent(MainActivity.this, Ricardo.class);
                startActivity(playintent);
            }
        });

        shootButton = findViewById(R.id.shoot);
        shootButton.setOnClickListener(onClickListener);

        alreadyShooting = false;

        up = findViewById(R.id.button);
        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                handleTouch(view, motionEvent);
                return true;
            }
        });

        left = findViewById(R.id.button2);
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                handleTouch(view, motionEvent);
                return true;
            }
        });
        right = findViewById(R.id.button3);
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                handleTouch(view, motionEvent);
                return true;
            }
        });


        setControl();

        InitBackgroundAudio(this,R.raw.background_music);
    }

    private void InitBackgroundAudio(Context c, int id){
        this.mediaPlayer = MediaPlayer.create(c, id);
        this.mediaPlayer.seekTo(0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setControl();
    }

    private void setControl(){
        SharedPreferences prefs = getSharedPreferences(Constants.controlSettings, MODE_PRIVATE);
        isJoystick = prefs.getBoolean(controlSettingsKey, false);
        if(isJoystick){
            up.setVisibility(View.INVISIBLE);
            left.setVisibility(View.INVISIBLE);
            right.setVisibility(View.INVISIBLE);
            gameSurface.isJoystick = true;
        }
        else{
            up.setVisibility(View.VISIBLE);
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
            gameSurface.isJoystick = false;
        }
    }


    private void handleTouch(View view, MotionEvent event) {
        int action = event.getAction();
        if (view.getId() == R.id.button){
            if (action == MotionEvent.ACTION_DOWN){
                jump();
                up.setBackground(getDrawable(R.drawable.ic_arrow_upward_black_24dp_pressed));
            }
            else if (action == MotionEvent.ACTION_UP) {
                up.setBackground(getDrawable(R.drawable.ic_arrow_upward_black_24dp));
            }
        }
        if (view.getId() == R.id.button2){
            if (action == MotionEvent.ACTION_DOWN){
                setMovementX(-1, false);
                left.setBackground(getDrawable(R.drawable.ic_arrow_back_black_24dp_pressed));
            }
            else if (action == MotionEvent.ACTION_UP) {
                setMovementX(0, true);
                left.setBackground(getDrawable(R.drawable.ic_arrow_back_black_24dp));
            }
        }
        if (view.getId() == R.id.button3){
            if (action == MotionEvent.ACTION_DOWN){
                setMovementX(1, false);
                right.setBackground(getDrawable(R.drawable.ic_arrow_forward_black_24dp_pressed));
            }
            else if (action == MotionEvent.ACTION_UP) {
                setMovementX(0, true);
                right.setBackground(getDrawable(R.drawable.ic_arrow_forward_black_24dp));
            }
        }
    }


    private void setMovementX(int x, boolean stopMovement) {
        gameSurface.setCharacterHorizontalAcceleration(x, stopMovement);
    }

    private void jump() {
        gameSurface.setCharacterVerticalAcceleration(-1);
    }

    boolean alreadyShooting;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(!alreadyShooting) {
                alreadyShooting = true;
                gameSurface.createBullet();

                startRepeatingTask();
            }
        }
    };


    int counter = 0;
    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            if(counter == 4){
                stopRepeatingTask();
                counter=0;
                alreadyShooting = false;
                return;
            }

            doSomething();
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    public void doSomething() {
        switch (counter) {
            case 0: {
                shootButton.setBackground(getDrawable(R.drawable.pistol0));
                counter++;
                break;

            }
            case 1: {
                shootButton.setBackground(getDrawable(R.drawable.pistol1));
                counter++;
                break;

            }
            case 2: {
                shootButton.setBackground(getDrawable(R.drawable.pistol2));
                counter++;
                break;

            }
            case 3: {
                shootButton.setBackground(getDrawable(R.drawable.pistol3));
                counter++;
                break;

            }
        }
    }

    void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }

    @SuppressLint("MissingSuperCall")
    protected void onStop () {
        super.onStop();
        Log.i("MYTAG", "stop");
        //StopBackgroundAudio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MYTAG", "destroy");
        //StopBackgroundAudio();
        this.mediaPlayer.stop();
        this.mediaPlayer.release();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MYTAG", "resume");
        if (!this.mediaPlayer.isPlaying())
        {
            this.mediaPlayer.setLooping(true);
            this.mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MYTAG", "pause");
        this.mediaPlayer.pause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("MYTAG", "restart");
        this.mediaPlayer.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MYTAG", "start");
    }
}
