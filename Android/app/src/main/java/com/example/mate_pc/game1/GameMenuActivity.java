package com.example.mate_pc.game1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mate_pc.game1.graphical_stuff.Ricardo;

import static com.example.mate_pc.game1.Constants.RESULT_CODE_CONNECTOR_RETURN;
import static com.example.mate_pc.game1.Constants.RESULT_CODE_SETTINGS_MAY_CHANGED;
import static com.example.mate_pc.game1.Constants.START_SETTINGS_CODE;
import static com.example.mate_pc.game1.Constants.WON_THE_MATCH_KEY;

public class GameMenuActivity extends Activity {

    private Button settingsBtn;
    private TextView basic_text;
    private Button playAgain;
    private Button playVideo;

    private int activityResultCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_menu_layout);
        getWindow().setBackgroundDrawableResource(R.color.transparent2);

        Intent creatorIntent = getIntent();
        boolean wonMatch = creatorIntent.getBooleanExtra(WON_THE_MATCH_KEY,false);

        basic_text = findViewById(R.id.basic_text);

        playAgain = findViewById(R.id.playAgain);
        playAgain.setOnClickListener(onConnectListener);


        playVideo = findViewById(R.id.play);
        if (wonMatch) {
            playVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent playIntent = new Intent(GameMenuActivity.this, Ricardo.class);
                    startActivity(playIntent);
                }
            });
        }
        else {
            playVideo.setVisibility(View.INVISIBLE);
        }


        settingsBtn = findViewById(R.id.settings_Btn);
        settingsBtn.setBackground(getDrawable(R.drawable.settings_icon));

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsBtn.setVisibility(View.INVISIBLE);
                basic_text.setVisibility(View.INVISIBLE);
                playAgain.setVisibility(View.INVISIBLE);
                playVideo.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(GameMenuActivity.this, SettingsActivity.class);
                startActivityForResult(intent, START_SETTINGS_CODE);
            }
        });

        activityResultCode = RESULT_CODE_CONNECTOR_RETURN;
    }

    View.OnClickListener onConnectListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent data = new Intent();
            setResult(activityResultCode);
            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        settingsBtn.setVisibility(View.VISIBLE);
        basic_text.setVisibility(View.VISIBLE);
        playAgain.setVisibility(View.VISIBLE);
        playVideo.setVisibility(View.VISIBLE);
        activityResultCode = RESULT_CODE_SETTINGS_MAY_CHANGED;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // ToDo: delete these lines to not allow to start game, without connection
        setResult(activityResultCode);
        finish();
    }
}