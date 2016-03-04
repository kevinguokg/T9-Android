package com.kevinguo.t9.views;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.kevinguo.t9.R;
import com.kevinguo.t9.models.Board;
import com.kevinguo.t9.models.Cell;
import com.kevinguo.t9.models.Game;
import com.kevinguo.t9.models.Pad;
import com.kevinguo.t9.models.PadRaw;
import com.kevinguo.t9.utils.TypeFaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by kevinguo on 16-01-29.
 */
public class GameBoardView extends View {
    // view models
    private Paint mainPaint, highlightPaint, unHighlightPaint, textPaint, padPaint, cellPaint, player1TurnPaint, player2TurnPaint, middlePannelPaint;
    private android.text.TextPaint androidTextPaint;

    private RectF rectFPad;
    private RectF rectFCell;
    private RectF player1TurnRect, player2TurnRect, middlePanelRect;
    private Context context;
    private float density;
    private List<GameButton> buttonList;

    private static final float INACTIVE_PAD_WIDTH_RATIO = 0.8f;
    private static final float ACTIVE_PAD_WIDTH_RATIO = 1.0f;
    private static final float ACTIVE_TURN_WIDTH_RATIO = 0.3f;
    private static final float INACTIVE_TURN_WIDTH_RATIO = 0.1f;
    private static final float DRAW_TURN_WIDTH_RATIO = 0.2f;


    private float inactivePadWidthRatio;
    private float activePadWidthRatio;

    private float animatingTurnWidthRatio;

    // Firebase references
    private Firebase myFirebaseRef;

    // data models
    private Game game;

    private PadRaw[][][][] padRaw;

    // view models
    private GameButton localGameBtn, friendGameBtn, onlineGameBtn, howToPlayBtn;
    private GameButton rematchBtn, goBackBtn;
    private GameButton howToPlayBackBtn, hotToPlayNextBtn;
    private GameBoardV gameBoardV;

    // game properties
    private int roomNumber;
    private GameButton touchedButton;

    private boolean isGameHost;
    private boolean isRematchRequester;

    private GameTurnThread gameTurnThread, gameButtonThread;

    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void init() {
        density = context.getResources().getDisplayMetrics().density;
        mainPaint = new Paint();
        mainPaint.setColor(context.getResources().getColor(R.color.T9OrangeDark));
        mainPaint.setStrokeWidth(12.0f);
        mainPaint.setTextSize(36.0f);

        highlightPaint = new Paint();
        highlightPaint.setColor(context.getResources().getColor(R.color.T9BlueLight));
        highlightPaint.setStrokeWidth(12.0f);
        highlightPaint.setTextSize(36.0f);

        unHighlightPaint = new Paint();
        unHighlightPaint.setColor(context.getResources().getColor(R.color.T9OrangeDark));
        unHighlightPaint.setStrokeWidth(12.0f);
        unHighlightPaint.setTextSize(36.0f);

        textPaint = new Paint();
        textPaint.setColor(context.getResources().getColor(R.color.T9White));
        textPaint.setStrokeWidth(12.0f);
        textPaint.setTextSize(getResources().getDimension(R.dimen.font_large));
        textPaint.setTypeface(TypeFaces.get(context, "arcade_classic"));

        androidTextPaint = new TextPaint();
        androidTextPaint.setColor(context.getResources().getColor(R.color.T9White));
        androidTextPaint.setStrokeWidth(2.0f);
        androidTextPaint.setTextSize(getResources().getDimension(R.dimen.font_medium));
        androidTextPaint.setTypeface(TypeFaces.get(context, "arcade_classic"));

        padPaint = new Paint();
        padPaint.setStrokeWidth(1.0f);
        padPaint.setStyle(Paint.Style.STROKE);
        padPaint.setAntiAlias(true);
        padPaint.setColor(context.getResources().getColor(R.color.T9Gray1));

        cellPaint = new Paint();
        cellPaint.setStyle(Paint.Style.FILL);
        cellPaint.setAntiAlias(true);
        cellPaint.setColor(context.getResources().getColor(R.color.T9Gray1));

        player1TurnPaint = new Paint();
        player1TurnPaint.setStyle(Paint.Style.FILL);
        player1TurnPaint.setAntiAlias(true);
        player1TurnPaint.setColor(context.getResources().getColor(R.color.T9Player1));

        player2TurnPaint = new Paint();
        player2TurnPaint.setStyle(Paint.Style.FILL);
        player2TurnPaint.setAntiAlias(true);
        player2TurnPaint.setColor(context.getResources().getColor(R.color.T9Player2));

        middlePannelPaint = new Paint();
        middlePannelPaint.setStyle(Paint.Style.FILL);
        middlePannelPaint.setAntiAlias(true);
        middlePannelPaint.setColor(context.getResources().getColor(R.color.T9MiddleStatusBar));
        middlePannelPaint.setTextSize(64.0f);

        activePadWidthRatio = ACTIVE_PAD_WIDTH_RATIO;
        inactivePadWidthRatio = INACTIVE_PAD_WIDTH_RATIO;

        animatingTurnWidthRatio = ACTIVE_TURN_WIDTH_RATIO;

        localGameBtn = new GameButton();
        friendGameBtn = new GameButton();
        onlineGameBtn = new GameButton();
        howToPlayBtn = new GameButton();
        rematchBtn = new GameButton();
        goBackBtn = new GameButton();
        howToPlayBackBtn = new GameButton();

        buttonList = new ArrayList<>();
        buttonList.add(localGameBtn);
        buttonList.add(friendGameBtn);
        buttonList.add(onlineGameBtn);
        buttonList.add(howToPlayBtn);
        buttonList.add(rematchBtn);
        buttonList.add(goBackBtn);
        buttonList.add(howToPlayBackBtn);


        rectFPad = new RectF();
        rectFCell = new RectF();
        player1TurnRect = new RectF();
        player2TurnRect = new RectF();
        middlePanelRect = new RectF();

        // creates demo game
        game = new Game(Game.GameType.DEMO);

        gameBoardV = new GameBoardV();

        initPadRaw();

        gameTurnThread = new GameTurnThread(this);
        gameButtonThread = new GameTurnThread(this);

        /*getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (!gameTurnThread.isAlive()) {
                    gameTurnThread.setRunning(true);
                    gameTurnThread.start();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameTurnThread.setRunning(false);
                while (retry) {
                    try {
                        gameTurnThread.join();
                        Log.d("huhuhu", "huhuhu:: gameTurnThread join");
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }
        });*/
    }

