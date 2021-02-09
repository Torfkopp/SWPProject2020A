package de.uol.swp.common.game.map;

import de.uol.swp.common.game.map.Hexes.IGameHex;

/**
 * Interface for the game's map
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public interface IGameMapManagement {

    /**
     * Gets the edges in a usable format for rendering them as a jagged array with some extra positions filled with null
     *
     * @return A jagged array containing the hexes
     */
    IEdge[][] getEdgesAsJaggedArrayWithNullFiller();

    /**
     * Gets the hex at a specified place
     *
     * @param place The hex's place (int)
     *
     * @return The hex
     */
    IGameHex getHex(int place);

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
     * @return int Position of the robber
     */
    int getRobberPos();

    /**
     * Moves the robber
     *
     * @param newHex The hex the robber has moved to
     */
    void moveRobber(int newHex);

    /**
     * Places a street
     *
     * @param player   The player wanting to build the street
     * @param position The position of the road
     *
     * @return True if placement was successful; false if not
     */
    boolean placeRoad(Player player, int position);

    /**
     * Places a settlement
     *
     * @param player   The player wanting to build the settlement (1-4)
     * @param position The position of the intersection
     *
     * @return True if placement was successful; false if not
     */
    boolean placeSettlement(Player player, int position);

    /**
     * Checks if a street is placeable
     *
     * @param player   The player wanting to place the street
     * @param position The position of the road
     *
     * @return True if placement is possible; false if not
     */
    boolean roadPlaceable(Player player, int position);

    /**
     * Checks if a settlement is placeable
     *
     * @param player   The player wanting to place the settlement
     * @param position The position of the intersection
     *
     * @return True if placement is possible; false if not
     */
    boolean settlementPlaceable(Player player, int position);

    /**
     * Upgrades a settlement
     *
     * @param player   The player wanting to upgrade the settlement
     * @param position The position of the intersection
     *
     * @return True if placement was successful; false if not
     */
    boolean upgradeSettlement(Player player, int position);
}
