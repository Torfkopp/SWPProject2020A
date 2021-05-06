package de.uol.swp.common.game.request;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Request sent to the server when a user wants to update his Inventory
 * after a successful trade
 *
 * @author Alwin Bossert
 * @author Maximilian Lindner
 * @see de.uol.swp.common.game.request.UpdateInventoryRequest
 * @since 2021-02-21
 */
public class ExecuteTradeWithBankRequest extends UpdateInventoryRequest {

    private final ResourceType getResource;
    private final ResourceType giveResource;

    /**
     * Constructor
     *
     * @param user         The User who wants to update his inventory after trade
     * @param originLobby  The lobby where the trade happened
     * @param getResource  The name of the resource he gets from the bank
     * @param giveResource The name of the resource he has to give to the bank
     */
    public ExecuteTradeWithBankRequest(UserOrDummy user, LobbyName originLobby, ResourceType getResource,
                                       ResourceType giveResource) {
        super(user, originLobby);
        this.getResource = getResource;
        this.giveResource = giveResource;
    }

    /**
     * Gets the name of the resource he gets from the bank
     *
     * @return name of the resource
     */
    public ResourceType getGetResource() {
        return getResource;
    }

    /**
     * Gets the name of the resource he has to give to the bank
     *
     * @return name of the resource
     */
    public ResourceType getGiveResource() {
        return giveResource;
    }
}
