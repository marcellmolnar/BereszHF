package com.example.mate_pc.game1;



import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Character extends GameObject {

    private static final int MAX_SPEED = 7;
    private static final int START_JUMP_SPEED = -20;
    private static final int GRAVITY = 2;

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

    // Velocity of game character (pixel/millisecond)
    public static final float VELOCITY = 0.1f;

    private int movingVectorX = 0;
    private int movingVectorY = 0;

    private boolean stopMovement = false;
    private boolean startedSlowing = false;
    private int accelerationX = 0;
    private int accelerationY = 0;
    private boolean stopJumping = true;
    private int jumpDistance = 0;

    private int floorHeight = 0;

    private long lastDrawNanoTime = -1;
    private double pixelsWalked = 0;

    private GameSurface gameSurface;

    public Character(GameSurface gameSurface, Bitmap image, int x, int y) {
        super(image, 4, 3, x, y);

        this.gameSurface = gameSurface;

        topToBottoms = new Bitmap[colCount]; // 3
        rightToLefts = new Bitmap[colCount]; // 3
        leftToRights = new Bitmap[colCount]; // 3
        bottomToTops = new Bitmap[colCount]; // 3

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
            if (y + movingVectorY < floorHeight) {
                movingVectorY += GRAVITY;
            }
            else {
                y = floorHeight;
                movingVectorY = 0;
            }
        }

        if (pixelsWalked > 200) {
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
        // Change nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        int deltaTime = (int) ((now - lastDrawNanoTime) / 1000000 );

        jumpDistance += Math.abs(movingVectorY) * deltaTime;

        // Distance moves
        double velocity = Math.sqrt(movingVectorX * movingVectorX + movingVectorY * movingVectorY);
        double distance = velocity * deltaTime;
        pixelsWalked += distance;

        // Calculate the new position of the game character.
        x = x + movingVectorX;
        y = y + movingVectorY;

        // When the game's character touches the edge of the screen, then change direction

        if(x < 0 ) {
            x = 0;
            movingVectorX = -movingVectorX;
        } else if(x > gameSurface.getWidth() - width) {
            x = gameSurface.getWidth() - width;
            movingVectorX = -movingVectorX;
        }

        if(y < 0 ) {
            y = 0;
            movingVectorY = -movingVectorY;
        } else if(y > gameSurface.getHeight() - height) {
            y = gameSurface.getHeight() - height;
            movingVectorY = -movingVectorY ;
        }

        // rowUsing
        if( movingVectorX > 0 ) {
            rowUsing = ROW_LEFT_TO_RIGHT;
        }
        if( movingVectorX < 0 ) {
            rowUsing = ROW_RIGHT_TO_LEFT;
        }
        if (movingVectorY != 0) {
            rowUsing = ROW_TOP_TO_BOTTOM;
        }
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap,x, y, null);
        // Last draw time.
        lastDrawNanoTime = System.nanoTime();
    }

    public void setMovingVector(int movingVectorX, int movingVectorY) {
        this.movingVectorX = movingVectorX;
        this.movingVectorY = movingVectorY;
    }

    public void setHorizontalAcceleration(int accX, boolean stopMovement) {
        this.stopMovement = stopMovement;
        startedSlowing = false;
        if (!stopMovement) {
            accelerationX = accX;
        }
    }
    public void setVerticalAcceleration(int accY) {
        if(accY != 0 && y == floorHeight){
            movingVectorY = START_JUMP_SPEED;
        }
        jumpDistance = 0;
        stopJumping = false;
    }

    public void setFloorHeight(int height) {
        floorHeight = height;
    }
}
