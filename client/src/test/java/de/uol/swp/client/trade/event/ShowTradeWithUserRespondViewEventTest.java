package de.uol.swp.client.trade.event;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ShowTradeWithUserRespondViewEventTest {

    private final User defaultUser = new UserDTO(42, "harri_haaser", "safe", "harri@haaser.harri");
    private final LobbyName defaultLobby = new LobbyName("test");
    private final ResourceList resourceList = mock(ResourceList.class);
    private final ResourceList resourceList1 = mock(ResourceList.class);
    private final ResourceList resourceList2 = mock(ResourceList.class);
    private final TradeWithUserOfferResponse tradeWithUserOfferResponse = new TradeWithUserOfferResponse(defaultUser,
                                                                                                         resourceList,
                                                                                                         resourceList1,
                                                                                                         resourceList2,
                                                                                                         defaultLobby);
    private final ShowTradeWithUserRespondViewEvent event = new ShowTradeWithUserRespondViewEvent(defaultUser,
                                                                                                  defaultLobby,
                                                                                                  tradeWithUserOfferResponse);

    @Test
    void getLobbyName() {
        assertEquals(defaultLobby, event.getLobbyName());
    }

    @Test
    void getOfferingUser() {
        assertEquals(defaultUser, event.getOfferingUser());
    }

    @Test
    void getRsp() {
        assertEquals(tradeWithUserOfferResponse, event.getRsp());
        assertEquals(resourceList, event.getRsp().getResourceList());
        assertEquals(resourceList1, event.getRsp().getOfferedResources());
        assertEquals(resourceList2, event.getRsp().getDemandedResources());
        assertEquals(tradeWithUserOfferResponse.getOfferingUser(), event.getRsp().getOfferingUser());
        assertEquals(tradeWithUserOfferResponse.getLobbyName(), event.getRsp().getLobbyName());
    }
}