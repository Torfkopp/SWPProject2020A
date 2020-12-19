package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.lobby.Lobby;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllOnlineLobbysResponse extends AbstractResponseMessage {

    final private ArrayList<LobbyDTO> lobbys = new ArrayList<>();

    /**
     * Constructor
     *
     * This constructor generates a new List of the existing lobbys from the given
     * Collection. The significant difference between the two being that the new
     * List contains copies of the Lobby objects.
     *
     * @param lobbys Collection of all existing lobbys
     * @since 2020-11-29
     */
    public AllOnlineLobbysResponse(Collection<Lobby> lobbys) {
        for (Lobby lobby : lobbys) {
            this.lobbys.add(LobbyDTO.create(lobby));
        }
    }

    /**
     * Getter for the list of existing lobbys
     *
     * @return list of existing lobbys
     * @since 2020-11-29
     */
    public List<LobbyDTO> getName() {
        return lobbys;
    }
}
