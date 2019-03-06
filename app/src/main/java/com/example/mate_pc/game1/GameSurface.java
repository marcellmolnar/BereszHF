package com.example.mate_pc.game1;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
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

        for(int i = 0; i < myBullets.size(); i++) {
            for (Platform platform : platforms) {
                if (myBullets.get(i).isHit(platform)) {
                    myBullets.remove(myBullets.get(i));
                }
            }
        }

        for(int i = 0; i < myBullets.size(); i++)
        {
            if(myBullets.get(i).getX() < 0 || myBullets.get(i).getX() + myBullets.get(i).getWidth() > this.getWidth())
            {
                myBullets.remove(myBullets.get(i));
            }
        }

        character.setFloorHeight(highestFloorBelow);

        character.update();

        for(int i = 0; i < myBullets.size(); i++){
            myBullets.get(i).update();
        }
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
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameFloorHeight = (int)(getHeight()*0.8);
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
        character = new Character(this,chibiBitmap1,100, gameFloorHeight, (int) (getHeight()*0.14));

        Bitmap platformIm = BitmapFactory.decodeResource(getResources(), R.drawable.platform_tr);
        platforms = new Platform[4];
        platforms[0] = new Platform(this, platformIm, (int)((double)getWidth()/2.6), (int)((double)getHeight()/1.5), (int)((double)getHeight()/24));
        platforms[1] = new Platform(this, platformIm, (int)((double)getWidth()/3.2), (int)((double)getHeight()/1.9), (int)((double)getHeight()/24));
        platforms[2] = new Platform(this, platformIm, (int)((double)getWidth()/2.3), (int)((double)getHeight()/2.7), (int)((double)getHeight()/24));
        platforms[3] = new Platform(this, platformIm, (int)((double)getWidth()/3.6), (int)((double)getHeight()/4.5), (int)((double)getHeight()/24));

        myBullets = new ArrayList<>();

        gameThread = new GameThread(this,holder);
        gameThread.setRunning(true);
        gameThread.start();
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

}