package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request for initialising the user list in the client
 * <p>
 * This message is sent during the initialization of the user list. The server will
 * respond with a AllLobbyMembersResponse.
 *
 * @author Alwin Bossert
 * @author Steven Luong
 * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
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
