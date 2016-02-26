package com.kevinguo.t9.views;

import android.graphics.RectF;

/**
 * Created by kevinguo on 16-02-26.
 */
class GameBoardV {
    private RectF rectF;
    private GamePadV[][] gamePadVs;

    public GameBoardV() {

        // instantiates view models
        this.gamePadVs = new GamePadV[3][3];

        // this would work
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gamePadVs[i][j] = new GamePadV();
            }
        }
    }

    public void setDemoBoardV() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                //gamePadVs[i][j].g
            }
        }
    }

    public RectF getRectF() {
        return rectF;
    }

    public GamePadV[][] getGamePadVs() {
        return gamePadVs;
    }
}
