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
import static com.example.mate_pc.game1.Constants.backgroundSettingsKey;
import static com.example.mate_pc.game1.Constants.controlSettingsKey;
import static com.example.mate_pc.game1.Constants.soundSettingsKey;


public class SettingsActivity extends Activity {

    Button doneBtn;
    Switch controlSwitch;
    Switch soundSwitch;
    ImageView background1;
    ImageView background2;
    ImageView background3;
    ImageView background4;
    int chosenBackgroundNumber = 2;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        getWindow().setBackgroundDrawableResource(R.color.transparent);


        //Local UI variables:
        doneBtn = findViewById(R.id.buttonDone);
        controlSwitch = findViewById(R.id.switchControl);
        soundSwitch = findViewById(R.id.switchSound);
        background1 = (ImageView)findViewById(R.id.imageView);
        background2 = (ImageView)findViewById(R.id.imageView2);
        background3 = (ImageView)findViewById(R.id.imageView3);
        background4 = (ImageView)findViewById(R.id.imageView4);

        //Get shared prefs:
        final SharedPreferences prefs = getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
        controlSwitch.setChecked(prefs.getBoolean(controlSettingsKey, false)); //false default
        soundSwitch.setChecked(prefs.getBoolean(soundSettingsKey, false)); //false default
        chosenBackgroundNumber = prefs.getInt(backgroundSettingsKey, 2);   //second background on default


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
                SettingsActivity.super.onBackPressed();
            }
        });

        // Switches:
        controlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
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
        prefs.edit().putBoolean(controlSettingsKey, controlSwitch.isChecked()).apply();
        prefs.edit().putBoolean(soundSettingsKey, soundSwitch.isChecked()).apply();
        prefs.edit().putInt(backgroundSettingsKey, chosenBackgroundNumber).apply();
    }



    private void alertGameSurface(int selectedBackground){
        Intent intent = new Intent();
        intent.setAction(GameSurface.MyBroadcastReceiver.ACTION);
        intent.putExtra(SELECTED_BACKGROUND_INTENT_EXTRA, selectedBackground);
        getApplicationContext().sendBroadcast(intent);
    }
}
