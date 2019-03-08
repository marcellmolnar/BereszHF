package com.example.mate_pc.game1;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {


    private GameThread gameThread;

    private Character character;
    // ToDo: second character should be added.

    private Platform[] platforms;

    private int gameFloorHeight = 0;

    private Bitmap background;
    private Bitmap joystick_bg;
    private Bitmap joystick;
    private double joystick_size;

    ArrayList<Bullet> myBullets;

    public GameSurface(Context context)  {
        super(context);

        // Make Game Surface focusable so it can handle events.
        setFocusable(true);

        // Set callback.
        getHolder().addCallback(this);

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

        for(int i = 0; i < myBullets.size(); i++)
        {
            if(myBullets.get(i).getX() + myBullets.get(i).getWidth() < 0 || myBullets.get(i).getX() > this.getWidth())
            {
                myBullets.remove(myBullets.get(i));
            }
        }

        ArrayList<Integer> indexesToDelete = new ArrayList<>();
        for(int i = 0; i < myBullets.size(); i++) {
            for (Platform platform : platforms) {
                if (myBullets.get(i).isHit(platform)) {
                    indexesToDelete.add(i);
                }
            }
        }
        for(int ind : indexesToDelete){
            myBullets.remove(ind);
        }

        character.setFloorHeight(highestFloorBelow);

        character.update();

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

        for(int i = 0;i < myBullets.size(); i++){
            myBullets.get(i).draw(canvas);
        }

        if (isJoystickOn) {
            // ToDo: comment out next line to get transparent joystick
            canvas.drawBitmap(joystick_bg, (int)(startPosX-joystick_size/2.0), (int)(startPosY-joystick_size/2.0), null);
            canvas.drawBitmap(joystick, (int)(currPosX-joystick_size/6.0), (int)(currPosY-joystick_size/6.0), null);
        }
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameFloorHeight = (int)(getHeight()*0.9);
        // Background image
        Bitmap bg = BitmapFactory.decodeResource(getResources(),R.drawable.game_background);

        // Scaling up and cropping image to size of the screen
        int scale = 1;
        if(bg.getWidth() < getWidth()) {
            scale = getWidth() / bg.getWidth();
        }
        if(bg.getHeight() < getHeight()) {
            int scale2 = getHeight() / bg.getHeight();
            if (scale2 > scale) {
                scale = scale2;
            }
        }
        Bitmap scaled = createSubImage(bg,0,0, bg.getWidth()*scale, bg.getHeight()*scale);
        int x_offset = 0;
        int y_offset = 0;
        if (scaled.getWidth() > getWidth()) {
            x_offset = (scaled.getWidth() - getWidth()) / 2;
        }
        if (scaled.getHeight() > getHeight()) {
            y_offset = (scaled.getHeight() - getHeight()) / 2;
        }
        background = createSubImage(scaled,x_offset,y_offset, getWidth(), getHeight());

        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.guy);
        character = new Character(this,chibiBitmap1,(int)((double)getWidth()/5.2), gameFloorHeight, (int) (getHeight()*0.14));

        Bitmap platformIm = BitmapFactory.decodeResource(getResources(), R.drawable.platform_tr);
        platforms = new Platform[4];
        platforms[0] = new Platform(this, platformIm, (int)((double)getWidth()/2.6), (int)((double)getHeight()/1.4), (int)((double)getHeight()/24));
        platforms[1] = new Platform(this, platformIm, (int)((double)getWidth()/3.2), (int)((double)getHeight()/1.8), (int)((double)getHeight()/24));
        platforms[2] = new Platform(this, platformIm, (int)((double)getWidth()/2.3), (int)((double)getHeight()/2.2), (int)((double)getHeight()/24));
        platforms[3] = new Platform(this, platformIm, (int)((double)getWidth()/3.6), (int)((double)getHeight()/3.0), (int)((double)getHeight()/24));

        joystick_size = (int)(getHeight()/1.7);
        Bitmap js_bg = drawableToBitmap(getResources().getDrawable(R.drawable.ic_fiber_manual_record_black_24dp));
        joystick_bg = Bitmap.createScaledBitmap(js_bg, (int)(joystick_size), (int)(joystick_size), false);
        Bitmap js = drawableToBitmap(getResources().getDrawable(R.drawable.ic_fiber_manual_record_black_24dp_stick));
        joystick = Bitmap.createScaledBitmap(js, (int)((double)(joystick_size)/3), (int)((double)(joystick_size)/3), false);


        myBullets = new ArrayList<>();

        gameThread = new GameThread(this,holder);
        gameThread.setRunning(true);
        gameThread.start();
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
        myBullets.add(new Bullet(this, bullet, (int) (character.getX() + extraX),character.getY(), bulletHeight, velocity));

    }



    boolean usingJoystick = true;

    private boolean isJoystickOn = false;
    private float startPosX;
    private float startPosY;
    private float currPosX;
    private float currPosY;


    public void onJoystickTouchListener(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            isJoystickOn = true;
            startPosX = motionEvent.getX();
            startPosY = motionEvent.getY();
        }
        else if (action == MotionEvent.ACTION_UP) {
            isJoystickOn = false;
            setCharacterHorizontalAcceleration(0, true);
        }
        else if (action == MotionEvent.ACTION_MOVE) {
            float cx = motionEvent.getX();
            float cy = motionEvent.getY();
            if (cx > startPosX + joystick_size/4) {
                currPosX = (float) (startPosX + joystick_size/2.0 - joystick_size/3.0);
                setCharacterHorizontalAcceleration(1, false);
            }
            else if (cx < startPosX - joystick_size/4) {
                currPosX = (float) (startPosX - joystick_size/2.0 + joystick_size/3.0);
                setCharacterHorizontalAcceleration(-1, false);
            }
            else {
                currPosX = startPosX;
                setCharacterHorizontalAcceleration(0, true);
            }
            if (cy < startPosY - joystick_size/4) {
                currPosX = startPosX;
                currPosY = (float) (startPosY - joystick_size/2.0 + joystick_size/3.0);
                setCharacterVerticalAcceleration(-1);
            }
            else {
                currPosY = startPosY;
            }
        }
    }

}