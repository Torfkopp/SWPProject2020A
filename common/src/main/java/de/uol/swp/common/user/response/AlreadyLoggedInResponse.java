package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.Session;

import java.util.Optional;

/**
 * A message containing a session (typically for a new logged in user)
 * <p>
 * This response is sent to the client whose LoginRequest was successful
 *
 * @author Marvin Drees
 * @author Eric Vuong
 * @see de.uol.swp.common.user.request.LoginRequest
 * @see Session
 * @see AbstractResponseMessage
 * @since 2021-03-02
 */
public class AlreadyLoggedInResponse extends AbstractResponseMessage {

    private final Session oldSession;

    /**
     * Constructor
     *
     * @param oldSession The already logged in session
     */
    public AlreadyLoggedInResponse(Session oldSession) {
        this.oldSession = oldSession;
    }

    public Optional<Session> getLoggedInSession() {
        return Optional.of(oldSession);
    }
}
