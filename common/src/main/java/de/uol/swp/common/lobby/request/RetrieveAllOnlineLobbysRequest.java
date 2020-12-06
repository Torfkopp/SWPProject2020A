package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request for initialising the lobby list in the client
 *
 * This message is sent during the initialization of the lobby list. The server will
 * respond with a AllOnlineLobbysResponse.
 *
 * @see de.uol.swp.common.lobby.response.AllOnlineLobbysResponse
 * @since 2020-12-05
 */

public class RetrieveAllOnlineLobbysRequest extends AbstractRequestMessage {
}
