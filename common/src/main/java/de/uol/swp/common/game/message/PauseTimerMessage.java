package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Message sent to pause the roundTimer.
 *
 * @author Alwin Bossert
 * @since 2021-05-02
 */
public class PauseTimerMessage extends AbstractLobbyMessage {

    /**
     * Constructor
     *
     * @param lobbyName The lobby this game is taking place in
     * @param user      The user who wants to trade
     */
    public PauseTimerMessage(String lobbyName, UserOrDummy user) {
        super(lobbyName, user);
    }
}
