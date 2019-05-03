package com.example.mate_pc.game1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import static com.example.mate_pc.game1.Constants.MY_SETTINGS;
import static com.example.mate_pc.game1.Constants.SELECTED_BACKGROUND_INTENT_EXTRA;
import static com.example.mate_pc.game1.Constants.BACKGROUND_SETTINGS_KEY;
import static com.example.mate_pc.game1.Constants.CONTROL_SETTINGS_KEY;
import static com.example.mate_pc.game1.Constants.SHOW_JOYSTICK_SETTINGS_KEY;
import static com.example.mate_pc.game1.Constants.SOUND_SETTINGS_KEY;


public class SettingsActivity extends Activity {

    Button doneBtn;
    Switch controlSwitch;
    Switch showJoystick;
    Switch soundSwitch;
    ImageView background1;
    ImageView background2;
    ImageView background3;
    ImageView background4;
    int chosenBackgroundNumber = 2;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MyTag", "still good");
        setContentView(R.layout.settings_layout);
        Log.i("MyTag", "nincs itt baj");
        getWindow().setBackgroundDrawableResource(R.color.transparent);


        //Local UI variables:
        doneBtn = findViewById(R.id.buttonDone);
        controlSwitch = findViewById(R.id.switchControl);
        showJoystick = findViewById(R.id.showJoystick);
        soundSwitch = findViewById(R.id.switchSound);
        background1 = findViewById(R.id.imageView);
        background2 = findViewById(R.id.imageView2);
        background3 = findViewById(R.id.imageView3);
        background4 = findViewById(R.id.imageView4);

        //Get shared prefs:
        final SharedPreferences prefs = getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
        controlSwitch.setChecked(prefs.getBoolean(CONTROL_SETTINGS_KEY, false)); //false default
        showJoystick.setChecked(prefs.getBoolean(SHOW_JOYSTICK_SETTINGS_KEY, true)); //true default
        soundSwitch.setChecked(prefs.getBoolean(SOUND_SETTINGS_KEY, false)); //false default
        chosenBackgroundNumber = prefs.getInt(BACKGROUND_SETTINGS_KEY, 2);   //second background on default

        if(controlSwitch.isChecked()) {
            showJoystick.setClickable(true);
            showJoystick.setAlpha((float) 1.0);
            findViewById(R.id.showJoystickText).setAlpha((float) 1.0);
            findViewById(R.id.showJoystickText2).setAlpha((float) 1.0);
        }
        else {
            showJoystick.setClickable(false);
            showJoystick.setAlpha((float) 0.25);
            findViewById(R.id.showJoystickText).setAlpha((float) 0.25);
            findViewById(R.id.showJoystickText2).setAlpha((float) 0.25);
        }

        //Set starting state of images:
        switch (chosenBackgroundNumber){
            case 1:
                background1.setColorFilter(Color.argb(0, 0, 0, 0));
                background2.setColorFilter(Color.argb(75, 0, 0, 0));
                background3.setColorFilter(Color.argb(75, 0, 0, 0));
                background4.setColorFilter(Color.argb(75, 0, 0, 0));
                break;
            case 3:
                background3.setColorFilter(Color.argb(0, 0, 0, 0));
                background2.setColorFilter(Color.argb(75, 0, 0, 0));
                background1.setColorFilter(Color.argb(75, 0, 0, 0));
                background4.setColorFilter(Color.argb(75, 0, 0, 0));
                break;
            case 4:
                background4.setColorFilter(Color.argb(0, 0, 0, 0));
                background2.setColorFilter(Color.argb(75, 0, 0, 0));
                background3.setColorFilter(Color.argb(75, 0, 0, 0));
                background1.setColorFilter(Color.argb(75, 0, 0, 0));
                break;
            case 2:
            default:
                background2.setColorFilter(Color.argb(0, 0, 0, 0));
                background1.setColorFilter(Color.argb(75, 0, 0, 0));
                background3.setColorFilter(Color.argb(75, 0, 0, 0));
                background4.setColorFilter(Color.argb(75, 0, 0, 0));
                break;
        }


        // Done button:
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SettingsActivity.super.onBackPressed();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        // Switches:
        controlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // When buttons are selected, no selection option for joystick
                if(isChecked) {
                    showJoystick.setClickable(true);
                    showJoystick.setAlpha((float) 1.0);
                    findViewById(R.id.showJoystickText).setAlpha((float) 1.0);
                    findViewById(R.id.showJoystickText2).setAlpha((float) 1.0);
                }
                else {
                    showJoystick.setClickable(false);
                    showJoystick.setAlpha((float) 0.25);
                    findViewById(R.id.showJoystickText).setAlpha((float) 0.25);
                    findViewById(R.id.showJoystickText2).setAlpha((float) 0.25);
                }
            }
        });

        showJoystick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });


        //On click listeners for changing wallpapers,
        //when clicked, it changes a shared-preferences variable which is then read in MainActivity:
        background1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    background1.setColorFilter(Color.argb(0, 0, 0, 0));
                    background2.setColorFilter(Color.argb(75, 0, 0, 0));
                    background3.setColorFilter(Color.argb(75, 0, 0, 0));
                    background4.setColorFilter(Color.argb(75, 0, 0, 0));
                    chosenBackgroundNumber = 1;
                    alertGameSurface(chosenBackgroundNumber);
                }
                return false;
            }
        });

        background2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    background2.setColorFilter(Color.argb(0, 0, 0, 0));
                    background1.setColorFilter(Color.argb(75, 0, 0, 0));
                    background3.setColorFilter(Color.argb(75, 0, 0, 0));
                    background4.setColorFilter(Color.argb(75, 0, 0, 0));
                    chosenBackgroundNumber = 2;
                    alertGameSurface(chosenBackgroundNumber);
                }
                return false;
            }
        });

        background3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    background3.setColorFilter(Color.argb(0, 0, 0, 0));
                    background2.setColorFilter(Color.argb(75, 0, 0, 0));
                    background1.setColorFilter(Color.argb(75, 0, 0, 0));
                    background4.setColorFilter(Color.argb(75, 0, 0, 0));
                    chosenBackgroundNumber = 3;
                    alertGameSurface(chosenBackgroundNumber);
                }
                return false;
            }
        });

        background4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    background4.setColorFilter(Color.argb(0, 0, 0, 0));
                    background2.setColorFilter(Color.argb(75, 0, 0, 0));
                    background3.setColorFilter(Color.argb(75, 0, 0, 0));
                    background1.setColorFilter(Color.argb(75, 0, 0, 0));
                    chosenBackgroundNumber = 4;
                    alertGameSurface(chosenBackgroundNumber);
                }
                return false;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Saving the shared preferences upon exit:
        final SharedPreferences prefs = getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
        prefs.edit().putBoolean(CONTROL_SETTINGS_KEY, controlSwitch.isChecked()).apply();
        prefs.edit().putBoolean(SHOW_JOYSTICK_SETTINGS_KEY, showJoystick.isChecked()).apply();
        prefs.edit().putBoolean(SOUND_SETTINGS_KEY, soundSwitch.isChecked()).apply();
        prefs.edit().putInt(BACKGROUND_SETTINGS_KEY, chosenBackgroundNumber).apply();
    }



    // new broadcast event
    private void alertGameSurface(int selectedBackground){
        Intent intent = new Intent();
        intent.setAction(GameSurface.MyBroadcastReceiver.ACTION);
        intent.putExtra(SELECTED_BACKGROUND_INTENT_EXTRA, selectedBackground);
        getApplicationContext().sendBroadcast(intent);
    }
}
