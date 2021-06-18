package de.uol.swp.common.lobby.message;

import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.message.AbstractServerMessage;
import de.uol.swp.common.specialisedUtil.SimpleLobbyMap;

import java.util.ArrayList;
import java.util.List;

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
    public AllLobbiesMessage(SimpleLobbyMap lobbies) {
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
