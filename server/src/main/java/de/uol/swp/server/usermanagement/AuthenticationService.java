package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.*;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.common.user.response.KillOldClientResponse;
import de.uol.swp.common.user.response.NukeUsersSessionsResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.communication.UUIDSession;
import de.uol.swp.server.message.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.*;

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

    /**
     * The list of all currently logged in users
     */
    private final Map<Session, User> userSessions = new HashMap<>();

    private final IUserManagement userManagement;

    /**
     * Constructor
     *
     * @param bus            The EventBus used throughout the entire server
     * @param userManagement Object of the UserManagement to use
     *
     * @see de.uol.swp.server.usermanagement.UserManagement
     * @since 2019-08-30
     */
    @Inject
    public AuthenticationService(EventBus bus, UserManagement userManagement) {
        super(bus);
        this.userManagement = userManagement;
    }

    /**
     * Searches the session for a given user
     *
     * @param user User whose session is to be searched
     *
     * @return Either an empty Optional or an Optional containing the session
     *
     * @see de.uol.swp.common.user.Session
     * @see de.uol.swp.common.user.User
     * @since 2019-09-04
     */
    public Optional<Session> getSession(User user) {
        Optional<Map.Entry<Session, User>> entry = userSessions.entrySet().stream()
                                                               .filter(e -> e.getValue().equals(user)).findFirst();
        return entry.map(Map.Entry::getKey);
    }

    /**
     * Searches the sessions for a set of given users
     *
     * @param users Set of users whose sessions are to be searched
     *
     * @return List containing the sessions that where found
     *
     * @see de.uol.swp.common.user.Session
     * @see de.uol.swp.common.user.User
     * @since 2019-10-08
     */
    public List<Session> getSessions(Set<User> users) {
        List<Session> sessions = new ArrayList<>();
        users.forEach(u -> {
            Optional<Session> session = getSession(u);
            session.ifPresent(sessions::add);
        });
        return sessions;
    }

    /**
     * Handles a LoginRequest found on the EventBus
     * <p>
     * If a LoginRequest is detected on the EventBus, this method is called.
     * It tries to login a user via the UserManagement.
     * If this succeeds, the user and his session are stored in the userSessions map,
     * and a ClientAuthorisedMessage is posted onto the EventBus. Additionally if
     * the user already had a session registered the oldSession attribute of the
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received LoginRequest for User " + msg.getUsername());
        }
        ServerInternalMessage returnMessage;
        try {
            User newUser = userManagement.login(msg.getUsername(), msg.getPassword());
            if (userSessions.containsValue(newUser)) {
                returnMessage = new ClientAuthorisedMessage(newUser, true);
            } else {
                returnMessage = new ClientAuthorisedMessage(newUser, false);
            }
            Session newSession = UUIDSession.create(newUser);
            userSessions.put(newSession, newUser);
            returnMessage.setSession(newSession);
        } catch (Exception e) {
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
        if (msg.getSession().isPresent()) {
            Session session = msg.getSession().get();
            User userToLogOut = userSessions.get(session);
            // Could be already logged out
            if (userToLogOut != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("---- Logging out user " + userToLogOut.getUsername());
                }
                userManagement.logout(userToLogOut);
                userSessions.remove(session);
                Message returnMessage = new UserLoggedOutMessage(userToLogOut.getUsername());
                post(returnMessage);
            }
        }
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
     * @see de.uol.swp.common.user.request.NukeUsersSessionsRequest
     * @see de.uol.swp.common.user.response.NukeUsersSessionsResponse
     * @author Eric Vuong
     * @author Marvin Drees
     * @since 2021-03-03
     */
    @Subscribe
    private void onNukeUsersSessionsRequest(NukeUsersSessionsRequest req) {
        LOG.debug("Received NukeUsersSessionsRequest");
        if (req.getUser() == null) return;
        User userToLogOut = req.getUser();
        LOG.debug("---- Logging out user " + userToLogOut.getUsername());
        userManagement.logout(userToLogOut);
        while (getSession(userToLogOut).isPresent()) {
            post(new FetchUserContextInternalRequest(getSession(userToLogOut).get(), new KillOldClientResponse()));
            userSessions.remove(getSession(userToLogOut).get());
        }
        post(new UserLoggedOutMessage(userToLogOut.getUsername()));
        NukeUsersSessionsResponse response = new NukeUsersSessionsResponse();
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
        Message response = new AllOnlineUsersResponse(userSessions.values());
        response.initWithMessage(msg);
        post(response);
    }
}
