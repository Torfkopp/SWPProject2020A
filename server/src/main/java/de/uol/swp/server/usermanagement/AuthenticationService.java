package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.sessions.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.message.ClientAuthorisedMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import de.uol.swp.server.message.ServerInternalMessage;
import de.uol.swp.server.sessionmanagement.ISessionManagement;
import de.uol.swp.server.sessionmanagement.SessionManagementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;

/**
 * Mapping authentication's EventBus calls to UserManagement calls
 *
 * @author Marco Grawunder
 * @see de.uol.swp.server.AbstractService
 * @since 2019-08-30
 */
@SuppressWarnings("UnstableApiUsage")
public class AuthenticationService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(AuthenticationService.class);
    private final IUserManagement userManagement;
    private final ISessionManagement sessionManagement;

    /**
     * Constructor
     *
     * @param bus               The EventBus used throughout the entire server
     * @param userManagement    Object of the UserManagement to use
     * @param sessionManagement Object of the SessionManagement to use
     *
     * @see de.uol.swp.server.usermanagement.UserManagement
     * @since 2019-08-30
     */
    @Inject
    public AuthenticationService(EventBus bus, IUserManagement userManagement, ISessionManagement sessionManagement) {
        super(bus);
        this.userManagement = userManagement;
        this.sessionManagement = sessionManagement;
        LOG.debug("AuthenticationService started");
    }

    /**
     * Handles a LoginRequest found on the EventBus
     * <p>
     * If a LoginRequest is detected on the EventBus, this method is called.
     * It checks if the user is logged in via the UserManagement.
     * If this succeeds, the user and his newly created session are stored
     * in the userSessions map and a ClientAuthorisedMessage is posted onto the EventBus.
     * Additionally if the user was already logged in, the oldSession attribute of the
     * ClientAuthorizedMessage gets set to true, otherwise to false.
     * When login fails, a ServerExceptionMessage gets posted there.
     *
     * @param msg The LoginRequest
     *
     * @see de.uol.swp.common.user.request.LoginRequest
     * @see de.uol.swp.server.message.ClientAuthorisedMessage
     * @see de.uol.swp.server.message.ServerExceptionMessage
     * @since 2019-08-30
     */
    @Subscribe
    private void onLoginRequest(LoginRequest msg) {
        String username = msg.getUsername();
        LOG.debug("Received LoginRequest for User {}", username);
        ServerInternalMessage returnMessage;
        try {
            if (userManagement.isLoggedIn(username)) {
                LoginException e = new LoginException("User [" + username + "] already logged in");
                returnMessage = new ServerExceptionMessage(e);
                LOG.debug("Sending ServerExceptionMessage [{}]", e.getMessage());
            } else {
                User newUser = userManagement.login(username, msg.getPassword());
                returnMessage = new ClientAuthorisedMessage(newUser);
                returnMessage.setSession(sessionManagement.createSession(newUser));
                LOG.debug("Sending ClientAuthorisedMessage for User {}", username);
            }
        } catch (SecurityException e) {
            LOG.error(e);
            LoginException e1 = new LoginException("Cannot auth user " + username);
            returnMessage = new ServerExceptionMessage(e1);
            LOG.debug("Sending ServerExceptionMessage [{}]", e1.getMessage());
        }
        returnMessage.initWithMessage(msg);
        post(returnMessage);
    }

    /**
     * Handles a LogoutRequest found on the EventBus
     * <p>
     * If a LogoutRequest is detected on the EventBus, this method is called.
     * It tries to log out a user via the UserManagement. If this succeeds,
     * the user and his Session are removed from the userSessions Map,
     * and a UserLoggedOutMessage is posted onto the EventBus.
     *
     * @param msg The LogoutRequest
     *
     * @see de.uol.swp.common.user.request.LogoutRequest
     * @see de.uol.swp.common.user.message.UserLoggedOutMessage
     * @since 2019-08-30
     */
    @Subscribe
    private void onLogoutRequest(LogoutRequest msg) {
        LOG.debug("Received LogoutRequest");
        if (msg.getSession().isEmpty()) return;
        Session session = msg.getSession().get();
        User userToLogOut = session.getUser();
        // Could be already logged out
        if (userToLogOut == null) return;
        LOG.debug("---- Logging out User {}", userToLogOut.getUsername());
        userManagement.logout(userToLogOut);
        try {
            sessionManagement.removeSession(session);
        } catch (SessionManagementException e) {
            LOG.error(e);
        }
        Message returnMessage = new UserLoggedOutMessage(userToLogOut.getUsername());
        LOG.debug("Sending UserLoggedOutMessage");
        post(returnMessage);
    }

    /**
     * Handles a RetrieveAllOnlineUsersRequest found on the EventBus
     * <p>
     * If a RetrieveAllOnlineUsersRequest is detected on the EventBus, this method
     * is called. It posts an AllOnlineUsersResponse containing User objects for
     * every logged in user on the EvenBus.
     *
     * @param msg RetrieveAllOnlineUsersRequest found on the EventBus
     *
     * @see de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest
     * @see de.uol.swp.common.user.response.AllOnlineUsersResponse
     * @since 2019-08-30
     */
    @Subscribe
    private void onRetrieveAllOnlineUsersRequest(RetrieveAllOnlineUsersRequest msg) {
        LOG.debug("Received RetrieveAllOnlineUsersRequest");
        Message response = new AllOnlineUsersResponse(sessionManagement.getAllUsers());
        response.initWithMessage(msg);
        LOG.debug("Sending AllOnlineUsersResponse");
        post(response);
    }
}
