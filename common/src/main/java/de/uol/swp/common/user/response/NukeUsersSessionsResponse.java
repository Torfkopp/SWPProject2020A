package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * A response from server to client to confirm successful session removal
 * <p>
 * This response gets sent to new client whose users old sessions have been
 * successfully removed from the session store to allow a new login.
 *
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @author Eric Vuong
 * @author Marvin Drees
 * @since 2021-03-03
 */
public class NukeUsersSessionsResponse extends AbstractResponseMessage {}
