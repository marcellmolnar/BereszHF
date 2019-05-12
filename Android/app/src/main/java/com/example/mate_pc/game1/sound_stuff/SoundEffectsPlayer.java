package com.example.mate_pc.game1.sound_stuff;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.example.mate_pc.game1.R;

/**
 * Class playing sound effects when a bullet has been shot, or a bullet hits something.
 */
public class SoundEffectsPlayer {

    private Context mainActivityContext;

    /**
     * maximum size if parallel music streams
     */
    private static final int MAX_STREAMS=100;

    //ids for specific sound effects
    private int soundIdOnHitCharacter;
    private int soundIdOnHitPlatform;
    private int soundIdOnShoot;
    private int soundIdOnKill;
    private int soundIdOnWin;
    private int soundIdOnHitOpponent;
    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    public SoundEffectsPlayer(Context c){
        mainActivityContext = c;
        initSoundPool();
    }


    /**
     * initialization of soundPool
     */
    private void initSoundPool()  {
        // With Android API >= 21.
        if (Build.VERSION.SDK_INT >= 21 ) {

            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder= new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            this.soundPool = builder.build();
        }
        // With Android API < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        // When SoundPool load complete.
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPoolLoaded = true;
            }
        });

        //load the sound of shoot into pool
        this.soundIdOnShoot= this.soundPool.load(mainActivityContext, R.raw.shoot,1);

        //load the sound of character hit into pool
        this.soundIdOnHitCharacter = this.soundPool.load(mainActivityContext, R.raw.character_hit,1);

        //load the sound of opponent hit into pool  //should we separate this?
        this.soundIdOnHitOpponent = this.soundPool.load(mainActivityContext, R.raw.character_hit,1);


        //load the sound of platform hit into pool
        this.soundIdOnHitPlatform = this.soundPool.load(mainActivityContext,R.raw.platform_hit,1);

        //load the sound of kill into pool
        this.soundIdOnKill = this.soundPool.load(mainActivityContext,R.raw.fail,1);

        //load the sound of win into pool
        this.soundIdOnWin = this.soundPool.load(mainActivityContext,R.raw.win,1);
    }

    //audio effect functions for specific sound ID

    public void playEffectOnCharacterHit() {
        if(this.soundPoolLoaded) {
            float leftVolumn = 1.0f;
            float rightVolumn =  1.0f;
            int streamId = this.soundPool.play(this.soundIdOnHitCharacter,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }
    public void playEffectOnOpponentHit() {
        if(this.soundPoolLoaded) {
            float leftVolumn = 1.0f;
            float rightVolumn =  1.0f;
            int streamId = this.soundPool.play(this.soundIdOnHitOpponent,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }


    public void playEffectOnPlatformHit() {
        if(this.soundPoolLoaded) {
            float leftVolumn = 0.8f;
            float rightVolumn =  0.8f;
            int streamId = this.soundPool.play(this.soundIdOnHitPlatform,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }

    public void playEffectOnShoot() {
        if(this.soundPoolLoaded) {
            float leftVolumn = 0.8f;
            float rightVolumn =  0.8f;
            int streamId = this.soundPool.play(this.soundIdOnShoot,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }

    public void playEffectOnWin() {
        if(this.soundPoolLoaded) {
            float leftVolumn = 0.8f;
            float rightVolumn =  0.8f;
            int streamId = this.soundPool.play(this.soundIdOnWin,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }

    public void playEffectOnKill() {
        if(this.soundPoolLoaded) {
            float leftVolumn = 0.8f;
            float rightVolumn =  0.8f;
            int streamId = this.soundPool.play(this.soundIdOnKill,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }

}
