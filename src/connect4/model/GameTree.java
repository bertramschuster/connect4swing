package connect4.model;

import java.util.ArrayList;

/**
 * Stores a treelike data structure
 * by storing a {@link Node} as root Node.
 * The root Node does not have a parent (parent == null).
 * as {@link ArrayList}.
 */
public class GameTree {
    /**
     * The root {@link Node}.
     */
    private final Node root;

    /**
     * Instantiate a new gameTree.
     * A new gameTree needs the {@link Connect4Board}
     * of the {@link #root} as a parameter.
     * @param board Board the root should store.
     */
    public GameTree(final Connect4Board board) {
        root = new Node(board);
        root.setDepth(0);
    }

    /**
     * Get the root of the tree.
     * @return {@link Node} of the root.
     */
    public Node getRoot() {
        return root;
    }
}
