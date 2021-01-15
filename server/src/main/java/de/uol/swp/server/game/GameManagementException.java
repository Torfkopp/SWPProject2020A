package de.uol.swp.server.game;

/**
 * Exception thrown in GameManagement
 * <p>
 * This exception is thrown if an oopsie happens in our glorious game D:
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @see de.uol.swp.server.game.GameManagement
 * @since 2021-01-15
 */
public class GameManagementException extends RuntimeException {

    /**
     * Constructor
     *
     * @param s String containing the exception's cause.
     * @since 2020-12-16
     */
    GameManagementException(String s) {
        super(s);
    }
}