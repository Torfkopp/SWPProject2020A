package de.uol.swp.common.lobby.message;

import de.uol.swp.common.game.map.configuration.IConfiguration;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.specialisedUtil.ActorColourMap;
import de.uol.swp.common.specialisedUtil.ActorPlayerMap;
import de.uol.swp.common.specialisedUtil.ActorSet;
import de.uol.swp.common.user.Actor;

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

    private final ActorPlayerMap actorPlayerMap;
    private final ActorColourMap actorColourMap;
    private final IConfiguration configuration;
    private final boolean startUpPhaseEnabled;
    private final ActorSet playerList;

    /**
     * Constructor
     *
     * @param name                The Name of the Lobby
     * @param user                The User who started the Session
     * @param configuration       The field configuration used in the game
     * @param startUpPhaseEnabled Whether the game has the startup phase enabled
     * @param actorPlayerMap      The Map with actorPlayer
     * @param actorColourMap      The Map with actorColour
     * @param playerList          List of the players order
     */
    public StartSessionMessage(LobbyName name, Actor user, IConfiguration configuration, boolean startUpPhaseEnabled,
                               ActorPlayerMap actorPlayerMap, ActorColourMap actorColourMap, ActorSet playerList) {
        super(name, user);
        this.configuration = configuration;
        this.startUpPhaseEnabled = startUpPhaseEnabled;
        this.actorPlayerMap = actorPlayerMap;
        this.actorColourMap = actorColourMap;
        this.playerList = playerList;
    }

    /**
     * Gets the Map of Actors and Colours
     */
    public ActorColourMap getActorColourMap() {
        return actorColourMap;
    }

    /**
     * Gets the Map of Actors and Players
     */
    public ActorPlayerMap getActorPlayerMap() {
        return actorPlayerMap;
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
    public ActorSet getPlayerList() {
        return playerList;
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
