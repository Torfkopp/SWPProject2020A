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
     *
     * @author Temmo Junkhoff
     * @since 2021-03-8
     */
    void createBeginnerMap();

    /**
     * Gets an edge that connects two intersections
     *
     * @param intersection1 First intersection of edge
     * @param intersection2 Second intersection of edge
     *
     * @return The edge connecting the given intersections
     *
     * @author Temmo Junkhoff
     * @since 2021-03-05
     */
    IEdge edgeConnectingIntersections(IIntersection intersection1, IIntersection intersection2);

    /**
     * Gets all the edges around the hex
     *
     * @param mapPoint The hex's mapPoint
     *
     * @return Set of edges
     *
     * @author Mario Fokken
     * @since 2021-03-15
     */
    Set<IEdge> getEdgesFromHex(MapPoint mapPoint);

    /**
     * Gets all the intersections around the hex
     *
     * @param mapPoint The hex's mapPoint
     *
     * @return Set of intersections
     *
     * @author Mario Fokken
     * @since 2021-03-15
     */
    Set<IIntersection> getIntersectionFromHex(MapPoint mapPoint);

    /**
     * Gets the hex at a specified place
     *
     * @param position The hex's coordinates
     *
     * @return The hex
     *
     * @author Mario Fokken
     * @since 2021-01-16
     */
    IGameHex getHex(MapPoint position);

    /**
     * Gets the resource hex with a specified token
     *
     * @param Token The hex's token
     *
     * @return The ResourceHex
     *
     * @author Mario Fokken
     * @since 2021-03-15
     */
    Set<MapPoint> getHex(int Token);

    /**
     * Gets the hexes in a usable format for rendering them as a jagged array
     *
     * @return A jagged array containing the hexes
     *
     * @author Temmo Junkhoff
     * @since 2021-01-21
     */
    IGameHex[][] getHexesAsJaggedArray();

    /**
     * Gets the intersection object at a given position
     *
     * @param position The position of the intersection
     *
     * @return An intersection object
     *
     * @author Temmo Junkhoff
     * @since 2021-03-05
     */
    IIntersection getIntersection(MapPoint position);

    /**
     * Gets the intersections in a usable format for rendering them as a jagged array
     *
     * @return A jagged array containing the intersections
     *
     * @author Temmo Junkhoff
     * @since 2021-01-31
     */
    IIntersection[][] getIntersectionsAsJaggedArray();

    /**
     * Gets the amount of points the player made with
     * settlements and cities.
     *
     * @return int The amount of points the player has
     *
     * @author Mario Fokken
     * @since 2021-02-05
     */
    int getPlayerPoints(Player player);

    /**
     * Gets the robber's position
     *
     * @return A MapPoint containing the position of the robber
     *
     * @author Temmo Junkhoff
     * @since 2021-03-05
     */
    MapPoint getRobberPosition();

    /**
     * Gets the incident edges of a given intersection
     *
     * @param intersection The intersection of which the edges should be returned
     *
     * @return A Set<> containing all edge objects
     *
     * @author Temmo Junkhoff
     * @since 2021-03-05
     */
    Set<IEdge> incidentEdges(IIntersection intersection);

    /**
     * Moves the robber
     *
     * @param newPosition The hex the robber has moved to
     *
     * @author Mario Fokken
     * @since 2021-01-16
     */
    void moveRobber(MapPoint newPosition);

    /**
     * Places a road for the given player on the given edge.
     *
     * @param player The player wanting to build the road
     * @param edge   The edge to place a road on
     *
     * @return True if placement was successful; false if not
     *
     * @author Mario Fokken
     * @since 2021-01-16
     */
    boolean placeRoad(Player player, IEdge edge);

    /**
     * Places a settlement
     *
     * @param player   The player wanting to build the settlement (1-4)
     * @param position The position of the intersection
     *
     * @return True if placement was successful; false if not
     *
     * @author Mario Fokken
     * @since 2021-01-16
     */
    boolean placeSettlement(Player player, MapPoint position);

    /**
     * Checks if a street is placeable
     *
     * @param player The player wanting to place the street
     * @param edge   The edge to place a road on
     *
     * @return True if placement is possible; false if not
     *
     * @author Mario Fokken
     * @since 2021-01-16
     */
    boolean roadPlaceable(Player player, IEdge edge);

    /**
     * Checks if a settlement is placeable
     *
     * @param player   The player wanting to place the settlement
     * @param position The position of the intersection
     *
     * @return True if placement is possible; false if not
     *
     * @author Mario Fokken
     * @since 2021-01-16
     */
    boolean settlementPlaceable(Player player, MapPoint position);

    /**
     * Upgrades a settlement
     *
     * @param player   The player wanting to upgrade the settlement
     * @param position The position of the intersection
     *
     * @return True if placement was successful; false if not
     *
     * @author Mario Fokken
     * @since 2021-01-16
     */
    boolean upgradeSettlement(Player player, MapPoint position);
}
