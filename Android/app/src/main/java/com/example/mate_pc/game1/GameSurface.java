package com.example.mate_pc.game1;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import com.example.mate_pc.game1.graphical_stuff.Bullet;
import com.example.mate_pc.game1.graphical_stuff.Character;
import com.example.mate_pc.game1.graphical_stuff.OpponentCharacter;
import com.example.mate_pc.game1.graphical_stuff.Platform;
import com.example.mate_pc.game1.network_stuff.OnConnectionChangedListener;
import com.example.mate_pc.game1.network_stuff.WebSocketClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mate_pc.game1.Constants.MY_SETTINGS;
import static com.example.mate_pc.game1.Constants.SELECTED_BACKGROUND_INTENT_EXTRA;
import static com.example.mate_pc.game1.Constants.BACKGROUND_SETTINGS_KEY;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback, OnConnectionChangedListener {


    private GameThread gameThread;
    private SenderClass senderClass;

    private Character character;
    private OpponentCharacter opponent;

    private Platform[] platforms;

    private int gameFloorHeight = 0;

    private boolean usingJoystick = false;
    private boolean showJoystick = true;

    private Bitmap background;
    private Bitmap joystick_bg;
    private Bitmap joystick;
    private double joystick_size;

    ArrayList<Bullet> myBullets;
    ArrayList<Bullet> opponentBullets;

    private static final int MAX_STREAMS=100;   //maximum size if parallel music streams
    private int soundIdOnHitCharacter;          //ids for specific sound effects
    private int soundIdOnHitPlatform;
    private int soundIdOnShoot;
    private int soundIdOnKill;
    private int soundIdOnWin;
    private int soundIdOnHitOpponent;
    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    private WebSocketClass webSocket;

    private MyBroadcastReceiver receiver;

    private Context context;
    private Activity activity;

    public GameSurface(Activity activity, Context context)  {
        super(context);

        this.activity = activity;
        this.context = context;

        // Make Game Surface focusable so it can handle events.
        setFocusable(true);

        // Set callback.
        getHolder().addCallback(this);
        initSoundPool();

        this.gameThread = null;

        receiver = new MyBroadcastReceiver();
        activity.registerReceiver(receiver, new IntentFilter(MyBroadcastReceiver.ACTION));

    }

    // ToDo (for Mate ;) ): create class for sound handling

    //initialization of soundpool
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
        this.soundIdOnShoot= this.soundPool.load(this.getContext(), R.raw.shoot,1);

        //load the sound of character hit into pool
        this.soundIdOnHitCharacter = this.soundPool.load(this.getContext(), R.raw.character_hit,1);

        //load the sound of opponent hit into pool  //should we separate this?
        this.soundIdOnHitOpponent = this.soundPool.load(this.getContext(), R.raw.character_hit,1);


        //load the sound of platform hit into pool
        this.soundIdOnHitPlatform = this.soundPool.load(this.getContext(),R.raw.platform_hit,1);

        //load the sound of kill into pool
        this.soundIdOnKill = this.soundPool.load(this.getContext(),R.raw.fail,1);

        //load the sound of win into pool
        this.soundIdOnWin = this.soundPool.load(this.getContext(),R.raw.win,1);
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

    public void setWebSocket(WebSocketClass webSocket) {
        this.webSocket = webSocket;
    }

    public void setOpponentXY(double relX, double relY) {
        opponent.setX((int) (relX * getWidth()));
        opponent.setY((int) (relY * getHeight()));
    }

    public void setOwnHealth(int hp){
        if (character.getHealth() > hp){
            playEffectOnCharacterHit();
            character.setHealth(hp);
        }
        //else?
    }

    public void update()  {

        int highestFloorBelow = gameFloorHeight; // searching for the one with the minimal y coordinate
        for(Platform platform : platforms) {
            if(platform.isBelow(character)){
                if (platform.getY() < highestFloorBelow) {
                    highestFloorBelow = platform.getY();
                }
            }
        }

        for(int i = 0; i < myBullets.size(); i++){
            myBullets.get(i).update();
        }

        ArrayList<Integer> indexesToDel = new ArrayList<>();
        for(int i = 0; i < myBullets.size(); i++){
            if (myBullets.get(i).isHit(opponent)){      //check if opponent hit
                if(opponent.getHealth() == 0) {
                    playEffectOnKill();
                }
                else {
                    playEffectOnOpponentHit();
                    opponent.setHealth(opponent.getHealth()-1);
                }
                indexesToDel.add(i);
            }

        }
        for(int ind : indexesToDel){                    //delete bullets that hit character
            myBullets.remove(ind);
        }


        for(int i = 0; i < myBullets.size(); i++)       //delete bullets that left screen
        {
            if(myBullets.get(i).getX() + myBullets.get(i).getWidth() < 0 || myBullets.get(i).getX() > this.getWidth())
            {
                myBullets.remove(myBullets.get(i));
            }
        }

        ArrayList<Integer> indexesToDelete = new ArrayList<>(); //check if bullets hit platform
        for(int i = 0; i < myBullets.size(); i++) {
            for (Platform platform : platforms) {
                if (myBullets.get(i).isHit(platform)) {
                    indexesToDelete.add(i);
                    playEffectOnPlatformHit();
                }
            }
        }
        for(int ind : indexesToDelete){                         //delete bullets that hit platform
            myBullets.remove(ind);
        }

        character.setFloorHeight(highestFloorBelow);

        character.update();

        opponent.update();

        //TODO: Set playbutton visibility upon winning :O
        /*if(OpponentHealth <= 0){
            MainActivity.playButton.setVisibility(View.VISIBLE);
        }
        else{
            playButton.setVisibility(View.INVISIBLE);
        }*/


    }



    @Override
    public void draw(Canvas canvas)  {
        super.draw(canvas);
        // Draw background
        canvas.drawBitmap(background,0, 0, null);
        // Draw object(s)
        for(Platform platform : platforms) {
            platform.draw(canvas);
        }
        character.draw(canvas);
        opponent.draw(canvas);

        for(int i = 0;i < myBullets.size(); i++){
            myBullets.get(i).draw(canvas);
        }

        for(int i = 0;i < opponentBullets.size(); i++){
            opponentBullets.get(i).draw(canvas);
        }

        if (isJoystickOn && usingJoystick && showJoystick) {
            canvas.drawBitmap(joystick_bg, (int)(startPosX-joystick_size/2.0), (int)(startPosY-joystick_size/2.0), null);
            canvas.drawBitmap(joystick, (int)(currPosX-joystick_size/6.0), (int)(currPosY-joystick_size/6.0), null);
        }

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (this.gameThread == null) {

            gameFloorHeight = (int) (getHeight() * 0.9);

            // Background image
            SharedPreferences prefs = context.getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
            int backgroundNum = prefs.getInt(BACKGROUND_SETTINGS_KEY, 2);
            onSettingsChanged(backgroundNum);

            Bitmap chibiBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.guy);
            character = new Character(this, chibiBitmap1, (int) ((double) getWidth() / 5.2), gameFloorHeight, (int) (getHeight() * 0.14));
            double oppStartX = (double) getWidth() *(1.0 - 1.0 / 5.2) - character.getWidth();
            opponent = new OpponentCharacter(this,chibiBitmap1, (int) (oppStartX), gameFloorHeight-character.getHeight(), (int) (getHeight() * 0.14));


            Bitmap platformIm = BitmapFactory.decodeResource(getResources(), R.drawable.platform_tr);
            platforms = new Platform[8];
            double w1 = (double) getWidth() / 2.6;
            double w2 = (double) getWidth() / 3.2;
            double w3 = (double) getWidth() / 2.5;
            double w4 = (double) getWidth() / 3.6;
            platforms[0] = new Platform(platformIm, (int) (w1), (int) ((double) getHeight() / 1.4), (int) ((double) getHeight() / 24));
            platforms[1] = new Platform(platformIm, (int) (getWidth() - platforms[0].getWidth() - w1), (int) ((double) getHeight() / 1.4), (int) ((double) getHeight() / 24));
            platforms[2] = new Platform(platformIm, (int) (w2), (int) ((double) getHeight() / 1.8), (int) ((double) getHeight() / 24));
            platforms[3] = new Platform(platformIm, (int) (getWidth() - platforms[0].getWidth() - w2), (int) ((double) getHeight() / 1.8), (int) ((double) getHeight() / 24));
            platforms[4] = new Platform(platformIm, (int) (w3), (int) ((double) getHeight() / 2.2), (int) ((double) getHeight() / 24));
            platforms[5] = new Platform(platformIm, (int) (getWidth() - platforms[0].getWidth() - w3), (int) ((double) getHeight() / 2.2), (int) ((double) getHeight() / 24));
            platforms[6] = new Platform(platformIm, (int) (w4), (int) ((double) getHeight() / 3.0), (int) ((double) getHeight() / 24));
            platforms[7] = new Platform(platformIm, (int) (getWidth() - platforms[0].getWidth() - w4), (int) ((double) getHeight() / 3.0), (int) ((double) getHeight() / 24));

            joystick_size = (int) (getHeight() / 2.75);
            Bitmap js_bg = drawableToBitmap(getResources().getDrawable(R.drawable.joystick));
            joystick_bg = Bitmap.createScaledBitmap(js_bg, (int) (joystick_size), (int) (joystick_size), false);
            Bitmap js = drawableToBitmap(getResources().getDrawable(R.drawable.ic_fiber_manual_record_black_24dp_stick));
            joystick = Bitmap.createScaledBitmap(js, (int) (joystick_size / 3), (int) (joystick_size / 3), false);


            myBullets = new ArrayList<>();
            opponentBullets = new ArrayList<>();
        }

        gameThread = new GameThread(this,holder);
        gameThread.setRunning(true);
        gameThread.start();

        senderClass = new SenderClass();
        senderClass.setRunning(true);
        senderClass.start();


    }


    private class SenderClass extends Thread {

        private boolean running;

        @Override
        public void run() {


            while(running)  {
                long waitTime = 10;

                try {
                    // Sleep.
                    sleep(waitTime);
                } catch(InterruptedException ignored)  {
                }


                JSONObject json = new JSONObject();  //send position and opponent health
                if (webSocket.isConnected()) {
                    JSONObject characterJson = new JSONObject();
                    try {
                        characterJson.put("x", (double) (getWidth() - character.getWidth() -
                                character.getX()) / getWidth());
                        characterJson.put("y", (double) (character.getY()) / getHeight());
                        characterJson.put("opponentHealth", opponent.getHealth());
                        json.put("character", characterJson);
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                }

                if (webSocket.isConnected()) {                          //send own bullets
                    JSONArray jsonArray = new JSONArray();
                    for (Bullet bullet : myBullets) {
                        JSONObject jbullet = new JSONObject();
                        try {
                            jbullet.put("x", (double) (getWidth() - bullet.getWidth() -
                                    bullet.getX()) / getWidth());
                            jbullet.put("y", (double) (bullet.getY()) / getHeight());
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
                }
                if (webSocket.isConnected()) {
                    webSocket.send(json.toString());
                }
                else {
                    webSocket.send("join");
                }

            }

        }

        void setRunning(boolean running)  {
            this.running = running;
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry) {
            retry = false;
            try {
                gameThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                gameThread.join();
            }catch(InterruptedException e)  {
                e.printStackTrace();
                retry = true;
            }
        }
    }

    public void setCharacterHorizontalAcceleration(int x, boolean stopMovement) {
        character.setHorizontalAcceleration(x, stopMovement);
    }

    public void setCharacterVerticalAcceleration(int y) {
        character.setVerticalAcceleration(y);
    }

    private Bitmap createSubImage(Bitmap bm, int xStart, int yStart, int width, int height) {
        // createBitmap(bitmap, x, y, width, height).
        return Bitmap.createBitmap(bm, xStart, yStart , width, height);
    }

    public void createBullet() {
        double extraX;
        Bitmap bullet;
        int velocity = (int) (Character.MAX_SPEED*1.5);
        int bulletHeight = (int)((double)getHeight()/30);
        if(character.isSeeingToRight()){
            bullet = BitmapFactory.decodeResource(getResources(),R.drawable.bullet_right);
            extraX = (character.getWidth() );

        }else{
            bullet = BitmapFactory.decodeResource(getResources(),R.drawable.bullet_left);
            extraX = -(bullet.getWidth()*bulletHeight/((double)bullet.getHeight()));
            velocity = -velocity;
        }
        myBullets.add(new Bullet(bullet, (int) (character.getX() + extraX),(character.getY() + character.getHeight()/2), bulletHeight, velocity));
        playEffectOnShoot();
    }

    private Bitmap bulletRight = BitmapFactory.decodeResource(getResources(),R.drawable.bullet_right);
    private Bitmap bulletLeft = BitmapFactory.decodeResource(getResources(),R.drawable.bullet_left);

    public void createNewOpponentBullet(double[][] bullets, boolean[] isBulletSeeingRight, int bulletsSize) {
        opponentBullets.clear();
        int velocity = (int) (Character.MAX_SPEED*1.5);
        int bulletHeight = (int)((double)getHeight()/30);
        for (int i = 0; i < bulletsSize; i++) {
            double[] coordinates = bullets[i];
            Bitmap bullet;
            if (isBulletSeeingRight[i]) {
                bullet = bulletRight;
            } else {
                bullet = bulletLeft;
                velocity = -velocity;
            }
            int bulletX = (int) (coordinates[0] * this.getWidth());
            int bulletY = (int) (coordinates[1] * this.getHeight());
            opponentBullets.add(new Bullet(bullet, bulletX, bulletY, bulletHeight, velocity));
        }
    }

    public void setJoystickUsage(boolean useJoystick, boolean showTheJoystick){
        this.usingJoystick = useJoystick;
        this.showJoystick = showTheJoystick;
    }

    private boolean isJoystickOn = false;
    private float startPosX;
    private float startPosY;
    private float currPosX;
    private float currPosY;

    private boolean jumpFlag = true;
    long now;


    public void onJoystickTouchListener(MotionEvent motionEvent) {
        if(usingJoystick) {
            int action = motionEvent.getAction();
            if (System.nanoTime() >= now + 500000000) {
                jumpFlag = true;
            }
            if (action == MotionEvent.ACTION_DOWN) {
                isJoystickOn = true;
                startPosX = motionEvent.getX();
                startPosY = motionEvent.getY();
            } else if (action == MotionEvent.ACTION_UP) {
                isJoystickOn = false;
                setCharacterHorizontalAcceleration(0, true);
            } else if (action == MotionEvent.ACTION_MOVE) {
                float cx = motionEvent.getX();
                float cy = motionEvent.getY();
                if (cx > startPosX + joystick_size / 8) {                    // Going right
                    currPosX = (float) (startPosX + joystick_size / 2.0);
                    setCharacterHorizontalAcceleration(1, false);
                } else if (cx < startPosX - joystick_size / 8) {             // Going left
                    currPosX = (float) (startPosX - joystick_size / 2.0);
                    setCharacterHorizontalAcceleration(-1, false);
                } else {                           // if movement too small, do nothing
                    currPosX = startPosX;
                    setCharacterHorizontalAcceleration(0, true);
                }
                if ((cy < startPosY - joystick_size / 8)  ) {     // Jump
                    if (currPosX < startPosX) {
                        currPosX = (float) (startPosX - joystick_size / (2*1.41421356));
                        currPosY = (float) (startPosY - joystick_size / (2*1.41421356));
                    }
                    else if (currPosX > startPosX) {
                        currPosX = (float) (startPosX + joystick_size / (2*1.41421356));
                        currPosY = (float) (startPosY - joystick_size / (2*1.41421356));
                    }
                    else {
                        currPosY = (float) (startPosY - joystick_size / 2.0);
                    }
                    if (jumpFlag) {
                        setCharacterVerticalAcceleration(-1);
                        now = System.nanoTime();
                        jumpFlag = false;
                    }
                } else {
                    currPosY = startPosY;
                }
            }
        }
    }

    private boolean alreadyStartedCountDown = false;

    @Override
    public void onConnectionChanged() {
        if (!alreadyStartedCountDown) {
        }
    }




    public class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.example.mate_pc.BACKGROUND_CHANGED";
        @Override
        public void onReceive(Context context, Intent intent) {
            int selectedBackground = intent.getIntExtra(SELECTED_BACKGROUND_INTENT_EXTRA, 2);
            onSettingsChanged(selectedBackground);
        }
    }

    public void onSettingsChanged(int chosenBackgroundNumber) {

        int backgroundRes;
        switch (chosenBackgroundNumber){
            case 1:
                backgroundRes = R.drawable.game_background_15;
                break;
            case 3:
                backgroundRes = R.drawable.game_background_16;
                break;
            case 4:
                backgroundRes = R.drawable.game_background_14;
                break;
            case 2:
            default:
                backgroundRes = R.drawable.game_background;
                break;
        }
        // Background image
        Bitmap bg = BitmapFactory.decodeResource(getResources(), backgroundRes);
        // Scaling up and cropping image to size of the screen
        double scaledWidth = bg.getWidth();
        double scaledHeight = bg.getHeight();
        double widthScale = (double)(getWidth()) / bg.getWidth();
        double heightScale = (double)(getHeight()) / bg.getHeight();
        // Screen is bigger than image in one size
        if (widthScale > 1 || heightScale > 1) {
            if (widthScale > heightScale) {
                scaledWidth = getWidth();
                scaledHeight = widthScale*bg.getHeight();
            }
            else {
                scaledHeight = getHeight();
                scaledWidth = heightScale*bg.getWidth();
            }
        }
        else { // Image is bigger than screen in every size
            if (widthScale > heightScale) {
                scaledWidth = getWidth();
                scaledHeight = widthScale*bg.getHeight();
            }
            else {
                scaledHeight = getHeight();
                scaledWidth = heightScale*bg.getWidth();
            }
        }

        Bitmap scaled = Bitmap.createScaledBitmap(bg, (int)(scaledWidth), (int)(scaledHeight), false);
        int x_offset = 0;
        int y_offset = 0;
        if (scaled.getWidth() > getWidth()) {
            x_offset = (scaled.getWidth() - getWidth()) / 2;
        }
        if (scaled.getHeight() > getHeight()) {
            y_offset = (scaled.getHeight() - getHeight()) / 2;
        }
        // Make sure x_offset+getWidth() is not greater than scaled.getWidth()
        while(x_offset > 0 && x_offset+getWidth() > scaled.getWidth()){
            x_offset--;
        }
        while(y_offset > 0 && y_offset+getHeight() > scaled.getHeight()){
            y_offset--;
        }

        background = createSubImage(scaled, x_offset, y_offset, getWidth(), getHeight());
    }
}