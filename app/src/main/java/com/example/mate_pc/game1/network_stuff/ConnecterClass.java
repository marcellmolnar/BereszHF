package com.example.mate_pc.game1.network_stuff;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.mate_pc.game1.R;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static android.content.Context.WIFI_SERVICE;

public class ConnecterClass extends Activity implements ConnectionEstablishedListener{

    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.connecter_layout);
        getWindow().setBackgroundDrawableResource(R.color.transparent);

        /*dialog = new Dialog(this, R.style.Theme_AppCompat);
        View view = LayoutInflater.from(this).inflate(R.layout.connecter_layout, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog..setContentView(view);*/


        TextView myIP = findViewById(R.id.my_ip);
        myIP.setText(getMyIP());
    }

    @Override
    public boolean onConnectionEstablished(){
        this.finish();
        return true;
    }

    private String getMyIP(){
        WifiManager wm = (WifiManager) getBaseContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiinfo = wm.getConnectionInfo();
        byte[] myIPAddress = BigInteger.valueOf(wifiinfo.getIpAddress()).toByteArray();
        // you must reverse the byte array before conversion. Use Apache's commons library
        byte[] myIPAddressReverse = new byte[myIPAddress.length];
        for (int i = 0; i < myIPAddress.length; i++){
            myIPAddressReverse[i] = myIPAddress[myIPAddress.length-i-1];
        }
        InetAddress myInetIP = null;
        try {
            myInetIP = InetAddress.getByAddress(myIPAddressReverse);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (myInetIP != null) {
            return myInetIP.getHostAddress();
        }
        else {
            return "0.0.0.0";
        }
    }



}

