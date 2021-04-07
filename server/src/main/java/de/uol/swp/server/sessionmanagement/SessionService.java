package de.uol.swp.server.sessionmanagement;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.server.AbstractService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mapping EventBus calls to SessionManagement calls
 * <p>
 * (Currently none are implemented!)
 *
 * @author Eric Vuong
 * @author Marvin Drees
 * @see de.uol.swp.server.AbstractService
 * @since 2021-04-07
 */

public class SessionService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(SessionService.class);

    private final ISessionManagement sessionManagement;

    /**
     * Constructor
     *
     * @param eventBus          The EventBus used throughout the entire server (injected)
     * @param sessionManagement Object of the SessionManagement to use
     *
     * @see de.uol.swp.server.sessionmanagement.SessionManagement
     * @since 2021-04-07
     */
    @Inject
    public SessionService(EventBus eventBus, SessionManagement sessionManagement) {
        super(eventBus);
        if (LOG.isDebugEnabled()) LOG.debug("SessionService started");
        this.sessionManagement = sessionManagement;
    }
}
