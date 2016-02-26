package com.kevinguo.t9.views;

import android.graphics.Canvas;

import com.kevinguo.t9.views.GameBoardView;

/**
 * Created by kevinguo on 16-02-10.
 */
public class GameTurnThread extends Thread {
    private GameBoardView gameBoardView;
    private boolean running = false;

    //Frame speed
    long timeNow;
    long timePrev = 0;
    long timePrevFrame = 0;
    long timeDelta;

    public GameTurnThread(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        /*while (running) {
            Canvas c = null;

            //limit frame rate to max 60fps
            timeNow = System.currentTimeMillis();
            timeDelta = timeNow - timePrevFrame;
            if (timeDelta < 16) {
                try {
                    Thread.sleep(16 - timeDelta);
                } catch (InterruptedException e) {

                }
            }
            timePrevFrame = System.currentTimeMillis();

            try {

                c = gameBoardView.getHolder().lockCanvas();
                if (c != null) {
                    synchronized (gameBoardView.getHolder()) {
                        gameBoardView.onDraw(c);
                    }
                }
            } finally {
                if (c != null) {
                    gameBoardView.getHolder().unlockCanvasAndPost(c);
                }
            }
//            running = false;
        }*/
    }
}
