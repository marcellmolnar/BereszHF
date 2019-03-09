package com.example.mate_pc.game1.network_stuff;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mate_pc.game1.GameSurface;
import com.example.mate_pc.game1.R;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import static android.content.Context.WIFI_SERVICE;

public class ConnectorClass extends Activity implements ConnectionEstablishedListener{

    private Button connect;
    private WebSocket webSocket;
    private EditText editText;

    private GameSurface gameSurface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.connecter_layout);
        getWindow().setBackgroundDrawableResource(R.color.transparent);


        this.gameSurface = (GameSurface) getIntent().getSerializableExtra("gamesf");

        findViewById(R.id.create_socket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocketSever socketSever = new SocketSever(ConnectorClass.this, gameSurface);
            }
        });


        TextView myIP = findViewById(R.id.my_ip);

        myIP.setText(getMyIP()); //getMyIP()


        editText = findViewById(R.id.others_ip);

        connect = findViewById(R.id.connect);
        connect.setOnClickListener(onConnectListener);
    }

    View.OnClickListener onConnectListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            webSocket = new WebSocket(ConnectorClass.this);
            webSocket.setIP(editText.getText().toString());
            webSocket.try2connect();
        }
    };

    @Override
    public boolean onConnectionEstablished() {
        Log.i("MYTAG", "Connected");
        this.finish();
        return true;
    }

    private String getMyIP(){
        WifiManager wm = (WifiManager) getBaseContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm != null) {
            WifiInfo wifiinfo = wm.getConnectionInfo();
            byte[] myIPAddress = BigInteger.valueOf(wifiinfo.getIpAddress()).toByteArray();
            // you must reverse the byte array before conversion. Use Apache's commons library
            byte[] myIPAddressReverse = new byte[myIPAddress.length];
            for (int i = 0; i < myIPAddress.length; i++) {
                myIPAddressReverse[i] = myIPAddress[myIPAddress.length - i - 1];
            }
            InetAddress myInetIP = null;
            try {
                myInetIP = InetAddress.getByAddress(myIPAddressReverse);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            if (myInetIP != null) {
                return myInetIP.getHostAddress();
            } else {
                return "0.0.0.0";
            }
        }
        else {
            return "0.0.0.0";

        }
    }



}

