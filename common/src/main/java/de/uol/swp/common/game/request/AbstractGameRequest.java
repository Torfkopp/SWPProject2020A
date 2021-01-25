package de.uol.swp.common.game.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Base class of all game requests.
 * <p>
 * This class abstracts away the originLobby attribute
 * needed for checking which lobby the request originated from and where the
 * response or message should be sent to.
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @see AbstractRequestMessage
 * @since 2021-01-15
 */
public class AbstractGameRequest extends AbstractRequestMessage {
    private final String originLobby;

    /**
     * Constructor
     * <p>
     * This constructor sets the originLobby to the parameter provided when
     * calling the constructor.
     *
     * @param originLobby The Lobby from which a request originated from
     */
    public AbstractGameRequest(String originLobby) {
        this.originLobby = originLobby;
    }

    /**
     * Getter for the lobbyName attribute
     *
     * @return The name of the Lobby the EndTurnRequest originated from.
     */
    public String getOriginLobby() {
        return originLobby;
    }
}
