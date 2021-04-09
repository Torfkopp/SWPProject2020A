package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserOrDummy;

public class CheckForGameRequest extends AbstractGameRequest{
private final UserOrDummy user;
    /**
     * Constructor
     * <p>
     * This constructor sets the originLobby to the parameter provided when
     * calling the constructor.
     *
     * @param originLobby The Lobby from which a request originated from
     */
    public CheckForGameRequest(String originLobby, UserOrDummy user) {
        super(originLobby);
        this.user = user;
    }
}
