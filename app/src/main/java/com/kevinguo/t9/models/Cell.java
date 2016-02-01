package com.kevinguo.t9.models;

/**
 * Created by kevinguo on 16-01-29.
 */
public class Cell {
    public enum CellStatus {EMPTY, PLAYER1, PLAYER2};

    private int row, col;
    private CellStatus cellStatus;  //symbol

    public Cell(){

    }

    public Cell(int row, int col, CellStatus cellStatus){
        this.row = row;
        this.col = col;
        this.cellStatus = cellStatus;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public CellStatus getCellStatus() {
        return cellStatus;
    }

    public void setCellStatus(CellStatus cellStatus) {
        this.cellStatus = cellStatus;
    }
}
