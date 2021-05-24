package de.uol.swp.server.game;

import com.google.common.graph.*;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.configuration.Configuration;
import de.uol.swp.common.game.map.configuration.IConfiguration;
import de.uol.swp.common.game.map.gamemapDTO.*;
import de.uol.swp.common.game.map.hexes.*;
import de.uol.swp.common.game.map.management.*;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.server.game.map.IGameMapManagement;

import java.util.*;
import java.util.function.BiFunction;

import static de.uol.swp.common.game.map.management.IIntersection.IntersectionState.*;
import static de.uol.swp.common.game.map.management.MapPoint.*;

/**
 * Management of the gameMap
 *
 * @author Marvin Drees
 * @author Temmo Junkhoff
 * @since 2021-01-16
 */
@SuppressWarnings("UnstableApiUsage")
public class GameMapManagement implements IGameMapManagement {

    //Map mapping the player and his settlements/cities
    private final Map<Player, List<MapPoint>> playerSettlementsAndCities = new HashMap<>();
    private final Map<IIntersection, IHarbourHex.HarbourResource> harbourResourceMap = new HashMap<>();
    private final Set<MapPoint> foundingRoads = new HashSet<>();
    private MapPoint robberPosition = HexMapPoint(3, 3);
    private GameHexWrapper[][] hexMap;
    private IIntersection[][] intersectionMap;
    private ImmutableNetwork<GameHexWrapper, IEdge> hexEdgeNetwork;
    private ImmutableNetwork<IIntersection, IEdge> intersectionEdgeNetwork;
    private IConfiguration configuration;

    /**
     * Constructor
     */
    public GameMapManagement() {
        createHexEdgeNetwork();
        createIntersectionEdgeNetwork();
        hexMap[robberPosition.getX()][robberPosition.getY()].get().setRobberOnField(false);
    }

    @Override
    public Set<IIntersection> adjacentIntersections(IIntersection intersection) {
        return intersectionEdgeNetwork.adjacentNodes(intersection);
    }

    @Override
    public IGameMapManagement createMapFromConfiguration(IConfiguration configuration) {
        this.configuration = configuration;
        // create new LinkedLists because lists are transmitted ordered and read-only in the IConfiguration
        List<IHarbourHex.HarbourResource> harbourList = new LinkedList<>(configuration.getHarbourList());
        List<ResourceType> hexList = new LinkedList<>(configuration.getHexList());
        List<Integer> tokenList = new LinkedList<>(configuration.getTokenList());
        hexMap[0][0].set(new HarbourHex(hexMap[1][1], IHarbourHex.HarbourSide.SOUTHEAST, harbourList.remove(0)));
        hexMap[0][1].set(new WaterHex());
        hexMap[0][2].set(new HarbourHex(hexMap[1][2], IHarbourHex.HarbourSide.SOUTHWEST, harbourList.remove(0)));
        hexMap[0][3].set(new WaterHex());
        hexMap[1][0].set(new WaterHex());
        hexMap[1][1].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[1][2].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[1][3].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[1][4].set(new HarbourHex(hexMap[2][4], IHarbourHex.HarbourSide.SOUTHWEST, harbourList.remove(0)));
        hexMap[2][0].set(new HarbourHex(hexMap[2][1], IHarbourHex.HarbourSide.EAST, harbourList.remove(0)));
        hexMap[2][1].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[2][2].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[2][3].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[2][4].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[2][5].set(new WaterHex());
        hexMap[3][0].set(new WaterHex());
        hexMap[3][1].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[3][2].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[3][3].set(new DesertHex());
        hexMap[3][4].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[3][5].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[3][6].set(new HarbourHex(hexMap[3][6], IHarbourHex.HarbourSide.WEST, harbourList.remove(0)));
        hexMap[4][0].set(new HarbourHex(hexMap[4][1], IHarbourHex.HarbourSide.EAST, harbourList.remove(0)));
        hexMap[4][1].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[4][2].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[4][3].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[4][4].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[4][5].set(new WaterHex());
        hexMap[5][0].set(new WaterHex());
        hexMap[5][1].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[5][2].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[5][3].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[5][4].set(new HarbourHex(hexMap[4][4], IHarbourHex.HarbourSide.NORTHWEST, harbourList.remove(0)));
        hexMap[6][0].set(new HarbourHex(hexMap[5][1], IHarbourHex.HarbourSide.NORTHEAST, harbourList.remove(0)));
        hexMap[6][1].set(new WaterHex());
        hexMap[6][2].set(new HarbourHex(hexMap[5][2], IHarbourHex.HarbourSide.NORTHWEST, harbourList.remove(0)));
        hexMap[6][3].set(new WaterHex());
        createHarbourResourceMap();
        moveRobber(configuration.getRobberPosition());
        return this;
    }

    @Override
    public PlayerWithLengthOfLongestRoad findLongestRoad() {
        int maxLength = 0;
        MapPoint baseForMaxLength = null;
        for (MapPoint edge : foundingRoads) {
            int length = longestRoadWith(edge);
            if (length > maxLength) {
                maxLength = length;
                baseForMaxLength = edge;
            }
        }
        return new PlayerWithLengthOfLongestRoad(getEdge(baseForMaxLength).getOwner(), maxLength);
    }

