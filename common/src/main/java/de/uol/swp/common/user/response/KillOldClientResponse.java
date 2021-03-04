package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * A response from server to client to make it fall back to the login screen
 * <p>
 * This response is sent to the client whose session get logged out
 * by a different client so it no longer remains logged in.
 *
 * @author Marvin Drees
 * @author Eric Vuong
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2021-03-02
 */
public class KillOldClientResponse extends AbstractResponseMessage {}
