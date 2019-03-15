package com.example.mate_pc.game1.network_stuff;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.mate_pc.game1.GameSurface;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClass implements Parcelable {

    private WebSocketClient mWebSocketClient;

    private GameSurface gameSurface;
    private String message = "";

    private String ipAddress;

    private boolean connected;

    public WebSocketClass(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
        this.connected = false;
    }

    private WebSocketClass(Parcel in) {
        message = in.readString();
        ipAddress = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeString(ipAddress);
    }
    public static final Creator<WebSocketClass> CREATOR = new Creator<WebSocketClass>() {
        @Override
        public WebSocketClass createFromParcel(Parcel in) {
            return new WebSocketClass(in);
        }

        @Override
        public WebSocketClass[] newArray(int size) {
            return new WebSocketClass[size];
        }
    };




    public void destroySocket() {
        Log.i("MyTAG","Server killed..");
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
        }
    }

    public void send(String s) {
        if (connected){
            mWebSocketClient.send(s);
        }
    }

    public void setIP(String ip) {
        this.ipAddress = ip;
    }

    public void connect() {
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            URI uri;
            try {
                String myUri = "ws://" + WebSocketClass.this.ipAddress + ":8080";
                Log.i("WebSocket", "Connecting to: " + myUri);
                uri = new URI(myUri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }

            mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.i("WebSocket", "Opened");
                    mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                    WebSocketClass.this.connected = true;
                }

                @Override
                public void onMessage(String s) {
                    double x = 600;
                    double y = 700;
                    try {
                        JSONObject reader = new JSONObject(s);

                        JSONObject main  = reader.getJSONObject("character");
                        x = main.getDouble("x");
                        y = main.getDouble("y");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    gameSurface.setOpponentXY(x, y);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i("WebSocket", "Closed " + s);
                }

                @Override
                public void onError(Exception e) {
                    Log.i("WebSocket", "Error " + e.getMessage());
                }
            };
            mWebSocketClient.connect();
        }
    }


}