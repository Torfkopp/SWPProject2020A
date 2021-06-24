package de.uol.swp.common.lobby.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Message sent by the server when a new lobby was created
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @since 2019-10-08
 */
public class LobbyCreatedMessage extends AbstractLobbyMessage {

    /**
     * Constructor
     *
     * @param name The name of the lobby that was created
     * @param user The user who created the lobby
     */
    public LobbyCreatedMessage(LobbyName name, Actor user) {
        super(name, user);
    }
}
