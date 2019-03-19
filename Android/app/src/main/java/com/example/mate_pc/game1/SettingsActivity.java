package com.example.mate_pc.game1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import static com.example.mate_pc.game1.Constants.controlSettings;
import static com.example.mate_pc.game1.Constants.controlSettingsKey;
import static com.example.mate_pc.game1.Constants.soundSettings;
import static com.example.mate_pc.game1.Constants.soundSettingsKey;


public class SettingsActivity extends Activity {
    Button doneBtn;
    Switch controlSwitch;
    Switch soundSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        doneBtn = findViewById(R.id.buttonDone);
        controlSwitch = findViewById(R.id.switchControl);
        soundSwitch = findViewById(R.id.switchSound);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.super.onBackPressed();
            }
        });
        final SharedPreferences prefs = getSharedPreferences(controlSettings, MODE_PRIVATE);
        controlSwitch.setChecked(prefs.getBoolean(controlSettingsKey, false)); //false default

        controlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });


        final SharedPreferences prefs2 = getSharedPreferences(soundSettings, MODE_PRIVATE);
        soundSwitch.setChecked(prefs2.getBoolean(soundSettingsKey, false)); //false default

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        final SharedPreferences prefs = getSharedPreferences(controlSettings, MODE_PRIVATE);
        prefs.edit().putBoolean(controlSettingsKey, controlSwitch.isChecked()).apply();

        final SharedPreferences prefs2 = getSharedPreferences(soundSettings, MODE_PRIVATE);
        prefs2.edit().putBoolean(soundSettingsKey, soundSwitch.isChecked()).apply();
    }
}
