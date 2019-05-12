package com.example.mate_pc.game1;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.content.Intent;

import com.example.mate_pc.game1.network_stuff.ConnectorClass;
import com.example.mate_pc.game1.network_stuff.WebSocketClass;
import com.example.mate_pc.game1.sound_stuff.BackgroundSoundHandler;

import static com.example.mate_pc.game1.Constants.CONNECTOR_IP_KEY;
import static com.example.mate_pc.game1.Constants.START_CONNECTOR_CODE;
import static com.example.mate_pc.game1.Constants.START_GAME_MENU_CODE;
import static com.example.mate_pc.game1.Constants.WON_THE_MATCH_KEY;

public class MainActivity extends AppCompatActivity {

    GameSurface gameSurface;

    WebSocketClass webSocket;

    ControlInputHandler controlInputHandler;

    LinearLayout joystickSurface;

    boolean gameMenuVISIBLE;

    @SuppressLint("ClickableViewAccessibility") // Silencing "performClick" warning. Comment out this line to see affect.
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameMenuVISIBLE = false;

        gameSurface = new GameSurface(this, this);

        webSocket = new WebSocketClass(gameSurface);
        gameSurface.setWebSocket(webSocket);

        LinearLayout surface = findViewById(R.id.gameSurface);
        surface.addView(gameSurface);

        Intent intent = new Intent(MainActivity.this, ConnectorClass.class);
        startActivityForResult(intent, START_CONNECTOR_CODE);


        joystickSurface = findViewById(R.id.joystickSurface);
        joystickSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                controlInputHandler.onJoystickTouchListener(motionEvent);
                return true;
            }
        });


        new BackgroundSoundHandler(this);

        controlInputHandler = new ControlInputHandler(this, gameSurface);
        controlInputHandler.setControl();


        findViewById(R.id.shoot).setOnClickListener(controlInputHandler.onClickListener);

        findViewById(R.id.buttonUp).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                controlInputHandler.handleTouch(view, motionEvent);
                return true;
            }
        });

        findViewById(R.id.buttonLeft).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                controlInputHandler.handleTouch(view, motionEvent);
                return true;
            }
        });

        findViewById(R.id.buttonRight).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                controlInputHandler.handleTouch(view, motionEvent);
                return true;
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        gameMenuVISIBLE = false;

        controlInputHandler.setControl();

        webSocket.restart();

        if (data != null) {
            webSocket.setIP(data.getStringExtra(CONNECTOR_IP_KEY));
            webSocket.connect();
        }
        else {
            Log.i("MyTag", "EMPTY intent data");
        }
        gameSurface.restartGame();
        new CountDown(MainActivity.this, webSocket).execute();

    }



    public void showGameMenu(boolean wonTheMatch) {
        if(!gameMenuVISIBLE) {
            Intent intent = new Intent(MainActivity.this, GameMenuActivity.class);
            intent.putExtra(WON_THE_MATCH_KEY, wonTheMatch);
            startActivityForResult(intent, START_GAME_MENU_CODE);
            gameMenuVISIBLE = true;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MyTag", "destroy");

        gameSurface.destroy();

        webSocket.destroySocket();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MyTAG", "resume");

        gameSurface.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MyTAG", "pause");

        gameSurface.pause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("MyTAG", "restart");

        gameSurface.restart();
    }
}
