package connect4.controller;

import connect4.view.SwingView.Application;
import connect4.model.Board;
import connect4.model.Connect4Board;
import connect4.model.State;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class BoardController{
    Set<Observer> listeners = new HashSet<>();
    private final JFrame frame;
    private Connect4Board board;
    private final Stack<Connect4Board> boardStack = new Stack<>();
    private static Boolean locked = false;
    calculateNextBoard nextBoard;
    private static boolean undo = false;

    public BoardController(JFrame frame, Application listener) {
        listeners.add(listener);
        this.frame = frame;
        board = new Connect4Board(false);
        nextBoard = new calculateNextBoard(board, this);
    }

    public void quitAction() {
        nextBoard.interrupt();
        frame.dispose();
    }

    public void startAction() {
        boardStack.clear();
        nextBoard.interrupt();
        unlock();
        board = new Connect4Board(false);
        boardStack.push(board);
        if (board.getFirstPlayer() == State.COMPUTER) {
            machineMove();
        }
        notifyObservers();
    }

    public void switchAction() {
        boardStack.clear();
        nextBoard.interrupt();
        locked = false;
        board = new Connect4Board(true);
        if (board.getFirstPlayer() == State.COMPUTER) {
            machineMove();
        }
        //Lock only if Computer makes first move.
        locked = board.getFirstPlayer() != State.HUMAN;
        notifyObservers();
    }

    public void undoAction() {
        if (boardStack.isEmpty()) {
            notifyObserversOfError("No undo possible. This is the initial state");
        } else if (board.isGameOver()) {
            notifyObserversOfError("The game is already over.");
        } else {
            undo = true;
            //If the thread has already finished the calculation
            //2 steps need to be undone in order to maintain the order of the players.
            if (nextBoard.isAlive()) {
                nextBoard.interrupt();
            } else {
                boardStack.pop();
            }
            if (!boardStack.isEmpty()) {
                board = boardStack.pop();
            }
            locked = false;
            notifyObservers();
        }
    }

    public void undo(Connect4Board board) {
        if (undo) {
            undo = false;
            boardStack.clear();
            boardStack.push(board);
        } else {
            boardStack.add(board);
        }
    }

    public void moveAction(int xPos) {
        //No other move(s) should be made during the calculation.
        if (!locked) {
            locked = true;
            if (!board.isGameOver()) {
                Connect4Board nextBoard =  board.move(calculateCol(xPos));
                if (nextBoard != null) {
                    undo(board);
                    board = nextBoard;
                    notifyObservers();
                    machineMove();
                } else {
                    notifyObserversOfError("Invalid move, column is full.");
                    locked = false;
                }
            }
        }
    }

    private void machineMove() {
        locked = true;
        if (!board.isGameOver()) {
            nextBoard = new calculateNextBoard(board, this);
            nextBoard.start();
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

    public void notifyObservers() {
        for (Observer listener : listeners) {
            listener.update(board);
        }
    }

    public void notifyObserversOfError(String message) {
        for  (Observer listener : listeners) {
            listener.showError(message);
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
