package connect4.model;

/**
 * Exception for trying to make an illegal move on the board.
 * This exception should be thrown when the game is in a state,
 * where a player cannot perform the specified action
 * (e.g. it is not their turn or the game has ended).
 */
public class IllegalMoveException extends RuntimeException {
    /**
     * Constructor for an IllegalMoveException.
     * Receives a message as parameter,
     * which will be propagated to the {@link RuntimeException}.
     * @param message The exception message.
     */
    public IllegalMoveException(final String message) {
        super("Illegal move: " + message);
    }
}
