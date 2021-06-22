package de.uol.swp.common.lobby.message;

import de.uol.swp.common.game.map.configuration.IConfiguration;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.specialisedUtil.UserOrDummyColourMap;
import de.uol.swp.common.specialisedUtil.UserOrDummyPlayerMap;
import de.uol.swp.common.specialisedUtil.UserOrDummySet;
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

    private final UserOrDummyPlayerMap userOrDummyPlayerMap;
    private final UserOrDummyColourMap userOrDummyColourMap;
    private final IConfiguration configuration;
    private final boolean startUpPhaseEnabled;
    private final UserOrDummySet playerList;

    /**
     * Constructor
     *
     * @param name                 The Name of the Lobby
     * @param user                 The User who started the Session
     * @param configuration        The field configuration used in the game
     * @param startUpPhaseEnabled  Whether the game has the startup phase enabled
     * @param userOrDummyPlayerMap The Map with userOrDummyPlayer
     * @param userOrDummyColourMap The Map with userOrDummyColour
     * @param playerList           List of the players order
     */
    public StartSessionMessage(LobbyName name, UserOrDummy user, IConfiguration configuration,
                               boolean startUpPhaseEnabled, UserOrDummyPlayerMap userOrDummyPlayerMap,
                               UserOrDummyColourMap userOrDummyColourMap, UserOrDummySet playerList) {
        super(name, user);
        this.configuration = configuration;
        this.startUpPhaseEnabled = startUpPhaseEnabled;
        this.userOrDummyPlayerMap = userOrDummyPlayerMap;
        this.userOrDummyColourMap = userOrDummyColourMap;
        this.playerList = playerList;
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
     * Gets the player list
     *
     * @return The order of the players in the game
     *
     * @author Maximilian Lindner
     * @since 2021-06-11
     */
    public UserOrDummySet getPlayerList() {
        return playerList;
    }

    /**
     * Gets the Map of UserOrDummies and Colours
     */
    public UserOrDummyColourMap getUserOrDummyColourMap() {
        return userOrDummyColourMap;
    }

    /**
     * Gets the Map of UserOrDummies and Players
     */
    public UserOrDummyPlayerMap getUserOrDummyPlayerMap() {
        return userOrDummyPlayerMap;
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
