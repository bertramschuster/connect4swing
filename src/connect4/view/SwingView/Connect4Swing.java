package connect4.view.SwingView;

import connect4.view.HelpMessage;
import connect4.controller.BoardController;
import connect4.view.Observer;
import connect4.model.Board;
import connect4.model.Connect4Board;
import connect4.model.Coordinates2D;
import connect4.model.State;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Displays the current state of the board and various buttons to change
 * its state using Java Swing.
 */
public class Connect4Swing implements Observer {
    /**
     * The JFrame where the connect4 application is displayed.
     */
    private static JFrame frame;
    /**
     * The controller to link the swing view and the model.
     */
    private static BoardController controller;
    /**
     * List of all the {@link Slot}s of the board.
     */
    private static final ArrayList<Slot> slots = new ArrayList<>();
    /**
     * The JPanel displaying the board and the column and row indexes.
     */
    private static JPanel grid;

    //Colors:
    /**
     * The main color of the application.
     * This originally effects the background of the board
     * as well as the text of the column & row indexes.
     * Originally blue.
     */
    private static final Color PRIMARY_COLOR = Color.BLUE;
    /**
     * The secondary color of the application.
     * This originally effects empty Slots
     * and the background of column and row indexes.
     * Originally white.
     */
    public static final Color SECONDARY_COLOR = Color.WHITE;
    /**
     * Color for highlighting the witness.
     * Originally the same as {@link #SECONDARY_COLOR}.
     */
    public static final Color HIGHLIGHT_COLOR = SECONDARY_COLOR;
    /**
     * Background color of the application.
     * Originally effects the space around the buttons and the menu bar.
     * Originally the same as {@link #SECONDARY_COLOR}.
     */
    public static final Color BACKGROUND_COLOR = SECONDARY_COLOR;
    /**
     * Color for all tokens placed by the human.
     * Originally yellow.
     */
    private static final Color HUMAN_COLOR = Color.YELLOW;
    /**
     * Color for all tokens placed by the computer.
     * Originally red.
     */
    private static final Color COMPUTER_COLOR = Color.RED;

    //Sizes
    /**
     * Used for the distance between each token on the board.
     * Originally 5.
     */
    private static final int GRID_PADDING = 5;
    /**
     * Used for the size of all borders.
     * Originally 5.
     */
    private static final int BORDER_SIZE = 5;
    /**
     * Used for the width/height of the index counters for the board.
     * Originally 30.
     */
    private static final int COUNT_PADDING = 30;
    /**
     * Used for all buttons.
     * Originally 90x40
     */
    private static final Dimension BUTTON_SIZE = new Dimension(90, 40);

    public Connect4Swing() {
        instantiateFrame();
        controller = new BoardController(frame, this);
        startApplication();
    }

    private static void instantiateFrame() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setTitle("connect4");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private static void startApplication() {
        frame.add(getBoard());
        frame.add(linkButtons());
        linkColButtons();
        menuBar();
    }

