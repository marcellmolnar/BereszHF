package com.example.mate_pc.game1;

import android.graphics.Bitmap;

public abstract class GameObject {

    protected Bitmap image;

    protected final int rowCount;
    protected final int colCount;

    protected final int WIDTH;
    protected final int HEIGHT;

    protected final int imWidth;
    protected final int imHeight;

    private final int realWidth;
    private final int realHeight;

    protected int x;
    protected int y;

    public GameObject(Bitmap image, int rowCount, int colCount, int x, int y, int height) {

        this.image = image;
        this.rowCount = rowCount;
        this.colCount = colCount;

        this.x = x;
        this.y = y;

        WIDTH = image.getWidth();
        HEIGHT = image.getHeight();

        imWidth = WIDTH / colCount;
        imHeight = HEIGHT / rowCount;

        realHeight = height;
        realWidth = imWidth*realHeight/imHeight;
    }


    public Bitmap createSubImageAt(int row, int col) {
        // createBitmap(bitmap, x, y, width, height).
        Bitmap subImage = Bitmap.createBitmap(image, col * imWidth, row * imHeight , imWidth, imHeight);
        Bitmap resized = Bitmap.createScaledBitmap(subImage, realWidth, realHeight, false);
        return resized;
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

    public int getRealHeight() {
        return realHeight;
    }

}