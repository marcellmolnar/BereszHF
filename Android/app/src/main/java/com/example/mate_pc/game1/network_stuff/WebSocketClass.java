package com.example.mate_pc.game1.network_stuff;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.mate_pc.game1.GameSurface;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClass implements Parcelable {

    private WebSocketClient mWebSocketClient;

    private GameSurface gameSurface;
    private String message = "";

    private String ipAddress;

    private boolean connected2server;
    private boolean connected2player;

    private OnConnectionChangedListener onConnectionChangedListener;

    public WebSocketClass(GameSurface gameSurface, OnConnectionChangedListener onConnectionChangedListener) {
        this.gameSurface = gameSurface;
        this.onConnectionChangedListener = onConnectionChangedListener;
        this.connected2server = false;
        this.connected2player = false;
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


    public boolean isConnected(){
        return this.connected2player;
    }


    public void destroySocket() {
        Log.i("MyTAG","Server killed..");
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
        }
    }

    public void send(String s) {
        if (connected2server){
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
                    WebSocketClass.this.connected2server = true;
                }

                @Override
                public void onMessage(String s) {
                    double x;
                    double y;
                    int OwnHealth;
                    try {
                        JSONObject reader = new JSONObject(s);

                        JSONObject main  = reader.getJSONObject("character");
                        x = main.getDouble("x");
                        y = main.getDouble("y");
                        OwnHealth = main.getInt("opponentHealth");
                        gameSurface.setOpponentXY(x, y);
                        gameSurface.setOwnHealth(OwnHealth);


                        JSONArray bullets = reader.getJSONArray("bullets");
                        double[][] bulletCoordinates = new double[bullets.length()][3];
                        boolean[] isBulletSeeingRight = new boolean[bullets.length()];
                        for(int i=0; i<bullets.length(); i++){

                            JSONObject json_data = bullets.getJSONObject(i);
                            double bulletX = json_data.getDouble("x");
                            double bulletY = json_data.getDouble("y");
                            boolean isSeeingRight = json_data.getBoolean("isSeeingRight");

                            bulletCoordinates[i][0] = bulletX;
                            bulletCoordinates[i][1] = bulletY;
                            isBulletSeeingRight[i] = isSeeingRight;

                        }

                        gameSurface.createNewOpponentBullet(bulletCoordinates, isBulletSeeingRight, bullets.length());

                    } catch (JSONException e) {
                        //e.printStackTrace();
                        if (s.compareTo("join") == 0) {
                            if (!WebSocketClass.this.isConnected()) {
                                mWebSocketClient.send("join");
                            }
                            WebSocketClass.this.connected2player = true;
                            onConnectionChangedListener.onConnectionChanged();
                        }
                    }
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