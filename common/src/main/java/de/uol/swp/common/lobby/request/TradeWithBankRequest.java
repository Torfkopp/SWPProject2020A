package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.User;

/**
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
    public TradeWithBankRequest(String name, User user) {
        super(name, user);
    }
}