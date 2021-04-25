package de.uol.swp.server.sessionmanagement;

import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.sessions.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

import java.util.*;

/**
 * An interface for all methods of the server´s SessionManagement
 *
 * @author Eric Vuong
 * @author Marvin Drees
 * @since 2021-04-07
 */
public interface ISessionManagement {

    /**
     * Create a new session
     *
     * @param user The user who creates a new Session
     *
     * @return The newly created Session
     */
    Session createSession(User user);

    /**
     * Returns a collection of all users found in the store
     *
     * @return Collection of users
     */
    Collection<User> getAllUsers();

    /**
     * Gets MessageContext for a specified Session
     *
     * @param session Session to get ctx from
     *
     * @return Optional Object containing the MessageContext if there is any
     *
     * @see de.uol.swp.common.sessions.Session
     * @see de.uol.swp.common.message.MessageContext
     */
    Optional<MessageContext> getCtx(Session session);

    /**
     * Gets a session from a provided User
     *
     * @param user The User of a Session
     *
     * @return The Session of the User
     */
    Optional<Session> getSession(UserOrDummy user);

    /**
     * Gets the session for a given MessageContext
     *
     * @param ctx The MessageContext
     *
     * @return Optional Object containing the session if found
     *
     * @see de.uol.swp.common.sessions.Session
     * @see de.uol.swp.common.message.MessageContext
     */
    Optional<Session> getSession(MessageContext ctx);

    /**
     * Gets a list of sessions from a set of users
     *
     * @param users Set of users to return sessions of
     *
     * @return List of sessions from provided users
     */
    List<Session> getSessions(Set<User> users);

    /**
     * Checks if a session is in the session store
     *
     * @param session Session to check
     *
     * @return Boolean whether key exists
     */
    boolean hasSession(Session session);

    /**
     * Checks if a user is in the session store
     *
     * @param user User to check
     *
     * @return Boolean whether value exists
     */
    boolean hasSession(User user);

    /**
     * Adds a new session to the active sessions
     *
     * @param ctx     The MessageContext belonging to the session
     * @param session The Session to add
     */
    void putSession(MessageContext ctx, Session session);

    /**
     * Removes a session
     *
     * @param session The Session to remove
     */
    void removeSession(Session session);

    /**
     * Removes a session for a given message context
     *
     * @param ctx MessageContext to remove session from
     */
    void removeSession(MessageContext ctx);
}
