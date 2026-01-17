package UI.Swing;

import connect4.model.Board;
import connect4.model.Connect4Board;
import connect4.model.State;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public class BoardController{
    Set<Observer> listeners = new HashSet<>();
    private final JFrame frame;
    private Connect4Board board;
    private static Boolean locked = false;
    calculateNextBoard nextBoard;

    public BoardController(JFrame frame, View listener) {
        this.frame = frame;
        board = new Connect4Board(false);
        listeners.add(listener);
        nextBoard = new calculateNextBoard(board, this);
    }

    public void quitAction() {
        nextBoard.interrupt();
        frame.dispose();
    }

    public void startAction() {
        nextBoard.interrupt();
        board = new Connect4Board(false);
        if (board.getFirstPlayer() == State.COMPUTER) {
            machineMove();
        }
        notifyObservers();
    }

    public void switchAction() {
        nextBoard.interrupt();
        board = new Connect4Board(true);
        if (board.getFirstPlayer() == State.COMPUTER) {
            machineMove();
        }
        //Lock only if Computer makes first move.
        locked = board.getFirstPlayer() != State.HUMAN;
        notifyObservers();
    }

    public void moveAction(int xPos) {
        //No other move(s) should be made during the calculation.
        if (!locked) {
            locked = true;
            if (!board.isGameOver()) {
                board = board.move(calculateCol(xPos));
            }
            notifyObservers();
            machineMove();
        }
    }

    private void machineMove() {
        locked = true;
        if (!board.isGameOver()) {
            nextBoard = new calculateNextBoard(board, this);
            nextBoard.start();
            notifyObservers();
        }
    }

    public void setDifficultyAction(Integer difficulty) {
        if (difficulty == null) {
            System.out.println("Difficulty is null");
            throw new IllegalArgumentException("Difficulty cannot be null");
        } else if (difficulty > Connect4Board.MAX_LEVEL) {
            throw new IllegalArgumentException("Difficulty is too high");
        }
        board.setLevel(difficulty);
    }

    private void notifyObservers() {
        for (Observer listener : listeners) {
            listener.update(board);
        }
    }

    private int calculateCol(int mousePosX) {
        int result = 1;
        int width = frame.getContentPane().getWidth();
        int widthCol = width / Board.COLS;

        while (mousePosX > widthCol * result) {
            ++result;
        }
        return result;
    }

    public void unlock() {
        locked = false;
    }

    public Connect4Board getBoard() {
        return board;
    }

    public void setBoard(Connect4Board board) {
        this.board = board;
    }


}
