package com.example.mate_pc.game1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mate_pc.game1.Constants.CONTROL_SETTINGS_KEY;
import static com.example.mate_pc.game1.Constants.MY_SETTINGS;
import static com.example.mate_pc.game1.Constants.SHOW_JOYSTICK_SETTINGS_KEY;

class ControlInputHandler {

    private Button shootButton;
    private boolean alreadyShooting;

    private Button left;
    private Button right;
    private Button up;

    private Activity mainActivity;

    private GameSurface gameSurface;

    // for shot button animation
    private final static int INTERVAL = 200;
    private Handler mHandler = new Handler();

    ControlInputHandler(Activity mainActivity, GameSurface gameSurface) {
        this.mainActivity = mainActivity;

        this.gameSurface = gameSurface;

        up = mainActivity.findViewById(R.id.buttonUp);
        left = mainActivity.findViewById(R.id.buttonLeft);
        right = mainActivity.findViewById(R.id.buttonRight);
        shootButton = mainActivity.findViewById(R.id.shoot);
        alreadyShooting = false;
    }



    void setControl(){
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


    void handleTouch(View view, MotionEvent event) {
        int action = event.getAction();
        if (view.getId() == R.id.buttonUp){
            if (action == MotionEvent.ACTION_DOWN){
                jump();
                up.setBackground(mainActivity.getDrawable(R.drawable.ic_arrow_upward_black_24dp_pressed));
            }
            else if (action == MotionEvent.ACTION_UP) {
                up.setBackground(mainActivity.getDrawable(R.drawable.ic_arrow_upward_black_24dp));
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



    private int counter = 0;
    private Runnable mHandlerTask = new Runnable()
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

    private void updateShootButtonState() {
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

    private void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    private void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }


    private void setJoystickUsage(boolean useJoystick, boolean showTheJoystick){
        this.usingJoystick = useJoystick;
        this.showTheJoystick = showTheJoystick;
    }

    private boolean usingJoystick = false;
    private boolean showTheJoystick = true;

    private float startPosX;
    private float startPosY;

    private boolean jumpFlag = true;
    private long now;


    void onJoystickTouchListener(MotionEvent motionEvent) {
        if(usingJoystick) {
            int action = motionEvent.getAction();
            if (System.nanoTime() >= now + 500000000) {
                jumpFlag = true;
            }
            if (action == MotionEvent.ACTION_DOWN) {
                startPosX = motionEvent.getX();
                startPosY = motionEvent.getY();
                // only show joystick when it's enabled in the settings
                gameSurface.setJoystickVisibility(showTheJoystick, startPosX, startPosY);
            }
            else if (action == MotionEvent.ACTION_UP) {
                gameSurface.setCharacterHorizontalAcceleration(0, true);
                gameSurface.setJoystickVisibility(false, 0,0);
            }
            else if (action == MotionEvent.ACTION_MOVE) {
                float cx = motionEvent.getX();
                float cy = motionEvent.getY();
                float currJoystickPosX;
                float currJoystickPosY;

                if (cx > startPosX + gameSurface.getJoystickSize() / 8) {                    // Going right
                    currJoystickPosX = (float) (startPosX + gameSurface.getJoystickSize() / 2.0);
                    gameSurface.setCharacterHorizontalAcceleration(1, false);
                }
                else if (cx < startPosX - gameSurface.getJoystickSize() / 8) {             // Going left
                    currJoystickPosX = (float) (startPosX - gameSurface.getJoystickSize() / 2.0);
                    gameSurface.setCharacterHorizontalAcceleration(-1, false);
                }
                else {                           // if movement too small, do nothing
                    currJoystickPosX = startPosX;
                    gameSurface.setCharacterHorizontalAcceleration(0, true);
                }

                if ((cy < startPosY - gameSurface.getJoystickSize() / 8)  ) {     // Jump
                    if (currJoystickPosX < startPosX) {
                        currJoystickPosX = (float) (startPosX - gameSurface.getJoystickSize() / (2*1.41421356));
                        currJoystickPosY = (float) (startPosY - gameSurface.getJoystickSize() / (2*1.41421356));
                    }
                    else if (currJoystickPosX > startPosX) {
                        currJoystickPosX = (float) (startPosX + gameSurface.getJoystickSize() / (2*1.41421356));
                        currJoystickPosY = (float) (startPosY - gameSurface.getJoystickSize() / (2*1.41421356));
                    }
                    else {
                        currJoystickPosY = (float) (startPosY - gameSurface.getJoystickSize() / 2.0);
                    }
                    if (jumpFlag) {
                        gameSurface.setCharacterVerticalAcceleration(-1);
                        now = System.nanoTime();
                        jumpFlag = false;
                    }
                }
                else {
                    currJoystickPosY = startPosY;
                }
                gameSurface.setJoystickPoint(currJoystickPosX, currJoystickPosY);
            }
        }
    }
}
