package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Response message for the RetrieveAllLobbiesRequest
 * <p>
 * This message gets sent to the client that sent a RetrieveAllLobbiesRequest.
 * It contains a list of lobby names.
 *
 * @author Mario
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
 * @see de.uol.swp.common.lobby.Lobby
 * @since 2020-12-12
 */
public class AllLobbiesResponse extends AbstractResponseMessage {

    private final ArrayList<String> lobbies = new ArrayList<>();
    private final ArrayList<LobbyDTO> lobbyDTOs = new ArrayList<>();

    /**
     * Default Constructor
     *
     * @implNote This constructor is needed for serialisation
     * @since 2020-12-12
     */
    public AllLobbiesResponse() {
        // needed for serialisation
    }

    /**
     * Constructor
     *
     * @param lobbies Map of lobby's name and the lobby itself
     * @since 2020-12-12
     */
    public AllLobbiesResponse(Map<String, Lobby> lobbies) {
        this.lobbies.addAll(lobbies.keySet());
    }

    /**
     * Gets the list of lobby names
     *
     * @return List of lobby names
     * @since 2020-12-12
     */
    public ArrayList<String> getLobbies() {
        return lobbies;
    }

    /**
     * Gets the list of existing lobbyDTOs
     *
     * @return List of existing lobbyDTOs
     * @author Marvin
     * @since 2020-12-16
     */
    public List<LobbyDTO> getDTO() {
        return lobbyDTOs;
    }
}
