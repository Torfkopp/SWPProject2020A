package de.uol.swp.client.trade.event;

import de.uol.swp.common.user.User;

/**
 * Event used to show the window for the trading with another user
 * <p>
 * In order to show the previous window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.client.SceneManager
 * @since 2021-02-23
 */
public class ShowTradeWithUserViewEvent {

    private final User offeringUser;
    private final String lobbyName;
    private final String respondingUserName;

    /**
     * Constructor
     *
     * @param offeringUser       User that wants to trade with another user
     * @param lobbyName          Lobby name of the lobby where the player wants to trade
     * @param respondingUserName Name of the user who the offer will be made to
     */
    public ShowTradeWithUserViewEvent(User offeringUser, String lobbyName, String respondingUserName) {
        this.offeringUser = offeringUser;
        this.lobbyName = lobbyName;
        this.respondingUserName = respondingUserName;
    }

    /**
     * Gets the lobby name of the lobby where the player want to
     * trade with the user
     *
     * @return Lobby name of the lobby where the player wants to trade
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the User who wants to trade with another User
     *
     * @return User who wants to trade with another User
     */
    public User getOfferingUser() {
        return offeringUser;
    }

    /**
     * Gets the name of the responding user.
     *
     * @return Name of the responding user
     */
    public String getRespondingUserName() {
        return respondingUserName;
    }
}
