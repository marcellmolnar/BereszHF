package com.example.mate_pc.game1;

import android.graphics.Bitmap;

public abstract class GameObject {

    protected Bitmap image;

    protected final int rowCount;
    protected final int colCount;

    protected final int WIDTH;
    protected final int HEIGHT;

    protected final int width;
    protected final int height;

    protected int x;
    protected int y;

    public GameObject(Bitmap image, int rowCount, int colCount, int x, int y) {

        this.image = image;
        this.rowCount = rowCount;
        this.colCount = colCount;

        this.x = x;
        this.y = y;

        WIDTH = image.getWidth();
        HEIGHT = image.getHeight();

        width = WIDTH / colCount;
        height = HEIGHT / rowCount;
    }


    public Bitmap createSubImageAt(int row, int col) {
        // createBitmap(bitmap, x, y, width, height).
        Bitmap subImage = Bitmap.createBitmap(image, col * width, row * height , width, height);
        return subImage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

}