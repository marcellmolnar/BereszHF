package com.example.mate_pc.game1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Bullet extends GameObject {

    private Bitmap image;

    private double movingVectorX = 0;

    private GameSurface gameSurface;

    public Bullet(GameSurface gameSurface, Bitmap image, int x, int y, int height,double movingVector) {
        super(image, 1, 1, x, y, height);

        this.gameSurface = gameSurface;
        this.image = createSubImageAt(0,0);
        this.movingVectorX = movingVector;
    }

    private Bitmap getCurrentMoveBitmap(){
        return image;
    }

    void draw(Canvas canvas) {
        super.draw(canvas);
        Bitmap bitmap = getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap, x, y, null);
    }

    boolean isHit(GameObject gameObject) {
        if(gameObject.getY() < this.getY() + this.getHeight()*0.8
                && this.getY() + this.getHeight()*0.2 < gameObject.getY() + gameObject.getHeight()) {

            if (0 < this.movingVectorX) {
                if (gameObject.getX() + (double)gameObject.getWidth()/10 < this.getX() + this.getWidth()
                     && this.getX() < gameObject.getX() + gameObject.getWidth()){
                    return true;
                }
            }
            else if (this.movingVectorX < 0) {
                if (this.getX() < gameObject.getX() + (double) gameObject.getWidth() * 9 / 10
                        && gameObject.getX() < this.getX() + this.getWidth()) {
                    return true;
                }
            }
        }
        else {
           return false;
        }
        return false;
    }

    public void update() {
        this.x = this.x + (int)this.movingVectorX;
    }


}
