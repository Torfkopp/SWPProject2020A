package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;

import java.util.List;
import java.util.Map;

/**
 * Message sent by the server when a game session was started.
 *
 * @author Eric Vuong
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.lobby.request.StartSessionRequest
 * @since 2021-01-21
 */
public class StartSessionMessage extends AbstractLobbyMessage {

    private final Map<String, List<Object>> configuration;
    private final boolean startUpPhaseEnabled;

    /**
     * Constructor
     *
     * @param name                The Name of the Lobby
     * @param user                The User who started the Session
     * @param configuration       The field configuration used in the game
     * @param startUpPhaseEnabled Whether the game has the startup phase enabled
     */
    public StartSessionMessage(String name, User user, Map<String, List<Object>> configuration,
                               boolean startUpPhaseEnabled) {
        super(name, user);
        this.configuration = configuration;
        this.startUpPhaseEnabled = startUpPhaseEnabled;
    }

    /**
     * Gets the field configuration used in the game
     *
     * @return The field configuration used in the game
     *
     * @author Finn Haase
     * @author Phillip-André Suhr
     * @since 2021-03-18
     */
    public Map<String, List<Object>> getConfiguration() {
        return configuration;
    }

    /**
     * Whether the game has the startup phase enabled or not
     *
     * @return true if the startup phase is enabled, false if not
     *
     * @author Finn Haase
     * @author Phillip-André Suhr
     * @since 2021-03-18
     */
    public boolean isStartUpPhaseEnabled() {
        return startUpPhaseEnabled;
    }
}
