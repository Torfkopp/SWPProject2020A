package de.uol.swp.common.lobby.response;

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

    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby which this Response is directed to
     */
    public AbstractLobbyResponse(String lobbyName) {this.lobbyName = lobbyName;}

    /**
     * Gets the name of the lobby
     *
     * @return Name of the Lobby
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
