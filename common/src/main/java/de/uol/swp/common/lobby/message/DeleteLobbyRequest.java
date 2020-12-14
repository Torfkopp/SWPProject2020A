package de.uol.swp.common.lobby.message;

/**
 * Request sent to the server when a lobby has to be deleted
 *
 * @author Mario
 * @see de.uol.swp.common.lobby.message.AbstractLobbyRequest
 * @since 2020-12-14
 */
public class DeleteLobbyRequest extends AbstractLobbyRequest {

    private String lobbyName;

    /**
     * Default constructor
     *
     * @implNote this constructor is needed for serialisation
     * @since 2020-12-14
     */
    public DeleteLobbyRequest() {

    }

    /**
     * Constructor
     *
     * @param lobbyName name of the lobby
     * @since 2020-12-14
     */
    public DeleteLobbyRequest(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Getter for the lobbyName
     *
     * @return Name of the lobby that is to be deleted
     * @since 2020-12-14
     */
    public String getLobbyName() {
        return lobbyName;
    }

}
