package com.example.mate_pc.game1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.mate_pc.game1.Constants;


public class MyOtherActivity extends Activity {
    Button doneBtn;
    Switch controlSwitch;

    boolean isok = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_activity_main);
        getWindow().setBackgroundDrawableResource(R.color.transparent2);
        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        doneBtn = findViewById(R.id.buttonDone);
        controlSwitch = findViewById(R.id.switchControl);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyOtherActivity.super.onBackPressed();
            }
        });
        final SharedPreferences prefs = getSharedPreferences(Constants.controlSettings, MODE_PRIVATE);
        controlSwitch.setChecked(prefs.getBoolean(Constants.controlSettingsKey, false)); //false default

        controlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        final SharedPreferences prefs = getSharedPreferences(Constants.controlSettings, MODE_PRIVATE);
        if (controlSwitch.isChecked()) {
            isok = prefs.edit().putBoolean(Constants.controlSettingsKey, true).commit();
        } else {
            isok = prefs.edit().putBoolean(Constants.controlSettingsKey, false).commit();
        }
    }
}
