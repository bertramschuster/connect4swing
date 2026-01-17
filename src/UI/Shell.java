package UI;

//Java imports.
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
//Package imports.
import connect4.model.Board;
import connect4.model.Connect4Board;
import connect4.model.Coordinates2D;
import connect4.model.State;

/**
 * Shell is a utility class, serving as an interface for the user.
 * It provides various operations the player can perform on a board.
 */
public final class Shell {
    /**
     * Represents if the application should be closed.
     * Originally false.
     */
    private static boolean quit = false;
    /**
     * The current board. All operations will be performed on this board.
     * On startup a new empty board is created.
     */
    private static Connect4Board board;

    private Shell() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated."
        );
    }

    /**
     * Launches the game.
     * @param args .
     * @throws IOException BufferedReader is used in a subclass
     *  (BufferedReader can throw IOException).
     */
    public static void main(final String[] args)
            throws IOException, InterruptedException {
        //Lazy initialization, so interruptedException can be thrown.
        initializeBoard();
        playGame();
    }

    private static void playGame() throws IOException, InterruptedException {
        BufferedReader reader
                = new BufferedReader(new InputStreamReader(System.in));

        while (!quit) {
            System.out.print("connect4> ");
            String input = reader.readLine();
            //bufferedReader can be null
            if (input == null) {
                error("Input cannot be null");
                break;
            }
            String[] tokens = input.trim().split("\\s+");
            evaluateTokens(tokens);
        }
    }

    private static void evaluateTokens(final String[] tokens)
            throws InterruptedException {
        if (tokens.length == 0 || tokens[0].isEmpty()) {
            error("Input cannot be empty");
        } else {
            char op = tokens[0].toUpperCase().charAt(0);
            switch (op) {
                case 'Q' -> quit = true;
                case 'H' -> helpOperation();
                case 'N' -> newOperation();
                case 'L' -> {
                    if (hasOneValidParameter(tokens)) {
                        setLevelOperation(Integer.parseInt(tokens[1]));
                    } else {
                        error("There was no valid parameter provided");
                    }
                }
                case 'S' -> switchOperation();
                case 'M' -> {
                    if (hasOneValidParameter(tokens)) {
                        moveOperation(Integer.parseInt(tokens[1]));
                    } else {
                        error("There was no valid parameter provided");
                    }
                }
                case 'W' -> getWitnessOperation();
                case 'P' -> System.out.println(board);
                default -> error("Invalid operation");
            }
        }
    }

    private static void helpOperation() {
        String text = (
            """
               Play a game of connect 4 against a computer.
               You can place a token on the playing board
               using the MOVE command.
               The computer will than make its move automatically.
               The first player to get a group of %d tokens
               in any direction (horizontal, vertical, diagonal)
               wins. Good luck!

               The following commands are supported:
               COMMAND DESCRIPTION
               NEW     Create a new board and delete the old board.
                       a new board is automatically created when the
                       program is started.
                       The first player remains identical.
               LEVEL   Set the difficulty level of the computer.
                       Level can be a number between 1 and %d.
               SWITCH  Create a new board and delete the old board.
                       The first player will be switched.
               MOVE    Place your token. The column to place the token in
                       has to be passed after the command.
                       The first column is column 1,
                       the last column is column %d,
               WITNESS Prints a group of %d. This operation can only be
                       executed if a the game is over and a player won.
               PRINT   Prints the current board to the console. You are
                       represented by %s, the computer is represented by %s.
               QUIT    Quit the program.

               Each command can be accessed by only using their first character.
               Capitalization is ignored.
            """
        ).formatted(
                Board.CONNECT,
                Connect4Board.MAX_LEVEL,
                Board.COLS,
                Board.CONNECT,
                State.HUMAN.toString(),
                State.COMPUTER.toString()
        ).stripTrailing();
        System.out.println(text);
    }

    private static void newOperation() throws InterruptedException {
        board = new Connect4Board(false);
    }

    private static void setLevelOperation(final int level) {
        if (level <= 0 || level > Connect4Board.MAX_LEVEL) {
            error("Invalid parameter: " + level
                    + " is out of bounds. "
                    + "Level has to be positive with a max value of "
                    + Connect4Board.MAX_LEVEL);
        } else {
            board.setLevel(level);
        }
    }

    private static void switchOperation() throws InterruptedException {
        board = new Connect4Board(true);
        if (board.getFirstPlayer() == State.COMPUTER) {
            board = board.machineMove();
        }
    }

    private static void moveOperation(final int col)
            throws InterruptedException {
        if (col <= 0 || col > Connect4Board.COLS) {
            error("Column out of bounds: all columns are between 1 and "
                    + Connect4Board.COLS
            );
        } else if (board.isGameOver()) {
            error("Game is already over");
        } else {
            Connect4Board updatedBoard = board.move(col);
            //Check if the column is already full.
            if (updatedBoard == null) {
                error("Column is full");
            } else {
                if (!updatedBoard.isGameOver()) {
                    try {
                        updatedBoard = updatedBoard.machineMove();
                    } catch (InterruptedException e) {
                        //This exception cannot happen with a console interface,
                        //therefore it can be handled by printing the stacktrace
                        //as it should never happen.
                        throw new InterruptedException(
                                "The machine was interrupted "
                                        + "trying to make its move."
                        );
                    }
                }
                board = updatedBoard;
                if (updatedBoard.isGameOver()) {
                    if (board.getWinner() == null) {
                        System.out.println("Nobody wins. Tie.");
                    } else {
                        if (board.getWinner() == State.HUMAN) {
                            System.out.println(
                                    "Congratulations! You won."
                            );
                        } else if  (board.getWinner() == State.COMPUTER) {
                            System.out.println(
                                    "Sorry! Machine wins."
                            );
                        }
                    }
                }
            }
        }
    }

    private static void getWitnessOperation() {
        if (!board.isGameOver()) {
            error("The game has not ended yet");
        } else if (board.isGameOver() && board.getWinner() == null) {
            System.out.println("A witness does not exist for a tie");
        } else {
            ArrayList<Coordinates2D> witness = board.getWitness();
            StringBuilder witnessString = new StringBuilder();
            for (int i = 0; i < witness.size() - 1; i++) {
                witnessString.append(witness.get(i).toString());
                witnessString.append(", ");
            }
            witnessString.append(witness.getLast().toString());
            System.out.println(witnessString);
        }
    }

    private static void error(final String message) {
        System.out.println("Error! " + message);
    }

    private static boolean hasOneValidParameter(final String[] input) {
        //Check if token has a second argument.
        if (input.length <= 1) {
            return false;
        }
        //Check if second argument has correct type (int).
        try {
            Integer.parseInt(input[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static void initializeBoard()
            throws InterruptedException {
        board = new Connect4Board(false);
    }
}
