package com.example.mate_pc.game1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mate_pc.game1.Constants.CONTROL_SETTINGS_KEY;
import static com.example.mate_pc.game1.Constants.MY_SETTINGS;
import static com.example.mate_pc.game1.Constants.SHOW_JOYSTICK_SETTINGS_KEY;

public class ControlInputHandler {

    private Button shootButton;
    private boolean alreadyShooting;

    private Button left;
    private Button right;
    private Button up;

    private Activity mainActivity;

    private GameSurface gameSurface;

    // for shot button animation
    private final static int INTERVAL = 200;
    Handler mHandler = new Handler();

    ControlInputHandler(Activity mainActivity, GameSurface gameSurface) {
        this.mainActivity = mainActivity;

        this.gameSurface = gameSurface;

        up = mainActivity.findViewById(R.id.buttonUp);
        left = mainActivity.findViewById(R.id.buttonLeft);
        right = mainActivity.findViewById(R.id.buttonRight);

        alreadyShooting = false;
    }



    public void setControl(){
        SharedPreferences prefs = mainActivity.getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
        boolean isJoystick = prefs.getBoolean(CONTROL_SETTINGS_KEY, false);
        boolean showJoystick = prefs.getBoolean(SHOW_JOYSTICK_SETTINGS_KEY, true);
        if(isJoystick){
            up.setVisibility(View.INVISIBLE);
            left.setVisibility(View.INVISIBLE);
            right.setVisibility(View.INVISIBLE);
        }
        else{
            up.setVisibility(View.VISIBLE);
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
        }
        setJoystickUsage(isJoystick, showJoystick);
    }


    public void handleTouch(View view, MotionEvent event) {
        int action = event.getAction();
        if (view.getId() == R.id.buttonUp){
            if (action == MotionEvent.ACTION_DOWN){
                jump();
                //up.setBackground(getDrawable(R.drawable.ic_arrow_upward_black_24dp_pressed));
            }
            else if (action == MotionEvent.ACTION_UP) {
                //up.setBackground(getDrawable(R.drawable.ic_arrow_upward_black_24dp));
            }
        }
        if (view.getId() == R.id.buttonLeft){
            if (action == MotionEvent.ACTION_DOWN){
                setMovementX(-1, false);
                left.setBackground(mainActivity.getDrawable(R.drawable.ic_arrow_back_black_24dp_pressed));
            }
            else if (action == MotionEvent.ACTION_UP) {
                setMovementX(0, true);
                left.setBackground(mainActivity.getDrawable(R.drawable.ic_arrow_back_black_24dp));
            }
        }
        if (view.getId() == R.id.buttonRight){
            if (action == MotionEvent.ACTION_DOWN){
                setMovementX(1, false);
                right.setBackground(mainActivity.getDrawable(R.drawable.ic_arrow_forward_black_24dp_pressed));
            }
            else if (action == MotionEvent.ACTION_UP) {
                setMovementX(0, true);
                right.setBackground(mainActivity.getDrawable(R.drawable.ic_arrow_forward_black_24dp));
            }
        }
    }


    private void setMovementX(int x, boolean stopMovement) {
        gameSurface.setCharacterHorizontalAcceleration(x, stopMovement);
    }

    private void jump() {
        gameSurface.setCharacterVerticalAcceleration(-1);
    }

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

            updateShootButtonState();
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    public void updateShootButtonState() {
        switch (counter) {
            case 0: {
                shootButton.setBackground(mainActivity.getDrawable(R.drawable.pistol0));
                counter++;
                break;

            }
            case 1: {
                shootButton.setBackground(mainActivity.getDrawable(R.drawable.pistol1));
                counter++;
                break;

            }
            case 2: {
                shootButton.setBackground(mainActivity.getDrawable(R.drawable.pistol2));
                counter++;
                break;

            }
            case 3: {
                shootButton.setBackground(mainActivity.getDrawable(R.drawable.pistol3));
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






    public void setJoystickUsage(boolean useJoystick, boolean showTheJoystick){
        this.usingJoystick = useJoystick;
        this.showJoystick = showTheJoystick;
    }

    private boolean usingJoystick = false;
    private boolean showJoystick = true;

    private float startPosX;
    private float startPosY;
    private float currPosX;
    private float currPosY;

    private boolean jumpFlag = true;
    long now;


    public void onJoystickTouchListener(MotionEvent motionEvent) {
        if(usingJoystick) {
            int action = motionEvent.getAction();
            if (System.nanoTime() >= now + 500000000) {
                jumpFlag = true;
            }
            if (action == MotionEvent.ACTION_DOWN) {
                startPosX = motionEvent.getX();
                startPosY = motionEvent.getY();
                gameSurface.setJoystickVisibility(true, startPosX, startPosY);
            }
            else if (action == MotionEvent.ACTION_UP) {
                gameSurface.setCharacterHorizontalAcceleration(0, true);
            }
            else if (action == MotionEvent.ACTION_MOVE) {
                float cx = motionEvent.getX();
                float cy = motionEvent.getY();
                if (cx > startPosX + gameSurface.getJoystickSize() / 8) {                    // Going right
                    currPosX = (float) (startPosX + gameSurface.getJoystickSize() / 2.0);
                    gameSurface.setCharacterHorizontalAcceleration(1, false);
                }
                else if (cx < startPosX - gameSurface.getJoystickSize() / 8) {             // Going left
                    currPosX = (float) (startPosX - gameSurface.getJoystickSize() / 2.0);
                    gameSurface.setCharacterHorizontalAcceleration(-1, false);
                }
                else {                           // if movement too small, do nothing
                    currPosX = startPosX;
                    gameSurface.setCharacterHorizontalAcceleration(0, true);
                }

                if ((cy < startPosY - gameSurface.getJoystickSize() / 8)  ) {     // Jump
                    if (currPosX < startPosX) {
                        currPosX = (float) (startPosX - gameSurface.getJoystickSize() / (2*1.41421356));
                        currPosY = (float) (startPosY - gameSurface.getJoystickSize() / (2*1.41421356));
                    }
                    else if (currPosX > startPosX) {
                        currPosX = (float) (startPosX + gameSurface.getJoystickSize() / (2*1.41421356));
                        currPosY = (float) (startPosY - gameSurface.getJoystickSize() / (2*1.41421356));
                    }
                    else {
                        currPosY = (float) (startPosY - gameSurface.getJoystickSize() / 2.0);
                    }
                    if (jumpFlag) {
                        gameSurface.setCharacterVerticalAcceleration(-1);
                        now = System.nanoTime();
                        jumpFlag = false;
                    }
                }
                else {
                    currPosY = startPosY;
                }
                gameSurface.setJoystickPoint(currPosX, currPosY);
            }
        }
    }
}
