package de.uol.swp.common.game.map.configuration;

import de.uol.swp.common.game.map.hexes.IHarbourHex;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;

import java.util.List;

/**
 * Class used to transfer a game configuration, especially useful for making
 * sure all clients see the same randomly generated game board configuration.
 *
 * @author Finn Haase
 * @author Phillip-André Suhr
 * @implNote All returned Lists are unmodifiable and ordered, so new LinkedList
 * objects must be created to create a map from them
 * @see de.uol.swp.common.game.map.configuration.IConfiguration
 * @since 2021-03-27
 */
public class Configuration implements IConfiguration {

    private final List<ResourceType> hexList;
    private final List<IHarbourHex.HarbourResource> harbourList;
    private final List<Integer> tokenList;
    private final MapPoint robberPosition;

    /**
     * Constructor
     *
     * @param harbourList Unmodifiable, ordered List of Harbour resources
     * @param hexList     Unmodifiable, ordered List of Hex resource types
     * @param tokenList   Unmodifiable, ordered List of Tokens to be placed on Hexes
     */
    public Configuration(List<IHarbourHex.HarbourResource> harbourList, List<ResourceType> hexList,
                         List<Integer> tokenList, MapPoint robberPosition) {
        this.harbourList = harbourList;
        this.hexList = hexList;
        this.tokenList = tokenList;
        this.robberPosition = robberPosition;
    }

    @Override
    public List<IHarbourHex.HarbourResource> getHarbourList() {
        return harbourList;
    }

    @Override
    public List<ResourceType> getHexList() {
        return hexList;
    }

    @Override
    public MapPoint getRobberPosition() {
        return robberPosition;
    }

    @Override
    public List<Integer> getTokenList() {
        return tokenList;
    }
}
