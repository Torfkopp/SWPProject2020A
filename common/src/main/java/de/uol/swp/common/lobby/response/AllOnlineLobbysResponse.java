package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.lobby.Lobby;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllOnlineLobbysResponse extends AbstractResponseMessage {

    final private ArrayList<LobbyDTO> lobbys = new ArrayList<>();

    public AllOnlineLobbysResponse(){
        // needed for serialization
    }

    /**
     * Constructor
     *
     * This constructor generates a new List of the logged in users from the given
     * Collection. The significant difference between the two being that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     *
     * @param lobbys Collection of all users currently logged in
     * @since 2019-08-13
     */
    public AllOnlineLobbysResponse(Collection<Lobby> lobbys) {
        for (Lobby lobby : lobbys) {
            this.lobbys.add(LobbyDTO.create(lobby));
        }
    }

    /**
     * Getter for the list of users currently logged in
     *
     * @return list of users currently logged in
     * @since 2019-08-13
     */
    public List<LobbyDTO> getName() {
        return lobbys;
    }
}
