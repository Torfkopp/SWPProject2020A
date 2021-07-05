package de.uol.swp.server.sessionmanagement;

import de.uol.swp.common.sessions.Session;
import de.uol.swp.common.user.User;

import java.util.Objects;
import java.util.UUID;

/**
 * Class used to store connected clients and users in an identifiable way
 *
 * @author Marco Grawunder
 * @see de.uol.swp.server.usermanagement.AuthenticationService
 * @see de.uol.swp.common.sessions.Session
 * @since 2017-03-17
 */
public class UUIDSession implements Session {

    private final String sessionId;
    private User user;

    /**
     * Private constructor
     *
     * @param user The user connected to the session
     *
     * @since 2017-03-17
     */
    private UUIDSession(User user) {
        synchronized (UUIDSession.class) {
            this.sessionId = String.valueOf(UUID.randomUUID());
            this.user = user;
        }
    }

    /**
     * Builder for the UUIDSession
     * <p>
     * Builder exposed to every class in the server. Used because the constructor is private
     *
     * @param user The user connected to the session
     *
     * @return T new UUIDSession object for the user
     *
     * @since 2019-08-07
     */
    public static Session create(User user) {
        return new UUIDSession(user);
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void replaceUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UUIDSession session = (UUIDSession) o;
        return Objects.equals(sessionId, session.sessionId);
    }

    @Override
    public String toString() {
        return "SessionId: " + sessionId;
    }
}
