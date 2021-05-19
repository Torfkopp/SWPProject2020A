package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.sessions.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.*;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.common.user.response.KillOldClientResponse;
import de.uol.swp.common.user.response.NukedUsersSessionsResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.message.*;
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
        LOG.debug("Received LoginRequest for User {}", msg.getUsername());
        ServerInternalMessage returnMessage;
        try {
            if (userManagement.isLoggedIn(msg.getUsername())) {
                // Don't need isPresent check here as it is implied by isLoggedIn
                returnMessage = new ClientAuthorisedMessage(userManagement.getUser(msg.getUsername()).get(), true);
            } else {
                User newUser = userManagement.login(msg.getUsername(), msg.getPassword());
                returnMessage = new ClientAuthorisedMessage(newUser, false);
                returnMessage.setSession(sessionManagement.createSession(newUser));
            }
        } catch (SecurityException e) {
            LOG.error(e);
            returnMessage = new ServerExceptionMessage(new LoginException("Cannot auth user " + msg.getUsername()));
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
        post(returnMessage);
    }

    /**
     * Handles a NukeUsersSessionsRequest found on the EventBus
     * <p>
     * If a NukeUsersSessionsRequest is detected on the EventBus, this method is called.
     * It takes the user from received request and logs it out via UserManagement.
     * After that it cycles through the session store and removes all sessions matching
     * that particular user along with sending a KillOldClientResponse to log the old
     * client out. At last a NukeUsersSessionsResponse is posted on the EventBus
     * as a confirmation for the requesting client.
     *
     * @param req NukeUsersSessionsRequest found on the EventBus
     *
     * @author Eric Vuong
     * @author Marvin Drees
     * @see de.uol.swp.common.user.request.NukeUsersSessionsRequest
     * @see de.uol.swp.common.user.response.NukedUsersSessionsResponse
     * @since 2021-03-03
     */
    @Subscribe
    private void onNukeUsersSessionsRequest(NukeUsersSessionsRequest req) {
        LOG.debug("Received NukeUsersSessionsRequest");
        if (req.getUser() == null) return;
        User userToLogOut = req.getUser();
        LOG.debug("---- Logging out User {}", userToLogOut.getUsername());
        userManagement.logout(userToLogOut);
        // With the new logic there should only ever be one session but keep this to be safe.
        while (sessionManagement.getSession(userToLogOut).isPresent()) {
            post(new FetchUserContextInternalRequest(sessionManagement.getSession(userToLogOut).get(),
                                                     new KillOldClientResponse()));
            try {
                sessionManagement.removeSession(sessionManagement.getSession(userToLogOut).get());
            } catch (SessionManagementException e) {
                LOG.error(e);
            }
        }
        post(new UserLoggedOutMessage(userToLogOut.getUsername()));
        ResponseMessage response = new NukedUsersSessionsResponse(userToLogOut);
        response.initWithMessage(req);
        post(response);
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
        Message response = new AllOnlineUsersResponse(sessionManagement.getAllUsers());
        response.initWithMessage(msg);
        post(response);
    }
}
