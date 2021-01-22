package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;

/**
 * Message sent by the server when a game session was started.
 *
 * @author Eric Vuong
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.lobby.request.StartSessionRequest
 * @since 2021-01-21
 */
public class StartSessionMessage extends AbstractLobbyMessage {

    /**
     * Constructor
     *
     * @param name The Name of the Lobby
     * @param user The User who started the Session
     */
    public StartSessionMessage(String name, User user) {
        super(name, user);
    }
}
