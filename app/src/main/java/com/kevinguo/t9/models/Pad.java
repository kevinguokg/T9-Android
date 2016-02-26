package com.kevinguo.t9.models;

/**
 * Created by kevinguo on 16-01-29.
 */
public class Pad {
    public enum PadStatus {INACTIVE, ACTIVE};
    public enum PadWinner {NONE, PLAYER1, PLAYER2};

    private PadWinner padWinner;  // local winner
    private PadStatus padStatus; // padActive

    private int row, col;

    private Cell[][] cells;

    public Pad(int row, int col) {
        init(row, col);
    }

    private void init(int row, int col){
        initPadStatuses();
        initPads(row, col);
        initCells();
    }

    public PadWinner getPadWinner() {
        return padWinner;
    }

    public void setPadWinner(PadWinner padWinner) {

        this.padWinner = padWinner;
    }

    private void initPadStatuses(){
        padWinner = PadWinner.NONE;
        padStatus = PadStatus.ACTIVE;
    }

    private void initCells() {
        this.cells = new Cell[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = new Cell(i, j, Cell.CellStatus.EMPTY);
            }
        }
    }

    private void initPads(int row, int col){
        this.row = row;
        this.col = col;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public PadStatus getPadStatus() {
        return padStatus;
    }

    public void setPadStatus(PadStatus padStatus) {
        this.padStatus = padStatus;
    }
}
