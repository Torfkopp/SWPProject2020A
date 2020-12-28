package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request for initialising the lobbies in the client
 * <p>
 * This message is sent during the initialisation of the lobby list.
 * The server will respond with a AllLobbiesResponse
 *
 * @author Mario
 * @see de.uol.swp.common.lobby.response.AllLobbiesResponse
 * @since 2020-12-12
 */
public class RetrieveAllLobbiesRequest extends AbstractRequestMessage {
}
