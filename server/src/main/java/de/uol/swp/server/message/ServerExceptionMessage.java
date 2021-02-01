package de.uol.swp.server.message;

/**
 * This message is used if something went wrong
 * <p>
 * This ServerMessage is used if something went wrong, e.g. the login process fails
 *
 * @author Marco Grawunder
 * @see de.uol.swp.server.message.AbstractServerInternalMessage
 * @see de.uol.swp.server.usermanagement.AuthenticationService#onLoginRequest
 * @since 2019-08-07
 */
public class ServerExceptionMessage extends AbstractServerInternalMessage {

    private final Exception e;

    /**
     * Constructor
     *
     * @param e The exception that is the reason for the creation of this
     *
     * @since 2019-08-07
     */
    public ServerExceptionMessage(Exception e) {
        super();
        this.e = e;
    }

    /**
     * Gets the Exception
     *
     * @return Exception passed in the constructor
     *
     * @since 2019-08-07
     */
    public Exception getException() {
        return e;
    }
}
