package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

/**
 * This request is posted onto the EventBus to change
 * the settings of a specific lobby.
 *
 * @author Maximilian Lindner
 * @author Aldin Dervisi
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @since 2021-03-14
 */
public class ChangeLobbySettingsRequest extends AbstractLobbyRequest {

    private final int allowedPlayers;
    private final boolean startUpPhaseEnabled;
    private final boolean commandsAllowed;
    private final int moveTime;
    private final boolean randomPlayFieldEnabled;

    /**
     * Constructor
     *
     * @param name                   Name of the lobby
     * @param user                   User responsible for the creation of this message
     * @param allowedPlayers         New allowed players amount.
     * @param startUpPhaseEnabled    Whether the startup phase is enabled or not
     * @param commandsAllowed        Whether commands are enabled or not
     * @param moveTime               The maximum move time in seconds
     * @param randomPlayFieldEnabled Whether a randomly generated play field will be used
     */
    public ChangeLobbySettingsRequest(LobbyName name, User user, int allowedPlayers, boolean startUpPhaseEnabled,
                                      boolean commandsAllowed, int moveTime, boolean randomPlayFieldEnabled) {
        super(name, user);
        this.allowedPlayers = allowedPlayers;
        this.startUpPhaseEnabled = startUpPhaseEnabled;
        this.commandsAllowed = commandsAllowed;
        this.moveTime = moveTime;
        this.randomPlayFieldEnabled = randomPlayFieldEnabled;
    }

    /**
     * Gets the new allowed amount of players
     *
     * @return New amount of players
     */
    public int getAllowedPlayers() {
        return allowedPlayers;
    }

    /**
     * Gets the new maximum move time in seconds
     *
     * @return The new maximum move time in seconds
     */
    public int getMoveTime() {
        return moveTime;
    }

    /**
     * Gets whether commands are allowed or not
     *
     * @return true if commands are allowed, false if not
     */
    public boolean isCommandsAllowed() {
        return commandsAllowed;
    }

    /**
     * Gets whether a randomly generated play field will be used or not
     *
     * @return true if a randomly generated play field will be used, false if not
     */
    public boolean isRandomPlayFieldEnabled() {
        return randomPlayFieldEnabled;
    }

    /**
     * Gets whether the lobby has a start up phase or not
     *
     * @return true if the startup phase is enabled, false if not
     */
    public boolean isStartUpPhaseEnabled() {
        return startUpPhaseEnabled;
    }
}
