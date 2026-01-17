package UI.Swing;

import connect4.model.Connect4Board;

public class calculateNextBoard extends Thread {
    private Connect4Board board;
    private final BoardController controller;

    public calculateNextBoard(Connect4Board board, BoardController controller) {
        this.board = board;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            board = board.machineMove();
            if (!this.isInterrupted()) {
                controller.setBoard(board);
                controller.unlock();
            }
        } catch (InterruptedException e) {
            System.out.println("Calculation not finished");
        }
    }

    private synchronized Connect4Board getBoard() {
        return board;
    }
}
