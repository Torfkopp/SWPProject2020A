package de.uol.swp.client.lobby.event;

import de.uol.swp.common.lobby.LobbyName;

/**
 * This event is used to close a RobberTax window
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @since 2021-04-08
 */
public class CloseRobberTaxViewEvent {

    private final LobbyName lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     */
    public CloseRobberTaxViewEvent(LobbyName lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the lobby's name
     *
     * @return String lobbyName
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }
}