    private static JPanel getBoard() {
        //The grid and the column indexes.
        JPanel gridCol = new JPanel();
        gridCol.setLayout(new BorderLayout());
        gridCol.setBackground(SECONDARY_COLOR);

        //Only the grid.
        GridLayout gridLayout = new GridLayout(Board.ROWS,Board.COLS);
        gridLayout.setHgap(GRID_PADDING);
        gridLayout.setVgap(GRID_PADDING);
        grid = new JPanel(gridLayout);
        grid.setBackground(PRIMARY_COLOR);
        grid.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, GRID_PADDING));
        grid.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //Fill the grid.
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                Slot slot = new Slot(SECONDARY_COLOR);
                slot.setBackground(PRIMARY_COLOR);
                slots.add(slot);
                grid.add(slot);
            }
        }
        gridCol.add(grid, BorderLayout.CENTER);

        //Indexes of the columns.
        JPanel colCount = new JPanel();
        colCount.setBackground(SECONDARY_COLOR);
        colCount.setLayout(new BoxLayout(colCount, BoxLayout.X_AXIS));
        for (int i = 0; i < Board.COLS; i++) {
            JLabel label = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            label.setForeground(PRIMARY_COLOR);
            label.setOpaque(true);
            label.setBackground(SECONDARY_COLOR);
            label.setMaximumSize((new Dimension(Integer.MAX_VALUE, COUNT_PADDING)));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setAlignmentY(Component.CENTER_ALIGNMENT);

            colCount.add(label);

            if (i != Board.COLS - 1) {
                JPanel padding = new JPanel();
                padding.setBackground(PRIMARY_COLOR);
                setSize(padding, GRID_PADDING, Integer.MAX_VALUE);
                colCount.add(padding);
            }
        }
        colCount.setPreferredSize(new Dimension(Short.MAX_VALUE, COUNT_PADDING));
        gridCol.add(colCount, BorderLayout.SOUTH);

        //The indexes for the rows.
        JPanel rowCount = new JPanel();
        rowCount.setBackground(SECONDARY_COLOR);
        rowCount.setLayout(new BoxLayout(rowCount, BoxLayout.Y_AXIS));
        for (int i = Board.ROWS - 1; i >= 0; i--) {
            JLabel label = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            label.setForeground(PRIMARY_COLOR);
            label.setOpaque(true);
            label.setBackground(SECONDARY_COLOR);
            label.setMaximumSize((new Dimension(COUNT_PADDING, Integer.MAX_VALUE)));
            label.setAlignmentY(Component.CENTER_ALIGNMENT);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);

            rowCount.add(label);

            JPanel padding = new JPanel();
            if (i != 0) {
                padding.setBackground(PRIMARY_COLOR);
                setSize(padding, COUNT_PADDING, GRID_PADDING);
            } else {
                padding.setBackground(SECONDARY_COLOR);
                setSize(padding, COUNT_PADDING, COUNT_PADDING);
            }
            rowCount.add(padding);
        }

        //The entire board, including both row and col counters.
        JPanel board = new JPanel();
        board.setLayout(new BorderLayout());
        board.setBackground(SECONDARY_COLOR);
        board.add(gridCol, BorderLayout.CENTER);
        board.add(rowCount, BorderLayout.WEST);
        board.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, BORDER_SIZE));
        return board;
    }

    private static void setSize(Component component, int width, int height) {
        component.setMinimumSize(new Dimension(width, height));
        component.setPreferredSize(new Dimension(width, height));
        component.setMaximumSize(new Dimension(width, height));
    }

    private static JPanel linkButtons() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        JComboBox<Integer> difficultyBtn = new JComboBox<>();
        for (int i = 1; i <= Connect4Board.MAX_LEVEL; i++) {
            difficultyBtn.addItem(i);
        }
        difficultyBtn.setSelectedItem(4);
        difficultyBtn.addActionListener(e -> {
            Integer selected = (Integer) difficultyBtn.getSelectedItem();
            controller.setDifficultyAction(selected);
        });
        JButton startBtn = new JButton("Start");
        startBtn.addActionListener(e -> {
            controller.startAction();
        });
        JButton switchBtn = new JButton("Switch");
        switchBtn.addActionListener(e -> {
            controller.switchAction();
        });
        JButton undoBtn = new JButton("Undo");
        undoBtn.addActionListener(e -> {
            controller.undoAction();
        });
        JButton quitBtn = new JButton("Quit");
        quitBtn.addActionListener(e -> {
            controller.quitAction();
        });
        setBtnSize(difficultyBtn);
        setBtnSize(startBtn);
        setBtnSize(switchBtn);
        setBtnSize(undoBtn);
        setBtnSize(quitBtn);
        buttons.add(difficultyBtn);
        buttons.add(startBtn);
        buttons.add(switchBtn);
        buttons.add(undoBtn);
        buttons.add(quitBtn);
        buttons.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        buttons.setBackground(BACKGROUND_COLOR);
        return buttons;
    }

    private static void setBtnSize(JComponent btn) {
        btn.setPreferredSize(BUTTON_SIZE);
        btn.setMaximumSize(BUTTON_SIZE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private static void linkColButtons() {
        grid.addMouseListener(new MouseAdapter() {
                                  @Override
                                  public void mousePressed(MouseEvent e){
                                      if (controller.getBoard().isGameOver()) {
                                          JOptionPane.showMessageDialog(frame, "Game is already over");
                                      } else {
                                          controller.moveAction(e.getX());
                                      }
                                  }
                              }
        );
    }

    private static void menuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Help");
        JMenuItem general = new JMenuItem("Info");
        general.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, HelpMessage.generalHelpMessage());
        });
        JMenuItem move = new JMenuItem("How to place a token");
        move.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, HelpMessage.moveHelpMessage());
        });
        JMenuItem win = new JMenuItem("How to win");
        win.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, HelpMessage.winHelpMessage());
        });
        JMenuItem start = new JMenuItem("How to start a new game");
        start.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, HelpMessage.startHelpMessage());
        });
        helpMenu.add(general);
        helpMenu.add(move);
        helpMenu.add(win);
        helpMenu.add(start);
        helpMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuBar.add(helpMenu);
        menuBar.setBackground(BACKGROUND_COLOR);
        frame.setJMenuBar(menuBar);
    }

    @Override
    public void update(Connect4Board board) {
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                State current = board.getSlot(i,j);
                Color color;
                switch(current) {
                    case HUMAN -> {
                        color = HUMAN_COLOR;
                    }
                    case COMPUTER -> {
                        color = COMPUTER_COLOR;
                    }
                    default -> color = SECONDARY_COLOR;
                }
                Slot currentSlot = slots.get(calculateIndex(i,j));
                currentSlot.highlight((null));
                currentSlot.changeColor(color);
            }
        }
        if (board.isGameOver()) {
            if (board.getWinner() != null) {
                ArrayList<Coordinates2D> witness = board.getWitness();
                for (Coordinates2D c : witness ) {
                    c = transformCoordinate(c);
                    Slot currentSlot = slots.get(calculateIndex(c.x(),c.y()));
                    currentSlot.highlight(HIGHLIGHT_COLOR);
                }
            }
            //Message has to show for tie as well.
            displayGameOverMessage(board);
        }
    }

    private int calculateIndex(int i, int j) {
        return i * Board.COLS +  j;
    }

    private static Coordinates2D transformCoordinate(final Coordinates2D coordinate) {
        return new Coordinates2D(
                Board.ROWS - coordinate.x(), coordinate.y() - 1
        );
    }

    private void displayGameOverMessage(Connect4Board board) {
        if (!board.isGameOver()) {
            throw new IllegalStateException("Game is not over yet");
        }

        State winner = board.getWinner();
        String message = "";
        if  (winner == null) {
            message = "Nobody wins. Tie!";
        } else {
            switch (winner) {
                case HUMAN -> {
                    message = "Congratulations! You won.";
                }
                case COMPUTER -> {
                    message = "Sorry! Machine wins.";
                }
            }
        }
        JOptionPane.showMessageDialog(frame, message);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }
}
