package de.uol.swp.common.game.request;

import de.uol.swp.common.user.User;

public class UpdateInventoryAfterTradeWithBankRequest extends UpdateInventoryRequest {

    private final String getResource;
    private final String giveResource;

    public UpdateInventoryAfterTradeWithBankRequest(User user, String originLobby, String getResource,
                                                    String giveResource) {
        super(user, originLobby);
        this.getResource = getResource;
        this.giveResource = giveResource;
    }

    public String getGiveResource() {
        return giveResource;
    }

    public String getGetResource() {
        return getResource;
    }
}
