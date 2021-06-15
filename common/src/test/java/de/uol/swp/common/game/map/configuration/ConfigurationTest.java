package de.uol.swp.common.game.map.configuration;

import de.uol.swp.common.game.map.hexes.IHarbourHex;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigurationTest {

    @Test
    void testConfiguration() {
        List<IHarbourHex.HarbourResource> harbourResourceList = new ArrayList<>();
        harbourResourceList.add(IHarbourHex.HarbourResource.ANY);
        List<ResourceType> hexList = new ArrayList<>();
        hexList.add(ResourceType.GRAIN);
        List<Integer> tokenList = new ArrayList<>();
        tokenList.add(2);
        MapPoint robberPosition = MapPoint.HexMapPoint(3, 3);
        Configuration configuration = new Configuration(harbourResourceList, hexList, tokenList, robberPosition);

        assertEquals(harbourResourceList, configuration.getHarbourList());
        assertEquals(hexList, configuration.getHexList());
        assertEquals(tokenList, configuration.getTokenList());
        assertEquals(robberPosition, configuration.getRobberPosition());
    }
}