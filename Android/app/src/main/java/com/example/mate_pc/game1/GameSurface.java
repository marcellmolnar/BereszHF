package com.example.mate_pc.game1;

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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import com.example.mate_pc.game1.graphical_stuff.Bullet;
import com.example.mate_pc.game1.graphical_stuff.Character;
import com.example.mate_pc.game1.graphical_stuff.OpponentCharacter;
import com.example.mate_pc.game1.graphical_stuff.Platform;
import com.example.mate_pc.game1.network_stuff.SenderClass;
import com.example.mate_pc.game1.network_stuff.WebSocketClass;
import com.example.mate_pc.game1.sound_stuff.BackgroundSoundHandler;
import com.example.mate_pc.game1.sound_stuff.SoundEffectsPlayer;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mate_pc.game1.Constants.MY_SETTINGS;
import static com.example.mate_pc.game1.Constants.SELECTED_BACKGROUND_INTENT_EXTRA_KEY;
import static com.example.mate_pc.game1.Constants.BACKGROUND_SETTINGS_KEY;


/**
 * This class is responsible for:
 *      storing the game objects (characters, platforms, etc.),
 *      execute the update functions of the objects regarding to the game's logic,
 *      and draw every visible object to the screen.
 */
public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {


    private GameThread gameThread;
    private SenderClass senderClass;

    private Character character;
    private OpponentCharacter opponent;

    private Platform[] platforms;

    private int gameFloorHeight;

    private boolean showJoystick;
    private float startPosX;
    private float startPosY;
    private float currPosX;
    private float currPosY;

    private Bitmap background;
    private Bitmap joystick_bg;
    private Bitmap joystick;
    private double joystick_size;

    ArrayList<Bullet> myBullets;
    ArrayList<Bullet> opponentBullets;


    private SoundEffectsPlayer soundEffectsPlayer;


    private Context mainActivityContext;
    private MainActivity mainActivity;

    private BackgroundSoundHandler backgroundSoundHandler;
    private MyBroadcastReceiver updateEventReceiver;

    /**
     * Constructor
     * @param activity  we use MainActivity's function to show the game menu
     * @param context  we should able to use the app's context (sound handling and shared prefs)
     */
    public GameSurface(MainActivity activity, Context context)  {
        super(context);

        this.mainActivity = activity;
        this.mainActivityContext = context;

        // Make Game Surface focusable so it can handle events.
        setFocusable(true);

        // Set callback.
        getHolder().addCallback(this);
        soundEffectsPlayer = new SoundEffectsPlayer(mainActivityContext);

        this.gameThread = null;

        updateEventReceiver = new MyBroadcastReceiver();


        senderClass = new SenderClass(this);
        senderClass.setRunning(true);
        senderClass.start();

        showJoystick = false;
        gameFloorHeight = 0;
    }

    /**
     * Setting the web socket at start.
     * @param webSocket  the web socket we want to use
     */
    public void setWebSocket(WebSocketClass webSocket) {
        senderClass.setWebSocket(webSocket);
    }

    public void destroy() {
        if (backgroundSoundHandler != null) {
            backgroundSoundHandler.destroy();
        }
    }

    public void resume() {
        if (backgroundSoundHandler != null) {
            backgroundSoundHandler.resume();
            mainActivity.registerReceiver(updateEventReceiver, new IntentFilter(MyBroadcastReceiver.ACTION));
        }
    }

    public void pause() {
        if (backgroundSoundHandler != null) {
            backgroundSoundHandler.pause();
            mainActivity.unregisterReceiver(updateEventReceiver);
        }
    }

    public void restart() {
        if (backgroundSoundHandler != null) {
            backgroundSoundHandler.restart();
        }
    }

    /**
     * This function is called periodically to update the object's state.
     */
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
                    soundEffectsPlayer.playEffectOnKill();
                }
                else {
                    soundEffectsPlayer.playEffectOnOpponentHit();
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
                    soundEffectsPlayer.playEffectOnPlatformHit();
                }
            }
        }
        for(int ind : indexesToDelete){                         //delete bullets that hit platform
            myBullets.remove(ind);
        }

        character.setFloorHeight(highestFloorBelow);

        character.update();

        opponent.update();

        if(opponent.getHealth() <= 0){
            senderClass.sendLastMessage();
            resetCharactersHealth();
            mainActivity.showGameMenu(true);
        }

    }


    /**
     * This function is called periodically to draw every visible object to the screen.
     */
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

        if (showJoystick) {
            canvas.drawBitmap(joystick_bg, (int)(startPosX-joystick_size/2.0), (int)(startPosY-joystick_size/2.0), null);
            canvas.drawBitmap(joystick, (int)(currPosX-joystick_size/6.0), (int)(currPosY-joystick_size/6.0), null);
        }

    }

    /**
     * Implementing method of SurfaceHolder.Callback
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (this.gameThread == null) {

            gameFloorHeight = (int) (getHeight() * 0.9);

            // Background image
            SharedPreferences prefs = mainActivityContext.getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
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


        backgroundSoundHandler = new BackgroundSoundHandler(mainActivityContext);
        backgroundSoundHandler.setSound();
    }

    /**
     * Implementing method of SurfaceHolder.Callback
     */
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

    /**
     * Implementing method of SurfaceHolder.Callback
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void wonByDisconnection() {
        mainActivity.showGameMenu(true);
    }

    public void restartGame() {
        character.resetCharacter();
        opponent.resetCharacter();
    }

    /**
     * Resetting characters' health immediately after match ends.
     */
    public void resetCharactersHealth() {
        character.setHealth(3);
        opponent.setHealth(3);
    }

    /**
     * Converts a Drawable resource to Bitmap.
     */
    public Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Creates a bitmap from the specified subset of the source bitmap.
     */
    private Bitmap createSubImage(Bitmap bm, int xStart, int yStart, int width, int height) {
        // createBitmap(bitmap, x, y, width, height).
        return Bitmap.createBitmap(bm, xStart, yStart , width, height);
    }

    /**
     * Sets the character's horizontal acceleration.
     * This function is called once, when the user decides to start moving in a direction.
     */
    public void setCharacterHorizontalAcceleration(int x, boolean stopMovement) {
        character.setHorizontalAcceleration(x, stopMovement);
    }

    /**
     * Sets the character's horizontal acceleration.
     * This function is called once, when the user decides to jump.
     */
    public void setCharacterVerticalAcceleration(int y) {
        character.setVerticalAcceleration(y);
    }

    /**
     * Sets the visibility of the joystick. If the buttons are used to move with the character, joystick should not be visible.
     */
    public void setJoystickVisibility(boolean showJoystick, float startPosX, float startPosY) {
        this.showJoystick = showJoystick;
        this.startPosX = startPosX;
        this.startPosY = startPosY;
    }

    /**
     * Used to set the joystick stick's position.
     */
    public void setJoystickPoint(float currPosX, float currPosY) {
        this.currPosX = currPosX;
        this.currPosY = currPosY;
    }

    /**
     * Used to set the opponent's position.
     * It is called from the web socket.
     */
    public void setOpponentXY(double relX, double relY) {
        opponent.setX((int) (relX * getWidth()));
        opponent.setY((int) (relY * getHeight()));
    }

    /**
     * Used to set our character's health.
     * It is called from the web socket.
     */
    public void setOwnHealth(int hp){
        if (character.getHealth() > hp){
            soundEffectsPlayer.playEffectOnCharacterHit();
            character.setHealth(hp);
        }
        if(hp == 0){
            resetCharactersHealth();
            mainActivity.showGameMenu(false);
        }
    }

    /**
     * Creates a new bullet.
     * This function is called once, when the user decides to shoot a bullet.
     */
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
        soundEffectsPlayer.playEffectOnShoot();
    }

    private Bitmap bulletRight = BitmapFactory.decodeResource(getResources(),R.drawable.bullet_right);
    private Bitmap bulletLeft = BitmapFactory.decodeResource(getResources(),R.drawable.bullet_left);

    /**
     * Creates a new bullet (shot by the opponent).
     * It is called from the web socket.
     */
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


    public Character getCharacter() {
        return this.character;
    }

    public Character getOpponent() {
        return this.opponent;
    }

    public ArrayList<Bullet> getMyBullets() {
        return this.myBullets;
    }

    public double getJoystickSize() {
        return this.joystick_size;
    }

    /**
     * Used the receive event of background changes. Called from the SettingsActivity.
     */
    public class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.example.mate_pc.BACKGROUND_CHANGED";
        @Override
        public void onReceive(Context context, Intent intent) {
            int selectedBackground = intent.getIntExtra(SELECTED_BACKGROUND_INTENT_EXTRA_KEY, 2);
            onSettingsChanged(selectedBackground);
        }
    }

    /**
     * Handling background change events.
     */
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