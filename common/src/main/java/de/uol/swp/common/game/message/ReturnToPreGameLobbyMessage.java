package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.user.User;

/**
 * Message sent to return to the Pre-Game Lobby
 *
 * @author Steven Luong
 * @author Finn Haase
 * @since 2021-03-22
 */
public class ReturnToPreGameLobbyMessage extends AbstractLobbyMessage {

    /**
     * Constructor
     *
     * @param lobbyName The lobby this game takes place in
     * @param user      The user in the lobby
     */
    public ReturnToPreGameLobbyMessage(String lobbyName, User user) {
        super(lobbyName, user);
    }
}
