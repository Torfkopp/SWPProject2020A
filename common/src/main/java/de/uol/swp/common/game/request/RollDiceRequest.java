package de.uol.swp.common.game.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Request sent to the server when a user wants to roll the dices
 *
 * @author Mario Fokken
 * @author Sven Ahrens
 * @since 2021-02-22
 */
public class RollDiceRequest extends AbstractGameRequest {

    private final Actor user;

    public RollDiceRequest(Actor user, LobbyName originLobby) {
        super(originLobby);
        this.user = user;
    }

    public Actor getActor() {
        return user;
    }
}
