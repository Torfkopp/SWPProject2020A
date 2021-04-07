package de.uol.swp.common.game.map;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.ImmutableNetwork;
import com.google.common.graph.NetworkBuilder;
import de.uol.swp.common.game.map.Hexes.*;
import de.uol.swp.common.game.map.configuration.Configuration;
import de.uol.swp.common.game.map.configuration.IConfiguration;

import java.util.*;

import static de.uol.swp.common.game.map.IIntersection.IntersectionState.CITY;
import static de.uol.swp.common.game.map.IIntersection.IntersectionState.SETTLEMENT;
import static de.uol.swp.common.game.map.MapPoint.*;

/**
 * Management of the gameMap
 *
 * @author Marvin Drees
 * @author Temmo Junkhoff
 * @since 2021-01-16
 */
@SuppressWarnings("UnstableApiUsage")
public class GameMap implements IGameMap {

    //Map mapping the player and his settlements/cities
    private final Map<Player, List<MapPoint>> playerSettlementsAndCities = new HashMap<>();
    private MapPoint robberPosition = HexMapPoint(3, 3);
    private GameHexWrapper[][] hexMap;
    private IIntersection[][] intersectionMap;
    private ImmutableNetwork<GameHexWrapper, IEdge> hexEdgeNetwork;
    private ImmutableNetwork<IIntersection, IEdge> intersectionEdgeNetwork;
    private IConfiguration configuration;

    /**
     * Constructor
     */
    public GameMap() {
        createHexEdgeNetwork();
        createIntersectionEdgeNetwork();
        hexMap[robberPosition.getX()][robberPosition.getY()].get().setRobberOnField(false);
    }

    @Override
    public IGameMap createMapFromConfiguration(IConfiguration configuration) {
        this.configuration = configuration;
        // create new LinkedLists because lists are transmitted ordered and read-only in the IConfiguration
        List<IHarborHex.HarborResource> harborList = new LinkedList<>(configuration.getHarborList());
        List<IResourceHex.ResourceHexType> hexList = new LinkedList<>(configuration.getHexList());
        List<Integer> tokenList = new LinkedList<>(configuration.getTokenList());
        hexMap[0][0].set(new HarborHex(hexMap[1][1], IHarborHex.HarborSide.SOUTHEAST, harborList.remove(0)));
        hexMap[0][1].set(new WaterHex());
        hexMap[0][2].set(new HarborHex(hexMap[1][2], IHarborHex.HarborSide.SOUTHWEST, harborList.remove(0)));
        hexMap[0][3].set(new WaterHex());
        hexMap[1][0].set(new WaterHex());
        hexMap[1][1].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[1][2].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[1][3].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[1][4].set(new HarborHex(hexMap[2][4], IHarborHex.HarborSide.SOUTHWEST, harborList.remove(0)));
        hexMap[2][0].set(new HarborHex(hexMap[2][1], IHarborHex.HarborSide.EAST, harborList.remove(0)));
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
        hexMap[3][6].set(new HarborHex(hexMap[3][6], IHarborHex.HarborSide.WEST, harborList.remove(0)));
        hexMap[4][0].set(new HarborHex(hexMap[4][1], IHarborHex.HarborSide.EAST, harborList.remove(0)));
        hexMap[4][1].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[4][2].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[4][3].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[4][4].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[4][5].set(new WaterHex());
        hexMap[5][0].set(new WaterHex());
        hexMap[5][1].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[5][2].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[5][3].set(new ResourceHex(hexList.remove(0), tokenList.remove(0)));
        hexMap[5][4].set(new HarborHex(hexMap[4][4], IHarborHex.HarborSide.NORTHWEST, harborList.remove(0)));
        hexMap[6][0].set(new HarborHex(hexMap[5][1], IHarborHex.HarborSide.NORTHEAST, harborList.remove(0)));
        hexMap[6][1].set(new WaterHex());
        hexMap[6][2].set(new HarborHex(hexMap[5][2], IHarborHex.HarborSide.NORTHWEST, harborList.remove(0)));
        hexMap[6][3].set(new WaterHex());

        return this;
    }

