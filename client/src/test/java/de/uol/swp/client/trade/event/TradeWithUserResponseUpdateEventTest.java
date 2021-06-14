package de.uol.swp.client.trade.event;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class TradeWithUserResponseUpdateEventTest {

    @Test
    void getRsp() {
        User offeringUser = new UserDTO(420, "HardmanHenry", "wifeyHardman123", "henryandwifey@hardman.ngo");
        LobbyName lobbyName = new LobbyName("test");
        ResourceList inventory = mock(ResourceList.class);
        ResourceList offered = mock(ResourceList.class);
        ResourceList demanded = mock(ResourceList.class);
        TradeWithUserOfferResponse rsp = new TradeWithUserOfferResponse(offeringUser, inventory, offered, demanded,
                                                                        lobbyName);

        TradeWithUserResponseUpdateEvent event = new TradeWithUserResponseUpdateEvent(rsp);

        assertEquals(rsp, event.getRsp());
        assertEquals(rsp.getLobbyName(), event.getRsp().getLobbyName());
        assertEquals(rsp.getOfferingUser(), event.getRsp().getOfferingUser());
        assertEquals(rsp.getResourceList(), event.getRsp().getResourceList());
        assertEquals(rsp.getOfferedResources(), event.getRsp().getOfferedResources());
        assertEquals(rsp.getDemandedResources(), event.getRsp().getDemandedResources());
    }
}