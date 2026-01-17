package connect4.model;

import java.util.ArrayList;

/**
 * The board the game is played on.
 * The board could theoretically be any size, but the formula the
 * computer evaluates the moves with has to be altered when {@link #CONNECT}
 * or {@link #COLS} is changed.
 */
public class Connect4Board implements Board {
    /**
     * The number of rows of the game grid. Originally 6.
     */
    public static final int ROWS = 6;
    /**
     * The number of columns of the game grid. Originally 7.
     */
    public static final int COLS = 7;
    /**
     * The number of how many tiles must be lined up to win. Originally 4.
     */
    public static final int CONNECT = 4;
    /**
     * The max difficulty level of the computer. Originally 5.
     */
    public static final int MAX_LEVEL = 10;

    /**
     * The default difficulty level of the computer. Originally 4.
     */
    private static final int DEFAULT_LEVEL = 4;
    /**
     * The Player that can place a token first.
     * On startup the {@link State#HUMAN} is the first player.
     * If not switched it remains the same for future games.
     */
    private static State firstPlayer = State.HUMAN;
    /**
     * The player that can make the next move.
     */
    private static State currentPlayer;
    /**
     * Stores if the game is over.
     */
    private static boolean gameOver = false;
    /**
     * Stores a group of witnesses that testify the win for one of the players.
     */
    private static ArrayList<Coordinates2D> witness = null;
    /**
     * The difficulty level of the computer.
     */
    private static int level = DEFAULT_LEVEL;
    /**
     * The board where the game is played on.
     */
    private State[][] board = new State[ROWS][COLS];

