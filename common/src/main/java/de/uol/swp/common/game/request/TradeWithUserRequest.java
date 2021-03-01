package de.uol.swp.common.game.request;

import de.uol.swp.common.lobby.request.AbstractLobbyRequest;
import de.uol.swp.common.user.User;

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

    private final String respondingUser;

    /**
     * Constructor
     *
     * @param name           The Name of the lobby
     * @param user           The User who wants to start a game session
     * @param tradingPartner The username of the trading partner
     */
    public TradeWithUserRequest(String name, User user, String tradingPartner) {
        super(name, user);
        this.respondingUser = tradingPartner;
    }

    /**
     * Gets The username of the trading partner
     *
     * @return The username of the trading partner
     */
    public String getRespondingUser() {
        return respondingUser;
    }
}
