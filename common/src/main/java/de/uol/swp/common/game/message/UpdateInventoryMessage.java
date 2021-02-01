package de.uol.swp.common.game.message;

/**
 * Message sent to the server when a user wants to update his Inventory
 *
 * @author Sven Ahrens
 * @author Finn Haase
 * @see de.uol.swp.common.game.message.AbstractGameMessage
 * @since 2021-1-25
 */

public class UpdateInventoryMessage extends AbstractGameMessage{
    String lobby;


    public UpdateInventoryMessage(String game){

    }

    /**
     * Gets the lobby
     *
     * @return String The lobby's name
     */
    public String getLobby() {
        return lobby;
    }
}
