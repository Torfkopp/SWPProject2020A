package de.uol.swp.common.game.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Request sent to the server when a user wants to buy a random developmentCard
 *
 * @author Maximilian Lindner
 * @author Alwin Bossert
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-02-22
 */
public class BuyDevelopmentCardRequest extends AbstractGameRequest {

    private final Actor user;

    /**
     * Constructor
     *
     * @param user        The User wanting to buy a developmentCard
     * @param originLobby The Lobby from which a request originated from
     */
    public BuyDevelopmentCardRequest(Actor user, LobbyName originLobby) {
        super(originLobby);
        this.user = user;
    }

    /**
     * Gets the user attribute
     *
     * @return The user who wants to buy a DevelopmentCard
     */
    public Actor getActor() {
        return user;
    }
}
