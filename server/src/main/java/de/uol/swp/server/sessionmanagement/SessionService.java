package de.uol.swp.server.sessionmanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.sessions.Session;
import de.uol.swp.common.user.Computer;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.event.ForwardToUserInternalRequest;
import de.uol.swp.server.message.FetchUserContextInternalRequest;
import de.uol.swp.server.message.ReplaceUserSessionInternalRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * Mapping EventBus calls to SessionManagement calls
 * <p>
 *
 * @author Eric Vuong
 * @author Marvin Drees
 * @see de.uol.swp.server.AbstractService
 * @since 2021-04-07
 */
@SuppressWarnings("UnstableApiUsage")
public class SessionService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(SessionService.class);

    private final ISessionManagement sessionManagement;

    /**
     * Constructor
     *
     * @param eventBus          The EventBus used throughout the entire server (injected)
     * @param sessionManagement Object of the SessionManagement to use
     *
     * @see de.uol.swp.server.sessionmanagement.ISessionManagement
     * @since 2021-04-07
     */
    @Inject
    public SessionService(EventBus eventBus, ISessionManagement sessionManagement) {
        super(eventBus);
        this.sessionManagement = sessionManagement;
        LOG.debug("SessionService started");
    }

    /**
     * Handles a ForwardToUserInternalRequest found on the EventBus
     * <p>
     * If a ForwardToUserInternalRequest is found on the EventBus this
     * method gets the Session of the User contained in the ForwardToUserInternalRequest.
     * Then it posts a FetchUserContextInternalRequest with the session of the
     * User and .the ResponseMessage contained in the ForwardToUserInternalRequest,
     * which will be handled by the ServerHandler.
     *
     * @param event ForwardToUserInternalRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.server.game.event.ForwardToUserInternalRequest
     * @see de.uol.swp.server.message.FetchUserContextInternalRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onForwardToUserInternalRequest(ForwardToUserInternalRequest event) {
        LOG.debug("Received ForwardToUserInternalRequest");
        Optional<Session> session = sessionManagement.getSession(event.getTargetUser());
        if (event.getTargetUser() instanceof Computer)
            LOG.debug("Ignoring ForwardToUserInternalRequest because it was sent to a Computer");
        else if (session.isEmpty()) LOG.error(new RuntimeException("UserSession not found"));
        else {
            LOG.debug("Sending FetchUserContextInternalRequest");
            post(new FetchUserContextInternalRequest(session.get(), event.getResponseMessage()));
        }
    }

    /**
     * Handles a ReplaceUserSessionInternalRequest found on the EventBus
     * <p>
     * If a ReplaceUserSessionInternalRequest is found on the EventBus this
     * method replaces the old User of the current Session with the new User
     * contained in the ReplaceUserSessionInternalRequest.
     *
     * @param req ReplaceUserSessionInternalRequest found on the EventBus
     *
     * @author Steven Luong
     * @see de.uol.swp.server.message.ReplaceUserSessionInternalRequest
     * @since 2021-07-05
     */
    @Subscribe
    private void onReplaceUserSessionInternalRequest(ReplaceUserSessionInternalRequest req) {
        LOG.debug("Received ReplaceUserSessionInternalRequest");
        sessionManagement.replaceUser(req.getCurrentSession(), req.getNewUser());
    }
}
