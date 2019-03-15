package com.example.mate_pc.game1.graphical_stuff;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Platform extends GameObject {

    private Bitmap image;

    public Platform(Bitmap image, int x, int y, int height) {
        super(image, 1, 1, x, y, height);

        this.image = createSubImageAt(0,0);
    }

    private Bitmap getCurrentMoveBitmap(){
        return image;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Bitmap bitmap = getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public boolean isBelow(Character character) {
        if(character.getX() + character.getWidth()*4/5 < this.getX() || this.getX()+this.getWidth() < character.getX()) {
            return false;
        }
        else {
            return getY() >= character.getBottomHeight();
        }
    }


}
