package de.uol.swp.client.lobby.event;

/**
 * This event is used to close a RobberTax window
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @since 2021-04-08
 */
public class CloseRobberTaxViewEvent {

    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     */
    public CloseRobberTaxViewEvent(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the lobby's name
     *
     * @return String lobbyName
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
