package connect4.model;

import java.util.ArrayList;

import static connect4.model.Connect4Board.CONNECT;

/**
 * Evaluates a {@link Board} based on the position and group size
 * of the {@link State}s placed on the board.
 * A token placed in the center gets more points than near a border.
 * The bigger the groups, the more points get awarded.
 * The board is evaluated from the view of the computer, meaning
 * tokens with {@link State#COMPUTER} are evaluated positively,
 * and tokens with {@link State#HUMAN} negatively.
 * A token in the center is rewarded with {@link Board#COLS} / 2.
 * For other columns the reward is reduced by x if is off the center by
 * x columns.
 * Evaluation only works for {@link Board#CONNECT} <= 4,
 * for bigger values factors that reward groupSize accordingly have to be set.
 */
public class BoardEvaluator {
    /**
     * Points added to the points rewarded for group size unconditionally.
     */
    private static final int STARTING_POINTS = 50;
    /**
     * Points added to/ subtracted from the calculation per group of 2.
     */
    private static final int FACTOR_GROUPS_OF_2 = 1;
    /**
     * Points added to/ subtracted from the calculation per group of 3.
     */
    private static final int FACTOR_GROUPS_OF_3 = 4;
    /**
     * Points added to/ subtracted from the calculation per group of 4.
     */
    private static final int FACTOR_GROUPS_OF_4 = 5000;
    //Add more factors a CONNECT > 4
    /**
     * Points subtracted from the calculation per group of {@link Board#CONNECT}
     * if the {@link State} is {@link State#HUMAN}.
     */
    private static final int FACTOR_HUMAN_WIN = 500000;

    /**
     * The board for which the evaluation should happen.
     */
    private final State[][] board;
    /**
     * Stores the amount of consecutive {@link State}s,
     * where the State is {@link State#COMPUTER} with a certain size
     * in this array.
     * The amount of groups with size x can be accessed with
     * groupCountComputer[x].
     */
    private final int[] groupCountComputer = new int[CONNECT + 1];
    /**
     * Stores the amount of consecutive {@link State}s,
     * where the State is {@link State#HUMAN} with a certain size in this array.
     * The amount of groups with size x can be accessed with
     * groupCountComputer[x].
     */
    private final int[] groupCountHuman = new int[CONNECT + 1];
    /**
     * Stores the amount of tokens of {@link State#COMPUTER} for each column.
     * The amount of tokens in column x can be accessed with
     * groupCountComputer[x-1].
     */
    private final int[] colCountComputer = new int[Board.COLS];
    /**
     * Stores the amount of tokens of {@link State#COMPUTER} for each column.
     * The amount of tokens in column x can be accessed with
     * groupCountComputer[x-1].
     */
    private final int[] colCountHuman = new int[Board.COLS];
    /**
     * Stores the first witness (= a group with the size {@link Board#CONNECT})
     * found, or null if there is no witness.
     */
    private final ArrayList<Coordinates2D> witness = new ArrayList<>(CONNECT);

    /**
     * Instantiate a new BoardEvaluator,
     * that calculates the amount of groups for each size per {@link State},
     * as well as the amount of tokens placed in each column per {@link State}.
     * @param board The board to be evaluated.
     */
    public BoardEvaluator(final Connect4Board board) {
        this.board = board.getBoard();
        countHorizontalGroups();
        countVerticalGroups();
        countDiagonalGroups();
        countCols();
    }

    /**
     * Calculate & return the points of the current board.
     * Considering points awarded for column placement as
     * well as points awarded for group size.
     * @return The points for this board.
     */
    public int calculatePoints() {
        return calculateColPoints() + calculateGroupPoints();
    }

    /**
     * Returns the {@link Coordinates2D} of the first witness
     * (= a group of size {@link Board#CONNECT}) found.
     * Witnesses are checked horizontally first,
     * then vertically and finally diagonally.
     * @return {@link ArrayList} with {@link Coordinates2D}
     *  of all the members in the group that form the witness.
     */
    public ArrayList<Coordinates2D> getWitness() {
        if (witness.size() != Board.CONNECT) {
            return null;
        }
        return witness;
    }

    /**
     * Get the groupCount of the computer as an int Array.
     * Array[x] = y means there are y groups
     * of size x on the {@link Connect4Board}.
     * @return An int array, storing the amount of groups
     *  the computer has placed on the playing board.
     */
    public int[] getGroupCountComputer() {
        return groupCountComputer;
    }

    private int calculateColPoints() {
        int cols = colCountComputer.length;
        int sum = 0;
        int factor = 0;
        boolean reverse = false;
        for (int i = 0; i < cols; i++) {
            sum += colCountComputer[i] * factor;
            sum -= colCountHuman[i] * factor;
            if (factor < cols / 2 && !reverse) {
                ++factor;
            } else {
                reverse = true;
                --factor;
            }
        }
        return sum;
    }

