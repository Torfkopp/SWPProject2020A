package de.uol.swp.common.game.request;

public class UpdateBankInventoryRequest extends AbstractGameRequest{

    /**
     * Constructor
     * <p>
     *
     * @param originLobby The Lobby from which a request originated from
     */

    public UpdateBankInventoryRequest(String originLobby) {
        super(originLobby);
    }
}
