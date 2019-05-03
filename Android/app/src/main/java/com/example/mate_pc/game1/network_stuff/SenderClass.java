package com.example.mate_pc.game1.network_stuff;

import com.example.mate_pc.game1.GameSurface;
import com.example.mate_pc.game1.graphical_stuff.Bullet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SenderClass extends Thread {

    private GameSurface gameSurface;

    private WebSocketClass webSocket;

    private boolean running;

    public SenderClass(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
    }

    public void setWebSocket(WebSocketClass webSocket) {
        this.webSocket = webSocket;
    }

    @Override
    public void run() {


        while(running)  {
            long waitTime = 10;

            try {
                // Sleep.
                sleep(waitTime);
            } catch(InterruptedException ignored)  {
            }

            JSONObject json = new JSONObject();  //send position, opponent's health and bullets

            if (webSocket.isConnected2Player()) {
                JSONObject characterJson = new JSONObject();
                try {
                    characterJson.put("x", (double) (gameSurface.getWidth() - gameSurface.getCharacter().getWidth() -
                            gameSurface.getCharacter().getX()) / gameSurface.getWidth());
                    characterJson.put("y", (double) (gameSurface.getCharacter().getY()) / gameSurface.getHeight());
                    characterJson.put("opponentHealth", gameSurface.getOpponent().getHealth());
                    json.put("character", characterJson);
                } catch (JSONException je) {
                    je.printStackTrace();
                }

                //send own bullets
                JSONArray jsonArray = new JSONArray();
                for (Bullet bullet : gameSurface.getMyBullets()) {
                    JSONObject jbullet = new JSONObject();
                    try {
                        jbullet.put("x", (double) (gameSurface.getWidth() - bullet.getWidth() -
                                bullet.getX()) / gameSurface.getWidth());
                        jbullet.put("y", (double) (bullet.getY()) / gameSurface.getHeight());
                        jbullet.put("isSeeingRight", bullet.isSeeingToRight());
                        jsonArray.put(jbullet);
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                }
                try {
                    json.put("bullets", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                webSocket.send(json.toString());
            }
            if(webSocket.shouldTry2ConnectPlayer()) {
                // ToDo: sending "join" maybe good here. Not sure.
                webSocket.send("join");
            }

        }

    }

    public void setRunning(boolean running)  {
        this.running = running;
    }

}
