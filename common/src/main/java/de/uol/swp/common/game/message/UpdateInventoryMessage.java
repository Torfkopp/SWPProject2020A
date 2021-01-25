package de.uol.swp.common.game.message;

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