    @Override
    public IConfiguration getBeginnerConfiguration() {
        List<ResourceType> hexList = new LinkedList<>();
        hexList.add(ResourceType.ORE);
        hexList.add(ResourceType.WOOL);
        hexList.add(ResourceType.LUMBER);
        hexList.add(ResourceType.GRAIN);
        hexList.add(ResourceType.BRICK);
        hexList.add(ResourceType.WOOL);
        hexList.add(ResourceType.BRICK);
        hexList.add(ResourceType.GRAIN);
        hexList.add(ResourceType.LUMBER);
        hexList.add(ResourceType.LUMBER);
        hexList.add(ResourceType.ORE);
        hexList.add(ResourceType.LUMBER);
        hexList.add(ResourceType.ORE);
        hexList.add(ResourceType.GRAIN);
        hexList.add(ResourceType.WOOL);
        hexList.add(ResourceType.BRICK);
        hexList.add(ResourceType.GRAIN);
        hexList.add(ResourceType.WOOL);
        List<IHarbourHex.HarbourResource> harbourList = new LinkedList<>();
        harbourList.add(IHarbourHex.HarbourResource.ANY);
        harbourList.add(IHarbourHex.HarbourResource.GRAIN);
        harbourList.add(IHarbourHex.HarbourResource.ORE);
        harbourList.add(IHarbourHex.HarbourResource.LUMBER);
        harbourList.add(IHarbourHex.HarbourResource.ANY);
        harbourList.add(IHarbourHex.HarbourResource.BRICK);
        harbourList.add(IHarbourHex.HarbourResource.WOOL);
        harbourList.add(IHarbourHex.HarbourResource.ANY);
        harbourList.add(IHarbourHex.HarbourResource.ANY);
        List<Integer> tokenList = new LinkedList<>();
        tokenList.add(10);
        tokenList.add(2);
        tokenList.add(9);
        tokenList.add(12);
        tokenList.add(6);
        tokenList.add(4);
        tokenList.add(10);
        tokenList.add(9);
        tokenList.add(11);
        tokenList.add(3);
        tokenList.add(8);
        tokenList.add(8);
        tokenList.add(3);
        tokenList.add(4);
        tokenList.add(5);
        tokenList.add(5);
        tokenList.add(6);
        tokenList.add(11);
        // wrapped as unmodifiable so it can be reliably retrieved.
        // Create new LinkedList objects with the Getter results when creating the map from a Configuration
        configuration = new Configuration(Collections.unmodifiableList(harbourList),
                                          Collections.unmodifiableList(hexList),
                                          Collections.unmodifiableList(tokenList), robberPosition);
        return configuration;
    }

    @Override
    public IEdge getEdge(MapPoint position) {
        if (position.getType() != MapPoint.Type.EDGE) return null;
        if (position.getL().getType() == MapPoint.Type.INTERSECTION && position.getR()
                                                                               .getType() == MapPoint.Type.INTERSECTION)
            return intersectionEdgeNetwork
                    .edgeConnectingOrNull(getIntersection(position.getL()), getIntersection(position.getR()));
        else if (position.getL().getType() == MapPoint.Type.HEX && position.getR().getType() == MapPoint.Type.HEX)
            return hexEdgeNetwork.edgeConnectingOrNull(getHexWrapper(position.getL()), getHexWrapper(position.getR()));
        return null;
    }

    @Override
    public MapPoint getEdgeMapPoint(IEdge edge) {
        EndpointPair<IIntersection> pair = intersectionEdgeNetwork.incidentNodes(edge);
        return MapPoint.EdgeMapPoint(getIntersectionMapPoint(pair.nodeU()), getIntersectionMapPoint(pair.nodeV()));
    }

    @Override
    public Set<IEdge> getEdgesAroundIntersection(IIntersection intersection) {
        return intersectionEdgeNetwork.incidentEdges(intersection);
    }

    @Override
    public Set<IEdge> getEdgesAroundIntersection(MapPoint mapPoint) {
        if (mapPoint.getType() != Type.INTERSECTION) {
            return new HashSet<>();
        }
        return getEdgesAroundIntersection(getIntersection(mapPoint));
    }

    @Override
    public Set<IEdge> getEdgesFromHex(MapPoint mapPoint) {
        return hexEdgeNetwork.incidentEdges(hexMap[mapPoint.getY()][mapPoint.getX()]);
    }

    @Override
    public IGameMap getGameMapDTO(Map<Player, UserOrDummy> playerUserMapping) {
        return new GameMapDTO(getHexesAsJaggedArray(), getIntersectionsWithEdges(playerUserMapping));
    }

    @Override
    public IHarbourHex.HarbourResource getHarbourResource(MapPoint point) {
        IIntersection intersection = getIntersection(point);
        if (!harbourResourceMap.containsKey(intersection)) return null;
        return harbourResourceMap.get(intersection);
    }

    @Override
    public IGameHex getHex(MapPoint position) {
        GameHexWrapper returnValue = getHexWrapper(position);
        return returnValue == null ? null : returnValue.get();
    }

    @Override
    public Set<MapPoint> getHex(int token) {
        ResourceHex hex;
        Set<MapPoint> mapPoints = new HashSet<>();
        MapPoint m;
        //Checks ResourceHexes with y=1 and y=5
        for (int i = 1; i < 4; i++) {
            m = HexMapPoint(1, i);
            hex = (ResourceHex) getHex(m);
            if (hex.getToken() == token) mapPoints.add(m);
            m = HexMapPoint(5, i);
            hex = (ResourceHex) getHex(m);
            if (hex.getToken() == token) mapPoints.add(m);
        }
        //Checks ResourceHexes with y=2 and y=4
        for (int i = 1; i < 5; i++) {
            m = HexMapPoint(2, i);
            hex = (ResourceHex) getHex(m);
            if (hex.getToken() == token) mapPoints.add(m);
            m = HexMapPoint(4, i);
            hex = (ResourceHex) getHex(m);
            if (hex.getToken() == token) mapPoints.add(m);
        }
        //Checks ResourceHexes with y=3
        for (int i : new int[]{1, 2, 4, 5}) {
            m = HexMapPoint(3, i);
            hex = (ResourceHex) getHex(m);
            if (hex.getToken() == token) mapPoints.add(m);
        }
        return mapPoints;
    }

    @Override
    public IGameHex[][] getHexesAsJaggedArray() {
        IGameHex[][] ret = new IGameHex[hexMap.length][];
        for (int i = 0; i < hexMap.length; i++) {
            ret[i] = new IGameHex[hexMap[i].length];
            for (int j = 0; j < hexMap[i].length; j++) {
                ret[i][j] = hexMap[i][j].get();
            }
        }
        return ret;
    }

    @Override
    public IIntersection getIntersection(MapPoint position) {
        return position.getType() == MapPoint.Type.INTERSECTION ? intersectionMap[position.getY()][position.getX()] :
               null;
    }

