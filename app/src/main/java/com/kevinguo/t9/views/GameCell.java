package com.kevinguo.t9.views;

import android.graphics.RectF;

import com.kevinguo.t9.models.Cell;

/**
 * Created by kevinguo on 16-02-26.
 */
class GameCell {
    private RectF rectF;
    private Cell.CellStatus cellStatus;  //symbol


    public GameCell() {
        setCellStatus(Cell.CellStatus.EMPTY);
    }

    public GameCell(RectF rectF) {
        this.rectF = rectF;
    }

    public void setRectF(RectF rectF) {
//            Log.d("setRectF", "rectF (l,t,r,b): (" + rectF.left + ", " + rectF.top + ", " + rectF.right + ", " + rectF.bottom + "), (h,w): (" + rectF.height() + ", " + rectF.width() + ")");
        this.rectF = new RectF(rectF);
    }

    public RectF getRectF() {
        return rectF;
    }

    public Cell.CellStatus getCellStatus() {
        return cellStatus;
    }

    public void setCellStatus(Cell.CellStatus cellStatus) {
        this.cellStatus = cellStatus;
    }

}
