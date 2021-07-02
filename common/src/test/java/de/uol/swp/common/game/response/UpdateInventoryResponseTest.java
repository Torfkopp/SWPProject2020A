package de.uol.swp.common.game.response;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.IDevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.IResourceList;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class UpdateInventoryResponseTest {

    @Test
    void testUpdateInventoryResponse() {
        Actor actor = mock(Actor.class);
        LobbyName lobbyName = mock(LobbyName.class);
        IResourceList resourceList = mock(IResourceList.class);
        IDevelopmentCardList developmentCardList = mock(IDevelopmentCardList.class);
        int knightAmount = 12;
        UpdateInventoryResponse response = new UpdateInventoryResponse(actor, lobbyName, resourceList,
                                                                       developmentCardList, knightAmount);

        assertEquals(actor, response.getActor());
        assertEquals(lobbyName, response.getLobbyName());
        assertEquals(resourceList, response.getResourceList());
        assertEquals(developmentCardList, response.getDevelopmentCardList());
        assertEquals(12, response.getKnightAmount());
    }
}