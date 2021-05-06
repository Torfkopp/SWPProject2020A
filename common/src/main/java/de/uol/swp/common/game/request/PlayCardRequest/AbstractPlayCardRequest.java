package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

/**
 * Abstract class for all PlayCard requests
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
abstract class AbstractPlayCardRequest extends AbstractGameRequest {

    private final User user;

    /**
     * Constructor.
     *
     * @param originLobby The origin lobby
     * @param user        The user
     */
    public AbstractPlayCardRequest(LobbyName originLobby, User user) {
        super(originLobby);
        this.user = user;
    }

    /**
     * Gets the user.
     *
     * @return The user
     */
    public User getUser() {
        return user;
    }
}
