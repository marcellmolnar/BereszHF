package com.example.mate_pc.game1.network_stuff;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mate_pc.game1.SettingsActivity;
import com.example.mate_pc.game1.R;

import static com.example.mate_pc.game1.Constants.CONNECTOR_IP_CODE;
import static com.example.mate_pc.game1.Constants.RESULT_CODE_CONNECTOR_RETURN;
import static com.example.mate_pc.game1.Constants.RESULT_CODE_SETTINGS_MAY_CHANGED;
import static com.example.mate_pc.game1.Constants.START_SETTINGS_CODE;

public class ConnectorClass extends Activity {

    private Button settingsBtn;
    private TextView writeIpHereText;
    private Button connect;
    private EditText ipAddress;

    private int activityResultCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connector_layout);
        getWindow().setBackgroundDrawableResource(R.color.transparent2);


        ipAddress = findViewById(R.id.others_ip);

        writeIpHereText = findViewById(R.id.write_ip_here_text);

        connect = findViewById(R.id.connect);
        connect.setOnClickListener(onConnectListener);


        settingsBtn = findViewById(R.id.settings_Btn);
        settingsBtn.setBackground(getDrawable(R.drawable.settings_icon));

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsBtn.setVisibility(View.INVISIBLE);
                writeIpHereText.setVisibility(View.INVISIBLE);
                connect.setVisibility(View.INVISIBLE);
                ipAddress.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(ConnectorClass.this, SettingsActivity.class);
                startActivityForResult(intent, START_SETTINGS_CODE);
            }
        });

        activityResultCode = RESULT_CODE_CONNECTOR_RETURN;
    }

    View.OnClickListener onConnectListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent data = new Intent();
            data.putExtra(CONNECTOR_IP_CODE, ipAddress.getText().toString());
            setResult(activityResultCode, data);
            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        settingsBtn.setVisibility(View.VISIBLE);
        writeIpHereText.setVisibility(View.VISIBLE);
        connect.setVisibility(View.VISIBLE);
        ipAddress.setVisibility(View.VISIBLE);
        activityResultCode = RESULT_CODE_SETTINGS_MAY_CHANGED;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // ToDo: delete these lines to not allow to start game, without connection
        Intent data = new Intent();
        data.putExtra(CONNECTOR_IP_CODE, ipAddress.getText().toString());
        setResult(activityResultCode, data);
        finish();
    }
}

