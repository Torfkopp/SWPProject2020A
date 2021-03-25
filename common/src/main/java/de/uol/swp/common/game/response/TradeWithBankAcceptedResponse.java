package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Response of the server is sent to the lobby where a trade with the bank
 * happened if it was successful.
 *
 * @author Alwin Bossert
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-21
 */
public class TradeWithBankAcceptedResponse extends AbstractLobbyResponse {

    private final UserOrDummy user;

    /**
     * Constructor
     *
     * @param user      user who had a successful trade with the bank
     * @param lobbyName name of the lobby where the trade happened
     */
    public TradeWithBankAcceptedResponse(UserOrDummy user, String lobbyName) {
        super(lobbyName);
        this.user = user;
    }

    /**
     * Gets the user who had a successful trade
     *
     * @return The User who had a successful trade
     */
    public UserOrDummy getUser() {
        return user;
    }
}
