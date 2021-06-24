package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.user.Actor;

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
    public PauseTimerMessage(LobbyName lobbyName, Actor user) {
        super(lobbyName, user);
    }
}