    /**
     * Instantiate a new empty Connect4Board,
     * where all slots are occupied by {@link State#NONE}.
     * Switches the {@link #firstPlayer} if specified.
     * @param switchFirstPlayer Specifies if the new board should
     *  keep the same player as before as {@link #firstPlayer} ({@code false})
     *  or if it should switch to the other player ({@code true}).
     */
    public Connect4Board(final boolean switchFirstPlayer) {
        gameOver = false;
        //Fill the board.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = State.NONE;
            }
        }
        if (switchFirstPlayer) {
            switch (firstPlayer) {
                case HUMAN -> firstPlayer = State.COMPUTER;
                case COMPUTER -> firstPlayer = State.HUMAN;
                default -> throw new IllegalStateException(
                        "Unexpected value: " + firstPlayer
                );
            }
        }
        currentPlayer = firstPlayer;
    }

    /**
     * Instantiate a new Connect4Board
     * by making a deep copy of another Connect4Board.
     * @param board The board that is copied.
     */
    public Connect4Board(final State[][] board) {
        this.board = new State[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, COLS);
        }
    }

    /**
     * Instantiate a new Connect4Board,
     * that places a token with the specified State in the specifies column,
     * modifying the board passed originally.
     * This ignored the {@link #currentPlayer}.
     * Only places a token if the column is not full,
     * otherwise returns a new Connect4Board without the new token in the
     * column.
     * @param board The board to be copied.
     * @param col The column in which the token should be placed.
     * @param state The state the token should have.
     */
    public Connect4Board(
            final Connect4Board board,
            final int col,
            final State state) {
        this.board = board.board;
        if (!columnFull(col)) {
            this.board =  placeToken(col, state).board;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public State getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connect4Board move(final int col) {
        if (gameOver) {
            throw new IllegalStateException("Game over");
        } else if (columnFull(col - 1)) {
            return null;
        } else {
            //Array has base 0, input has base 1.
            int updatedCol = col - 1;
            //Check if column exists.
            if (updatedCol > COLS || updatedCol < 0) {
                throw new IllegalArgumentException("Column does not exist");
                //Check if it is the humans turn.
            } else if (currentPlayer != State.HUMAN) {
                throw new IllegalMoveException(
                        "Wait for the other player to make their move first "
                                + "or start a new game."
                );
            } else {
                Connect4Board updatedBoard = placeToken(
                        updatedCol, State.HUMAN
                );
                checkForGameOver(updatedBoard);
                if (!gameOver) {
                    currentPlayer = State.COMPUTER;
                }
                return updatedBoard;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connect4Board machineMove() throws InterruptedException {
        if (gameOver) {
            throw new IllegalStateException("Game over");
        } else if (currentPlayer != State.COMPUTER) {
                throw new IllegalMoveException(
                        "Wait for the other player to make their move first "
                                + "or start a new game."
                );
        } else {
            //Calculate best move and execute it.
            GameTreeBuilder gameTree = new GameTreeBuilder(this, level);
            int col = gameTree.getBestNode().getColumn();
            Connect4Board updatedBoard = placeToken(col, State.COMPUTER);
            checkForGameOver(updatedBoard);
            if (!gameOver) {
                currentPlayer = State.HUMAN;
            }
            return updatedBoard;
        }
    }

    /**
     * Set a new value for the current skill level of the computer.
     * This takes effect immediately, not after the round has ended.
     * @param level The skill level, must be between 1 and {@link #MAX_LEVEL}
     */
    @Override
    public void setLevel(final int level) {
        if (level <= 0 || level > MAX_LEVEL) {
            throw new IllegalArgumentException(
                    "Level has to be a positive int"
                    + "and cannot be greater than " + MAX_LEVEL
            );
        }
        Connect4Board.level = level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public State getWinner() {
        if (tie()) {
            return null;
        } else if (gameOver) {
            return currentPlayer;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Coordinates2D> getWitness() {
        if (getWinner() == null && !gameOver) {
            throw new IllegalStateException(
                    "There is no winning group, because no one won (yet)"
            );
        } else if (getWinner() == null && gameOver) {
            throw new IllegalStateException(
                    "There is no winning group, because you are tied"
            );
        }
        ArrayList<Coordinates2D> output = new ArrayList<>();
        for (Coordinates2D coordinate : witness) {
            output.add(transformCoordinate(coordinate));
        }
        output.sort(null);
        return output;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public State getSlot(final int row, final int col) {
        return board[row][col];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board clone() {
        try {
            super.clone();
            return new Connect4Board(board);
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                boardString.append(board[i][j]);
                if (j < COLS - 1) {
                    boardString.append(" ");
                }
            }
            if (i < ROWS - 1) {
                boardString.append("\n");
            }
        }
        return boardString.toString();
    }
    //Not part of interface, Shell should not call these:
    /**
     * Get {@link #board} of this Connect4Board instance.
     * @return {@link #board}
     */
    public State[][] getBoard() {
        return board;
    }

    /**
     * Checks if a specific column on the board is full.
     * @param col The col to check for.
     * @return {@code True} if the column is full<br>
     *  {@code False} otherwise.
     */
    public boolean columnFull(final int col) {
        return board[0][col] != State.NONE;
    }


    /**
     * Places a token of a specified {@link State} regardless of
     * the currentPlayer. Does not change the state of this instance, which is
     * treated as immutable and instead creates a new board and returns it.
     * @param col Column to place the token in.
     * @param state The {@link State} of the new token.
     * @return New Connect4Board with the updated {@link #board}.
     */
    private Connect4Board placeToken(final int col, final State state) {
        if (board[0][col] != State.NONE) {
            throw new IllegalMoveException(
                    "Cannot place token: "
                            + "Column is full"
            );
        } else {
            Connect4Board newBoard = ((Connect4Board) this.clone());
            int row = 0;
            while (row + 1 != ROWS
                    && newBoard.board[row + 1][col] == State.NONE) {
                ++row;
            }
            newBoard.board[row][col] = state;
            return newBoard;
        }
    }

    /**
     * Checks for a tie on the playing board.
     * @return {@code true} if a tie occurred.
     */
    private boolean tie() {
        for (int j = 0; j < COLS; j++) {
            if (board[0][j] == State.NONE) {
                return false;
            }
        }
        return true;
    }

    private void checkForGameOver(final Connect4Board board) {
        BoardEvaluator evaluator = new BoardEvaluator(board);
        if (evaluator.getWitness() != null) {
            gameOver = true;
            witness = evaluator.getWitness();
        }
        if (!gameOver) {
            gameOver = board.tie();
        }
    }

    /**
     * Changes the coordinates to a for humans readable format.
     * The origin of the coordinates is now based on 1 ((1,1))
     * and is set to the down-left corner.
     * @param coordinate A {@link Coordinates2D} to be transformed.
     * @return A new {@link Coordinates2D}
     *  that is equal to the original coordinate, but in the new format.
     */
    private Coordinates2D transformCoordinate(final Coordinates2D coordinate) {
        return new Coordinates2D(
                ROWS - coordinate.x(), coordinate.y() + 1
        );
    }
}
