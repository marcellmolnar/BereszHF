package com.example.mate_pc.game1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


public class MyOtherActivity extends AppCompatActivity {
    Button doneBtn;
    Switch controlSwitch;

    boolean isok = false;
    String controlSettingsKey = "controlSettings_isJoystick";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_activity_main);

        //findViewById(R.id.root).setAlpha((float)0.4);

        doneBtn = findViewById(R.id.buttonDone);
        controlSwitch = findViewById(R.id.switchControl);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyOtherActivity.super.onBackPressed();
            }
        });
        final SharedPreferences prefs = getSharedPreferences("controlSettings", MODE_PRIVATE);
        controlSwitch.setChecked(prefs.getBoolean(controlSettingsKey, false)); //false default

        controlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isok = prefs.edit().putBoolean(controlSettingsKey, true).commit();
                } else {
                    isok = prefs.edit().putBoolean(controlSettingsKey, false).commit();
                }
            }
        });



    }


}
