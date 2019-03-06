package com.example.mate_pc.game1;



import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Character extends GameObject {

    public static int MAX_SPEED = 0;
    private static int ACCELERATION = 0;
    private static double START_JUMP_SPEED = 0;
    private static double GRAVITY = 0;

    private static final int ROW_TOP_TO_BOTTOM = 0;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private static final int ROW_LEFT_TO_RIGHT = 2;
    private static final int ROW_BOTTOM_TO_TOP = 3;

    // Row index of Image are being used.
    private int rowUsing = ROW_LEFT_TO_RIGHT;

    private int colUsing;

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;


    private double movingVectorX = 0;
    private double movingVectorY = 0;

    private boolean stopMovement = false;
    private boolean startedSlowing = false;
    private int accelerationX = 0;

    private boolean seeingToRight;

    private int floorHeight = 0;

    private long lastDrawNanoTime = -1;
    private double pixelsWalked = 0;

    private GameSurface gameSurface;

    public Character(GameSurface gameSurface, Bitmap image, int x, int y, int height) {
        super(image, 4, 3, x, y, height);

        this.gameSurface = gameSurface;

        seeingToRight = true;

        MAX_SPEED = gameSurface.getWidth()/120;
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
    }

    public Bitmap[] getMoveBitmaps() {
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

    public Bitmap getCurrentMoveBitmap() {
        Bitmap[] bitmaps = getMoveBitmaps();
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

    public int getBottomHeight() {
        return y+getHeight();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        Bitmap bitmap = getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap,x, y, null);
        // Last draw time.
        lastDrawNanoTime = System.nanoTime();
    }

    public void setMovingVector(int movingVectorX, int movingVectorY) {
        this.movingVectorX = movingVectorX;
        this.movingVectorY = movingVectorY;
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
}