    @Override
    public MapPoint getIntersectionMapPoint(IIntersection intersection) {
        MapPoint mp;
        for (int i = 0; i <= 5; i++)
            for (int j = 0; j <= 10; j++) {
                //Why? see GameMapManagement.createIntersectionEdgeNetwork
                if ((i == 0 || i == 5) && j >= 7) break;
                else if ((i == 1 || i == 4) && j >= 9) break;
                mp = MapPoint.IntersectionMapPoint(i, j);
                if (intersection == getIntersection(mp)) return mp;
            }
        return null;
    }

    @Override
    public IIntersection[][] getIntersectionsAsJaggedArray() {
        return intersectionMap;
    }

    @Override
    public Set<IIntersection> getIntersectionsFromHex(MapPoint mapPoint) {
        return getIntersectionsFromEdges(getEdgesFromHex(mapPoint));
    }

    @Override
    public int getPlayerPoints(Player player) {
        if (!playerSettlementsAndCities.containsKey(player)) return 0;
        int points = 0;
        IIntersection.IntersectionState state;
        for (MapPoint point : playerSettlementsAndCities.get(player)) {
            state = intersectionMap[point.getY()][point.getX()].getState();
            if (state.equals(SETTLEMENT)) {
                points++;
            } else if (state.equals(CITY)) {
                points += 2;
            }
        }
        return points;
    }

    @Override
    public Map<Player, List<MapPoint>> getPlayerSettlementsAndCities() {
        return playerSettlementsAndCities;
    }

    @Override
    public Set<Player> getPlayersAroundHex(MapPoint mapPoint) {
        Set<Player> players = new HashSet<>();
        for (IIntersection i : getIntersectionsFromHex(mapPoint)) if (i.getOwner() != null) players.add(i.getOwner());
        return players;
    }

    @Override
    public MapPoint getRandomFreeIntersection(Game game, Player nextPlayer) {
        int randomYCoordinate = 0;
        int randomXCoordinate = 0;
        while (true) {
            randomYCoordinate = (int) (Math.random() * intersectionMap.length);
            randomXCoordinate = (int) (Math.random() * intersectionMap[randomYCoordinate].length);
            if (settlementPlaceableInFoundingPhase(nextPlayer, MapPoint.IntersectionMapPoint(randomYCoordinate,
                                                                                             randomXCoordinate))) {
                return MapPoint.IntersectionMapPoint(randomYCoordinate, randomXCoordinate);
            }
        }
    }

    @Override
    public IConfiguration getRandomisedConfiguration() {
        List<IHarbourHex.HarbourResource> harbourList = new ArrayList<>();
        harbourList.addAll(Collections.nCopies(4, IHarbourHex.HarbourResource.ANY));
        harbourList.addAll(Collections.nCopies(1, IHarbourHex.HarbourResource.GRAIN));
        harbourList.addAll(Collections.nCopies(1, IHarbourHex.HarbourResource.ORE));
        harbourList.addAll(Collections.nCopies(1, IHarbourHex.HarbourResource.LUMBER));
        harbourList.addAll(Collections.nCopies(1, IHarbourHex.HarbourResource.BRICK));
        harbourList.addAll(Collections.nCopies(1, IHarbourHex.HarbourResource.WOOL));
        List<ResourceType> hexList = new ArrayList<>();
        hexList.addAll(Collections.nCopies(4, ResourceType.LUMBER));
        hexList.addAll(Collections.nCopies(4, ResourceType.GRAIN));
        hexList.addAll(Collections.nCopies(3, ResourceType.ORE));
        hexList.addAll(Collections.nCopies(4, ResourceType.WOOL));
        hexList.addAll(Collections.nCopies(3, ResourceType.BRICK));
        List<Integer> tokenList = new ArrayList<>();
        tokenList.addAll(Collections.nCopies(1, 2));
        tokenList.addAll(Collections.nCopies(1, 12));
        tokenList.addAll(Collections.nCopies(2, 3));
        tokenList.addAll(Collections.nCopies(2, 4));
        tokenList.addAll(Collections.nCopies(2, 5));
        tokenList.addAll(Collections.nCopies(2, 6));
        tokenList.addAll(Collections.nCopies(2, 8));
        tokenList.addAll(Collections.nCopies(2, 9));
        tokenList.addAll(Collections.nCopies(2, 10));
        tokenList.addAll(Collections.nCopies(2, 11));
        Collections.shuffle(harbourList);
        Collections.shuffle(hexList);
        Collections.shuffle(tokenList);
        // wrapped as unmodifiable so it can be reliably retrieved.
        // Create new LinkedList objects with the Getter results when creating the map from a Configuration
        configuration = new Configuration(Collections.unmodifiableList(harbourList),
                                          Collections.unmodifiableList(hexList),
                                          Collections.unmodifiableList(tokenList), robberPosition);
        return configuration;
    }

    @Override
    public Set<MapPoint> getResourceHexesFromIntersection(MapPoint position) {
        Set<MapPoint> returnSet = new HashSet<>();
        Set<IEdge> iEdges = incidentEdges(getIntersection(position));
        for (IEdge edge : iEdges) {
            EndpointPair<GameHexWrapper> gameHexWrappers = hexEdgeNetwork.incidentNodes(edge);
            IGameHex iGameHex = gameHexWrappers.nodeU().get();
            IGameHex iGameHex1 = gameHexWrappers.nodeV().get();
            for (int i = 0; i < hexMap.length; i++) {
                for (int j = 0; j < hexMap[i].length; j++) {
                    IGameHex gameHex = hexMap[i][j].get();
                    if (gameHex.getType() != IGameHex.HexType.RESOURCE) continue;
                    if (gameHex.equals(iGameHex) || gameHex.equals(iGameHex1)) {
                        MapPoint hexPoint = HexMapPoint(i, j);
                        if (!hexPointContainedInSet(returnSet, hexPoint)) returnSet.add(hexPoint);
                    }
                }
            }
        }
        return returnSet;
    }

    @Override
    public MapPoint getRobberPosition() {
        return robberPosition;
    }

    @Override
    public Set<IEdge> incidentEdges(IIntersection intersection) {
        return intersectionEdgeNetwork.incidentEdges(intersection);
    }

