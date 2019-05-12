package com.example.mate_pc.game1.sound_stuff;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.mate_pc.game1.R;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.mate_pc.game1.Constants.MY_SETTINGS;
import static com.example.mate_pc.game1.Constants.SOUND_SETTINGS_KEY;

public class BackgroundSoundHandler {

    private Context mainActivityContext;
    private MediaPlayer mediaPlayer;    //mediaPlayer for background music

    public BackgroundSoundHandler(Context c){
        mainActivityContext = c;

        this.mediaPlayer = MediaPlayer.create(c, R.raw.background_music);
        this.mediaPlayer.seekTo(0);
    }

    public void destroy() {
        this.mediaPlayer.stop();
        this.mediaPlayer.release();
    }

    public void resume() {
        this.mediaPlayer.setLooping(true);
        this.mediaPlayer.start();
        setSound();
    }

    public void pause() {
        this.mediaPlayer.pause();
    }

    public void restart() {
        this.mediaPlayer.start();
        setSound();
    }


    public void setSound(){
        SharedPreferences prefs2 = mainActivityContext.getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
        boolean isSound = prefs2.getBoolean(SOUND_SETTINGS_KEY, false);
        AudioManager amanager;
        amanager = (AudioManager)mainActivityContext.getSystemService(AUDIO_SERVICE);

        if (isSound) {
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        } else {
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }
    }

}
