package com.kevinguo.t9.views;

import android.graphics.RectF;

import com.kevinguo.t9.models.Pad;

/**
 * Created by kevinguo on 16-02-26.
 */
class GamePadV {
    private RectF rectF;
    private GameCell[][] gameCells;
    private Pad.PadStatus padStatus;
    private boolean isAnimating;


    public GamePadV() {
        gameCells = new GameCell[9][9];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameCells[i][j] = new GameCell();
            }
        }
        setPadStatus(Pad.PadStatus.ACTIVE);
        setIsAnimating(false);
    }

    public GamePadV(RectF rectF) {
        this.rectF = rectF;
    }

    public GameCell[][] getGameCells() {
        return gameCells;
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
    }

    public void setGameCells(GameCell[][] gameCells) {
        this.gameCells = gameCells;
    }

    public Pad.PadStatus getPadStatus() {
        return padStatus;
    }

    public void setPadStatus(Pad.PadStatus padStatus) {
        this.padStatus = padStatus;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void setIsAnimating(boolean isAnimating) {
        this.isAnimating = isAnimating;
    }

    public RectF getRectF() {
        return rectF;
    }
}
