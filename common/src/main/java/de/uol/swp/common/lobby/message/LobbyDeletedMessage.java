package de.uol.swp.common.lobby.message;

import de.uol.swp.common.LobbyName;

/**
 * Message sent by the server when a lobby was deleted
 *
 * @author Temmo Junkhoff
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @since 2020-12-17
 */
public class LobbyDeletedMessage extends AbstractLobbyMessage {

    /**
     * Constructor
     *
     * @param name The name of the lobby that was deleted
     */
    public LobbyDeletedMessage(LobbyName name) {
        super(name, null);
    }
}
