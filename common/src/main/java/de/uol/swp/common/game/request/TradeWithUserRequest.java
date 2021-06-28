package de.uol.swp.common.game.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.request.AbstractLobbyRequest;
import de.uol.swp.common.user.Actor;

/**
 * Request is sent to the Server the get the Inventory
 * of the player who wants to trade with a user.
 *
 * @author Finn Haase
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @since 2021-02-21
 */
public class TradeWithUserRequest extends AbstractLobbyRequest {

    private final Actor respondingUser;
    private final boolean counterOffer;

    /**
     * Constructor
     *
     * @param name           The Name of the lobby
     * @param user           The User who is making a trade offer
     * @param tradingPartner The trading partner
     * @param counterOffer   Whether the offer is a counter offer or not
     */
    public TradeWithUserRequest(LobbyName name, Actor user, Actor tradingPartner, boolean counterOffer) {
        super(name, user);
        this.respondingUser = tradingPartner;
        this.counterOffer = counterOffer;
    }

    /**
     * Gets The username of the trading partner
     *
     * @return The username of the trading partner
     */
    public Actor getRespondingUser() {
        return respondingUser;
    }

    /**
     * Gets the counter offer status
     *
     * @return Whether the offer is a counter offer or not
     */
    public boolean isCounterOffer() {
        return counterOffer;
    }
}
