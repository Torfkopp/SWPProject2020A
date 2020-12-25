package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request for initialising the user list in the client
 *
 * This message is sent during the initialisation of the user list.
 * The server will respond with an AllOnlineUsersResponse.
 *
 * @see de.uol.swp.common.user.response.AllOnlineUsersResponse
 * @author Marco Grawunder
 * @since 2019-08-07
 */
public class RetrieveAllOnlineUsersRequest extends AbstractRequestMessage {
}
