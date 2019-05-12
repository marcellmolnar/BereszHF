package com.example.mate_pc.game1.graphical_stuff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.example.mate_pc.game1.GameSurface;
import com.example.mate_pc.game1.R;

public class Character extends GameObject {

    public static int MAX_SPEED = 0;
    private static int ACCELERATION = 0;
    private static double START_JUMP_SPEED = 0;
    private static double GRAVITY = 0;

    static final int ROW_TOP_TO_BOTTOM = 0;
    static final int ROW_RIGHT_TO_LEFT = 1;
    static final int ROW_LEFT_TO_RIGHT = 2;
    static final int ROW_BOTTOM_TO_TOP = 3;

    // Row index of Image are being used.
    protected int rowUsing = ROW_LEFT_TO_RIGHT;
    protected int colUsing;

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;


    protected Bitmap[] healthBars;


    protected double movingVectorX = 0;
    protected double movingVectorY = 0;

    private boolean stopMovement = false;
    private boolean startedSlowing = false;
    private int accelerationX = 0;

    protected boolean seeingToRight;

    private int floorHeight = 0;

    protected long lastDrawNanoTime = -1;
    protected double pixelsWalked = 0;

    private int ownHealth;
    protected int healthBarHeight;
    protected int healthBarWidth;

    GameSurface gameSurface;

    public Character(GameSurface gameSurface, Bitmap image, int x, int y, int height) {
        super(image, 4, 3, x, y, height);

        this.gameSurface = gameSurface;

        seeingToRight = true;

        MAX_SPEED = gameSurface.getWidth()/150;
        ACCELERATION = gameSurface.getWidth()/120;
        START_JUMP_SPEED = (double)(-gameSurface.getHeight()/25);
        GRAVITY = (double)(gameSurface.getHeight())/280;

        topToBottoms = new Bitmap[colCount];
        rightToLefts = new Bitmap[colCount];
        leftToRights = new Bitmap[colCount];
        bottomToTops = new Bitmap[colCount];

        for(int col = 0; col< colCount; col++ ) {
            topToBottoms[col] = createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            rightToLefts[col] = createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            leftToRights[col] = createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            bottomToTops[col] = createSubImageAt(ROW_BOTTOM_TO_TOP, col);
        }

        this.ownHealth = 3;

        // ToDo (optional): create image with empty bar. Health points that are lost should be represented with ..
        //       a non-filled hearth.
        healthBars = new Bitmap[4];
        Bitmap health00 = BitmapFactory.decodeResource(this.gameSurface.getContext().getResources(),
                R.drawable.health3);

        this.healthBarHeight = (int) (this.gameSurface.getHeight() * 0.1);
        this.healthBarWidth = (int)((double)(this.healthBarHeight * health00.getWidth()) / health00.getHeight());

        Bitmap health0 = Bitmap.createScaledBitmap(health00, healthBarWidth, healthBarHeight, false);
        healthBars[0] = Bitmap.createBitmap(health0, 0, 0 , healthBarWidth, healthBarHeight);

        Bitmap health10 = BitmapFactory.decodeResource(this.gameSurface.getContext().getResources(),
                R.drawable.health3);
        Bitmap health1 = Bitmap.createScaledBitmap(health10, healthBarWidth, healthBarHeight, false);
        healthBars[1] = Bitmap.createBitmap(health1, 0, 0 , healthBarWidth, healthBarHeight);

        Bitmap health20 = BitmapFactory.decodeResource(this.gameSurface.getContext().getResources(),
                R.drawable.health2);
        Bitmap health2 = Bitmap.createScaledBitmap(health20, healthBarWidth, healthBarHeight, false);
        healthBars[2] = Bitmap.createBitmap(health2, 0, 0 , healthBarWidth, healthBarHeight);

        Bitmap health30 = BitmapFactory.decodeResource(this.gameSurface.getContext().getResources(),
                R.drawable.health1);
        Bitmap health3 = Bitmap.createScaledBitmap(health30, healthBarWidth, healthBarHeight, false);
        healthBars[3] = Bitmap.createBitmap(health3, 0, 0 , healthBarWidth, healthBarHeight);
    }

    private Bitmap[] getMoveBitmaps() {
        switch (rowUsing) {
            case ROW_BOTTOM_TO_TOP: {
                return bottomToTops;
            }
            case ROW_LEFT_TO_RIGHT: {
                return leftToRights;
            }
            case ROW_RIGHT_TO_LEFT: {
                return rightToLefts;
            }
            case ROW_TOP_TO_BOTTOM: {
                return topToBottoms;
            }
            default: {
                return null;
            }
        }
    }

