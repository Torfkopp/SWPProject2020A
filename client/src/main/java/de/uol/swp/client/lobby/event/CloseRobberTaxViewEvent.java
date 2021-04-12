package de.uol.swp.client.lobby.event;

import de.uol.swp.common.user.User;

/**
 * This event is used to close a RobberTax window
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @since 2021-04-08
 */
public class CloseRobberTaxViewEvent {

    private final String lobbyName;
    private final User user;

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user      User closing the view
     */
    public CloseRobberTaxViewEvent(String lobbyName, User user) {
        this.lobbyName = lobbyName;
        this.user = user;
    }

    /**
     * Gets the lobby's name
     *
     * @return String lobbyName
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the user
     *
     * @return User user
     */
    public User getUser() {
        return user;
    }
}
