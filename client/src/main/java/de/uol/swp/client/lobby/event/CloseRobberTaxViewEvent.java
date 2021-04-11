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

    public CloseRobberTaxViewEvent(String lobbyName, User user) {
        this.lobbyName = lobbyName;
        this.user = user;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public User getUser() {
        return user;
    }
}
