package com.kevinguo.t9.models;

/**
 * Created by kevinguo on 16-01-29.
 */
public class Game {
    public enum GameStatus {PENDING, STARTED, COMPLETED, ABORTED};
    public enum GameTurn {PLAYER1, PLAYER2};
    public enum GameWinner {NONE, PLAYER1, PLAYER2};
    public enum GameType {DEMO, LOCAL, FRIENDS, ONLINE, TUTORIAL};

    public static final int MAX_TURN_COUNT = 81;

    private GameType gameType;
    private GameTurn gameTurn;
    private GameTurn gameHostTurn;
    private GameStatus gameStatus;
    private GameWinner gameWinner;
    private boolean isWinnerDeclared;
    private int turnCount;

    private Board gameBoard;

    public Game(GameType gameType){
        if (gameType == null) {
            gameType = GameType.DEMO;
        }
        init(gameType);
    }

    public Game(GameType gameType, GameStatus gameStatus, GameTurn gameTurn, GameWinner gameWinner, boolean isWinnerDeclared, Board gameBoard){
        this.gameType = gameType;
        this.gameStatus = gameStatus;
        this.gameTurn = gameTurn;
        this.gameWinner = gameWinner;
        this.isWinnerDeclared = isWinnerDeclared;
        this.gameBoard = gameBoard;

    }

    private void init(GameType gameType){
        initGameStatuses(gameType);
        initGameBoard();
    }

    public GameType getGameType() {
        return gameType;
    }

    private void initGameStatuses(GameType gameType){
        this.gameType = gameType;
        this.gameStatus = gameType == GameType.LOCAL ? GameStatus.STARTED : GameStatus.PENDING;
        this.gameTurn = GameTurn.PLAYER1;
        this.gameWinner = GameWinner.NONE;
        this.isWinnerDeclared = false;
        this.gameHostTurn = GameTurn.PLAYER1;
        this.turnCount = 0;
    }

    private void initGameBoard(){
        this.gameBoard = new Board();
    }

    public Board getGameBoard(){
        return gameBoard;
    }

    public GameTurn getGameTurn() {
        return gameTurn;
    }

    public void setGameTurn(GameTurn gameTurn) {
        this.gameTurn = gameTurn;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public GameWinner getGameWinner() {
        return gameWinner;
    }

    public void setGameBoard(Board gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void setIsWinnerDeclared(boolean isWinnerDeclared) {
        this.isWinnerDeclared = isWinnerDeclared;
    }

    public void setGameWinner(GameWinner gameWinner) {
        this.gameWinner = gameWinner;
        setIsWinnerDeclared(true);
        setGameStatus(GameStatus.COMPLETED);
    }

    public void declareDraw() {
        setGameStatus(GameStatus.COMPLETED);
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public void switchGameTurn(){
        if (this.gameTurn == GameTurn.PLAYER1)
            this.gameTurn = GameTurn.PLAYER2;
        else
            this.gameTurn = GameTurn.PLAYER1;

        turnCount ++;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public GameTurn getGameHostTurn() {
        return gameHostTurn;
    }

    public void setGameHostTurn(GameTurn gameHostTurn) {
        this.gameHostTurn = gameHostTurn;
    }
}
