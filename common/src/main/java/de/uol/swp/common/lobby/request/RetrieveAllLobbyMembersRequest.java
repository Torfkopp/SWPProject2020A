package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request for initialising the user list in the client
 *
 * This message is sent during the initialisation of the user list.
 * The server will respond with an AllLobbyMembersResponse.
 *
 * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
 * @author Alwin Bossert
 * @author Steven Luong
 * @since 2020-12-20
 */
public class RetrieveAllLobbyMembersRequest extends AbstractRequestMessage {

    private String lobbyName;

    public RetrieveAllLobbyMembersRequest() {
    }

    public RetrieveAllLobbyMembersRequest(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }
}
