package de.uol.swp.common.game.map;

import de.uol.swp.common.game.map.Hexes.IGameHex;

import java.util.Set;

/**
 * Interface for the game's map
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public interface IGameMapManagement {

    IEdge edgeConnectingIntersections(IIntersection intersection1, IIntersection intersection2);

    /**
     * Gets the hex at a specified place
     *
     * @param position The hex's coordinates
     *
     * @return The hex
     */
    IGameHex getHex(MapPoint position);

    /**
     * Gets the hexes in a usable format for rendering them as a jagged array
     *
     * @return A jagged array containing the hexes
     */
    IGameHex[][] getHexesAsJaggedArray();

    /**
     * Gets the intersections in a usable format for rendering them as a jagged array
     *
     * @return A jagged array containing the intersections
     */
    IIntersection[][] getIntersectionsAsJaggedArray();

    IIntersection getIntersection(MapPoint position);

    /**
     * Gets the amount of points the player made with
     * settlements and cities.
     *
     * @return int The amount of points the player has
     */
    int getPlayerPoints(Player player);

    MapPoint getRobberPosition();

    Set<IEdge> incidentEdges(IIntersection intersection);

    /**
     * Moves the robber
     *
     * @param newHex The hex the robber has moved to
     */
    void moveRobber(MapPoint newHex);

    /**
     * Places a street
     *
     * @param player The player wanting to build the street
     * @param edge   The edge to place a road on
     *
     * @return True if placement was successful; false if not
     */
    boolean placeRoad(Player player, IEdge edge);

    /**
     * Places a settlement
     *
     * @param player   The player wanting to build the settlement (1-4)
     * @param position The position of the intersection
     *
     * @return True if placement was successful; false if not
     */
    boolean placeSettlement(Player player, MapPoint position);

    /**
     * Checks if a street is placeable
     *
     * @param player The player wanting to place the street
     * @param edge   The edge to place a road on
     *
     * @return True if placement is possible; false if not
     */
    boolean roadPlaceable(Player player, IEdge edge);

    /**
     * Checks if a settlement is placeable
     *
     * @param player   The player wanting to place the settlement
     * @param position The position of the intersection
     *
     * @return True if placement is possible; false if not
     */
    boolean settlementPlaceable(Player player, MapPoint position);

    /**
     * Upgrades a settlement
     *
     * @param player   The player wanting to upgrade the settlement
     * @param position The position of the intersection
     *
     * @return True if placement was successful; false if not
     */
    boolean upgradeSettlement(Player player, MapPoint position);
}
