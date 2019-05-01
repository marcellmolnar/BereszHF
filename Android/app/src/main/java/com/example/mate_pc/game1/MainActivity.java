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

import com.example.mate_pc.game1.graphical_stuff.Ricardo;
import com.example.mate_pc.game1.network_stuff.ConnectorClass;
import com.example.mate_pc.game1.network_stuff.WebSocketClass;
import com.example.mate_pc.game1.sound_stuff.BackgroundSoundHandler;

import static com.example.mate_pc.game1.Constants.CONNECTOR_IP_CODE;
import static com.example.mate_pc.game1.Constants.START_CONNECTOR_CODE;

public class MainActivity extends AppCompatActivity {

    GameSurface gameSurface;

    WebSocketClass webSocket;

    ControlInputHandler controlInputHandler;

    Button playButton;

    LinearLayout joystickSurface;

    @SuppressLint("ClickableViewAccessibility") // Silencing "performClick" warning. Comment out this line to see affect.
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameSurface = new GameSurface(this, this);

        // Passing gameSurface as GameSurface and as OnConnectionChangedListener.
        webSocket = new WebSocketClass(gameSurface, gameSurface);
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


        playButton = findViewById(R.id.play);
        playButton.setVisibility(View.INVISIBLE);       //Should be set to visible upon winning the game
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playIntent = new Intent(MainActivity.this, Ricardo.class);
                startActivity(playIntent);
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

        controlInputHandler.setControl();

        if (data != null) {
            webSocket.setIP(data.getStringExtra(CONNECTOR_IP_CODE));
            webSocket.connect();
        }
        else {
            Log.i("MyTAG", "EMPTY intent data");
        }
        new CountDown(MainActivity.this, webSocket).execute();

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MyTAG", "destroy");

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
