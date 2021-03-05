package de.uol.swp.server.game.event;

import de.uol.swp.common.lobby.request.KickUserRequest;
import de.uol.swp.server.message.AbstractServerInternalMessage;

/**
 * This event is sent to the LobbyService if there is no
 * active game in the lobby of the request.
 *
 * @author Maximilian Lindner
 * @author Sven Ahrens
 * @see de.uol.swp.common.lobby.request.KickUserRequest
 * @since 2021-03-02
 */
public class KickUserEvent extends AbstractServerInternalMessage {

    private final KickUserRequest request;

    /**
     * Constructor
     *
     * @param request The kickUserRequest found on the EventBus
     */
    public KickUserEvent(KickUserRequest request) {
        this.request = request;
    }

    /**
     * Gets the KickUserRequest
     *
     * @return KickUserRequest found on the EventBus
     */
    public KickUserRequest getRequest() {
        return request;
    }
}
