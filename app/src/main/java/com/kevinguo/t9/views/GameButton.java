package com.kevinguo.t9.views;

import android.graphics.RectF;

/**
 * Created by kevinguo on 16-02-26.
 */
class GameButton {
    private RectF rect;
    private int top, left, right, bottom, width, height;
    private boolean isTouched = false;
    private boolean isAnimating = false;
    private boolean isHidden;
    private String buttonText;

    public GameButton() {
        rect = new RectF();
        isHidden = true;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public GameButton(int left, int top, int right, int bottom) {
        rect = new RectF();
        this.top = top;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        rect.set(left, top, right, bottom);

        this.width = right - left;
        this.height = bottom - top;
    }

    public GameButton(RectF rect) {
        this.rect = rect;
    }

    public void setRect(int left, int top, int right, int bottom) {
        this.top = top;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        rect.set(left, top, right, bottom);

        this.width = right - left;
        this.height = bottom - top;
    }

    public int getWidth() {
        return this.width;
    }

    public void setIsTouched(boolean isTouched) {
        this.isTouched = isTouched;
    }

    public boolean isTouched() {
        return this.isTouched;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void setIsAnimating(boolean isAnimating) {
        this.isAnimating = isAnimating;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public RectF getRect() {
        return rect;
    }

    public int getTop() {
        return top;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getHeight() {
        return height;
    }
}

