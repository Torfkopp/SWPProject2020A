package de.uol.swp.common.lobby.request;

import de.uol.swp.common.Colour;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Request sent to the server when a user wants to change
 * their colour.
 *
 * @author Mario Fokken
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @since 2021-06-02
 */
public class SetColourRequest extends AbstractLobbyRequest {

    private final Colour colour;

    /**
     * Constructor
     *
     * @param name Name of the lobby
     * @param user User responsible for the creation of this message
     */
    public SetColourRequest(LobbyName name, UserOrDummy user, Colour colour) {
        super(name, user);
        this.colour = colour;
    }

    /**
     * Gets the colour
     *
     * @return Colour
     */
    public Colour getColour() {
        return colour;
    }
}
