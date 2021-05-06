package de.uol.swp.common.game.response;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.IDevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.IResourceList;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

/**
 * This Response has up-to-date info about what the inventory of a specified player contains
 *
 * @author Sven Ahrens
 * @author Finn Haase
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-01-25
 */
public class UpdateInventoryResponse extends AbstractLobbyResponse {

    private final UserOrDummy user;
    private final IResourceList resourceList;
    private final IDevelopmentCardList developmentCardList;

    /**
     * Constructor
     *
     * @param user                The user wanting to update the inventory
     * @param lobbyName           The lobby for which the update is supposed to happen in
     * @param developmentCardList List of the Development Cards in the user's inventory
     * @param resourceList        List of the Resources in the user's inventory
     *
     * @author Phillip-André Suhr
     * @since 2021-04-17
     */
    public UpdateInventoryResponse(UserOrDummy user, LobbyName lobbyName, IResourceList resourceList,
                                   IDevelopmentCardList developmentCardList) {
        super(lobbyName);
        this.user = user;
        this.resourceList = resourceList;
        this.developmentCardList = developmentCardList;
    }

    /**
     * Gets the list of development cards.
     *
     * @return The development card list
     *
     * @author Phillip-André Suhr
     * @since 2021-04-17
     */
    public IDevelopmentCardList getDevelopmentCardList() {
        return developmentCardList;
    }

    /**
     * Gets the list of resources.
     *
     * @return The resource list.
     *
     * @author Phillip-André Suhr
     * @since 2021-04-17
     */
    public IResourceList getResourceList() {
        return resourceList;
    }

    /**
     * Gets the user whose inventory is being updated
     *
     * @return The User whose inventory is being updated
     */
    public UserOrDummy getUser() {
        return user;
    }
}
