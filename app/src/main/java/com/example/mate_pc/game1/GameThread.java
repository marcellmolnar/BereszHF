package com.example.mate_pc.game1;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

    private boolean running;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;
    // ToDo: communicator class (which is a thread class) should be added.

    public GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder)  {
        this.gameSurface = gameSurface;
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void run()  {
        long startTime = System.nanoTime();

        while(running)  {
            Canvas canvas = null;
            try {
                // Get Canvas from Holder and lock it.
                canvas = surfaceHolder.lockCanvas();

                // Synchronized
                synchronized (canvas)  {
                    gameSurface.update();
                    gameSurface.draw(canvas);
                }
            }catch(Exception e)  {
                // Do nothing.
            } finally {
                if(canvas!= null)  {
                    // Unlock Canvas.
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            long now = System.nanoTime() ;
            // Interval to redraw game
            long waitTime = (now - startTime)/1000000;
            if(waitTime < 10)  {
                waitTime = 10;
            }

            try {
                // Sleep.
                sleep(waitTime);
            } catch(InterruptedException ignored)  {
            }

            startTime = System.nanoTime();
        }
    }

    public void setRunning(boolean running)  {
        this.running = running;
    }

    public boolean isRunning()  {
        return this.running;
    }
}