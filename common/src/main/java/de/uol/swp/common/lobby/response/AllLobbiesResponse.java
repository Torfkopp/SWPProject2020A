package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.specialisedUtil.SimpleLobbyMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Response message for the RetrieveAllLobbiesRequest
 * <p>
 * This message gets sent to the client that sent a RetrieveAllLobbiesRequest.
 * It contains a list of lobby names.
 *
 * @author Mario Fokken
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
 * @see de.uol.swp.common.lobby.ISimpleLobby
 * @since 2020-12-12
 */
public class AllLobbiesResponse extends AbstractResponseMessage {

    private final List<LobbyName> lobbyNames = new ArrayList<>();
    private final List<ISimpleLobby> lobbies = new ArrayList<>();

    /**
     * Constructor
     *
     * @param lobbies Map of lobby's name and the lobby itself
     *
     * @since 2020-12-12
     */
    public AllLobbiesResponse(SimpleLobbyMap lobbies) {
        lobbies.forEach((lobbyName, lobby) -> {
            this.lobbyNames.add(lobbyName);
            this.lobbies.add(lobby);
        });
    }

    /**
     * Getter for the list of existing Lobby objects
     *
     * @return List of existing lobbies
     *
     * @author Marvin Drees
     * @since 2020-12-16
     */
    public List<ISimpleLobby> getLobbies() {
        return lobbies;
    }

    /**
     * Gets the list of lobby names
     *
     * @return List of lobby names
     *
     * @since 2020-12-12
     */
    public List<LobbyName> getLobbyNames() {
        return lobbyNames;
    }
}
