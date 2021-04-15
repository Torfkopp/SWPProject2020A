package de.uol.swp.common.game.request;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.lobby.request.AbstractLobbyRequest;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Request is sent to the Server to get the Inventory
 * of the player who wants to trade with the bank.
 *
 * @author Alwin Bossert
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @since 2021-02-21
 */
public class TradeWithBankRequest extends AbstractLobbyRequest {

    /**
     * Constructor
     *
     * @param name The Name of the lobby
     * @param user The User who wants to start a game session
     */
    public TradeWithBankRequest(LobbyName name, UserOrDummy user) {
        super(name, user);
    }
}