    private int calculateGroupPoints() {
        int sum = STARTING_POINTS;
        final int[] factors = new int[Board.CONNECT + 1];
        factors[2] = FACTOR_GROUPS_OF_2;
        factors[3] = FACTOR_GROUPS_OF_3;
        factors[4] = FACTOR_GROUPS_OF_4;
        //For connect != 4, more factors have to be added here.

        for (int i = 2; i <= Board.CONNECT; i++) {
            sum += factors[i] * groupCountComputer[i];
            if (i != Board.CONNECT) {
                sum -= factors[i] * groupCountHuman[i];
            } else {
                sum -= FACTOR_HUMAN_WIN * groupCountHuman[i];
            }
        }
        return sum;
    }

    private void countCols() {
        for (int col = 0; col < board[0].length; col++) {
            for (int row = 0; row < board.length; row++) {
                switch (board[row][col]) {
                    case HUMAN ->  colCountHuman[col]++;
                    case COMPUTER ->  colCountComputer[col]++;
                    default -> {
                        continue;
                    }
                }
            }
        }
    }

    private void countHorizontalGroups() {
        for (int i = 0; i < board.length; i++) {
            countLine(i, 0, 0, 1);
        }
    }

    private void countVerticalGroups() {
        for (int i = 0; i < board[0].length; i++) {
            countLine(0, i, 1, 0);
        }
    }

    private void countDiagonalGroups() {
        for (int col = 0; col < board[0].length; col++) {
            countLine(0, col, 1, 1);
            countLine(board.length - 1, col, -1, 1);
        }
        for (int row = 1; row < board.length; row++) {
            countLine(row, 0, 1, 1);
        }
        for (int row = 1; row < board.length - 1; row++) {
            countLine(row, 0, -1, 1);
        }
    }

    /**
     * Counts the groups of the same State of the board that are
     * in the same line.
     * The line is specified by the starting column, the starting row
     * and the offset of column and row.
     * The offset of column and row cannot both be 0 (= no offset).
     * Groups of {@link State#NONE}s won't be counted.
     * The result of the counting will be stored in an array for each State:
     * {@link #groupCountHuman} for the {@link State#HUMAN}
     * and {@link #groupCountComputer} for the {@link State#COMPUTER}.
     * @param startRow The row to start counting.
     * @param startCol The column to start counting.
     * @param rowOffset The row offset per iteration.
     * @param colOffset the column offset per iteration.
     */
    private void countLine(
            final int startRow,
            final int startCol,
            final int rowOffset,
            final int colOffset) {
        if (rowOffset == 0 && colOffset == 0) {
            throw new IllegalArgumentException(
                    "Offset of row and col cannot be 0 at the same time"
            );
        }
        int currentRow = startRow;
        int currentCol = startCol;
        State previous = State.NONE;
        int count = 0;
        while (withinBounds(
                new Coordinates2D(currentRow, currentCol)
        )) {
            State current = board[currentRow][currentCol];
            if (current != State.NONE
                    && (previous == State.NONE
                    || previous == current)) {
                addToWitness(currentRow, currentCol);
                ++count;
            } else if (count > 0) {
                clearWitness();
                addGroup(count, previous);
                count = 0;
                //This element is already the first member of the next group
                // -> counter should be 1.
                if (current != State.NONE) {
                    ++count;
                    addToWitness(currentRow, currentCol);
                }
            }
            //Check if group of max size is found, if true:
            // evaluate this group and start counting the next group;
            if (count == CONNECT) {
                addGroup(CONNECT, current);
                previous = State.NONE;
                count = 0;
            } else {
                previous = current;
            }
            currentRow += rowOffset;
            currentCol += colOffset;
        }
        //If line has ended,
        // evaluation should happen for the last group in the line.
        if (count > 0) {
            addGroup(count, previous);
        }
        clearWitness();
    }

    /**
     * Increases the groupCount that matches the new group by 1.
     * {@link State#NONE} is not counted, therefore nothing happens.
     * A group with a size smaller than 2 cannot be considered a group,
     * nothing happens as well.
     * @param groupSize The size of the new group.
     * @param state The {@link State} of the new group.
     */
    private void addGroup(final int groupSize, final State state) {
        if (groupSize > 1) {
            switch (state) {
                case HUMAN -> groupCountHuman[groupSize] += 1;
                case COMPUTER -> groupCountComputer[groupSize] += 1;
                default -> {
                    return;
                }
            }
        }
    }

    private boolean withinBounds(final Coordinates2D coordinates) {
        int x = coordinates.x();
        int y = coordinates.y();
        return x >= 0 && x < Board.ROWS && y >= 0 && y < Board.COLS;
    }

    //If the group size is too small delete the current group,
    //as it cannot be a witness.
    private void clearWitness() {
        if (witness.size() != Board.CONNECT) {
            witness.clear();
        }
    }

    //Add a coordinate,
    // that could potentially be part of the witness.
    //Do nothing if a witness was already found.
    private void addToWitness(final int row, final int col) {
        if (witness.size() != Board.CONNECT) {
            witness.add(new Coordinates2D(row, col));
        }
    }
}