    @Override
    public IConfiguration getBeginnerConfiguration() {
        List<IResourceHex.ResourceHexType> hexList = new LinkedList<>();
        hexList.add(IResourceHex.ResourceHexType.MOUNTAINS);
        hexList.add(IResourceHex.ResourceHexType.PASTURE);
        hexList.add(IResourceHex.ResourceHexType.FOREST);
        hexList.add(IResourceHex.ResourceHexType.FIELDS);
        hexList.add(IResourceHex.ResourceHexType.HILLS);
        hexList.add(IResourceHex.ResourceHexType.PASTURE);
        hexList.add(IResourceHex.ResourceHexType.HILLS);
        hexList.add(IResourceHex.ResourceHexType.FIELDS);
        hexList.add(IResourceHex.ResourceHexType.FOREST);
        hexList.add(IResourceHex.ResourceHexType.FOREST);
        hexList.add(IResourceHex.ResourceHexType.MOUNTAINS);
        hexList.add(IResourceHex.ResourceHexType.FOREST);
        hexList.add(IResourceHex.ResourceHexType.MOUNTAINS);
        hexList.add(IResourceHex.ResourceHexType.FIELDS);
        hexList.add(IResourceHex.ResourceHexType.PASTURE);
        hexList.add(IResourceHex.ResourceHexType.HILLS);
        hexList.add(IResourceHex.ResourceHexType.FIELDS);
        hexList.add(IResourceHex.ResourceHexType.PASTURE);
        List<IHarborHex.HarborResource> harborList = new LinkedList<>();
        harborList.add(IHarborHex.HarborResource.ANY);
        harborList.add(IHarborHex.HarborResource.GRAIN);
        harborList.add(IHarborHex.HarborResource.ORE);
        harborList.add(IHarborHex.HarborResource.LUMBER);
        harborList.add(IHarborHex.HarborResource.ANY);
        harborList.add(IHarborHex.HarborResource.BRICK);
        harborList.add(IHarborHex.HarborResource.WOOL);
        harborList.add(IHarborHex.HarborResource.ANY);
        harborList.add(IHarborHex.HarborResource.ANY);
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
        configuration = new Configuration(Collections.unmodifiableList(harborList),
                                          Collections.unmodifiableList(hexList),
                                          Collections.unmodifiableList(tokenList));
        return configuration;
    }

