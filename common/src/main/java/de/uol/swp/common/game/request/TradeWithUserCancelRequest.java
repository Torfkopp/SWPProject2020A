package de.uol.swp.common.game.request;

/**
 * Request is sent to the Server to notify the responding user that
 * the trade was canceled by the offering user.
 *
 * @author Alwin Bossert
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @since 2021-03-01
 */
public class TradeWithUserCancelRequest extends AbstractGameRequest {

    private final String respondingUser;

    /**
     * Constructor
     *
     * @param originLobby    The lobby where the trade was opened
     * @param respondingUser The user to whom the offer was made
     */
    public TradeWithUserCancelRequest(String originLobby, String respondingUser) {
        super(originLobby);
        this.respondingUser = respondingUser;
    }

    /**
     * Gets the responding user.
     *
     * @return The name of the user to whom the offer was made
     */
    public String getRespondingUser() {
        return respondingUser;
    }
}
