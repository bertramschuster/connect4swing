package connect4.view;

import connect4.model.Board;

public class HelpMessage {
    public HelpMessage() {
        throw new IllegalStateException("Utility class");
    }

    public static String generalHelpMessage() {
        return """
                Play a game of connect4 against a bot.
                If you are unfamiliar with the rules, check the other help menus.
                """;
    }

    public static String moveHelpMessage() {
        return """
                Click on a column to place a token in it.
                The token will slide down (starting in the top row),
                as long as there is still a unoccupied slot below it.
                """;
    }

    public static String startHelpMessage() {
        return """
                Reset the board by clicking the start button.
                The first player will stay identical to the previous match.
                Press the switch button to reset the board
                and switch the first player.
                """;
    }

    public static String winHelpMessage() {
        String message =
               """
               A player wins if they have %s tokens
               placed next to each other in a vertical,
               horizontal or diagonal line.
               """;
        return String.format(message, Board.CONNECT);
    }
}
