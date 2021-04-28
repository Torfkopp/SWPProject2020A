package de.uol.swp.common.game.map.configuration;

import de.uol.swp.common.game.map.Hexes.IHarborHex;
import de.uol.swp.common.game.map.Hexes.IResourceHex;
import de.uol.swp.common.game.map.management.MapPoint;

import java.io.Serializable;
import java.util.List;

/**
 * Interface to unify different ways to transfer a game board configuration
 *
 * @author Finn Haase
 * @author Phillip-André Suhr
 * @implNote All returned Lists are unmodifiable and ordered, so new LinkedList
 * objects must be created to create a map from them
 * @see de.uol.swp.common.game.map.configuration.Configuration
 * @since 2021-03-27
 */
public interface IConfiguration extends Serializable {

    /**
     * Gets the List of Harbor resources
     *
     * @return Unmodifiable, ordered List of Harbor resources
     *
     * @implSpec Create new LinkedList objects from this due to it being unmodifiable and ordered
     * @see de.uol.swp.common.game.map.Hexes.IHarborHex.HarborResource
     */
    List<IHarborHex.HarborResource> getHarborList();

    /**
     * Gets the List of Hex resource types
     *
     * @return Unmodifiable, ordered List of Hex resource types
     *
     * @implSpec Create new LinkedList objects from this due to it being unmodifiable and ordered
     * @see de.uol.swp.common.game.map.Hexes.IResourceHex.ResourceHexType
     */
    List<IResourceHex.ResourceHexType> getHexList();

    /**
     * Gets the robberPosition
     *
     * @return MapPoint of robber
     */
    MapPoint getRobberPosition();

    /**
     * Gets the List of Tokens to be placed on Hexes
     *
     * @return Unmodifiable, ordered List of Tokens to be placed on Hexes
     *
     * @implSpec Create new LinkedList objects from this due to it being unmodifiable and ordered
     */
    List<Integer> getTokenList();
}
