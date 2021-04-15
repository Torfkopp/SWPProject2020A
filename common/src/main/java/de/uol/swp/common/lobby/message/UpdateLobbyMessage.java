package de.uol.swp.common.lobby.message;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.UserOrDummy;

/**
 * This Message is used to update the pre-game settings
 * of a specific lobby.
 *
 * @author Maximilian Lindner
 * @author Aldin Dervisi
 * @since 2021-03-14
 */
public class UpdateLobbyMessage extends AbstractLobbyMessage {

    private final Lobby lobby;

    /**
     * Constructor
     *
     * @param name  The name of the lobby that was updated
     * @param user  The user who initiated the update
     * @param lobby The object of the lobby that was updated
     */
    public UpdateLobbyMessage(LobbyName name, UserOrDummy user, Lobby lobby) {
        super(name, user);
        this.lobby = lobby;
    }

    /**
     * Gets the Lobby where the pre-game settings were changed.
     *
     * @return The lobby where the settings were changed.
     */
    public Lobby getLobby() {
        return lobby;
    }
}
