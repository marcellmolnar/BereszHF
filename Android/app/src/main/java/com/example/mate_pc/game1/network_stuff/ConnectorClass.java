package com.example.mate_pc.game1.network_stuff;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mate_pc.game1.SettingsActivity;
import com.example.mate_pc.game1.R;

import java.util.regex.Pattern;

import static com.example.mate_pc.game1.Constants.CONNECTOR_IP_INTENT_EXTRA_KEY;
import static com.example.mate_pc.game1.Constants.RESULT_CODE_CONNECTOR_RETURN;
import static com.example.mate_pc.game1.Constants.RESULT_CODE_SETTINGS_MAY_CHANGED;
import static com.example.mate_pc.game1.Constants.START_SETTINGS_CODE;

/**
 *  Implements the GUI of the pre-connection state of the game.
 */
public class ConnectorClass extends Activity {

    private Button settingsBtn;
    private TextView writeIpHereText;
    private TextView ip_error_text;
    private Button connect;
    private EditText ipAddress;

    private int activityResultCode;

    private static final Pattern ipRegExPattern
            = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connector_layout);
        getWindow().setBackgroundDrawableResource(R.color.transparent2);


        ipAddress = findViewById(R.id.server_ip);
        ipAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ip_error_text.setText("");
            }
        });

        writeIpHereText = findViewById(R.id.write_ip_here_text);
        ip_error_text = findViewById(R.id.ip_error_text);

        connect = findViewById(R.id.connect);
        connect.setOnClickListener(onConnectListener);


        settingsBtn = findViewById(R.id.settings_Btn);
        settingsBtn.setBackground(getDrawable(R.drawable.settings_icon));

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsBtn.setVisibility(View.INVISIBLE);
                writeIpHereText.setVisibility(View.INVISIBLE);
                ip_error_text.setVisibility(View.INVISIBLE);
                connect.setVisibility(View.INVISIBLE);
                ipAddress.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(ConnectorClass.this, SettingsActivity.class);
                startActivityForResult(intent, START_SETTINGS_CODE);
            }
        });

        activityResultCode = RESULT_CODE_CONNECTOR_RETURN;
    }

    private boolean isIpValid(CharSequence s) {
        return ipRegExPattern.matcher(s).matches();
    }

    View.OnClickListener onConnectListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isIpValid(ipAddress.getText().toString())){
                Intent data = new Intent();
                data.putExtra(CONNECTOR_IP_INTENT_EXTRA_KEY, ipAddress.getText().toString());
                setResult(activityResultCode, data);
                finish();
            }
            else {
                ip_error_text.setText(R.string.ip_error);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        settingsBtn.setVisibility(View.VISIBLE);
        writeIpHereText.setVisibility(View.VISIBLE);
        ip_error_text.setVisibility(View.VISIBLE);
        connect.setVisibility(View.VISIBLE);
        ipAddress.setVisibility(View.VISIBLE);
        activityResultCode = RESULT_CODE_SETTINGS_MAY_CHANGED;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // ToDo: delete these lines to not allow to start game, without connection
        Intent data = new Intent();
        data.putExtra(CONNECTOR_IP_INTENT_EXTRA_KEY, ipAddress.getText().toString());
        setResult(activityResultCode, data);
        finish();
    }
}

