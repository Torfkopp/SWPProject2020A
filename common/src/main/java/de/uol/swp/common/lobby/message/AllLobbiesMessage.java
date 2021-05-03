package de.uol.swp.common.lobby.message;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.message.AbstractServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Message that contains a list of all lobbies
 *
 * @author Eric Vuong
 * @author Steven Luong
 * @author Phillip-André-Suhr
 * @since 2021-03-01
 */
public class AllLobbiesMessage extends AbstractServerMessage {

    private final List<ISimpleLobby> lobbies = new ArrayList<>();

    /**
     * Constructor
     *
     * @param lobbies Map of lobby's name and the lobby itself
     */
    public AllLobbiesMessage(Map<LobbyName, ISimpleLobby> lobbies) {
        lobbies.forEach((lobbyName, lobby) -> this.lobbies.add(lobby));
    }

    /**
     * Getter for the list of existing Lobby objects
     *
     * @return List of existing LobbyDTO
     */
    public List<ISimpleLobby> getLobbies() {
        return lobbies;
    }
}
