package connect4.controller;

import connect4.model.Connect4Board;

/**
 * Interface for Observers.
 */
public interface Observer {
    void update(Connect4Board board);
    void showError(String message);
}
