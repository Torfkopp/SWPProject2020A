package de.uol.swp.client.trade.event;

import de.uol.swp.common.user.UserOrDummy;

/**
 * Event used to trigger the updating of the Trade with Bank Button status
 * in the according lobby if a trade was successful
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

    private final UserOrDummy user;
    private final String lobbyName;

    /**
     * Constructor
     *
     * @param user      User that wants to update the button status
     * @param lobbyName The name of the lobby where the button should be disabled
     */
    public TradeLobbyButtonUpdateEvent(UserOrDummy user, String lobbyName) {
        this.user = user;
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the name of the lobby where the button should be disabled
     *
     * @return The name of the lobby where the button should be disabled
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the User who wants to update the button status
     *
     * @return The User who wants to update the button status
     */
    public UserOrDummy getUser() {
        return user;
    }
}
