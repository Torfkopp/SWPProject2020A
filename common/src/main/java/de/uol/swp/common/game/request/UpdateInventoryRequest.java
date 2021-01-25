package de.uol.swp.common.game.request;

import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a user wants to update his Inventory
 *
 * @author Sven Ahrens
 * @author Finn Haase
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-1-25
 *
 */

public class UpdateInventoryRequest extends AbstractGameRequest{
    private final User user;

    /**
     * Constructor
     * <p>
     * This constructor is used to determine the user that
     * sent this request.
     * @param user The User wanting to update his Inventory
     * @param originLobby The Lobby from which a request originated from
     */
    public UpdateInventoryRequest(User user, String originLobby) {
        super(originLobby);
        this.user = user;
    }

    /**
     * Gets the user attribute
     *
     * @return The user of the UpdateInventoryRequest
     */
    public User getUser() {
        return user;
    }
}
