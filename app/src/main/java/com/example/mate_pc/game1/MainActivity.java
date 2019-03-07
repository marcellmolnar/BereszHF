package com.example.mate_pc.game1;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    GameSurface gameSurface;

    Button shootButton;
    Button left;
    Button right;
    Button up;

    private final static int INTERVAL = 200;
    Handler mHandler = new Handler();


    @SuppressLint("ClickableViewAccessibility") // Silencing "performClick" warning. Delete line to see affect.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout surface = findViewById(R.id.gameSurface);
        gameSurface = new GameSurface(this);
        surface.addView(gameSurface);

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
                // ToDo: when counter is 0, it is the moment we should shoot. Bullet class should be added.
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

}
