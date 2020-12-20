package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request for initialising the user list in the client
 *
 * This message is sent during the initialization of the user list. The server will
 * respond with a AllLobbyMembersResponse.
 *
 * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
 * @author Marco Grawunder
 * @since 2019-08-07
 */
public class RetrieveAllLobbyMembersRequest extends AbstractRequestMessage {
}
