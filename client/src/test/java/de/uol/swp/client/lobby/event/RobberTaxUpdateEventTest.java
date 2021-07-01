package de.uol.swp.client.lobby.event;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RobberTaxUpdateEventTest {

    private final LobbyName defaultLobby = new LobbyName("test");
    private final ResourceList inventory = new ResourceList();
    int taxAmount = 4;
    private final RobberTaxUpdateEvent event = new RobberTaxUpdateEvent(defaultLobby, taxAmount, inventory);

    @Test
    void getInventory() {
        // ResourceLists get re-created on getInventory, so we only check for size equality
        assertEquals(inventory.getTotal(), event.getInventory().getTotal());
    }

    @Test
    void getLobbyName() {
        assertEquals(defaultLobby, event.getLobbyName());
    }

    @Test
    void getTaxAmount() {
        assertEquals(taxAmount, event.getTaxAmount());
    }
}