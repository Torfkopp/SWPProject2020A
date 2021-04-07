package de.uol.swp.server.sessionmanagement;

/**
 * Exception thrown in SessionManagement
 * This exception is thrown if someone wants to remove a not existing session
 * or tries to add a new session to the active session, when the session is already
 * bound to connection.
 *
 * @author Eric Vuong
 * @author Marvin Drees
 * @see de.uol.swp.server.sessionmanagement.SessionManagement
 * @since 2021-04-07
 */
public class SessionManagementException extends RuntimeException {

    /**
     * Constructor
     *
     * @param s ExceptionMessage
     */
    SessionManagementException(String s) {
        super(s);
    }
}
