package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.hexes.IGameHex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameMapDTOTest {

    @Test
    void testGameMapDTO() {
        IGameHex[][] gameHexes = new IGameHex[][]{};
        IntersectionWithEdges[][] intersectionsWithEdges = new IntersectionWithEdges[][]{};
        GameMapDTO gameMapDTO = new GameMapDTO(gameHexes, intersectionsWithEdges);

        assertEquals(gameHexes, gameMapDTO.getHexes());
        assertEquals(intersectionsWithEdges, gameMapDTO.getIntersections());
    }
}