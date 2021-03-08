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
public interface IGameMap {

    /**
     * Creates the beginner map
     */
    void createBeginnerMap();

    /**
     * Gets an edge that connects two intersections
     *
     * @param intersection1 First intersection of edge
     * @param intersection2 Second intersection of edge
     *
     * @return The edge connecting the given intersections
     */
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
     * Gets the intersection object at a given position
     *
     * @param position The position of the intersection
     *
     * @return An intersection object
     */
    IIntersection getIntersection(MapPoint position);

    /**
     * Gets the intersections in a usable format for rendering them as a jagged array
     *
     * @return A jagged array containing the intersections
     */
    IIntersection[][] getIntersectionsAsJaggedArray();

    /**
     * Gets the amount of points the player made with
     * settlements and cities.
     *
     * @return int The amount of points the player has
     */
    int getPlayerPoints(Player player);

    /**
     * Gets the robber's position
     *
     * @return A MapPoint containing the position of the robber
     */
    MapPoint getRobberPosition();

    /**
     * Gets the incident edges of a given intersection
     *
     * @param intersection The intersection of which the edges should be returned
     *
     * @return A Set<> containing all edge objects
     */
    Set<IEdge> incidentEdges(IIntersection intersection);

    /**
     * Moves the robber
     *
     * @param newPosition The hex the robber has moved to
     */
    void moveRobber(MapPoint newPosition);

    /**
     * Places a road for the given player on the given edge.
     *
     * @param player The player wanting to build the road
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
