package de.uol.swp.server.game;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.user.AI;

import java.util.HashMap;
import java.util.List;

import static de.uol.swp.common.game.map.hexes.IHarbourHex.HarbourResource;
import static de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType.*;

/**
 * Specialised class to map
 * an AI to a list of its harbours
 *
 * @author Mario Fokken
 * @since 2021-06-16
 */
public class AIHarbourMap extends HashMap<AI, List<HarbourResource>> {

    /**
     * Return which resource the AI can and should get from the harbour
     *
     * @param ai    The AI to use the harbour
     * @param round The round the game is in
     *
     * @return ResourceType or null if no resource is available
     */
    public ResourceType tradeGet(AI ai, int round) {
        if (round < 14) {
            if (get(ai).contains(HarbourResource.LUMBER)) return LUMBER;
            if (get(ai).contains(HarbourResource.BRICK)) return BRICK;
        } else {
            if (get(ai).contains(HarbourResource.ORE)) return ORE;
            if (get(ai).contains(HarbourResource.GRAIN)) return GRAIN;
        }
        if (get(ai).contains(HarbourResource.WOOL)) return WOOL;
        else return null;
    }
}
