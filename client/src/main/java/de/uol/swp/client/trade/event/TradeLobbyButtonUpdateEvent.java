package de.uol.swp.client.trade.event;

import de.uol.swp.common.user.User;

/**
 * Event used to trigger the updating of the Trade with Bank Button in the according lobby
 * <p>
 * In order to change the status of the button in the right lobby, post an
 * instance of it onto the EventBus the LobbyPresenter is subscribed to.
 *
 * @author Maximilian Lindner
 * @author Alwin Bossert
 * @see de.uol.swp.client.lobby.LobbyPresenter
 * @since 2021-02-20
 */
public class TradeLobbyButtonUpdateEvent {

    private final User user;
    private final String lobbyName;

    /**
     * Constructor
     *
     * @param user      User that wants to update the button status
     * @param lobbyName The name of the lobby where the button should be enabled
     */
    public TradeLobbyButtonUpdateEvent(User user, String lobbyName) {
        this.user = user;
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the User who wants to update the button status
     *
     * @return User object of the event
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the name of the lobby where the button should be enabled
     *
     * @return LobbyName object of the event
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
