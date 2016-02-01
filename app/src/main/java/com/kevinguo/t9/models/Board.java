package com.kevinguo.t9.models;

/**
 * Created by kevinguo on 16-01-29.
 */
public class Board {
    private Pad[][] pads;

    public Board() {
        initPads();
    }

    private void initPads() {
        this.pads = new Pad[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                pads[i][j] = new Pad(i, j);
            }
        }
    }

    public Pad[][] getPads() {
        return pads;
    }
}