    private void initPadRaw() {
        padRaw = new PadRaw[3][3][3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        padRaw[i][j][k][l] = new PadRaw(k, l, 0, "E", 1, 0);
                    }
                }
            }
        }
    }

    public void setFireBaseRef(Firebase myFirebaseRef) {
        this.myFirebaseRef = myFirebaseRef;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawButtons(canvas);
        drawDemoBoard(canvas);
        drawTurnBar(canvas);
        drawTutorialInstructions(canvas);
    }

    private void drawTurnBar(Canvas canvas) {
        if (game.getGameType() == Game.GameType.DEMO || game.getGameType() == Game.GameType.TUTORIAL)
            return;

        // get three bar measurements based on turn
        float player1BarWidth, middleBarWidth, player2BarWidth;
        float barHeight = canvas.getHeight() * 0.05f;

        String gameResultStr = "";
        if (game.getGameStatus() == Game.GameStatus.COMPLETED) {
            if (game.getGameWinner() == Game.GameWinner.NONE) {
                // draw case
                player1BarWidth = canvas.getWidth() * 0.2f;
                middleBarWidth = canvas.getWidth() * 0.6f;
                player2BarWidth = canvas.getWidth() * 0.2f;
                gameResultStr = "Draw!";
            } else {
                // win case
                player1BarWidth = canvas.getWidth() * 0.0f;
                middleBarWidth = canvas.getWidth();
                player2BarWidth = canvas.getWidth() * 0.0f;
                gameResultStr = "Player " + (game.getGameWinner() == Game.GameWinner.PLAYER1 ? "1" : "2") + " is the winner!";
            }
        } else if (game.getGameStatus() == Game.GameStatus.ABORTED) {
            player1BarWidth = canvas.getWidth() * 0.2f;
            middleBarWidth = canvas.getWidth() * 0.6f;
            player2BarWidth = canvas.getWidth() * 0.2f;
            gameResultStr = "Your opponent has left!";
        } else {
            if (game.getGameTurn() == Game.GameTurn.PLAYER1) {
                // animating player1BarWidth between 0.3f and 0.1f
//                player1BarWidth = canvas.getWidth() * 0.3f;
                player1BarWidth = canvas.getWidth() * animatingTurnWidthRatio;
                middleBarWidth = canvas.getWidth() * 0.6f;
                player2BarWidth = canvas.getWidth() * 0.1f;
            } else if (game.getGameTurn() == Game.GameTurn.PLAYER2) {
//                player1BarWidth = canvas.getWidth() * 0.1f;
                player1BarWidth = canvas.getWidth() * animatingTurnWidthRatio;
                middleBarWidth = canvas.getWidth() * 0.6f;
                player2BarWidth = canvas.getWidth() * 0.3f;
            } else {
                // default to draw case
//                player1BarWidth = canvas.getWidth() * 0.2f;
                player1BarWidth = canvas.getWidth() * animatingTurnWidthRatio;
                middleBarWidth = canvas.getWidth() * 0.6f;
                player2BarWidth = canvas.getWidth() * 0.2f;
            }
        }

        // draw player 1 turn bar
        player1TurnRect.set(0.0f, 0.0f, player1BarWidth, barHeight);
        canvas.drawRect(player1TurnRect, player1TurnPaint);

        // draw middle bar
        middlePanelRect.set(player1BarWidth, 0.0f, player1BarWidth + middleBarWidth, barHeight);
        canvas.drawRect(middlePanelRect, middlePannelPaint);

        float textWidth = textPaint.measureText(gameResultStr);
        canvas.drawText(gameResultStr, (middlePanelRect.left + middleBarWidth / 2) - textWidth / 2, middlePanelRect.bottom - barHeight / 2 + measureTextHeight(gameResultStr) / 2, textPaint);

        // draw player 2 turn bar
        player2TurnRect.set(player1BarWidth + middleBarWidth, 0.0f, canvas.getWidth(), barHeight);
        canvas.drawRect(player2TurnRect, player2TurnPaint);
    }

    private void drawButtons(Canvas canvas) {
        if (game.getGameType() == Game.GameType.DEMO)
            drawDemoButtons(canvas);
        else if (game.getGameType() == Game.GameType.TUTORIAL) {
//            drawTutorialButtons(canvas);
        } else {
            drawInGameButtons(canvas);
        }
    }

    private void drawTutorialButtons(Canvas canvas) {
        int left = (int) (context.getResources().getDimension(R.dimen.space_large));
        int buttonWidth = (canvas.getWidth() - 2 * left);
        int buttonHeight = (int) (canvas.getHeight() * 0.2) / 4; // four menu buttons on bottom
        int top = (int) (canvas.getHeight() * 0.75) + (buttonHeight + (int) (context.getResources().getDimension(R.dimen.space_medium))) * 3;

        int right = left + buttonWidth;
        int bottom = top + buttonHeight;

        howToPlayBackBtn.setRect(left, top, right, bottom);
        howToPlayBackBtn.setIsHidden(false);
        canvas.drawRoundRect(howToPlayBackBtn.getRect(), 20f, 20f, howToPlayBackBtn.isTouched() || howToPlayBackBtn.isAnimating() ? highlightPaint : mainPaint);
        String str = context.getResources().getString(R.string.go_back_btn_text);
        howToPlayBackBtn.setButtonText(str);
        float textWidth = textPaint.measureText(str);
        canvas.drawText(str, (howToPlayBackBtn.getLeft() + howToPlayBackBtn.getWidth() / 2) - textWidth / 2, howToPlayBackBtn.getRect().bottom - howToPlayBackBtn.getHeight() / 2 + measureTextHeight(str) / 2, textPaint);
    }

    private void drawInGameButtons(Canvas canvas) {
        int left = (int) (context.getResources().getDimension(R.dimen.space_large));
        int buttonWidth = (canvas.getWidth() - 3 * left) / 2;  // two side padding, one in-between paddings
        int buttonHeight = (int) (canvas.getHeight() * 0.2) / 4;
        int top = (int) (canvas.getHeight() * 0.85);
        int right = left + buttonWidth;
        int bottom = top + buttonHeight;

        // rematch button
        rematchBtn.setRect(left, top, right, bottom);
        rematchBtn.setIsHidden(false);
        canvas.drawRoundRect(rematchBtn.getRect(), 20f, 20f, rematchBtn.isTouched() ? highlightPaint : mainPaint);
        String str = context.getResources().getString(R.string.rematch_btn_text);
        rematchBtn.setButtonText(str);
        float textWidth = textPaint.measureText(str);
        canvas.drawText(str, (rematchBtn.getLeft() + rematchBtn.getWidth() / 2) - textWidth / 2, rematchBtn.getRect().bottom - rematchBtn.getHeight() / 2 + measureTextHeight(str) / 2, textPaint);

        // go back button
        left = right + left;
        right = left + buttonWidth;
        goBackBtn.setRect(left, top, right, bottom);
        goBackBtn.setIsHidden(false);
        canvas.drawRoundRect(goBackBtn.getRect(), 20f, 20f, goBackBtn.isTouched() ? highlightPaint : mainPaint);

        str = context.getResources().getString(R.string.go_back_btn_text);
        goBackBtn.setButtonText(str);
        textWidth = textPaint.measureText(str);
        canvas.drawText(str, (goBackBtn.getLeft() + goBackBtn.getWidth() / 2) - textWidth / 2, goBackBtn.getRect().bottom - goBackBtn.getHeight() / 2 + measureTextHeight(str) / 2, textPaint);
    }

    private void drawDemoButtons(Canvas canvas) {
        Log.d("hihihi", "drawDemoButtons");

        int left = (int) (context.getResources().getDimension(R.dimen.space_large));
        int buttonWidth = (canvas.getWidth() - 2 * left);
//        int buttonHeight = (int) (context.getResources().getDimension(R.dimen.space_xxxlarge));
        int buttonHeight = (int) (canvas.getHeight() * 0.2) / 4; // four menu buttons on bottom
//        int top = canvas.getHeight() - (int) (context.getResources().getDimension(R.dimen.padding_bottom_button)) - buttonHeight;
        int top = (int) (canvas.getHeight() * 0.75);
        int right = left + buttonWidth;
        int bottom = top + buttonHeight;

        // local game button
        localGameBtn.setRect(left, top, right, bottom);
        localGameBtn.setIsHidden(false);
        canvas.drawRoundRect(localGameBtn.getRect(), 20f, 20f, localGameBtn.isTouched() || localGameBtn.isAnimating() ? highlightPaint : mainPaint);
        String str = context.getResources().getString(R.string.local_game_btn_text);
        localGameBtn.setButtonText(str);
        float textWidth = textPaint.measureText(str);
        canvas.drawText(str, (localGameBtn.getLeft() + localGameBtn.getWidth() / 2) - textWidth / 2, localGameBtn.getRect().bottom - localGameBtn.getHeight() / 2 + measureTextHeight(str) / 2, textPaint);

        // friend game button
        top = localGameBtn.getBottom() + (int) (context.getResources().getDimension(R.dimen.space_medium));
        bottom = top + buttonHeight;
        friendGameBtn.setRect(left, top, right, bottom);
        friendGameBtn.setIsHidden(false);
        canvas.drawRoundRect(friendGameBtn.getRect(), 20f, 20f, friendGameBtn.isTouched() || friendGameBtn.isAnimating() ? highlightPaint : mainPaint);

        str = context.getResources().getString(R.string.friend_game_btn_text);
        friendGameBtn.setButtonText(str);
        textWidth = textPaint.measureText(str);
        canvas.drawText(str, (friendGameBtn.getLeft() + friendGameBtn.getWidth() / 2) - textWidth / 2, friendGameBtn.getRect().bottom - friendGameBtn.getHeight() / 2 + measureTextHeight(str) / 2, textPaint);

        // online game button
        top = friendGameBtn.getBottom() + (int) (context.getResources().getDimension(R.dimen.space_medium));
        bottom = top + buttonHeight;
        onlineGameBtn.setRect(left, top, right, bottom);
        onlineGameBtn.setIsHidden(false);
        canvas.drawRoundRect(onlineGameBtn.getRect(), 20f, 20f, onlineGameBtn.isTouched() || onlineGameBtn.isAnimating() ? highlightPaint : mainPaint);

        str = context.getResources().getString(R.string.online_game_btn_text);
        onlineGameBtn.setButtonText(str);
        textWidth = textPaint.measureText(str);
        canvas.drawText(str, (onlineGameBtn.getLeft() + onlineGameBtn.getWidth() / 2) - textWidth / 2, onlineGameBtn.getRect().bottom - onlineGameBtn.getHeight() / 2 + measureTextHeight(str) / 2, textPaint);

        // how to play game button
        top = onlineGameBtn.getBottom() + (int) (context.getResources().getDimension(R.dimen.space_medium));
        bottom = top + buttonHeight;
        howToPlayBtn.setRect(left, top, right, bottom);
        howToPlayBtn.setIsHidden(false);
        canvas.drawRoundRect(howToPlayBtn.getRect(), 20f, 20f, howToPlayBtn.isTouched() || howToPlayBtn.isAnimating() ? highlightPaint : mainPaint);

        str = context.getResources().getString(R.string.how_to_play_btn_text);
        howToPlayBtn.setButtonText(str);
        textWidth = textPaint.measureText(str);
        canvas.drawText(str, (howToPlayBtn.getLeft() + howToPlayBtn.getWidth() / 2) - textWidth / 2, howToPlayBtn.getRect().bottom - howToPlayBtn.getHeight() / 2 + measureTextHeight(str) / 2, textPaint);
    }

    private void drawTutorialInstructions(Canvas canvas) {
        if (game.getGameType() == Game.GameType.TUTORIAL) {
            int left = (int) (context.getResources().getDimension(R.dimen.space_medium));
            int top = (int) gameBoardV.getGamePadVs()[2][0].getRectF().bottom + (int) (context.getResources().getDimension(R.dimen.space_large));
            int buttonWidth = (canvas.getWidth() - 2 * left);
            String str = "T9 is 9 normal games of Tic Tac Toe played simultaneously. The board is made up of 9 tiles, each of which contains 9 squares. Your move will dictate where your opponent can play however. So if you play your piece in the top right square of a tile, then your opponent must play their next piece in the top right tile. \n\n The first player to win 3 straight tiles in a row wins.";
            //canvas.drawText(str, left, top, textPaint);

            StaticLayout staticLayout = new StaticLayout(str, androidTextPaint, buttonWidth, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
            canvas.save();
            canvas.translate(left, top);
            staticLayout.draw(canvas);
            canvas.restore();
        }
    }

    private int measureTextHeight(String str) {
        Rect rectBound = new Rect();
        textPaint.getTextBounds(str, 0, str.length(), rectBound);

        return rectBound.height();
    }

    private void drawDemoBoard(Canvas canvas) {
        Board gameBoard = game.getGameBoard();

        for (Pad[] pads : gameBoard.getPads()) {
            for (Pad singlePad : pads) {
                drawPad(canvas, singlePad);
            }
        }
    }

    private void drawPad(Canvas canvas, Pad pad) {
        float horiPadding = context.getResources().getDimension(R.dimen.space_medium);
//        float vertiPadding = context.getResources().getDimension(R.dimen.space_large);
        float vertiPadding = canvas.getHeight() * 0.15f;
        float interPadPadding = context.getResources().getDimension(R.dimen.space_small);
        float padWidth = (canvas.getWidth() - horiPadding * 2 - interPadPadding * 2) / 3;

        float padLeft = (pad.getCol() == 0) ? horiPadding : horiPadding + pad.getCol() * (padWidth + interPadPadding);
        float padTop = (pad.getRow() == 0) ? vertiPadding : vertiPadding + pad.getRow() * (padWidth + interPadPadding);

        if (pad.getPadStatus() == Pad.PadStatus.ACTIVE) {
            float normalPadWidth = padWidth;

            if (gameBoardV.getGamePadVs()[pad.getRow()][pad.getCol()].isAnimating())
                padWidth *= activePadWidthRatio;
            else {
                padWidth *= ACTIVE_PAD_WIDTH_RATIO;
            }

            padLeft = padLeft + (normalPadWidth - padWidth) / 2;
            padTop = padTop + (normalPadWidth - padWidth) / 2;

            rectFPad.set(padLeft, padTop, padLeft + padWidth, padTop + padWidth);
        } else if (pad.getPadStatus() == Pad.PadStatus.INACTIVE) {
            float normalPadWidth = padWidth;

            if (gameBoardV.getGamePadVs()[pad.getRow()][pad.getCol()].isAnimating())
                padWidth *= inactivePadWidthRatio;
            else {
                padWidth *= INACTIVE_PAD_WIDTH_RATIO;
            }

            padLeft = padLeft + (normalPadWidth - padWidth) / 2;
            padTop = padTop + (normalPadWidth - padWidth) / 2;

            rectFPad.set(padLeft, padTop, padLeft + padWidth, padTop + padWidth);
        }

        if (pad.getPadWinner() == Pad.PadWinner.PLAYER1) {
            padPaint.setColor(context.getResources().getColor(R.color.T9Player1));
            padPaint.setStrokeWidth(2.0f);
        } else if (pad.getPadWinner() == Pad.PadWinner.PLAYER2) {
            padPaint.setColor(context.getResources().getColor(R.color.T9Player2));
            padPaint.setStrokeWidth(2.0f);
        } else {
            padPaint.setColor(context.getResources().getColor(R.color.T9Gray1));
            padPaint.setStrokeWidth(1.0f);
        }

        canvas.drawRect(rectFPad, padPaint);

        // needs to memorize this
        gameBoardV.getGamePadVs()[pad.getRow()][pad.getCol()].setRectF(rectFPad);
        GamePadV padView = gameBoardV.getGamePadVs()[pad.getRow()][pad.getCol()];

        for (Cell[] cells : pad.getCells()) {
            for (Cell singleCell : cells) {
                drawCell(canvas, padView, singleCell);
            }
        }
    }

    private void drawCell(Canvas canvas, GamePadV padView, Cell cell) {
        float padInnerPadding = context.getResources().getDimension(R.dimen.space_micro);
        float interCellPadding = context.getResources().getDimension(R.dimen.space_tiny);
        float cellWidth = (padView.getRectF().width() - padInnerPadding * 2 - interCellPadding * 2) / 3;

        float cellLeft = (cell.getCol() == 0) ? padView.getRectF().left + padInnerPadding : padView.getRectF().left + padInnerPadding + cell.getCol() * cellWidth + cell.getCol() * interCellPadding;
        float cellTop = (cell.getRow() == 0) ? padView.getRectF().top + padInnerPadding : padView.getRectF().top + padInnerPadding + cell.getRow() * cellWidth + cell.getRow() * interCellPadding;

        rectFCell.set(cellLeft, cellTop, cellLeft + cellWidth, cellTop + cellWidth);

        if (cell.getCellStatus() == Cell.CellStatus.PLAYER1) {
            cellPaint.setColor(context.getResources().getColor(R.color.T9Player1));
        } else if (cell.getCellStatus() == Cell.CellStatus.PLAYER2) {
            cellPaint.setColor(context.getResources().getColor(R.color.T9Player2));
        } else {
            cellPaint.setColor(context.getResources().getColor(R.color.T9Gray1));
        }
        canvas.drawRect(rectFCell, cellPaint);

        padView.getGameCells()[cell.getRow()][cell.getCol()].setRectF(rectFCell);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        final String local = context.getResources().getString(R.string.local_game_btn_text);
        final String friend = context.getResources().getString(R.string.friend_game_btn_text);
        final String online = context.getResources().getString(R.string.online_game_btn_text);
        final String howToPlay = context.getResources().getString(R.string.how_to_play_btn_text);
        final String rematch = context.getResources().getString(R.string.rematch_btn_text);
        final String goBack = context.getResources().getString(R.string.go_back_btn_text);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            for (GameButton btn : buttonList) {
                if (btn.isHidden())
                    continue;

                if (btn.getRect().contains(touchX, touchY) && touchedButton == btn) {
                    Log.d("hihihi", "a btn is pressed type " + btn.getButtonText() + " hidden? " + btn.isHidden());
//                    Toast.makeText(context, "button " + btn.getButtonText() + " touched", Toast.LENGTH_SHORT).show();

                    btn.setIsTouched(false);
                    btn.setIsAnimating(false);

                    if (game.getGameType() == Game.GameType.DEMO) {
                        if (btn.getButtonText().equals(local)) {
                            Log.d("hihihi", "local btn is pressed");

                            game = new Game(Game.GameType.LOCAL);

                            localGameBtn.setIsHidden(true);
                            friendGameBtn.setIsHidden(true);
                            onlineGameBtn.setIsHidden(true);
                            howToPlayBtn.setIsHidden(true);

                            // start local game
                        } else if (btn.getButtonText().equals(friend)) {
                            Log.d("hihihi", "friend btn is pressed");

                            // start friend game

                            do {
                                roomNumber = (int) (Math.random() * 100000);
                            } while (roomNumber == 0);

                            initInternetGame(Game.GameType.FRIENDS, Game.GameStatus.PENDING, Game.GameTurn.PLAYER1);

                            localGameBtn.setIsHidden(true);
                            friendGameBtn.setIsHidden(true);
                            onlineGameBtn.setIsHidden(true);
                            howToPlayBtn.setIsHidden(true);


                        } else if (btn.getButtonText().equals(online)) {
                            Log.d("hihihi", "online btn is pressed");

                            // look up waiting list, if there is one, grab that and start the game, if not, create a new one in waiting list TODO

                            Query waitlistQuery = myFirebaseRef.child("randomgames/waitinglist").orderByValue();

                            waitlistQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("hihihi", "onDataChange " + dataSnapshot.getValue());

                                    if (dataSnapshot.getValue() == null) {
                                        // if no waiting list or no items in waiting list, create the list

                                        do {
                                            roomNumber = (int) (Math.random() * 100000);
                                        } while (roomNumber == 0);

                                        myFirebaseRef.child("randomgames/waitinglist/" + roomNumber).setValue(roomNumber);

                                        // enters the onine game mode (as player 1), waiting for player2 to join

                                        //initInternetGame(Game.GameType.ONLINE, Game.GameStatus.PENDING, Game.GameTurn.PLAYER1);
                                        initInternetGame(Game.GameType.ONLINE, Game.GameStatus.PENDING, Game.GameTurn.PLAYER1);

                                        isGameHost = true;
                                    } else {
                                        // if waiting list has pending game, look for a room number to enter, say the first one on the list
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            Log.d("hihihi", "child.getValue() =  " + child.getValue());
                                            roomNumber = ((Long) child.getValue()).intValue();
                                            break;
                                        }

                                        Log.d("hihihi", "onDataChange roomNumber set to " + roomNumber);

                                        // removes this room from waiting list and enters the online game mode (as player 2)
                                        myFirebaseRef.child("randomgames/waitinglist/" + roomNumber).removeValue();
                                        initInternetGame(Game.GameType.ONLINE, Game.GameStatus.STARTED, Game.GameTurn.PLAYER2);

                                        isGameHost = false;
                                    }

                                    invalidate();
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    Log.d("hihihi", "onCancelled " + firebaseError.getDetails());

                                }
                            });

                            waitlistQuery.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Log.d("hihihi", "onChildAdded " + dataSnapshot.getValue() + ", " + s);
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    Log.d("hihihi", "onChildChanged " + dataSnapshot.getValue() + ", " + s);

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    Log.d("hihihi", "onChildRemoved " + dataSnapshot.getValue());

                                    // need to compare if the room number matches with the one P1 has, if yes, start the game
                                    if (((Long) dataSnapshot.getValue()).intValue() == roomNumber && isGameHost)
                                        initInternetGame(Game.GameType.ONLINE, Game.GameStatus.STARTED, Game.GameTurn.PLAYER1);
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                    Log.d("hihihi", "onChildMoved " + dataSnapshot.getValue());

                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    Log.d("hihihi", "onCancelled " + firebaseError.getDetails());

                                }

                            });

                        } else if (btn.getButtonText().equals(howToPlay)) {
                            Log.d("hihihi", "hot to play btn is pressed");

                            // start how to play instructions

                            game = new Game(Game.GameType.TUTORIAL);

                            localGameBtn.setIsHidden(true);
                            friendGameBtn.setIsHidden(true);
                            onlineGameBtn.setIsHidden(true);
                            howToPlayBtn.setIsHidden(true);
                        }

                    } else {
                        if (btn.getButtonText().equals(rematch)) {
                            Log.d("hihihi", "rematch btn is pressed");

                            if (game.getTurnCount() > 0) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Confirm")
                                        .setMessage("Are you sure to restart this game?")
                                        .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (game.getGameType() == Game.GameType.LOCAL) {
                                                    game = new Game(Game.GameType.LOCAL);
                                                    gameBoardV = new GameBoardV();
                                                    resetAnimatingItemDimenStatus();
                                                } else if (game.getGameType() == Game.GameType.FRIENDS || game.getGameType() == Game.GameType.ONLINE) {
                                                    // TODO sends restart game request
                                                    if (game.getGameType() == Game.GameType.ONLINE) {
                                                        Hashtable hashtable = new Hashtable();

                                                        hashtable.put("restartRequest", "0");
                                                        myFirebaseRef.child("randomgames/" + roomNumber).updateChildren(hashtable);
                                                        isRematchRequester = true;
                                                    } else {
                                                        game = new Game(game.getGameType() == Game.GameType.FRIENDS ? Game.GameType.FRIENDS : Game.GameType.ONLINE);
                                                        gameBoardV = new GameBoardV();
                                                        initPadRaw();
                                                        sendGameStatuses();
                                                    }
                                                }

                                                invalidate();
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .setIcon(android.R.drawable.ic_dialog_alert).create().show();
                            }

                            // start local game
                        } else if (btn.getButtonText().equals(goBack)) {
                            Log.d("hihihi", "go back btn is pressed");

                            if (game.getGameType() == Game.GameType.FRIENDS)
                                myFirebaseRef.child("friendgames/" + roomNumber).removeValue();
                            else if (game.getGameType() == Game.GameType.ONLINE) {
                                // removes room in online game
                                myFirebaseRef.child("randomgames/" + roomNumber).removeValue();

                                // removes room in waiting list if there is one
                                myFirebaseRef.child("randomgames/waitinglist/" + roomNumber).removeValue();
                            }

                            // go back now
                            game = new Game(Game.GameType.DEMO);

                            isGameHost = false;

                            rematchBtn.setIsHidden(true);
                            goBackBtn.setIsHidden(true);
                            howToPlayBackBtn.setIsHidden(true);

                            resetAnimatingItemDimenStatus();
                        }
                    }

                    invalidate();
                    return true;
                }
            }

            touchedButton = null;

            if (game.getGameType() == Game.GameType.TUTORIAL){
                Log.d("hihihi", "go back to main page!");

                // go back now
                game = new Game(Game.GameType.DEMO);

                isGameHost = false;
                resetAnimatingItemDimenStatus();
                invalidate();
                return true;
            }

            // game board interactions
            if (game.getGameType() == Game.GameType.LOCAL || game.getGameType() == Game.GameType.FRIENDS || game.getGameType() == Game.GameType.ONLINE) {
                if (game.getGameWinner() == Game.GameWinner.NONE && game.getGameStatus() != Game.GameStatus.COMPLETED) {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            for (int k = 0; k < 3; k++) {
                                for (int l = 0; l < 3; l++) {
                                    GameCell thisCell = gameBoardV.getGamePadVs()[i][j].getGameCells()[k][l];
                                    if (thisCell.getRectF().contains(touchX, touchY)) {
                                        boolean shouldInvalidate = false;
                                        //Toast.makeText(context, "pad[" + i + "][" + j + "], cell[" + k + "][" + l + "] is clicked!", Toast.LENGTH_SHORT).show();

                                        if (game.getGameType() == Game.GameType.LOCAL) {
                                            if (game.getGameBoard().getPads()[i][j].getCells()[k][l].getCellStatus() == Cell.CellStatus.EMPTY && game.getGameBoard().getPads()[i][j].getPadStatus() == Pad.PadStatus.ACTIVE) {
                                                // play game

                                                // update cell status
                                                thisCell.setCellStatus(game.getGameTurn() == Game.GameTurn.PLAYER1 ? Cell.CellStatus.PLAYER1 : Cell.CellStatus.PLAYER2);
                                                game.getGameBoard().getPads()[i][j].getCells()[k][l].setCellStatus(game.getGameTurn() == Game.GameTurn.PLAYER1 ? Cell.CellStatus.PLAYER1 : Cell.CellStatus.PLAYER2);

                                                // update pad status according to position of 'thisCell'
                                                updatePadsActiveness(i, j, k, l);

                                                // check pad and board win
                                                checkWin(i, j, k, l, game.getGameBoard().getPads()[i][j].getCells()[k][l].getCellStatus());

                                                // switch turn
                                                switchTurn();

                                                shouldInvalidate = true;
                                            }

                                        } else if (game.getGameType() == Game.GameType.FRIENDS || game.getGameType() == Game.GameType.ONLINE) {
                                            if (game.getGameStatus() != Game.GameStatus.ABORTED && game.getGameTurn() == game.getGameHostTurn() && padRaw[i][j][k][l].getOwnBy() == 0 && padRaw[i][j][0][0].getPadActive() == 1) {
                                                padRaw[i][j][k][l].setOwnBy(game.getGameTurn() == Game.GameTurn.PLAYER1 ? 1 : -1);
                                                padRaw[i][j][k][l].setSymbol(game.getGameTurn() == Game.GameTurn.PLAYER1 ? "X" : "O");
                                                updatePadsActiveness(i, j, k, l);

                                                thisCell.setCellStatus(game.getGameTurn() == Game.GameTurn.PLAYER1 ? Cell.CellStatus.PLAYER1 : Cell.CellStatus.PLAYER2);
                                                game.getGameBoard().getPads()[i][j].getCells()[k][l].setCellStatus(game.getGameTurn() == Game.GameTurn.PLAYER1 ? Cell.CellStatus.PLAYER1 : Cell.CellStatus.PLAYER2);

                                                // check pad and board win
                                                checkWin(i, j, k, l, game.getGameBoard().getPads()[i][j].getCells()[k][l].getCellStatus());

                                                // switch turn
                                                switchTurn();

                                                sendGameStatuses();
                                                shouldInvalidate = true;
                                            }
                                        }

                                        if (shouldInvalidate)
                                            animateGameBoardPerTurn();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            boolean shouldInvalidate = false;
            for (GameButton btn : buttonList) {
                if (btn.isHidden())
                    continue;
                if (btn.getRect().contains(touchX, touchY)) {
                    if (!btn.isTouched() && touchedButton == btn) {
                        btn.setIsTouched(true);
                        btn.setIsAnimating(true);
                        shouldInvalidate = true;
                    }
                } else if (btn.isTouched()) {
                    btn.setIsTouched(false);
                    btn.setIsAnimating(false);
                    shouldInvalidate = true;
                } else {
                    btn.setIsTouched(false);
                    btn.setIsAnimating(false);
                }
            }

            if (shouldInvalidate) {
                invalidate();
            }

        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            boolean shouldInvalidate = false;

            for (GameButton btn : buttonList) {
                if (btn.isHidden())
                    continue;
                if (btn.getRect().contains(touchX, touchY)) {
                    btn.setIsTouched(true);
                    btn.setIsAnimating(true);
                    touchedButton = btn;
                    shouldInvalidate = true;
                }
            }
            if (shouldInvalidate) {
                animateButtonColor(true, null);
            }
        }

        return true;
    }

    private void animateButtonColor(final boolean isHighlighted, final GameButton btn) {
        final int colorFrom = getResources().getColor(R.color.T9OrangeDark);
        int colorTo = getResources().getColor(R.color.T9BlueLight);
        int colorTo2 = getResources().getColor(R.color.T9MiddleStatusBar);

        ValueAnimator colorAnimation = isHighlighted ? ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo) : ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorTo2);
        colorAnimation.setDuration(isHighlighted ? 250 : 250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (isHighlighted)
                    highlightPaint.setColor((int) animator.getAnimatedValue());
                else {
                    int color = (int) animator.getAnimatedValue();
                    highlightPaint.setColor(color);
                }

                invalidate();
            }
        });
        colorAnimation.start();
    }

    private void animateGameBoardPerTurn() {
        float fromPadRatio = ACTIVE_PAD_WIDTH_RATIO; // from 1.0f
        float toPadRatio = INACTIVE_PAD_WIDTH_RATIO; // to 0.8f

        ValueAnimator gameBoardAnimation = ValueAnimator.ofFloat(fromPadRatio, toPadRatio);
        gameBoardAnimation.setDuration(300);
        gameBoardAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                inactivePadWidthRatio = (float) animation.getAnimatedValue();
                activePadWidthRatio = INACTIVE_PAD_WIDTH_RATIO + (ACTIVE_PAD_WIDTH_RATIO - (float) animation.getAnimatedValue());
                invalidate();
            }
        });

        gameBoardAnimation.setInterpolator(new DecelerateInterpolator());
        gameBoardAnimation.start();
    }

    private void animateTurn() {
        float activeTurnWidthRatio = ACTIVE_TURN_WIDTH_RATIO; // from 0.3f
        float inActiveTurnWidthRatio = INACTIVE_TURN_WIDTH_RATIO; // to 0.1f
        float drawTurnWidthRatio = DRAW_TURN_WIDTH_RATIO; // to 0.2f

        ValueAnimator turnAnimation;
        if (game.getGameStatus() == Game.GameStatus.COMPLETED && game.getGameWinner() == Game.GameWinner.NONE) {
            // animate switch turn for draw case
            turnAnimation = game.getGameTurn() == Game.GameTurn.PLAYER1 ? ValueAnimator.ofFloat(inActiveTurnWidthRatio, drawTurnWidthRatio) : ValueAnimator.ofFloat(activeTurnWidthRatio, drawTurnWidthRatio);
        } else {
            // animate switch turn for normal case
            turnAnimation = game.getGameTurn() == Game.GameTurn.PLAYER1 ? ValueAnimator.ofFloat(inActiveTurnWidthRatio, activeTurnWidthRatio) : ValueAnimator.ofFloat(activeTurnWidthRatio, inActiveTurnWidthRatio);
        }

        turnAnimation.setDuration(300);
        turnAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatingTurnWidthRatio = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        turnAnimation.setInterpolator(new DecelerateInterpolator());
        turnAnimation.start();
    }

    private void resetAnimatingItemDimenStatus() {
        animatingTurnWidthRatio = ACTIVE_TURN_WIDTH_RATIO;
    }

    private void sendGameStatuses() {
        Hashtable hashtable = new Hashtable();
        if (game.getGameStatus() == Game.GameStatus.STARTED)
            hashtable.put("gameStatus", "started");
        else if (game.getGameStatus() == Game.GameStatus.PENDING)
            hashtable.put("gameStatus", "pending");
        else if (game.getGameStatus() == Game.GameStatus.COMPLETED)
            hashtable.put("gameStatus", "completed");

//        hashtable.put("gameStatus", game.getGameStatus() == Game.GameStatus.STARTED ? "started" : "pending");
        hashtable.put("gameboard", padRaw);
        hashtable.put("turn", game.getGameTurn() == Game.GameTurn.PLAYER1 ? 0 : 1);
        if (game.getGameType() == Game.GameType.FRIENDS)
            myFirebaseRef.child("friendgames/" + roomNumber).setValue(hashtable);
        else if (game.getGameType() == Game.GameType.ONLINE)
            myFirebaseRef.child("randomgames/" + roomNumber).setValue(hashtable);
    }

    private void initInternetGame(final Game.GameType gameType, final Game.GameStatus gameStatus, final Game.GameTurn hostGameTurn) {
        final String gameModeUrl = (gameType == Game.GameType.FRIENDS) ? "friendgames/" : (gameType == Game.GameType.ONLINE) ? "randomgames/" : "/";

        Log.d("hihihi:: ", "hihihi:: gameModeUrl: " + gameModeUrl);

        // start friend game
        Hashtable hashtable = new Hashtable();
        hashtable.put("gameStatus", gameStatus == Game.GameStatus.PENDING ? "pending" : "started");

        myFirebaseRef.child(gameModeUrl + roomNumber).setValue(hashtable, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Log.d("hihihi:: ", "hihihi:: firebaseError: " + firebaseError);
                if (firebaseError != null) {
                    Toast.makeText(context, "Error occurred when creating game.", Toast.LENGTH_LONG).show();
                } else {

                    game = new Game(gameType);
                    game.setGameHostTurn(hostGameTurn);
                    initPadRaw();

                    localGameBtn.setIsHidden(true);
                    friendGameBtn.setIsHidden(true);
                    onlineGameBtn.setIsHidden(true);
                    howToPlayBtn.setIsHidden(true);

                    myFirebaseRef.child(gameModeUrl + roomNumber).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("dataSnapShot", "dataSnapShot is recved! =" + dataSnapshot.getValue());

                            boolean shouldAnimate = false;

                            if (dataSnapshot.getValue() == null) {
                                // opponent has left
                                game.setGameStatus(Game.GameStatus.ABORTED);
                                invalidate();
                                return;
                            }

                            HashMap hashMap = (HashMap) dataSnapshot.getValue(true);

                            if (hashMap.containsKey("gameboard")) {
                                for (int i = 0; i < 3; i++) {
                                    for (int j = 0; j < 3; j++) {
                                        for (int k = 0; k < 3; k++) {
                                            for (int l = 0; l < 3; l++) {
                                                HashMap padRow = (HashMap) ((HashMap) hashMap.get("gameboard")).get(String.valueOf(i));
                                                HashMap thatPad = (HashMap) padRow.get(String.valueOf(j));
                                                HashMap cellRow = (HashMap) thatPad.get(String.valueOf(k));
                                                HashMap thatCell = (HashMap) cellRow.get(String.valueOf(l));

                                                if (((Long) thatCell.get("ownBy")).intValue() != padRaw[i][j][k][l].getOwnBy()) {
                                                    game.getGameBoard().getPads()[i][j].getCells()[k][l].setCellStatus(game.getGameTurn() == Game.GameTurn.PLAYER1 ? Cell.CellStatus.PLAYER1 : Cell.CellStatus.PLAYER2);
                                                    // check pad and board win
                                                    checkWin(i, j, k, l, game.getGameBoard().getPads()[i][j].getCells()[k][l].getCellStatus());
                                                }

                                                padRaw[i][j][k][l].setLocalWinner(((Long) thatCell.get("localWinner")).intValue()); // TODO null pointer here
                                                padRaw[i][j][k][l].setOwnBy(((Long) thatCell.get("ownBy")).intValue());
                                                padRaw[i][j][k][l].setSymbol((String) thatCell.get("symbol"));                      // TODO null pointer here
                                                padRaw[i][j][k][l].setPadActive(((Long) thatCell.get("padActive")).intValue());

                                                gameBoardV.getGamePadVs()[i][j].setPadStatus(((Long) thatCell.get("padActive")).intValue() == 1 ? Pad.PadStatus.ACTIVE : Pad.PadStatus.INACTIVE);
                                                gameBoardV.getGamePadVs()[i][j].getGameCells()[k][l].setCellStatus(((Long) thatCell.get("ownBy")).intValue() == 0 ? Cell.CellStatus.EMPTY : ((Long) thatCell.get("ownBy")).intValue() == 1 ? Cell.CellStatus.PLAYER1 : Cell.CellStatus.PLAYER2);


                                            }
                                        }

                                        HashMap padRow = (HashMap) ((HashMap) hashMap.get("gameboard")).get(String.valueOf(i));
                                        HashMap thatPad = (HashMap) padRow.get(String.valueOf(j));
                                        HashMap cellRow = (HashMap) thatPad.get(String.valueOf(0));
                                        HashMap thatCell = (HashMap) cellRow.get(String.valueOf(0));

                                        //if (((Long) thatCell.get("padActive")).intValue() != padRaw[i][j][0][0].getPadActive()){
                                        // set active
                                        game.getGameBoard().getPads()[i][j].setPadStatus(((Long) thatCell.get("padActive")).intValue() == 1 ? Pad.PadStatus.ACTIVE : Pad.PadStatus.INACTIVE);
                                        gameBoardV.getGamePadVs()[i][j].setPadStatus(((Long) thatCell.get("padActive")).intValue() == 1 ? Pad.PadStatus.ACTIVE : Pad.PadStatus.INACTIVE);
                                        //}
                                    }
                                }
                            }

                            if (hashMap.containsKey("gameStatus")) {
                                String gameStatus = (String) (hashMap.get("gameStatus"));
                                switch (gameStatus) {
                                    case "pending":
                                        game.setGameStatus(Game.GameStatus.PENDING);
                                        break;

                                    case "started":
                                        game.setGameStatus(Game.GameStatus.STARTED);
                                        break;

                                    case "completed":
                                        game.setGameStatus(Game.GameStatus.COMPLETED);
                                        break;

                                    default:
                                        game.setGameStatus(Game.GameStatus.PENDING);
                                        break;
                                }
                            }

                            if (hashMap.containsKey("turn")) {
                                game.setGameTurn((((Long) hashMap.get("turn")).intValue() == 1 ? Game.GameTurn.PLAYER2 : Game.GameTurn.PLAYER1));
                                Log.d("hihihi:", "player " + (game.getGameTurn() == Game.GameTurn.PLAYER1 ? "1" : "2") + " turn");
                                shouldAnimate = true;
//                                Toast.makeText(context, "turn key recved! game darw? " + (game.getGameStatus() == Game.GameStatus.COMPLETED ? " YES" : " NO"), Toast.LENGTH_SHORT).show();
                            }

                            if (hashMap.containsKey("restartRequest")) {
                                if (hashMap.get("restartRequest").toString().equals("0") && !isRematchRequester) {
                                    new AlertDialog.Builder(context)
                                            .setTitle("Confirm")
                                            .setMessage("Your opponent wants a rematch, would you like to do so?")
                                            .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    if (game.getGameType() == Game.GameType.FRIENDS || game.getGameType() == Game.GameType.ONLINE) {
                                                        // TODO sends restart game request
                                                        if (game.getGameType() == Game.GameType.ONLINE) {
                                                            Hashtable hashtable = new Hashtable();

                                                            hashtable.put("restartRequest", "1");
                                                            myFirebaseRef.child("randomgames/" + roomNumber).updateChildren(hashtable);
                                                        }
                                                    }

                                                    invalidate();
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (game.getGameType() == Game.GameType.ONLINE) {
                                                        myFirebaseRef.child("randomgames/" + roomNumber + "/restartRequest").removeValue();
                                                    }
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert).create().show();
                                } else if ((hashMap.get("restartRequest").toString().equals("1"))) {
                                    game = new Game(game.getGameType() == Game.GameType.FRIENDS ? Game.GameType.FRIENDS : Game.GameType.ONLINE);
                                    game.setGameHostTurn(isGameHost ? Game.GameTurn.PLAYER1 : Game.GameTurn.PLAYER2);
                                    gameBoardV = new GameBoardV();
                                    isRematchRequester = false;
                                    initPadRaw();
                                    sendGameStatuses();
                                    Hashtable hashtable = new Hashtable();
                                    hashtable.put("restartRequest", "2");
                                    if (game.getGameType() == Game.GameType.ONLINE)
                                        myFirebaseRef.child("randomgames/" + roomNumber).updateChildren(hashtable);
                                } else if ((hashMap.get("restartRequest").toString().equals("2") && isRematchRequester)) {
                                    game = new Game(game.getGameType() == Game.GameType.FRIENDS ? Game.GameType.FRIENDS : Game.GameType.ONLINE);
                                    game.setGameHostTurn(isGameHost ? Game.GameTurn.PLAYER1 : Game.GameTurn.PLAYER2);
                                    gameBoardV = new GameBoardV();
                                    isRematchRequester = false;
                                    initPadRaw();
                                }

                            }

                            invalidate();

                            if (shouldAnimate)
                                animateTurn();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("", "https://t9.firebaseapp.com/#/friend/" + roomNumber);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, "Game link is copied to clipboard! Room number is " + roomNumber, Toast.LENGTH_LONG).show();

                    invalidate();
                }
            }


        });

    }

    private void updatePadsActiveness(int i, int j, int k, int l) {

        if (game.getGameType() == Game.GameType.LOCAL) {
            // set active
            if (game.getGameBoard().getPads()[k][l].getPadStatus() != Pad.PadStatus.ACTIVE)
                game.getGameBoard().getPads()[k][l].setPadStatus(Pad.PadStatus.ACTIVE);

            if (gameBoardV.getGamePadVs()[k][l].getPadStatus() != Pad.PadStatus.ACTIVE) {
                Log.d("huhuhu", "huhuhu:: gameBoardV.getGamePadVs()[k][l].getPadStatus() is " + gameBoardV.getGamePadVs()[k][l].getPadStatus());
                gameBoardV.getGamePadVs()[k][l].setPadStatus(Pad.PadStatus.ACTIVE);
                gameBoardV.getGamePadVs()[k][l].setIsAnimating(true);
                Log.d("huhuhu", "huhuhu:: gameBoardV.getGamePadVs()[k][l].setIsAnimating is true, active case");

            } else if ((i == k && j == l)) {
                gameBoardV.getGamePadVs()[k][l].setIsAnimating(false);
            }

            if (isPadFull(k, l)) {
                for (int a = 0; a < 3; a++) {
                    for (int b = 0; b < 3; b++) {
                        game.getGameBoard().getPads()[a][b].setPadStatus(Pad.PadStatus.ACTIVE);
                        gameBoardV.getGamePadVs()[a][b].setPadStatus(Pad.PadStatus.ACTIVE);
                        gameBoardV.getGamePadVs()[a][b].setIsAnimating(true);
                    }
                }

            } else {
                // set the rest inactive
                for (int a = 0; a < 3; a++) {
                    for (int b = 0; b < 3; b++) {
                        if (a == k && b == l)
                            continue;
                        if (game.getGameBoard().getPads()[a][b].getPadStatus() != Pad.PadStatus.INACTIVE) {
                            game.getGameBoard().getPads()[a][b].setPadStatus(Pad.PadStatus.INACTIVE);
                        }
                        if (gameBoardV.getGamePadVs()[a][b].getPadStatus() != Pad.PadStatus.INACTIVE) {
                            gameBoardV.getGamePadVs()[a][b].setPadStatus(Pad.PadStatus.INACTIVE);
                            gameBoardV.getGamePadVs()[a][b].setIsAnimating(true);
                            Log.d("huhuhu", "huhuhu:: gameBoardV.getGamePadVs()[k][l].setIsAnimating is true, inactive case");
                        } else {
                            gameBoardV.getGamePadVs()[a][b].setIsAnimating(false);
                        }
                    }
                }
            }


        } else {
            if (isPadFull(k, l)) {
                for (int a = 0; a < 3; a++) {
                    for (int b = 0; b < 3; b++) {
                        for (int c = 0; c < 3; c++) {
                            for (int d = 0; d < 3; d++) {
                                padRaw[a][b][c][d].setPadActive(1);
                            }
                        }
                    }
                }
            } else {
                for (int a = 0; a < 3; a++) {
                    for (int b = 0; b < 3; b++) {
                        for (int c = 0; c < 3; c++) {
                            for (int d = 0; d < 3; d++) {
                                if (a == k && b == l)
                                    padRaw[a][b][c][d].setPadActive(1);
                                else
                                    padRaw[a][b][c][d].setPadActive(0);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isPadFull(int i, int j) {
        int counter = 0;

        if (game.getGameType() == Game.GameType.LOCAL) {
            for (int a = 0; a < 3; a++) {
                for (int b = 0; b < 3; b++) {
                    if (game.getGameBoard().getPads()[i][j].getCells()[a][b].getCellStatus() == Cell.CellStatus.EMPTY)
                        break;
                    else {
                        counter++;
                    }
                }
            }
        } else {
            for (int a = 0; a < 3; a++) {
                for (int b = 0; b < 3; b++) {
                    if (padRaw[i][j][a][b].getOwnBy() == 0)
                        break;
                    else {
                        counter++;
                    }
                }
            }
        }

        return counter == 9;
    }

    private void switchTurn() {
        if (game.getGameStatus() == Game.GameStatus.COMPLETED || game.getGameStatus() == Game.GameStatus.ABORTED)
            return;

        // updates data model
        game.switchGameTurn();

        animateTurn();

        // sends results to cloud TODO
    }

    private void checkWin(int i, int j, int k, int l, Cell.CellStatus cellStatus) {

        // only check pad win if no winner is declared
        if (game.getGameBoard().getPads()[i][j].getPadWinner() == Pad.PadWinner.NONE) {
            Pad.PadWinner padWinner = checkPadWin(i, j, k, l, cellStatus);
            if (padWinner != Pad.PadWinner.NONE) {
                game.getGameBoard().getPads()[i][j].setPadWinner(padWinner);
                if (game.getGameType() == Game.GameType.FRIENDS || game.getGameType() == Game.GameType.ONLINE) {
                    padRaw[i][j][k][l].setLocalWinner(game.getGameTurn() == Game.GameTurn.PLAYER1 ? 1 : -1);
                    padRaw[i][j][0][0].setLocalWinner(game.getGameTurn() == Game.GameTurn.PLAYER1 ? 1 : -1);
                }
            }
        }

        // check game winner
        Game.GameWinner gameWinner = checkGameWin(i, j, game.getGameBoard().getPads()[i][j].getPadWinner());
        if (gameWinner != Game.GameWinner.NONE) {
            // yay! win!
            game.setGameWinner(gameWinner);
        } else if (game.getTurnCount() == Game.MAX_TURN_COUNT - 1) {
            // oops, draw!
            game.declareDraw();
        }
    }

    private Game.GameWinner checkGameWin(int i, int j, Pad.PadWinner padWinner) {
        if (game.getGameWinner() != Game.GameWinner.NONE)
            return game.getGameWinner();

        // check row
        for (int a = 0; a < 3; a++) {
            if (game.getGameBoard().getPads()[i][a].getPadWinner() != padWinner)
                break;
            if (a == 2) {
                return padWinner == (Pad.PadWinner.NONE) ? Game.GameWinner.NONE : (padWinner == Pad.PadWinner.PLAYER1) ? Game.GameWinner.PLAYER1 : Game.GameWinner.PLAYER2;
            }
        }

        // check col
        for (int a = 0; a < 3; a++) {
            if (game.getGameBoard().getPads()[a][j].getPadWinner() != padWinner)
                break;
            if (a == 2) {
                return padWinner == (Pad.PadWinner.NONE) ? Game.GameWinner.NONE : (padWinner == Pad.PadWinner.PLAYER1) ? Game.GameWinner.PLAYER1 : Game.GameWinner.PLAYER2;
            }
        }

        // check diagonal
        for (int a = 0, b = 0; a < 3 && b < 3; a++, b++) {
            if (game.getGameBoard().getPads()[a][b].getPadWinner() != padWinner)
                break;
            if (a == 2 && b == 2) {
                return padWinner == (Pad.PadWinner.NONE) ? Game.GameWinner.NONE : (padWinner == Pad.PadWinner.PLAYER1) ? Game.GameWinner.PLAYER1 : Game.GameWinner.PLAYER2;
            }
        }

        // check anti diagonal
        for (int a = 0, b = 2; a < 3 && b >= 0; a++, b--) {
            if (game.getGameBoard().getPads()[a][b].getPadWinner() != padWinner)
                break;
            if (a == 2 && b == 0) {
                return padWinner == (Pad.PadWinner.NONE) ? Game.GameWinner.NONE : (padWinner == Pad.PadWinner.PLAYER1) ? Game.GameWinner.PLAYER1 : Game.GameWinner.PLAYER2;
            }
        }

        return Game.GameWinner.NONE;
    }

    private Pad.PadWinner checkPadWin(int i, int j, int k, int l, Cell.CellStatus cellStatus) {
        if (game.getGameBoard().getPads()[i][j].getPadWinner() != Pad.PadWinner.NONE)
            return game.getGameBoard().getPads()[i][j].getPadWinner();

        // check row
        for (int a = 0; a < 3; a++) {
            if (game.getGameBoard().getPads()[i][j].getCells()[k][a].getCellStatus() != cellStatus)
                break;
            if (a == 2) {
                Toast.makeText(context, "player " + ((cellStatus == Cell.CellStatus.PLAYER1) ? "1" : "2") + " wins this pad!", Toast.LENGTH_SHORT).show();
                return cellStatus == Cell.CellStatus.PLAYER1 ? Pad.PadWinner.PLAYER1 : Pad.PadWinner.PLAYER2;
            }
        }

        // check col
        for (int a = 0; a < 3; a++) {
            if (game.getGameBoard().getPads()[i][j].getCells()[a][l].getCellStatus() != cellStatus)
                break;
            if (a == 2) {
                Toast.makeText(context, "player " + ((cellStatus == Cell.CellStatus.PLAYER1) ? "1" : "2") + " wins this pad!", Toast.LENGTH_SHORT).show();
                return cellStatus == Cell.CellStatus.PLAYER1 ? Pad.PadWinner.PLAYER1 : Pad.PadWinner.PLAYER2;
            }
        }

        // check diagonal
        for (int a = 0, b = 0; a < 3 && b < 3; a++, b++) {
            if (game.getGameBoard().getPads()[i][j].getCells()[a][b].getCellStatus() != cellStatus)
                break;
            if (a == 2 && b == 2) {
                Toast.makeText(context, "player " + ((cellStatus == Cell.CellStatus.PLAYER1) ? "1" : "2") + " wins this pad!", Toast.LENGTH_SHORT).show();
                return cellStatus == Cell.CellStatus.PLAYER1 ? Pad.PadWinner.PLAYER1 : Pad.PadWinner.PLAYER2;
            }
        }

        // check anti diagonal
        for (int a = 0, b = 2; a < 3 && b >= 0; a++, b--) {
            if (game.getGameBoard().getPads()[i][j].getCells()[a][b].getCellStatus() != cellStatus)
                break;
            if (a == 2 && b == 0) {
                Toast.makeText(context, "player " + ((cellStatus == Cell.CellStatus.PLAYER1) ? "1" : "2") + " wins this pad!", Toast.LENGTH_SHORT).show();
                return cellStatus == Cell.CellStatus.PLAYER1 ? Pad.PadWinner.PLAYER1 : Pad.PadWinner.PLAYER2;
            }
        }

        return Pad.PadWinner.NONE;
    }

}
