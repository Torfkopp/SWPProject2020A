package de.uol.swp.common.lobby.request;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request for initialising the lobby member list in the client
 * <p>
 * This message is sent during the initialisation of the member list.
 * The server will respond with an AllLobbyMembersResponse.
 *
 * @author Alwin Bossert
 * @author Steven Luong
 * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
 * @since 2020-12-20
 */
public class RetrieveAllLobbyMembersRequest extends AbstractRequestMessage {

    private final LobbyName lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName The name of the lobby for which to retrieve the list of members
     */
    public RetrieveAllLobbyMembersRequest(LobbyName lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the lobby name
     *
     * @return The lobby name
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }
}
