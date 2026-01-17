package connect4.model;

import java.util.ArrayList;

/**
 * A simple Node to build more complex structures with.
 * Each Node stores an {@link ArrayList} of all their children.
 */
public class Node {
    /**
     * All the direct children of this Node.
     */
    private final ArrayList<Node> children = new ArrayList<>();
    /**
     * The current board of this Node.
     */
    private final Connect4Board board;
    /**
     * The depth level of this Node.
     * The depth is equal to the amount of parents
     * that have to be traversed before the next parent is null.
     * (E.g. for a tree: 0 for the root itself,
     * 1 for all children of the root, ...)
     */
    private int depth;
    /**
     * The column in which a token was newly placed
     * (compared to the board of the parent).
     */
    private int column;
    /**
     * The points of the current Node.
     */
    private int points;

    /**
     * Instantiate a new Node.
     * The Node needs a reference to a board it should store.
     * The parent and the {@link #board} cannot be
     * changed after initialization.
     * @param board Board of the new Node.
     */
    public Node(final Connect4Board board) {
        this.board = board;
    }

    /**
     * Get all children of this Node as {@link ArrayList}.
     * @return All children of this Node.
     */
    public ArrayList<Node> getChildren() {
        return children;
    }

    /**
     * Adds a child Node to {@link #children} of this Node.
     * Once a child is added it cannot be removed.
     * @param child The Node to be added to {@link #children}
     */
    public void addChild(final Node child) {
        children.add(child);
    }

    /**
     * Get the board of this Node.
     * @return {@link #board} of this board.
     */
    public Connect4Board getConnect4Board() {
        return board;
    }

    /**
     * Get the depth level of this Node.
     * @return {@link #depth}
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Set the {@link #depth} of this Node.
     * @param depth The new depth of this Node.
     */
    public void setDepth(final int depth) {
        this.depth = depth;
    }

    /**
     * Get the column of this Node.
     * @return {@link #column} of this Node.
     */
    public int getColumn() {
        return column;
    }
    /**
     * Set the {@link #column} of this Node.
     * @param column The new column of this Node.
     */
    public void setColumn(final int column) {
        this.column = column;
    }

    /**
     * Get the points of this Node.
     * @return {@link #points} of this Node.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Set the {@link #points} of this Node.
     * @param points The new points of this Node.
     */
    public void setPoints(final int points) {
        this.points = points;
    }
}
