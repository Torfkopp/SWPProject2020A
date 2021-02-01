package de.uol.swp.common.game.message;

/**
 * Message sent to the server when a user wants to update his Inventory
 *
 * @author Sven Ahrens
 * @author Finn Haase
 * @see de.uol.swp.common.game.message.AbstractGameMessage
 * @since 2021-1-25
 */
public class UpdateInventoryMessage extends AbstractGameMessage {

    private final String lobbyName;

    public UpdateInventoryMessage(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the lobby
     *
     * @return String The lobby's name
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
