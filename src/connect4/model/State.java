package connect4.model;

/**
 * Represents the different types of players that can
 * occupy a slot on the {@link Connect4Board}.
 */
public enum State {
    /**
     * The human player occupies this slot.
     */
    HUMAN,
    /**
     * The computer player occupies this slot.
     */
    COMPUTER,
    /**
     * The slot is not occupied by a player.
     */
    NONE;

    /**
     * Returns a String representation of a State.
     * @return A String representation of the State:<br>
     * X for the human.<br>
     * O for the computer.<br>
     * . for none.
     */
    public String toString() {
        return switch (this) {
            case HUMAN -> "X";
            case COMPUTER -> "O";
            case NONE -> ".";
        };
    }
}
