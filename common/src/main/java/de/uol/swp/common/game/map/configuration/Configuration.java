package de.uol.swp.common.game.map.configuration;

import de.uol.swp.common.game.map.Hexes.IHarborHex;
import de.uol.swp.common.game.map.Hexes.IResourceHex;
import de.uol.swp.common.game.map.MapPoint;

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

    private final List<IResourceHex.ResourceHexType> hexList;
    private final List<IHarborHex.HarborResource> harborList;
    private final List<Integer> tokenList;
    private final MapPoint robberPosition;

    /**
     * Constructor
     *
     * @param harborList Unmodifiable, ordered List of Harbor resources
     * @param hexList    Unmodifiable, ordered List of Hex resource types
     * @param tokenList  Unmodifiable, ordered List of Tokens to be placed on Hexes
     */
    public Configuration(List<IHarborHex.HarborResource> harborList, List<IResourceHex.ResourceHexType> hexList,
                         List<Integer> tokenList, MapPoint robberPosition) {
        this.harborList = harborList;
        this.hexList = hexList;
        this.tokenList = tokenList;
        this.robberPosition = robberPosition;
    }

    @Override
    public List<IHarborHex.HarborResource> getHarborList() {
        return harborList;
    }

    @Override
    public List<IResourceHex.ResourceHexType> getHexList() {
        return hexList;
    }

    @Override
    public List<Integer> getTokenList() {
        return tokenList;
    }

    @Override
    public MapPoint getRobberPosition() {
        return robberPosition;
    }
}
