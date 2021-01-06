package de.uol.swp.common.user;

/**
 * Interface for different kinds of user objects.
 * <p>
 * This interface is for unifying different kinds of user objects throughout the project.
 * With this being the base project it is currently only used for the UUIDSession
 * objects within the server.
 *
 * @author Marco Grawunder
 * @since 2019-08-05
 */
public interface Session {

    /**
     * Gets the SessionID
     *
     * @return ID of the session as a string
     * @since 2019-08-05
     */
    String getSessionId();

    /**
     * Gets the user using the session
     *
     * @return The session's user as an object implementing the user
     * @see de.uol.swp.common.user.User
     * @since 2019-08-13
     */
    User getUser();
}
