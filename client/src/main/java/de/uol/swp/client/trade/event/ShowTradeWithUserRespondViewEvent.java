package de.uol.swp.client.trade.event;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Event used to show the accept window of a trade
 * <p>
 * In order to show the previous window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.client.SceneManager
 * @since 2021-02-25
 */
public class ShowTradeWithUserRespondViewEvent {

    private final UserOrDummy offeringUser;
    private final LobbyName lobbyName;
    private final TradeWithUserOfferResponse rsp;

    /**
     * Constructor
     *
     * @param offeringUser The User that sends the trading offer
     * @param lobbyName    The Name of the Lobby
     * @param rsp          The Response found on the EventBus
     */
    public ShowTradeWithUserRespondViewEvent(UserOrDummy offeringUser, LobbyName lobbyName,
                                             TradeWithUserOfferResponse rsp) {
        this.offeringUser = offeringUser;
        this.lobbyName = lobbyName;
        this.rsp = rsp;
    }

    /**
     * Gets the lobby name of the lobby where the user wants to
     * trade with the other user
     *
     * @return Lobby name of the lobby where the user wants to trade
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the username of the user that wants to trade
     *
     * @return Username of the user that wants to trade
     */
    public UserOrDummy getOfferingUser() {
        return offeringUser;
    }

    /**
     * Gets the TradeWithUserOfferResponse
     *
     * @return The TradeWithUserOfferResponse of the Event
     */
    public TradeWithUserOfferResponse getRsp() {
        return rsp;
    }
}
