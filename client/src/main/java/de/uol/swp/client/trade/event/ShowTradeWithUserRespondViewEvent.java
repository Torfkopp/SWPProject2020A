package de.uol.swp.client.trade.event;

import de.uol.swp.common.game.response.TradeWithUserOfferResponse;

/**
 * Event used to show the accept window of a trade
 * In order to show the previous window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.client.SceneManager
 * @since 2021-02-25
 */
public class ShowTradeWithUserRespondViewEvent {

    private final String offeringUser;
    private final String respondingUser;
    private final String lobbyName;
    private final TradeWithUserOfferResponse rsp;

    /**
     * Constructor
     *
     * @param offeringUser   The User that sends the trading offer
     * @param respondingUser The user that receives the trading offer
     * @param lobbyName      The Name of the Lobby
     * @param rsp            The Response found on the EventBus
     */
    public ShowTradeWithUserRespondViewEvent(String offeringUser, String respondingUser, String lobbyName,
                                             TradeWithUserOfferResponse rsp) {
        this.offeringUser = offeringUser;
        this.respondingUser = respondingUser;
        this.lobbyName = lobbyName;
        this.rsp = rsp;
    }

    /**
     * Gets the lobby name of the lobby where the user wants to
     * trade with the other user
     *
     * @return Lobby name of the lobby where the user wants to trade
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the username of the user that wants to trade
     *
     * @return Username of the user that wants to trade
     */
    public String getOfferingUser() {
        return offeringUser;
    }

    /**
     * Gets the username of the user that receives the trading offer
     *
     * @return Username of the user that receives the trading offer
     */
    public String getRespondingUser() {
        return respondingUser;
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