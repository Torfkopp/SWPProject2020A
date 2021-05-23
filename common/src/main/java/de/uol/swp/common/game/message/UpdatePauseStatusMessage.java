package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.AbstractServerMessage;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Message sent to everyone in the lobby to update everyone
 * about the pause status of the game
 *
 * @author Maximilian Lindner
 * @since 2021-05-21
 */
public class UpdatePauseStatusMessage extends AbstractServerMessage {

    private final LobbyName lobbyName;
    private final boolean paused;
    private final int pausedMembers;
    private final UserOrDummy activePlayer;

    /**
     * Constructor
     *
     * @param lobbyName     The name of the lobby that gets updated
     * @param paused        The paused status of the game
     * @param pausedMembers Amount of players who want to change the pause status of the game
     * @param activePlayer  The user whose turn it is
     */
    public UpdatePauseStatusMessage(LobbyName lobbyName, boolean paused, int pausedMembers, UserOrDummy activePlayer) {
        this.lobbyName = lobbyName;
        this.paused = paused;
        this.pausedMembers = pausedMembers;
        this.activePlayer = activePlayer;
    }

    /**
     * Gets the active player of the game
     *
     * @return The active player of the game
     */
    public UserOrDummy getActivePlayer() {
        return activePlayer;
    }

    /**
     * Gets the LobbyName
     *
     * @return The name of the lobby that gets updated
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the amount of players
     *
     * @return Amount of players who want to change the pause status of the game
     */
    public int getPausedMembers() {
        return pausedMembers;
    }

    /**
     * Gets the game status
     *
     * @return Whether the game is paused or not
     */
    public boolean isPaused() {
        return paused;
    }
}
