package de.uol.swp.common.lobby.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * Base class for all Responses concerning one single lobby. This class abstracts away
 * the lobbyName attribute.
 *
 * @author Maximilian Lindner
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2021-02-09
 */
public abstract class AbstractLobbyResponse extends AbstractResponseMessage {

    private final LobbyName lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby which this Response is directed to
     */
    public AbstractLobbyResponse(LobbyName lobbyName) {this.lobbyName = lobbyName;}

    /**
     * Gets the name of the lobby
     *
     * @return Name of the Lobby
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }
}
