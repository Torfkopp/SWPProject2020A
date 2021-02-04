package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * Base class of lobby responses. Basic handling of the lobby name.
 *
 * @author Maximilian Lindner
 * @since 2021-02-04
 */
public class AbstractLobbyResponse extends AbstractResponseMessage {

    private String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName name of the lobby
     */
    public AbstractLobbyResponse(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Default constructor
     *
     * @implNote This constructor is needed for serialisation
     */
    public AbstractLobbyResponse() {
    }

    /**
     * Gets the lobby name variable
     *
     * @return String containing the lobby's name
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