    @Override
    public IConfiguration getCurrentConfiguration() {
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
    public Set<IEdge> getEdgesFromHex(MapPoint mapPoint) {
        return hexEdgeNetwork.incidentEdges(hexMap[mapPoint.getY()][mapPoint.getX()]);
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
    public IConfiguration getRandomisedConfiguration() {
        List<IHarborHex.HarborResource> harborList = new ArrayList<>();
        harborList.addAll(Collections.nCopies(4, IHarborHex.HarborResource.ANY));
        harborList.addAll(Collections.nCopies(1, IHarborHex.HarborResource.GRAIN));
        harborList.addAll(Collections.nCopies(1, IHarborHex.HarborResource.ORE));
        harborList.addAll(Collections.nCopies(1, IHarborHex.HarborResource.LUMBER));
        harborList.addAll(Collections.nCopies(1, IHarborHex.HarborResource.BRICK));
        harborList.addAll(Collections.nCopies(1, IHarborHex.HarborResource.WOOL));
        List<IResourceHex.ResourceHexType> hexList = new ArrayList<>();
        hexList.addAll(Collections.nCopies(4, IResourceHex.ResourceHexType.FOREST));
        hexList.addAll(Collections.nCopies(4, IResourceHex.ResourceHexType.FIELDS));
        hexList.addAll(Collections.nCopies(3, IResourceHex.ResourceHexType.MOUNTAINS));
        hexList.addAll(Collections.nCopies(4, IResourceHex.ResourceHexType.PASTURE));
        hexList.addAll(Collections.nCopies(3, IResourceHex.ResourceHexType.HILLS));
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
        Collections.shuffle(harborList);
        Collections.shuffle(hexList);
        Collections.shuffle(tokenList);
        // wrapped as unmodifiable so it can be reliably retrieved.
        // Create new LinkedList objects with the Getter results when creating the map from a Configuration
        configuration = new Configuration(Collections.unmodifiableList(harborList),
                                          Collections.unmodifiableList(hexList),
                                          Collections.unmodifiableList(tokenList));
        return configuration;
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
    public void makeBeginnerSettlementsAndRoads(int playerCount) {
        //Create settlements
        intersectionMap[1][3].setOwnerAndState(Player.PLAYER_1, SETTLEMENT);
        intersectionMap[3][2].setOwnerAndState(Player.PLAYER_1, SETTLEMENT);
        intersectionMap[1][6].setOwnerAndState(Player.PLAYER_2, SETTLEMENT);
        intersectionMap[4][4].setOwnerAndState(Player.PLAYER_2, SETTLEMENT);
        intersectionMap[2][3].setOwnerAndState(Player.PLAYER_3, SETTLEMENT);
        intersectionMap[3][8].setOwnerAndState(Player.PLAYER_3, SETTLEMENT);

        //Create roads
        placeRoad(Player.PLAYER_1, getEdge(EdgeMapPoint(IntersectionMapPoint(1, 3), IntersectionMapPoint(1, 4))));
        placeRoad(Player.PLAYER_1, getEdge(EdgeMapPoint(IntersectionMapPoint(3, 2), IntersectionMapPoint(3, 3))));
        placeRoad(Player.PLAYER_2, getEdge(EdgeMapPoint(IntersectionMapPoint(1, 5), IntersectionMapPoint(1, 6))));
        placeRoad(Player.PLAYER_2, getEdge(EdgeMapPoint(IntersectionMapPoint(4, 4), IntersectionMapPoint(4, 5))));
        placeRoad(Player.PLAYER_3, getEdge(EdgeMapPoint(IntersectionMapPoint(3, 8), IntersectionMapPoint(2, 8))));
        placeRoad(Player.PLAYER_3, getEdge(EdgeMapPoint(IntersectionMapPoint(2, 2), IntersectionMapPoint(2, 3))));

        // For 4 players, create more settlements and roads
        if (playerCount == 4) {
            intersectionMap[4][2].setOwnerAndState(Player.PLAYER_4, SETTLEMENT);
            intersectionMap[4][6].setOwnerAndState(Player.PLAYER_4, SETTLEMENT);
            placeRoad(Player.PLAYER_4, getEdge(EdgeMapPoint(IntersectionMapPoint(4, 2), IntersectionMapPoint(4, 3))));
            placeRoad(Player.PLAYER_4, getEdge(EdgeMapPoint(IntersectionMapPoint(4, 6), IntersectionMapPoint(3, 7))));
        }
    }

    @Override
    public void moveRobber(MapPoint newPosition) {
        if (newPosition.getType() != MapPoint.Type.HEX)
            throw new IllegalArgumentException("The robber can only move to a hex");
        hexMap[robberPosition.getY()][robberPosition.getX()].get().setRobberOnField(false);
        robberPosition = newPosition;
        hexMap[robberPosition.getY()][robberPosition.getX()].get().setRobberOnField(false);
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
    public boolean placeRoad(Player player, MapPoint mapPoint){
        return placeRoad(player, getEdge(mapPoint));
    }

    @Override
    public boolean roadPlaceable(Player player, MapPoint mapPoint){
        return placeRoad(player, getEdge(mapPoint));
    }

    @Override
    public boolean placeSettlement(Player player, MapPoint position) {
        if (position.getType() != MapPoint.Type.INTERSECTION) return false;
        if (settlementPlaceable(player, position)) {
            if (!playerSettlementsAndCities.containsKey(player))
                playerSettlementsAndCities.put(player, new ArrayList<>());
            playerSettlementsAndCities.get(player).add(position);
            intersectionMap[position.getY()][position.getX()].setOwnerAndState(player, SETTLEMENT);
            return true;
        }
        return false;
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
        for (IEdge edge : intersectionEdgeNetwork.incidentEdges(intersectionMap[position.getY()][position.getX()])) {
            if (edge.getOwner() == player) hasRoad = true;
        }
        for (IIntersection intersection : intersectionEdgeNetwork
                .adjacentNodes(intersectionMap[position.getY()][position.getX()]))
            if (intersection.getState() != IIntersection.IntersectionState.FREE) neighbouringIntersectionsFree = false;

        return intersectionMap[position.getY()][position.getX()].getState()
                                                                .equals(IIntersection.IntersectionState.FREE) && hasRoad && neighbouringIntersectionsFree;
    }

    @Override
    public boolean upgradeSettlement(Player player, MapPoint position) {
         if (settlementUpgradeable(player, position)){
            intersectionMap[position.getY()][position.getX()]
                    .setOwnerAndState(player, IIntersection.IntersectionState.CITY);
            return true;
        }
        return false;
    }

    @Override
    public boolean settlementUpgradeable(Player player, MapPoint position){
        if (position.getType() != MapPoint.Type.INTERSECTION) return false;
        return (intersectionMap[position.getY()][position.getX()].getState() == SETTLEMENT && intersectionMap[position
                .getY()][position.getX()].getOwner() == player);
    }

    void setHex(MapPoint position, IGameHex newHex) {
        if (position.getType() != MapPoint.Type.HEX)
            throw new IllegalArgumentException("MapPoint should point to a hex");
        hexMap[position.getY()][position.getX()].set(newHex);
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
}
