package connect4.model;

/**
 * Stores a coordinate in a 2D space.
 * After setting the coordinates in the constructor they cannot be changed.
 *
 * @param x X coordinate of a point in a 2D space.
 * @param y Y coordinate of a point in a 2D space.
 */
public record Coordinates2D(int x, int y) implements Comparable<Coordinates2D> {
    /**
     * Constructor sets an x and y coordinate.
     * These values cannot be changed after initialization.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public Coordinates2D {
    }

    /**
     * Get the {@link #x} value of a coordinate.
     *
     * @return {@link #x}
     */
    @Override
    public int x() {
        return x;
    }

    /**
     * Get the {@link #y} value of a coordinate.
     *
     * @return {@link #y}
     */
    @Override
    public int y() {
        return y;
    }

    /**
     * Transforms a {@link Coordinates2D} into a String representation
     * with the following format:
     * "({@link Coordinates2D#x}, {@link Coordinates2D#y})".
     *
     * @return A String representation of this {@link Coordinates2D}.
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Compare this {@link Coordinates2D} with another coordinate.
     * Coordinates get compared by {@link #x} first.
     * The coordinate with the bigger x value is bigger.
     * If they are equal they will get compared by {@link #y}.
     * The coordinate with the bigger y value is bigger.
     * If both values are identical, the coordinates are equal.
     *
     * @param other The other coordinate.
     * @return -1 if this coordinate is smaller than the other,<br>
     * 0 if they are equal,<br>
     * 1 if this coordinate is bigger than the other.
     */
    @Override
    public int compareTo(final Coordinates2D other) {
        if (x < other.x()) {
            return -1;
        } else if (x > other.x()) {
            return 1;
        } else {
            if (y < other.y()) {
                return -1;
            } else if (y > other.y()) {
                return 1;
            }
        }
        return 0;
    }
}
