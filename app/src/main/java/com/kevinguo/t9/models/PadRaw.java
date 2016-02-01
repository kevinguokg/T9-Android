package com.kevinguo.t9.models;

/**
 * Created by kevinguo on 16-01-31.
 */
public class PadRaw {
    private int row;
    private int col;
    private int ownBy;
    private String symbol;
    private int padActive;
    private int localWinner;

    public PadRaw(int row, int col, int ownBy, String symbol, int padActive, int localWinner) {
        this.row = row;
        this.col = col;
        this.ownBy = ownBy;
        this.symbol = symbol;
        this.padActive = padActive;
        this.localWinner = localWinner;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getOwnBy() {
        return ownBy;
    }

    public void setOwnBy(int ownBy) {
        this.ownBy = ownBy;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getPadActive() {
        return padActive;
    }

    public void setPadActive(int padActive) {
        this.padActive = padActive;
    }

    public int getLocalWinner() {
        return localWinner;
    }

    public void setLocalWinner(int localWinner) {
        this.localWinner = localWinner;
    }
}
