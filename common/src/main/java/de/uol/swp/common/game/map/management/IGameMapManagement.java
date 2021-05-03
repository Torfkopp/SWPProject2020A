package de.uol.swp.common.game.map.management;

import de.uol.swp.common.game.map.Hexes.IGameHex;
import de.uol.swp.common.game.map.Hexes.IHarborHex;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.configuration.IConfiguration;
import de.uol.swp.common.game.map.gamemapDTO.IGameMap;
import de.uol.swp.common.user.UserOrDummy;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for the management of the game's map
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public interface IGameMapManagement {

    /**
     * Creates an IGameMap from the provided configuration
     *
     * @param configuration The map configuration used in the current Game
     *
     * @return The IGameMap with the configuration as provided
     *
     * @author Finn Haase
     * @author Phillip-André Suhr
     * @see de.uol.swp.common.game.map.configuration.IConfiguration
     * @since 2021-03-18
     */
    IGameMapManagement createMapFromConfiguration(IConfiguration configuration);

    /**
     * Creates the beginner map configuration
     * <p>
     * Creates the beginner's map configuration as shown in the manual WITHOUT beginner
     * settlements or roads; those are created by {@link #makeBeginnerSettlementsAndRoads(int)}
     *
     * @return The beginner, read-only configuration
     *
     * @author Finn Haase
     * @author Phillip-André Suhr
     * @see de.uol.swp.common.game.map.configuration.IConfiguration
     * @see <a href="https://www.catan.com/files/downloads/catan_base_rules_2020_200707.pdf">
     * https://www.catan.com/files/downloads/catan_base_rules_2020_200707.pdf</a>
     * @since 2021-03-18
     */
    IConfiguration getBeginnerConfiguration();

    /**
     * Gets the current configuration of the IGameMap
     *
     * @return The current, read-only configuration
     *
     * @author Finn Haase
     * @author Phillip-André Suhr
     * @implNote Not used currently; could be used in future (e.g. rejoining a game)
     * @see de.uol.swp.common.game.map.configuration.IConfiguration
     * @since 2021-03-18
     */
    IConfiguration getCurrentConfiguration();

    /**
     * Gets an edge that connects two intersections
     *
     * @param position The MapPoint of the Edge
     *
     * @return The edge connecting the given intersections
     *
     * @author Temmo Junkhoff
     * @since 2021-03-05
     */
    IEdge getEdge(MapPoint position);

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
     * Gets a GameMapDTO which represents the current state of the game map
     *
     * @return A GameMapDTO
     *
     * @author Temmo Junkhoff
     * @since 2021-04-08
     */
    IGameMap getGameMapDTO(Map<Player, UserOrDummy> playerUserMapping);

    /**
     * Gets the HarborResourceType of a specific Intersection MapPoint
     * <p>
     * If the point does not have a harbor, NONE is returned
     *
     * @param point specific Intersection MapPoint
     *
     * @return HarborResourceType
     *
     * @author Steven Luong
     * @author Maximilian Lindner
     * @since 2021-04-07
     */
    IHarborHex.HarborResource getHarborResource(MapPoint point);

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
     * Gets all the intersections around the hex
     *
     * @param mapPoint The hex's mapPoint
     *
     * @return Set of intersections
     *
     * @author Mario Fokken
     * @since 2021-03-15
     */
    Set<IIntersection> getIntersectionsFromHex(MapPoint mapPoint);

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
     * Gets all the Settlements and Cities presented in a map
     *
     * @return Map of Players and their Settlements / Cities
     *
     * @author Steven Luong
     * @author Maximilian Lindner
     * @since 2021-04-07
     */
    Map<Player, List<MapPoint>> getPlayerSettlementsAndCities();

    /**
     * Gets all the Players around a Hex
     *
     * @param mapPoint The hex
     *
     * @return Set of Players around said hex
     *
     * @author Mario Fokken
     * @since 2021-04-07
     */
    Set<Player> getPlayersAroundHex(MapPoint mapPoint);

    /**
     * Creates a randomised map with the standard tiles
     *
     * @return A randomised, read-only configuration
     *
     * @author Finn Haase
     * @author Phillip-André Suhr
     * @see de.uol.swp.common.game.map.configuration.IConfiguration
     * @since 2021-03-18
     */
    IConfiguration getRandomisedConfiguration();

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
     * Gets the length of the longest road that includes a specified map point
     *
     * @param mapPoint The map point that should be in the road
     *
     * @return The length of the road
     *
     * @author Temmo Junkhoff
     * @since 2021-04-10
     */
    int longestRoadWith(MapPoint mapPoint);

    /**
     * Places beginner settlements for 3 or 4 players, depending on the
     * provided parameter
     *
     * @param playerCount The amount of players in the game
     */
    void makeBeginnerSettlementsAndRoads(int playerCount);

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
     * Places a road for the given player on the given edge.
     *
     * @param player   The player wanting to build the road
     * @param mapPoint The mapPoint to place a road at
     *
     * @return True if placement was successful; false if not
     *
     * @author Temmo Junkhoff
     * @author Aldin Dervisi
     * @since 2021-04-07
     */
    boolean placeRoad(Player player, MapPoint mapPoint);

    GameMapManagement.PlayerWithLengthOfLongestRoad findLongestRoad();

    /**
     * Checks if a street is placeable
     *
     * @param player   The player wanting to place the street
     * @param mapPoint The mapPoint at which to place a road
     *
     * @return True if placement is possible; false if not
     *
     * @author Temmo Junkhoff
     * @author Aldin Dervisi
     * @since 2021-04-07
     */
    boolean roadPlaceable(Player player, MapPoint mapPoint);

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
     * Checks if a settlement is upgradeable
     *
     * @param player   The player wanting to upgrade the settlement
     * @param position The position of the intersection
     *
     * @return True if placement is possible; false if not
     *
     * @author Temmo Junkhoff
     * @author Aldin Dervisi
     * @since 2021-04-07
     */
    boolean settlementUpgradeable(Player player, MapPoint position);

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
    boolean placeSettlement(Player player,
                            MapPoint position) throws GameMapManagement.SettlementMightInterfereWithLongestRoadException;
}
