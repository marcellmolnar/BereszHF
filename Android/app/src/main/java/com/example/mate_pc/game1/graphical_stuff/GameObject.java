package com.example.mate_pc.game1.graphical_stuff;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Describes a gameobject abstract, which every object can use.
 */
public abstract class GameObject {

    private Bitmap image;

    final int rowCount;
    final int colCount;

    private final int imWidth;
    private final int imHeight;

    private final int realWidth;
    private final int realHeight;

    int x;
    int y;

    GameObject(Bitmap image, int rowCount, int colCount, int x, int y, int height) {

        this.image = image;
        this.rowCount = rowCount;
        this.colCount = colCount;

        this.x = x;
        this.y = y;

        int WIDTH = image.getWidth();
        int HEIGHT = image.getHeight();

        imWidth = WIDTH / colCount;
        imHeight = HEIGHT / rowCount;

        realHeight = height;
        realWidth = imWidth*realHeight/imHeight;
    }

    void draw(Canvas canvas) {
        /*
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(x, y, x+getWidth(), y+getHeight(), paint);
        */
    }

    Bitmap createSubImageAt(int row, int col) {
        // createBitmap(bitmap, x, y, width, height).
        Bitmap subImage = Bitmap.createBitmap(image, col * imWidth, row * imHeight , imWidth, imHeight);
        return Bitmap.createScaledBitmap(subImage, realWidth, realHeight, false);

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public int getHeight() {
        return realHeight;
    }

    public int getWidth() {
        return realWidth;
    }

}