    @Override
    public int longestRoadWith(MapPoint mapPoint) {
        /**
         * Local class path to store a path
         *
         * @author Temmo Junkhoff
         * @since 2021-05-04
         */
        class Path {

            private final int length;
            private final Set<IEdge> edges;

            /**
             * Instantiates a new Path.
             *
             * @param length The length of the path
             * @param edges  The edges defining the path
             *
             * @author Temmo Junkhoff
             * @since 2021-05-04
             */
            public Path(int length, Set<IEdge> edges) {
                this.length = length;
                this.edges = edges;
            }

            /**
             * Gets the edges.
             *
             * @return The edges
             *
             * @author Temmo Junkhoff
             * @since 2021-05-04
             */
            public Set<IEdge> getEdges() {
                return edges;
            }

            /**
             * Gets the length.
             *
             * @return The length
             *
             * @author Temmo Junkhoff
             * @since 2021-05-04
             */
            public int getLength() {
                return length;
            }
        }
        IEdge edge = getEdge(mapPoint);
        Set<IEdge> visited = new HashSet<>();
        visited.add(edge);
        Set<IEdge> nodeUEdges = new HashSet<>();
        Set<IEdge> nodeVEdges = new HashSet<>();

        //Function to calculate the lengths of the paths
        BiFunction<Set<IEdge>, Set<IEdge>, Integer> a = (leftNodeEdges, rightNodeEdges) -> {
            List<Integer> lengths = new LinkedList<>();
            List<Path> leftNodeLengths = new LinkedList<>();
            // Find the longest paths for the first side of the specified edge
            // and save them as a tuple of their length and their visited edges.
            for (IEdge nextEdge : leftNodeEdges) {
                Set<IEdge> temp = new HashSet<>(visited);
                leftNodeLengths
                        .add(new Path(roadLength(nextEdge, edge, null, edge.getOwner(), new HashSet<>(visited), temp),
                                      temp));
            }

            // Calculate all combinations of paths from both sides of the edge and store their length in a list
            lengths.add(0);
            if (leftNodeLengths.isEmpty()) {
                if (rightNodeEdges.isEmpty()) {
                    lengths.add(1);
                } else {
                    for (IEdge nextEdge : rightNodeEdges) {
                        lengths.add(1 + roadLength(nextEdge, edge, null, edge.getOwner(), new HashSet<>(visited),
                                                   new HashSet<>(visited)));
                    }
                }
            } else {
                for (Path x : leftNodeLengths) {
                    if (rightNodeEdges.isEmpty()) {
                        lengths.add(x.getLength() + 1);
                    }
                    for (IEdge nextEdge : rightNodeEdges) {
                        lengths.add(x.getLength() + 1 + roadLength(nextEdge, edge, null, edge.getOwner(), x.getEdges(),
                                                                   new HashSet<>(visited)));
                    }
                }
            }
            return Collections.max(lengths);
        };

        // Put all edges around the edge specified by the map point in Sets
        // depending on the intersection they share with the specified edge.

        {
            EndpointPair<IIntersection> nodes = intersectionEdgeNetwork.incidentNodes(edge);
            Outer:
            for (IEdge nextEdge : intersectionEdgeNetwork.adjacentEdges(edge)) {
                for (IEdge d : intersectionEdgeNetwork.incidentEdges(nodes.nodeU())) {
                    if (d == nextEdge) {
                        nodeUEdges.add(nextEdge);
                        continue Outer;
                    }
                }
                for (IEdge d : intersectionEdgeNetwork.incidentEdges(nodes.nodeV())) {
                    if (d == nextEdge) {
                        nodeVEdges.add(nextEdge);
                        continue Outer;
                    }
                }
            }
        }
        List<Integer> lengths = new LinkedList<>();
        lengths.add(a.apply(nodeUEdges, nodeVEdges));
        lengths.add(a.apply(nodeVEdges, nodeUEdges));

        // Return the length of the longest path found
        return Collections.max(lengths);
    }

    @Override
    public void makeBeginnerSettlementsAndRoads(int playerCount) {
        createPlayerSettlementsAndCitiesMap(playerCount);
        //Create settlements
        intersectionMap[1][3].setOwnerAndState(Player.PLAYER_1, SETTLEMENT);
        playerSettlementsAndCities.get(Player.PLAYER_1).add(IntersectionMapPoint(1, 3));
        intersectionMap[3][2].setOwnerAndState(Player.PLAYER_1, SETTLEMENT);
        playerSettlementsAndCities.get(Player.PLAYER_1).add(IntersectionMapPoint(3, 2));
        intersectionMap[1][6].setOwnerAndState(Player.PLAYER_2, SETTLEMENT);
        playerSettlementsAndCities.get(Player.PLAYER_2).add(IntersectionMapPoint(1, 6));
        intersectionMap[4][4].setOwnerAndState(Player.PLAYER_2, SETTLEMENT);
        playerSettlementsAndCities.get(Player.PLAYER_2).add(IntersectionMapPoint(4, 4));
        intersectionMap[2][3].setOwnerAndState(Player.PLAYER_3, SETTLEMENT);
        playerSettlementsAndCities.get(Player.PLAYER_3).add(IntersectionMapPoint(2, 3));
        intersectionMap[3][8].setOwnerAndState(Player.PLAYER_3, SETTLEMENT);
        playerSettlementsAndCities.get(Player.PLAYER_3).add(IntersectionMapPoint(3, 8));

        //Create roads
        {
            MapPoint road = EdgeMapPoint(IntersectionMapPoint(1, 3), IntersectionMapPoint(1, 4));
            placeRoad(Player.PLAYER_1, road);
            foundingRoads.add(road);
        }
        {
            MapPoint road = EdgeMapPoint(IntersectionMapPoint(3, 2), IntersectionMapPoint(3, 3));
            placeRoad(Player.PLAYER_1, road);
            foundingRoads.add(road);
        }
        {
            MapPoint road = EdgeMapPoint(IntersectionMapPoint(1, 5), IntersectionMapPoint(1, 6));
            placeRoad(Player.PLAYER_2, road);
            foundingRoads.add(road);
        }
        {
            MapPoint road = EdgeMapPoint(IntersectionMapPoint(4, 4), IntersectionMapPoint(4, 5));
            placeRoad(Player.PLAYER_2, road);
            foundingRoads.add(road);
        }
        {
            MapPoint road = EdgeMapPoint(IntersectionMapPoint(3, 8), IntersectionMapPoint(2, 8));
            placeRoad(Player.PLAYER_3, road);
            foundingRoads.add(road);
        }
        {
            MapPoint road = EdgeMapPoint(IntersectionMapPoint(2, 2), IntersectionMapPoint(2, 3));
            placeRoad(Player.PLAYER_3, road);
            foundingRoads.add(road);
        }
        // For 4 players, create more settlements and roads
        if (playerCount == 4) {
            intersectionMap[4][2].setOwnerAndState(Player.PLAYER_4, SETTLEMENT);
            playerSettlementsAndCities.get(Player.PLAYER_4).add(IntersectionMapPoint(4, 2));
            intersectionMap[4][6].setOwnerAndState(Player.PLAYER_4, SETTLEMENT);
            playerSettlementsAndCities.get(Player.PLAYER_4).add(IntersectionMapPoint(4, 6));
            {
                MapPoint road = EdgeMapPoint(IntersectionMapPoint(4, 2), IntersectionMapPoint(4, 3));
                placeRoad(Player.PLAYER_4, road);
                foundingRoads.add(road);
            }
            {
                MapPoint road = EdgeMapPoint(IntersectionMapPoint(4, 6), IntersectionMapPoint(3, 7));
                placeRoad(Player.PLAYER_4, road);
                foundingRoads.add(road);
            }
        }
    }

