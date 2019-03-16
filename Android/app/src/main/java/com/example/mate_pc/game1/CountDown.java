package com.example.mate_pc.game1;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.mate_pc.game1.network_stuff.WebSocketClass;

import java.util.concurrent.atomic.AtomicInteger;


public class CountDown extends AsyncTask<Void,Void, Void> {

    private Activity activity;
    private Dialog dialog;
    private View view;
    private WebSocketClass webSocket;

    CountDown(Activity activity, WebSocketClass webSocket) {
        this.activity = activity;
        this.webSocket = webSocket;
        dialog = new Dialog(activity, R.style.Theme_AppCompat);
        view = LayoutInflater.from(activity).inflate(R.layout.connector2player_layout, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent3);
        dialog.setContentView(view);

        view.findViewById(R.id.countdown).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (!webSocket.isConnected()){
            // ToDo: send "join" here!! (not in GameSurface)
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
                textView.setText(Integer.toString(n.get()));
                if(n.getAndDecrement() >= 1 )
                    handler.postDelayed(this, 1000);
                else {
                    textView.setVisibility(View.GONE);
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Log.i("MyTag", "done");
                }
            }
        };
        handler.postDelayed(counter, 1000);
    }
}