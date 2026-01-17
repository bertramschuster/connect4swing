package connect4.model;

import java.util.ArrayList;

/**
 * Builds a {@link GameTree} that stores all the {@link Connect4Board}s
 * that can possibly exist up to a certain lookahead
 * using a specific board as starting point. The lookahead specifies how
 * many new tokens are placed on the board, considering that the
 * tokens are alternating between the 2 players.
 */
public class GameTreeBuilder {
    /**
     * The gameTree that is built in this class.
     */
    private final GameTree gameTree;
    /**
     * Points rewarded if the computer can win with a move on depth 1.
     */
    private static final int WIN_POINTS = 5000000;
    /**
     * The depth for which the {@link #gameTree} should be built.
     * The calculation works for any depth but takes
     * exponentially more time for each additional depth level.
     */
    private final int maxDepth;

    /**
     * Instantiate a new gameTreeBuilder by passing the initial board,
     * based on which all the possible other boards are calculated.
     * As well as the depth level of the tree.
     * @param board The initial board.
     * @param maxDepth The depth of the tree.
     */
    public GameTreeBuilder(final Connect4Board board, final int maxDepth) {
        if (maxDepth <= 0) {
            throw new IllegalArgumentException(
                    "Depth of the tree has to be positive"
            );
        }
        gameTree = new GameTree(board);
        Node root = gameTree.getRoot();
        this.maxDepth = maxDepth;
        setAllChildNodes(root, board, 1);
        calculatePoints(root);
    }

    /**
     * Finds and returns the Node on depth 1 with the best
     * {@link Connect4Board} for the {@link State#COMPUTER}.
     * The Node storing a board where {@link BoardEvaluator#calculatePoints()}
     * returns the biggest value is the best.
     * If there are multiple Nodes that are equally good, the one
     * placing the new token in the smallest column is returned.
     * @return The best Node for the {@link State#COMPUTER},
     *  that is a direct child of root.
     */
    public Node getBestNode() {
        ArrayList<Node> nextMoves = gameTree.getRoot().getChildren();
        Node bestMove = null;
        int bestMovePoints = Integer.MIN_VALUE;
        for  (int i = 0; i < nextMoves.size(); i++) {
            Node nextMove = nextMoves.get(i);
            //Check if the column is already full before placing the token.
            boolean columnFull =
                    gameTree.getRoot().getConnect4Board().columnFull(i);
            if ((bestMove == null || nextMove.getPoints() > bestMovePoints)
                    && !columnFull) {
                bestMove = nextMove;
                bestMovePoints = nextMove.getPoints();
            }
        }
        return bestMove;
    }

    //Set all the child Nodes for root up to maxDepth.
    private void setAllChildNodes(
            final Node parent,
            final Connect4Board board,
            final int currentLevel) {
        if (currentLevel <= maxDepth) {
            if (currentLevel % 2 == 1) {
                setChildNodes(parent, board, currentLevel, State.COMPUTER);
            } else {
                setChildNodes(parent, board, currentLevel, State.HUMAN);
            }
        }
    }

    //Set the child Nodes for a single layer.
    private void setChildNodes(
            final Node parent,
            final Connect4Board board,
            final int currentLevel,
            final State state) {
        if (Thread.currentThread().isInterrupted()) {
            //Result is not needed, if thread is interrupted.
            return;
        }
        for (int i = 0; i < Board.COLS; i++) {
            Connect4Board childBoard;
            childBoard = new Connect4Board(board, i, state);
            Node childNode = new Node(childBoard);
            childNode.setDepth(parent.getDepth() + 1);
            childNode.setColumn(i);
            parent.addChild(childNode);
            setAllChildNodes(childNode, childBoard, currentLevel + 1);
        }
    }

    //Calculates the points for all Nodes of the tree.
    private void calculatePoints(final Node current) {
        BoardEvaluator evaluator =
                new BoardEvaluator(current.getConnect4Board());
        int currentPoints = evaluator.calculatePoints();
        //Calculate the points of the leaves.
        if (current.getChildren().isEmpty()) {
            current.setPoints(currentPoints);
            //Calculate points of the inner Nodes.
        } else {
            ArrayList<Node> children = current.getChildren();
            for (Node child : children) {
                calculatePoints(child);
            }
            if (current.getDepth() % 2 == 1) {
                //Check if instant win is possible on first move of the machine.
                if (current.getDepth() == 1) {
                    if (evaluator.getGroupCountComputer()[Board.CONNECT] > 0) {
                        currentPoints += WIN_POINTS;
                    }
                }
                //Humans turn => assume human makes the best possible move
                //(take minimum points of the direct children).
                current.setPoints(getMinChildPoints(current) + currentPoints);
            } else {
                //Computers turn => make the best possible move
                //(take maximum points of the direct children).
                current.setPoints(getMaxChildPoints(current) + currentPoints);
            }
        }
    }

    private int getMinChildPoints(final Node current) {
        int minChildPoints = Integer.MAX_VALUE;
        for (Node child : current.getChildren()) {
            if (child.getPoints() < minChildPoints) {
                minChildPoints = child.getPoints();
            }
        }
        return minChildPoints;
    }

    private int getMaxChildPoints(final Node current) {
        int minChildPoints = Integer.MIN_VALUE;
        for (Node child : current.getChildren()) {
            if (child.getPoints() > minChildPoints) {
                minChildPoints = child.getPoints();
            }
        }
        return minChildPoints;
    }
}