    @Override
    public void moveRobber(MapPoint newPosition) {
        if (newPosition.getType() != MapPoint.Type.HEX)
            throw new IllegalArgumentException("The robber can only move to a hex");
        hexMap[robberPosition.getY()][robberPosition.getX()].get().setRobberOnField(false);
        robberPosition = newPosition;
        hexMap[robberPosition.getY()][robberPosition.getX()].get().setRobberOnField(true);
    }

    @Override
    public boolean placeFoundingSettlement(Player player, MapPoint position) {
        boolean neighbouringIntersectionsFree = true;
        for (IIntersection intersection : intersectionEdgeNetwork
                .adjacentNodes(intersectionMap[position.getY()][position.getX()]))
            if (intersection.getState() != IIntersection.IntersectionState.FREE) neighbouringIntersectionsFree = false;

        IIntersection.IntersectionState state = intersectionMap[position.getY()][position.getX()].getState();
        if (!state.equals(IIntersection.IntersectionState.FREE) || !neighbouringIntersectionsFree) return false;

        if (!playerSettlementsAndCities.containsKey(player)) playerSettlementsAndCities.put(player, new ArrayList<>());
        playerSettlementsAndCities.get(player).add(position);
        intersectionMap[position.getY()][position.getX()].setOwnerAndState(player, SETTLEMENT);
        return true;
    }

