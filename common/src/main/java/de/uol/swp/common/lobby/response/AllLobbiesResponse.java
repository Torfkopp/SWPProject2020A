package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest;
import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Response message for the RetrieveAllLobbiesRequest
 * <p>
 * This message gets sent to the client that sent a RetrieveAllLobbiesRequest.
 * It contains a List of lobby names.
 *
 * @author Mario
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see RetrieveAllLobbiesRequest
 * @see de.uol.swp.common.lobby.Lobby
 * @since 2020-12-12
 */
public class AllLobbiesResponse extends AbstractResponseMessage {

    final private List<String> lobbyNames = new ArrayList<>();
    final private List<Lobby> lobbies = new ArrayList<>();

    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialisation
     * @since 2020-12-12
     */
    public AllLobbiesResponse() {
        // needed for serialisation
    }

    /**
     * Constructor
     *
     * @param lobbies map of lobby's name and the lobby itself
     * @since 2020-12-12
     */
    public AllLobbiesResponse(Map<String, Lobby> lobbies) {
        this.lobbyNames.addAll(lobbies.keySet());
    }

    /**
     * Getter for the list of lobby names
     *
     * @return List of lobby names
     * @since 2020-12-12
     */
    public List<String> getLobbyNames() {
        return lobbyNames;
    }

    /**
     * Getter for the list of existing Lobby objects
     *
     * @author Marvin
     * @return List of existing Lobby objects
     * @since 2020-12-16
     */
    public List<Lobby> getLobbies() {
        return lobbies;
    }
}
