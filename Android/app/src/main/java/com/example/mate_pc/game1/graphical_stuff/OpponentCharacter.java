package com.example.mate_pc.game1.graphical_stuff;
/**
 *  Handles the drawing of the opponent.
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.mate_pc.game1.GameSurface;

public class OpponentCharacter extends Character {

    private int lastX;
    private int lastY;

    public OpponentCharacter(GameSurface gameSurface, Bitmap image, int x, int y, int height) {
        super(gameSurface, image, x, y, height);
        lastX = x;
        lastY = y;
        rowUsing = ROW_RIGHT_TO_LEFT;
        seeingToRight = false;
    }


    @Override
    public void draw(Canvas canvas) {

        Bitmap bitmap = getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap,x, y, null);
        // Last draw time.
        lastDrawNanoTime = System.nanoTime();

        if (this.getHealth() > 0) {
            canvas.drawBitmap(this.healthBars[this.getHealth()], (float) (this.gameSurface.getWidth()
                    - 1.1 * this.healthBarWidth), (float) (0.1 * this.healthBarHeight), null);
        }

    }

    @Override
    public void update() {
        int movingVectorX = this.x - this.lastX;
        int movingVectorY = this.y - this.lastY;

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

        if( movingVectorX > 0 ) {
            rowUsing = ROW_LEFT_TO_RIGHT;
            seeingToRight = true;
        }
        if( movingVectorX < 0 ) {
            rowUsing = ROW_RIGHT_TO_LEFT;
            seeingToRight = false;
        }

        this.lastX = this.x;
        this.lastY = this.y;
    }


    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    @Override
    public void resetCharacter() {
        super.resetCharacter();
        seeingToRight = false;
        rowUsing = ROW_RIGHT_TO_LEFT;
    }

}