    @Override
    public boolean placeRoad(Player player, IEdge edge) {
        if (roadPlaceable(player, edge)) {
            edge.buildRoad(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean placeRoad(Player player, MapPoint mapPoint) {
        return placeRoad(player, getEdge(mapPoint));
    }

    @Override
    public boolean placeSettlement(Player player,
                                   MapPoint position) throws SettlementMightInterfereWithLongestRoadException {
        if (position.getType() != MapPoint.Type.INTERSECTION) return false;
        if (settlementPlaceable(player, position)) {
            if (!playerSettlementsAndCities.containsKey(player))
                playerSettlementsAndCities.put(player, new ArrayList<>());
            playerSettlementsAndCities.get(player).add(position);
            intersectionMap[position.getY()][position.getX()].setOwnerAndState(player, SETTLEMENT);
            for (IEdge x : intersectionEdgeNetwork.incidentEdges(getIntersection(position))) {
                if (x.getOwner() != player) {
                    throw new SettlementMightInterfereWithLongestRoadException();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean roadPlaceable(Player player, MapPoint mapPoint) {
        return roadPlaceable(player, getEdge(mapPoint));
    }

    @Override
    public boolean roadPlaceable(Player player, IEdge edge) {
        if (edge == null) return false;
        if (edge.getOwner() == null) {
            for (IEdge adjacentEdge : intersectionEdgeNetwork.adjacentEdges(edge)) {
                if (adjacentEdge.getOwner() == player) return true;
            }

            for (IIntersection incidentIntersection : intersectionEdgeNetwork.incidentNodes(edge)) {
                if (incidentIntersection.getOwner() == player) return true;
            }
        }
        return false;
    }

    @Override
    public boolean settlementPlaceable(Player player, MapPoint position) {
        boolean hasRoad = false;
        boolean neighbouringIntersectionsFree = true;
        if (position.getType() != MapPoint.Type.INTERSECTION) return false;
        for (IEdge edge : intersectionEdgeNetwork.incidentEdges(intersectionMap[position.getY()][position.getX()]))
            if (edge.getOwner() == player) {
                hasRoad = true;
                break;
            }
        for (IIntersection intersection : intersectionEdgeNetwork
                .adjacentNodes(intersectionMap[position.getY()][position.getX()]))
            if (intersection.getState() != IIntersection.IntersectionState.FREE) {
                neighbouringIntersectionsFree = false;
                break;
            }

        return intersectionMap[position.getY()][position.getX()].getState()
                                                                .equals(IIntersection.IntersectionState.FREE) && hasRoad && neighbouringIntersectionsFree;
    }

    @Override
    public boolean settlementPlaceableInFoundingPhase(Player player, MapPoint position) {
        boolean neighbouringIntersectionsFree = true;
        if (position.getType() != MapPoint.Type.INTERSECTION) return false;
        for (IIntersection intersection : intersectionEdgeNetwork
                .adjacentNodes(intersectionMap[position.getY()][position.getX()]))
            if (intersection.getState() != IIntersection.IntersectionState.FREE) {
                neighbouringIntersectionsFree = false;
                break;
            }

        return intersectionMap[position.getY()][position.getX()].getState()
                                                                .equals(IIntersection.IntersectionState.FREE) && neighbouringIntersectionsFree;
    }

    @Override
    public boolean settlementUpgradeable(Player player, MapPoint position) {
        if (position.getType() != MapPoint.Type.INTERSECTION) return false;
        return (intersectionMap[position.getY()][position.getX()].getState() == SETTLEMENT && intersectionMap[position
                .getY()][position.getX()].getOwner() == player);
    }

    @Override
    public boolean upgradeSettlement(Player player, MapPoint position) {
        if (position.getType() != MapPoint.Type.INTERSECTION) return false;
        if (intersectionMap[position.getY()][position.getX()].getState() == SETTLEMENT && intersectionMap[position
                .getY()][position.getX()].getOwner() == player) {
            intersectionMap[position.getY()][position.getX()]
                    .setOwnerAndState(player, IIntersection.IntersectionState.CITY);
            return true;
        }
        return false;
    }

    void setHex(MapPoint position, IGameHex newHex) {
        if (position.getType() != MapPoint.Type.HEX)
            throw new IllegalArgumentException("MapPoint should point to a hex");
        hexMap[position.getY()][position.getX()].set(newHex);
    }

    /**
     * Helper method to fill the harbourResourceMap
     * <p>
     * This method goes through the whole game map and gets all the
     * harbours with the according intersections and puts them into a
     * map
     *
     * @author Maximilian Lindner
     * @author Steven Luong
     * @since 2021-04-07
     */
    private void createHarbourResourceMap() {
        for (GameHexWrapper[] gameHexWrappers : hexMap) {
            for (GameHexWrapper hex : gameHexWrappers) {
                if (hex.get().getType().equals(IGameHex.HexType.HARBOUR)) {
                    HarbourHex harbourHex = (HarbourHex) hex.get();
                    EndpointPair<IIntersection> iIntersections = getIntersectionsBetweenHexes(hex, harbourHex
                            .getBelongingHex());
                    if (iIntersections == null) continue;
                    harbourResourceMap.put(iIntersections.nodeU(), harbourHex.getResource());
                    harbourResourceMap.put(iIntersections.nodeV(), harbourHex.getResource());
                }
            }
        }
    }

    /**
     * Creates a HexEdgeNetwork
     *
     * @author Temmo Junkhoff
     * @since 2021-03-05
     */
    private void createHexEdgeNetwork() {
        // @formatter:off
        var hexEdgeNetworkBuilder = NetworkBuilder.undirected().allowsParallelEdges(false)
                .nodeOrder(ElementOrder.insertion()).expectedNodeCount(37)
                .expectedEdgeCount(72).<GameHexWrapper, IEdge>immutable();
        // @formatter:on
        hexMap = new GameHexWrapper[7][];
        hexMap[0] = new GameHexWrapper[4];
        hexMap[1] = new GameHexWrapper[5];
        hexMap[2] = new GameHexWrapper[6];
        hexMap[3] = new GameHexWrapper[7];
        hexMap[4] = new GameHexWrapper[6];
        hexMap[5] = new GameHexWrapper[5];
        hexMap[6] = new GameHexWrapper[4];

        for (int y = 0; y < hexMap.length; y++)
            for (int x = 0; x < hexMap[y].length; x++)
                hexMap[y][x] = new GameHexWrapper();

        for (int y = 0; y < hexMap.length; y++) {
            for (int x = 0; x < hexMap[y].length; x++) {
                hexEdgeNetworkBuilder.addNode(hexMap[y][x]);
                if (y > 0 && x > 0 && y < hexMap.length - 1) {
                    hexEdgeNetworkBuilder.addEdge(hexMap[y][x], hexMap[y][x - 1], new Edge(IEdge.Orientation.SOUTH));
                }
                if (y > 0 && y <= hexMap.length / 2 && x > 0 && x < hexMap[y].length - 1) {
                    hexEdgeNetworkBuilder.addEdge(hexMap[y][x], hexMap[y - 1][x - 1], new Edge(IEdge.Orientation.EAST));
                }
                if (y > 0 && y <= hexMap.length / 2 && x > 0 && x < hexMap[y - 1].length) {
                    hexEdgeNetworkBuilder.addEdge(hexMap[y][x], hexMap[y - 1][x], new Edge(IEdge.Orientation.WEST));
                }
                if (y > hexMap.length / 2 && x > 0) {
                    hexEdgeNetworkBuilder.addEdge(hexMap[y][x], hexMap[y - 1][x], new Edge(IEdge.Orientation.EAST));
                }
                if (y > hexMap.length / 2 && x < hexMap[y - 1].length - 1 && x < hexMap[y].length - 1) {
                    hexEdgeNetworkBuilder.addEdge(hexMap[y][x], hexMap[y - 1][x + 1], new Edge(IEdge.Orientation.WEST));
                }
            }
        }
        hexEdgeNetwork = hexEdgeNetworkBuilder.build();
    }

    /**
     * Creates an IntersectionEdgeNetwork
     *
     * @author Temmo Junkhoff
     * @since 2021-03-05
     */
    private void createIntersectionEdgeNetwork() {
        var intersectionEdgeNetworkBuilder = NetworkBuilder.undirected().allowsParallelEdges(false)
                                                           .nodeOrder(ElementOrder.insertion()).expectedNodeCount(54)
                                                           .expectedEdgeCount(72).<IIntersection, IEdge>immutable();

        intersectionMap = new IIntersection[6][];
        intersectionMap[0] = new IIntersection[7];
        intersectionMap[1] = new IIntersection[9];
        intersectionMap[2] = new IIntersection[11];
        intersectionMap[3] = new IIntersection[11];
        intersectionMap[4] = new IIntersection[9];
        intersectionMap[5] = new IIntersection[7];

        for (int y = 0; y < intersectionMap.length; y++)
            for (int x = 0; x < intersectionMap[y].length; x++)
                intersectionMap[y][x] = new Intersection();

        for (int y = 0; y < intersectionMap.length; y++) {
            for (int x = 0; x < intersectionMap[y].length; x++) {
                try {
                    intersectionEdgeNetworkBuilder.addNode(intersectionMap[y][x]);
                    //Connections to the top
                    if (x % 2 == 0 && y == 3) {
                        intersectionEdgeNetworkBuilder.addEdge(intersectionMap[y][x], intersectionMap[y - 1][x],
                                                               hexEdgeNetwork.edgeConnectingOrNull(hexMap[y][x / 2],
                                                                                                   hexMap[y][(x / 2) + 1]));
                    } else if (x % 2 == 1 && y > 0 && y < 3) {
                        intersectionEdgeNetworkBuilder.addEdge(intersectionMap[y][x], intersectionMap[y - 1][x - 1],
                                                               hexEdgeNetwork
                                                                       .edgeConnectingOrNull(hexMap[y][(x - 1) / 2],
                                                                                             hexMap[y][(x + 1) / 2]));
                    } else if (x % 2 == 0 && y > 3) {
                        intersectionEdgeNetworkBuilder.addEdge(intersectionMap[y][x], intersectionMap[y - 1][x + 1],
                                                               hexEdgeNetwork.edgeConnectingOrNull(hexMap[y][x / 2],
                                                                                                   hexMap[y][(x / 2) + 1]));
                    }

                    //connections from high point to low point to the left
                    if (x % 2 == 1 && y < 3) {
                        intersectionEdgeNetworkBuilder.addEdge(intersectionMap[y][x], //
                                                               intersectionMap[y][x - 1], //
                                                               hexEdgeNetwork
                                                                       .edgeConnectingOrNull(hexMap[y][(x - 1) / 2],
                                                                                             hexMap[y + 1][(x + 1) / 2]));
                    } else if (y >= 3 && x % 2 == 0 && x > 1) {
                        intersectionEdgeNetworkBuilder.addEdge(intersectionMap[y][x], intersectionMap[y][x - 1],
                                                               hexEdgeNetwork.edgeConnectingOrNull(hexMap[y][x / 2],
                                                                                                   hexMap[y + 1][x / 2]));
                    }

                    //connections from low point to high point to the left
                    if ((x % 2 == 0 && x > 0 && y < 3)) {
                        intersectionEdgeNetworkBuilder.addEdge(intersectionMap[y][x], intersectionMap[y][x - 1],
                                                               hexEdgeNetwork.edgeConnectingOrNull(hexMap[y][x / 2],
                                                                                                   hexMap[y + 1][x / 2]));
                    } else if ((x % 2 == 1 && y >= 3)) {
                        intersectionEdgeNetworkBuilder.addEdge(intersectionMap[y][x], intersectionMap[y][x - 1],
                                                               hexEdgeNetwork
                                                                       .edgeConnectingOrNull(hexMap[y][(x / 2) + 1],
                                                                                             hexMap[y + 1][x / 2]));
                    }
                } catch (NullPointerException ignored) {
                    System.err.println("Error, I guess.");
                }
            }
        }
        intersectionEdgeNetwork = intersectionEdgeNetworkBuilder.build();
    }

    /**
     * Helper method to create the playerSettlementsAndCities Map according to
     * the amount of players.
     *
     * @param playerCount Amount of players in the according game
     *
     * @author Steven Luong
     * @author Maximilian Lindner
     * @since 2021-04-07
     */
    private void createPlayerSettlementsAndCitiesMap(int playerCount) {
        for (int i = 0; i < playerCount; i++) {
            playerSettlementsAndCities.put(Player.values()[i], new ArrayList<>());
        }
    }

    /**
     * Helper method to get the GameHexWrapper of a Hex instead of the IGameHex
     *
     * @param position The MapPoint of the Hex
     *
     * @return GameHexWrapper containing the Hex, or null
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @since 2021-03-23
     */
    private GameHexWrapper getHexWrapper(MapPoint position) {
        return position.getType() == MapPoint.Type.HEX ? hexMap[position.getY()][position.getX()] : null;
    }

    /**
     * Helper method to get a Pair of Intersections between 2 Hexes
     *
     * @param point1 First Hex
     * @param point2 Second Hex
     *
     * @return An EndpointPair of Intersections
     *
     * @author Maximilian Lindner
     * @author Steven Luong
     * @since 2021-04-07
     */
    private EndpointPair<IIntersection> getIntersectionsBetweenHexes(GameHexWrapper point1, GameHexWrapper point2) {
        Optional<IEdge> edge = hexEdgeNetwork.edgeConnecting(point1, point2);
        return edge.map(iEdge -> intersectionEdgeNetwork.incidentNodes(iEdge)).orElse(null);
    }

    /**
     * Helper method for getIntersectionFromHexes
     *
     * @param set Set of edges
     *
     * @return Set of intersections
     *
     * @author Mario Fokken
     * @since 2021-03-15
     */
    private Set<IIntersection> getIntersectionsFromEdges(Set<IEdge> set) {
        Set<IIntersection> intersectionSet = new HashSet<>();
        for (IEdge edge : set) {
            for (IIntersection i : intersectionEdgeNetwork.incidentNodes(edge)) {
                intersectionSet.add(i);
            }
        }
        return intersectionSet;
    }

    /**
     * Helper method to return an jagged array of IntersectionWithEdges
     *
     * @return An jagged array of IntersectionWithEdges
     *
     * @author Temmo Junkhoff
     * @since 2021-04-08
     */
    private IntersectionWithEdges[][] getIntersectionsWithEdges(Map<Player, UserOrDummy> playerUserMapping) {
        IntersectionWithEdges[][] returnMap;
        returnMap = new IntersectionWithEdges[6][];
        returnMap[0] = new IntersectionWithEdges[7];
        returnMap[1] = new IntersectionWithEdges[9];
        returnMap[2] = new IntersectionWithEdges[11];
        returnMap[3] = new IntersectionWithEdges[11];
        returnMap[4] = new IntersectionWithEdges[9];
        returnMap[5] = new IntersectionWithEdges[7];
        for (int y = 0; y < intersectionMap.length; y++) {
            for (int x = 0; x < intersectionMap[y].length; x++) {
                var a = intersectionMap[y][x];
                var b = incidentEdges(intersectionMap[y][x]);
                Set<IEdgeWithBuildable> c = new HashSet<>();
                for (var d : b) {
                    c.add(new EdgeWithBuildable(d.getOrientation(), d.getOwner(),
                                                getWhoCanBuildAt(d, playerUserMapping)));
                }
                returnMap[y][x] = new IntersectionWithEdges(new IntersectionWithBuildable(a.getOwner(), a.getState(),
                                                                                          getWhoCanBuildAt(
                                                                                                  IntersectionMapPoint(
                                                                                                          y, x),
                                                                                                  playerUserMapping)),
                                                            c);
            }
        }
        return returnMap;
    }

    /**
     * Returns a list of users that can build on a given edge
     *
     * @param edge              The edge
     * @param playerUserMapping A mapping of players to users
     *
     * @return A list of users that can build on the given edge.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-25
     */
    private List<UserOrDummy> getWhoCanBuildAt(IEdge edge, Map<Player, UserOrDummy> playerUserMapping) {
        var temp = new LinkedList<UserOrDummy>();
        for (Player player : Player.values()) {
            if (roadPlaceable(player, edge)) temp.add(playerUserMapping.get(player));
        }
        return temp;
    }

    /**
     * Returns a list of user that can build on a intersection given by a MapPoint
     *
     * @param mapPoint          The MapPoint
     * @param playerUserMapping A mapping of players to users
     *
     * @return A list of users that can build on the given intersection
     *
     * @author Temmo Junkhoff
     * @since 2021-04-25
     */
    private List<UserOrDummy> getWhoCanBuildAt(MapPoint mapPoint, Map<Player, UserOrDummy> playerUserMapping) {
        var temp = new LinkedList<UserOrDummy>();
        for (Player player : Player.values()) {
            if (settlementPlaceable(player, mapPoint) || settlementUpgradeable(player, mapPoint))
                temp.add(playerUserMapping.get(player));
        }
        return temp;
    }

    /**
     * Helper method to check whether a given MapPoint is contained in a given Set
     *
     * @param set   The set to check
     * @param point The MapPoint to look for
     *
     * @return true if the MapPoint is represented in the set, false otherwise
     *
     * @author Phillip-André Suhr
     * @since 2021-05-02
     */
    private boolean hexPointContainedInSet(Set<MapPoint> set, MapPoint point) {
        boolean contained = false;
        for (MapPoint setPoint : set) {
            if (setPoint.getX() == point.getX() && setPoint.getY() == point.getY()) contained = true;
        }
        return contained;
    }

    /**
     * A helper method for the recursion of longestRoadsForEachPlayer
     *
     * @param currentEdge        The current edge
     * @param previousEdge       The previous edge
     * @param secondPreviousEdge The second previous edge
     * @param owner              The owner
     * @param visited            The visited nodes for the current branch
     * @param allVisited         All visited nodes
     *
     * @return The maximum road length found
     *
     * @author Temmo Junkhoff
     * @since 2021-04-10
     */
    private int roadLength(IEdge currentEdge, IEdge previousEdge, IEdge secondPreviousEdge, Player owner,
                           Set<IEdge> visited, Set<IEdge> allVisited) {
        if (!Objects.equals(currentEdge.getOwner(), owner)) return 0;
        if (secondPreviousEdge == currentEdge) return 0;
        if (visited.contains(currentEdge)) return 0;

        // Find the intersection between currentEdge and previousEdge
        // and check if it is free or belongs to the specified owner
        {
            Set<IIntersection> c = new HashSet<>();
            Set<IIntersection> d = new HashSet<>();
            {
                var a = intersectionEdgeNetwork.incidentNodes(currentEdge);
                var b = intersectionEdgeNetwork.incidentNodes(previousEdge);
                c.add(a.nodeU());
                c.add(a.nodeV());
                d.add(b.nodeU());
                d.add(b.nodeV());
            }

            c.retainAll(d);
            IIntersection crossedNode = (new LinkedList<>(c)).get(0);
            if (crossedNode.getState() != FREE && crossedNode.getOwner() != owner) return 0;
        }

        // Check if the second previous edge is a neighbour of the current edge
        if (secondPreviousEdge != null) {
            if (intersectionEdgeNetwork.adjacentEdges(currentEdge).contains(secondPreviousEdge)) {
                return 0;
            }
        }

        // Add the current edge to visited and allVisited
        visited.add(currentEdge);
        allVisited.add(currentEdge);

        // Check the maximum road length reachable by all neighbours and find the highest value
        Set<Integer> lengths = new HashSet<>();
        for (IEdge nextEdge : intersectionEdgeNetwork.adjacentEdges(currentEdge)) {
            lengths.add(roadLength(nextEdge, currentEdge, previousEdge, owner, new HashSet<>(visited), allVisited));
        }
        int maxLength = lengths.isEmpty() ? 0 : Collections.max(lengths);
        return maxLength + 1;
    }

    /**
     * A Player with the length of the longest road.
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    public class PlayerWithLengthOfLongestRoad {

        private final Player player;
        private final int length;

        /**
         * Instantiates a new Player with length of longest road.
         *
         * @param player the player
         * @param length the length
         *
         * @author Temmo Junkhoff
         * @since 2021-05-03
         */
        public PlayerWithLengthOfLongestRoad(Player player, int length) {
            this.player = player;
            this.length = length;
        }

        /**
         * Gets the length.
         *
         * @return The length
         *
         * @author Temmo Junkhoff
         * @since 2021-05-03
         */
        public int getLength() {
            return length;
        }

        /**
         * Gets the player.
         *
         * @return The player
         *
         * @author Temmo Junkhoff
         * @since 2021-05-03
         */
        public Player getPlayer() {
            return player;
        }
    }

    /**
     * The Exception Settlement might interfere with longest road exception.
     * Used to indicate that the longest road needs to be rechecked.
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    public class SettlementMightInterfereWithLongestRoadException extends Exception {

        /**
         * Instantiates a new Settlement might interfere with longest road exception.
         *
         * @author Temmo Junkhoff
         * @since 2021-05-03
         */
        SettlementMightInterfereWithLongestRoadException() {
            super();
        }
    }
}
