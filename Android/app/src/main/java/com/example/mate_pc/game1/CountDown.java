package com.example.mate_pc.game1;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.mate_pc.game1.network_stuff.WebSocketClass;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates a Dialog onto the screen, prohibiting the user from movements before the game starts.
 * It also show a counter like 3, 2, 1. When it reaches 0, this Dialog will be destroyed.
 */
public class CountDown extends AsyncTask<Void,Void, Void> {

    private Dialog dialog;
    private View view;
    private WebSocketClass webSocket;

    CountDown(Activity activity, WebSocketClass webSocket) {
        this.webSocket = webSocket;
        dialog = new Dialog(activity, R.style.Theme_AppCompat);
        view = View.inflate(activity.getApplicationContext(), R.layout.count_down_layout, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent3);
        dialog.setContentView(view);

        view.findViewById(R.id.countdown).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (!webSocket.isConnected2Player()){
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected void onProgressUpdate(Void... progress) {
        super.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(Void result) {
        view.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.connecting2player).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.countdown).setVisibility(View.VISIBLE);
        countdown();
    }

    private void countdown(){
        final Handler handler = new Handler();
        final TextView textView = view.findViewById(R.id.countdown);
        final java.util.concurrent.atomic.AtomicInteger n = new AtomicInteger(3);
        final Runnable counter = new Runnable() {
            @Override
            public void run() {
                textView.setText(String.valueOf(n.get()));
                if(n.getAndDecrement() >= 1 )
                    handler.postDelayed(this, 1000);
                else {
                    textView.setVisibility(View.GONE);
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }
        };
        handler.postDelayed(counter, 1000);
    }
}