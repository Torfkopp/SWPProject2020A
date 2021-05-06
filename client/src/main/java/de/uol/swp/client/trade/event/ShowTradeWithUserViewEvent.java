package de.uol.swp.client.trade.event;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

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

    private final LobbyName lobbyName;
    private final UserOrDummy respondingUser;

    /**
     * Constructor
     *
     * @param lobbyName      Lobby name of the lobby where the player wants to trade
     * @param respondingUser User who the offer will be made to
     */
    public ShowTradeWithUserViewEvent(LobbyName lobbyName, UserOrDummy respondingUser) {
        this.lobbyName = lobbyName;
        this.respondingUser = respondingUser;
    }

    /**
     * Gets the lobby name of the lobby where the player want to
     * trade with the user
     *
     * @return Lobby name of the lobby where the player wants to trade
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the name of the responding user.
     *
     * @return Name of the responding user
     */
    public UserOrDummy getRespondingUser() {
        return respondingUser;
    }
}