    protected Bitmap getCurrentMoveBitmap() {
        Bitmap[] bitmaps = getMoveBitmaps();
        assert bitmaps != null;
        return bitmaps[colUsing];
    }


    public void update() {
        if (stopMovement && (movingVectorX > MAX_SPEED-1 || movingVectorX < -MAX_SPEED+1)) {
            startedSlowing = true;
            accelerationX = -accelerationX;
        }
        if (startedSlowing) {
            if (movingVectorX > 0 && movingVectorX + accelerationX <= 0) {
                movingVectorX = 0;
                accelerationX = 0;
                stopMovement = false;
                startedSlowing = false;
            }
            if (movingVectorX < 0 && movingVectorX + accelerationX >= 0) {
                movingVectorX = 0;
                accelerationX = 0;
                stopMovement = false;
                startedSlowing = false;
            }
        }
        movingVectorX += accelerationX;

        if (movingVectorX > MAX_SPEED) {
            movingVectorX = MAX_SPEED;
        }
        if (movingVectorX < -MAX_SPEED) {
            movingVectorX = -MAX_SPEED;
        }

        if (movingVectorY < 0) {
            movingVectorY += GRAVITY;
        }
        else {
            if (movingVectorY + GRAVITY + getBottomHeight() < floorHeight) {
                movingVectorY += GRAVITY;
            }
            else {
                y = floorHeight - getHeight() ;
                movingVectorY = 0;
            }
        }

        if (pixelsWalked > 500) {
            colUsing++;
            if (colUsing >= colCount) {
                colUsing = 0;
            }
            pixelsWalked = 0;
        }
        // Current time in nanoseconds
        long now = System.nanoTime();

        // Never once did draw.
        if(lastDrawNanoTime == -1) {
            lastDrawNanoTime = now;
        }
        // Changing nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        int deltaTime = (int) ((now - lastDrawNanoTime) / 1000000 );


        // Distance moves
        double velocity = Math.sqrt(movingVectorX * movingVectorX + movingVectorY * movingVectorY);
        double distance = velocity * deltaTime;
        pixelsWalked += distance;

        // Calculate the new position of the game character.
        x = (int)(x + movingVectorX);
        y = (int)(y + movingVectorY);

        // When the game's character touches the edge of the screen, then change direction

        if(x < 0 ) {
            x = 0;
            movingVectorX = -movingVectorX;
        } else if(x > gameSurface.getWidth() - getWidth()) {
            x = gameSurface.getWidth() - getWidth();
            movingVectorX = -movingVectorX;
        }

        if(y < 0 ) {
            y = 0;
            movingVectorY = -movingVectorY;
        } else if(y > gameSurface.getHeight() - getHeight()) {
            y = gameSurface.getHeight() - getHeight();
            movingVectorY = -movingVectorY ;
        }

        // rowUsing
        if( movingVectorX > 0 ) {
            rowUsing = ROW_LEFT_TO_RIGHT;
            seeingToRight = true;
        }
        if( movingVectorX < 0 ) {
            rowUsing = ROW_RIGHT_TO_LEFT;
            seeingToRight = false;
        }
        /*if (movingVectorY != 0) {
            rowUsing = ROW_TOP_TO_BOTTOM;
            colUsing = 1;
        }*/
    }

    int getBottomHeight() {
        return y+getHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Bitmap bitmap = getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap,x, y, null);
        // Last draw time.
        lastDrawNanoTime = System.nanoTime();

        if (this.ownHealth > 0) {
            canvas.drawBitmap(this.healthBars[this.getHealth()], (float) (0.1 * this.healthBarWidth),
                    (float) (0.1 * this.healthBarHeight), null);
        }

    }

    public boolean isSeeingToRight() {
       return this.seeingToRight;
    }

    public void setHorizontalAcceleration(int accX, boolean stopMovement) {
        this.stopMovement = stopMovement;
        startedSlowing = false;
        if (!stopMovement) {
            if (accX < 0) {
                accelerationX = -ACCELERATION;
            }
            else {
                accelerationX = ACCELERATION;
            }
        }
    }

    public void setVerticalAcceleration(int accY) {
        if(accY != 0 && getBottomHeight() == floorHeight){
            movingVectorY = START_JUMP_SPEED;
        }
    }

    public void setFloorHeight(int height) {
        floorHeight = height;
    }

    public int getHealth(){
        return this.ownHealth;
    }

    public void setHealth(int hp){
        this.ownHealth = hp;
    }

    public void resetCharacter() {
        this.setHealth(3);
        seeingToRight = true;
        rowUsing = ROW_LEFT_TO_RIGHT;
        movingVectorX = 0;
        movingVectorY = 0;
    }

}
