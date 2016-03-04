package com.kevinguo.t9.models;

/**
 * Created by kevinguo on 16-03-04.
 */
public class GameScore {
    private int gameScoreP1;
    private int gameScoreP2;

    public GameScore(){
        this.gameScoreP1 = 0;
        this.gameScoreP2 = 0;
    }

    public int getGameScoreP1() {
        return gameScoreP1;
    }

    public void setGameScoreP1(int gameScoreP1) {
        this.gameScoreP1 = gameScoreP1;
    }

    public int getGameScoreP2() {
        return gameScoreP2;
    }

    public void setGameScoreP2(int gameScoreP2) {
        this.gameScoreP2 = gameScoreP2;
    }

    public void bumpGameScoreP1(){
        setGameScoreP1(++gameScoreP1);
    }

    public void bumpGameScoreP2(){
        setGameScoreP2(++gameScoreP2);
    }

    public void bumpGameScore(Game.GameWinner gameWinner){
        if (gameWinner == Game.GameWinner.PLAYER1)
            bumpGameScoreP1();
        else if (gameWinner == Game.GameWinner.PLAYER2){
            bumpGameScoreP2();
        }
    }
}
