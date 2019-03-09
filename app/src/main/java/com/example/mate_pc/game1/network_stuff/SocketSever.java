package com.example.mate_pc.game1.network_stuff;

import android.util.Log;

import com.example.mate_pc.game1.GameSurface;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class SocketSever {
    WebSocketServer mWebSocketClient;

    ConnectionEstablishedListener connectionEstablishedListener;
    GameSurface gameSurface;
    ServerSocket serverSocket;
    String message = "";
    static final int socketServerPORT = 8080;

    public SocketSever(ConnectionEstablishedListener connectionEstablishedListener, GameSurface gameSurface) {
        this.connectionEstablishedListener = connectionEstablishedListener;
        this.gameSurface = gameSurface;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void onDestroy() {
        Log.i("MYTAG","Server killed..");
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        int count = 0;

        @Override
        public void run() {

            try {
                mWebSocketClient = new WebSocketServer() {
                    @Override
                    public void onOpen(WebSocket conn, ClientHandshake handshake) {
                        count++;
                        connectionEstablishedListener.onConnectionEstablished();
                    }

                    @Override
                    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

                    }

                    @Override
                    public void onMessage(WebSocket conn, String message) {
                        Log.i("MYTAG", message);
                    }

                    @Override
                    public void onError(WebSocket conn, Exception ex) {

                    }
                };
            } catch (UnknownHostException e) {
                Log.i("MYTAG", "Some error in creating server..");
                e.printStackTrace();
            }


        }
    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from Server, you are #" + cnt;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += "replayed: " + msgReply + "\n";



            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }


        }

    }


}