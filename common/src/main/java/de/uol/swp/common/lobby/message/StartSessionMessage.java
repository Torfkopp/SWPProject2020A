package de.uol.swp.common.lobby.message;

import de.uol.swp.common.game.map.configuration.IConfiguration;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

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

    private final IConfiguration configuration;
    private final boolean startUpPhaseEnabled;

    /**
     * Constructor
     *
     * @param name                The Name of the Lobby
     * @param user                The User who started the Session
     * @param configuration       The field configuration used in the game
     * @param startUpPhaseEnabled Whether the game has the startup phase enabled
     */
    public StartSessionMessage(LobbyName name, UserOrDummy user, IConfiguration configuration,
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
     * @implNote The Lists contained will be read-only and ordered, so create
     * new LinkedList objects with the results
     * @see de.uol.swp.common.game.map.configuration.IConfiguration
     * @since 2021-03-18
     */
    public IConfiguration getConfiguration() {
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
