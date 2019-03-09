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

    private String others_ip;

    private boolean connected;

    WebSocketClient mWebSocketClient;
    ConnectionEstablishedListener connectionEstablishedListener;

    public WebSocket(ConnectionEstablishedListener connectionEstablishedListener) {
        this.connectionEstablishedListener = connectionEstablishedListener;
        this.connected = false;
        //Thread socketClientThread = new Thread(new SocketClientThread());
        //socketClientThread.start();
    }

    public void setIP(String others_ip){
        this.others_ip = others_ip;
    }

    private class SocketClientThread extends Thread {


        @Override
        public void run() {
            URI uri;
            try {
                String myUri = ""+others_ip+":8080";
                Log.i("MYTAG", "Connecting to: "+myUri);
                uri = new URI(myUri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }

            mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.i("MYTAG", "Opened");
                    mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                }

                @Override
                public void onMessage(String s) {
                    final String message = s;
                    if (s.compareTo("konichiwa") == 0){
                        connected = true;
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

    public boolean conn(){
        URI uri;
        try {
            String myUri = ""+others_ip+":8080";
            Log.i("MYTAG", "Connecting to: "+myUri);
            uri = new URI(myUri);
        } catch (URISyntaxException e) {
            Log.i("MYTAG", "some error......");
            e.printStackTrace();
            return false;
        }

        if (uri == null){
            //return false;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("MYTAG", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                if (s.compareTo("konichiwa") == 0){
                    connected = true;
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
        return true;
    }

    public void try2connect() {
        conn();
        while (!connected) {
            sendInitMsg();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }


    public void sendInitMsg(){
        mWebSocketClient.send("konichiwa");
    }

}
