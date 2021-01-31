package de.uol.swp.common.game.map;


import de.uol.swp.common.game.map.Hexes.IGameHex;

/**
 * Interface for the game's map
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public interface IGameMapManagement {

    /**
     * Gets the hex at a specified place
     *
     * @param place The hex's place (int)
     * @return The hex
     */
    IGameHex getHex(int place);

    /**
     * Gets the hexes in a usable format for rendering them as a jagged array
     *
     * @return a jagged array containing the hexes
     */
    IGameHex[][] getHexesAsJaggedArray();

    /**
     * Gets the edges in a usable format for rendering them as a jagged array with some extra positions filled with null
     *
     * @return a jagged array containing the hexes
     */
    IEdge[][] getEdgesAsJaggedArrayWithNullFiller();

    /**
     * Gets the intersections in a usable format for rendering them as a jagged array
     *
     * @return a jagged array containing the intersections
     */
    IIntersection[][] getIntersectionsAsJaggedArray();

    /**
     * Places a settlement
     *
     * @param player   The number of the player
     *                 wanting to build the settlement (1-4)
     * @param position The position of the intersection
     * @return true if placement was successful; false if not
     */
    boolean placeSettlement(int player, int position);

    /**
     * Places a street
     *
     * @param player   The number of the player
     *                 wanting to build the street (1-4)
     * @param position The position of the road
     * @return true if placement was successful; false if not
     */
    boolean placeRoad(int player, int position);

    /**
     * Moves the robber
     *
     * @param newHex The hex the robber has moved to
     */
    void moveRobber(int newHex);

    /**
     * Gets the robber's position
     *
     * @return int Position of the robber
     */
    int getRobberPos();

    /**
     * Upgrades a settlement
     *
     * @param player   The number of the player
     *                 wanting to upgrade the settlement (1-4)
     * @param position The position of the intersection
     * @return true if placement was successful; false if not
     */
    boolean upgradeSettlement(int player, int position);

    /**
     * Checks if a settlement is placeable
     *
     * @param player   The number of the player
     *                 wanting to place the settlement (1-4)
     * @param position The position of the intersection
     * @return true if placement is possible; false if not
     */
    boolean settlementPlaceable(int player, int position);

    /**
     * Checks if a street is placeable
     *
     * @param player   The number of the player
     *                 wanting to place the street (1-4)
     * @param position The position of the road
     * @return true if placement is possible; false if not
     */
    boolean roadPlaceable(int player, int position);

}
