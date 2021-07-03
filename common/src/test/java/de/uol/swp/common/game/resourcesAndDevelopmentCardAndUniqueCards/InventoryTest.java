package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    protected void setUp() {
        inventory = new Inventory();
    }

    @AfterEach
    protected void tearDown() {
        inventory = null;
    }

    @Test
    void getAmountOfDevelopmentCards() {
        inventory.increase(DevelopmentCardType.ROAD_BUILDING_CARD);

        assertEquals(1, inventory.getAmountOfDevelopmentCards());
    }

    @Test
    void getKnights() {
        assertEquals(0, inventory.getKnights());
    }

    @Test
    void getResourceAmount() {
        inventory.increase(ResourceType.BRICK, 10);

        assertEquals(10, inventory.getResourceAmount());
    }

    @Test
    void hasCityResources() {
        assertFalse(inventory.hasCityResources());

        inventory.set(ResourceType.GRAIN, 2);
        inventory.set(ResourceType.ORE, 3);

        assertTrue(inventory.hasCityResources());
    }

    @Test
    void hasDevCardResources() {
        assertFalse(inventory.hasDevCardResources());

        inventory.set(ResourceType.ORE, 1);
        inventory.set(ResourceType.GRAIN, 1);
        inventory.set(ResourceType.WOOL, 1);

        assertTrue(inventory.hasDevCardResources());
    }

    @Test
    void hasRoadResources() {
        assertFalse(inventory.hasRoadResources());

        inventory.set(ResourceType.BRICK, 1);
        inventory.set(ResourceType.LUMBER, 1);

        assertTrue(inventory.hasRoadResources());
    }

    @Test
    void hasSettlementResources() {
        assertFalse(inventory.hasSettlementResources());

        inventory.set(ResourceType.BRICK, 1);
        inventory.set(ResourceType.GRAIN, 1);
        inventory.set(ResourceType.LUMBER, 1);
        inventory.set(ResourceType.WOOL, 1);

        assertTrue(inventory.hasSettlementResources());
    }

    @Test
    void increaseKnights() {
        assertEquals(0, inventory.getKnights());

        inventory.increaseKnights();

        assertEquals(1, inventory.getKnights());
    }

    @Test
    void isPlayableAndNextTurn() {
        inventory.increase(DevelopmentCardType.KNIGHT_CARD);

        assertFalse(inventory.isPlayable(DevelopmentCardType.KNIGHT_CARD));

        inventory.nextTurn();

        assertTrue(inventory.isPlayable(DevelopmentCardType.KNIGHT_CARD));
    }

    @Test
    void removeCityResources() {
        inventory.increase(ResourceType.GRAIN, 2);
        inventory.increase(ResourceType.ORE, 3);

        inventory.removeCityResources();

        assertEquals(0, inventory.get(ResourceType.GRAIN));
        assertEquals(0, inventory.get(ResourceType.ORE));
    }

    @Test
    void removeDevCardResources() {
        inventory.increase(ResourceType.ORE, 1);
        inventory.increase(ResourceType.GRAIN, 1);
        inventory.increase(ResourceType.WOOL, 1);

        inventory.removeDevCardResources();

        assertEquals(0, inventory.get(ResourceType.ORE));
        assertEquals(0, inventory.get(ResourceType.GRAIN));
        assertEquals(0, inventory.get(ResourceType.WOOL));
    }

    @Test
    void removeRoadResources() {
        inventory.increase(ResourceType.BRICK, 1);
        inventory.increase(ResourceType.LUMBER, 1);

        inventory.removeRoadResources();

        assertEquals(0, inventory.get(ResourceType.BRICK));
        assertEquals(0, inventory.get(ResourceType.LUMBER));
    }

    @Test
    void removeSettlementResources() {
        inventory.increase(ResourceType.BRICK, 1);
        inventory.increase(ResourceType.GRAIN, 1);
        inventory.increase(ResourceType.LUMBER, 1);
        inventory.increase(ResourceType.WOOL, 1);

        inventory.removeSettlementResources();

        assertEquals(0, inventory.get(ResourceType.BRICK));
        assertEquals(0, inventory.get(ResourceType.GRAIN));
        assertEquals(0, inventory.get(ResourceType.LUMBER));
        assertEquals(0, inventory.get(ResourceType.WOOL));
    }
}