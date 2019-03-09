package com.example.mate_pc.game1.network_stuff;

import android.os.Build;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocket {

    private Socket client;

    private String others_ip;

    WebSocketClient mWebSocketClient;
    ConnectionEstablishedListener connectionEstablishedListener;

    public WebSocket(ConnectionEstablishedListener connectionEstablishedListener, String others_ip) {
        this.connectionEstablishedListener = connectionEstablishedListener;
        this.others_ip = others_ip;
    }


    private void connectWebSocket() {
        URI uri;
        try {
            String myUri = "ws://"+others_ip+":8080";
            Log.i("Websocket", "Connecting to: "+myUri);
            uri = new URI(myUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                if (s.compareTo("konichiwa") == 0){
                    connectionEstablishedListener.onConnectionEstablished();
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

}
