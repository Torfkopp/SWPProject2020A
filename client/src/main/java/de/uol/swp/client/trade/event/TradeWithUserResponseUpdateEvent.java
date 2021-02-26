package de.uol.swp.client.trade.event;

import de.uol.swp.common.game.response.TradeWithUserOfferResponse;

/**
 * This Event is used to give a Responding User of a
 * trade between 2 Users the necessary information.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @since 2021-02-25
 */
public class TradeWithUserResponseUpdateEvent {

    private final TradeWithUserOfferResponse rsp;

    /**
     * Constructor
     *
     * @param rsp A TradeWithUserOfferResponse
     */
    public TradeWithUserResponseUpdateEvent(TradeWithUserOfferResponse rsp) {
        this.rsp = rsp;
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
