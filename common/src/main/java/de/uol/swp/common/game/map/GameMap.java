package de.uol.swp.common.game.map;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.ImmutableNetwork;
import com.google.common.graph.NetworkBuilder;
import de.uol.swp.common.game.map.Hexes.*;

import java.util.*;

import static de.uol.swp.common.game.map.IIntersection.IntersectionState.CITY;
import static de.uol.swp.common.game.map.IIntersection.IntersectionState.SETTLEMENT;

/**
 * Management of the gameMap
 *
 * @author Marvin Drees
 * @author Temmo Junkhoff
 * @since 2021-01-16
 */
public class GameMap implements IGameMap {

    //Map mapping the player and his settlements/cities
    private final Map<Player, List<MapPoint>> playerSettlementsAndCities = new HashMap<>();
    MapPoint robberPosition = new MapPoint(3, 3);
    private GameHexWrapper[][] hexMap;
    private IIntersection[][] intersectionMap;
    private ImmutableNetwork<GameHexWrapper, IEdge> hexEdgeNetwork;
    private ImmutableNetwork<IIntersection, IEdge> intersectionEdgeNetwork;

    /**
     * Constructor
     */
    public GameMap() {
        createHexEdgeNetwork();
        createIntersectionEdgeNetwork();
        hexMap[robberPosition.getX()][robberPosition.getY()].get().setRobberOnField(false);
    }

    /**
     * Creates the beginner's map as shown in the manual
     * ( https://www.catan.com/files/downloads/catan_base_rules_2020_200707.pdf )
     */
    public void createBeginnerMap() {
        //Creating the hexes
        hexMap[0][0].set(new HarborHex(hexMap[1][1], IHarborHex.HarborSide.SOUTHEAST, IHarborHex.HarborResource.ANY));
        hexMap[0][1].set(new WaterHex());
        hexMap[0][2].set(new HarborHex(hexMap[1][2], IHarborHex.HarborSide.SOUTHWEST, IHarborHex.HarborResource.GRAIN));
        hexMap[0][3].set(new WaterHex());
        hexMap[1][0].set(new WaterHex());
        hexMap[1][1].set(new ResourceHex(IResourceHex.ResourceHexType.MOUNTAINS, 10));
        hexMap[1][2].set(new ResourceHex(IResourceHex.ResourceHexType.PASTURE, 2));
        hexMap[1][3].set(new ResourceHex(IResourceHex.ResourceHexType.FOREST, 9));
        hexMap[1][4].set(new HarborHex(hexMap[2][4], IHarborHex.HarborSide.SOUTHWEST, IHarborHex.HarborResource.ORE));
        hexMap[2][0].set(new HarborHex(hexMap[2][1], IHarborHex.HarborSide.EAST, IHarborHex.HarborResource.LUMBER));
        hexMap[2][1].set(new ResourceHex(IResourceHex.ResourceHexType.FIELDS, 12));
        hexMap[2][2].set(new ResourceHex(IResourceHex.ResourceHexType.HILLS, 6));
        hexMap[2][3].set(new ResourceHex(IResourceHex.ResourceHexType.PASTURE, 4));
        hexMap[2][4].set(new ResourceHex(IResourceHex.ResourceHexType.HILLS, 10));
        hexMap[2][5].set(new WaterHex());
        hexMap[3][0].set(new WaterHex());
        hexMap[3][1].set(new ResourceHex(IResourceHex.ResourceHexType.FIELDS, 9));
        hexMap[3][2].set(new ResourceHex(IResourceHex.ResourceHexType.FOREST, 11));
        hexMap[3][3].set(new DesertHex());
        hexMap[3][4].set(new ResourceHex(IResourceHex.ResourceHexType.FOREST, 3));
        hexMap[3][5].set(new ResourceHex(IResourceHex.ResourceHexType.MOUNTAINS, 8));
        hexMap[3][6].set(new HarborHex(hexMap[3][6], IHarborHex.HarborSide.WEST, IHarborHex.HarborResource.ANY));
        hexMap[4][0].set(new HarborHex(hexMap[4][1], IHarborHex.HarborSide.EAST, IHarborHex.HarborResource.BRICK));
        hexMap[4][1].set(new ResourceHex(IResourceHex.ResourceHexType.FOREST, 8));
        hexMap[4][2].set(new ResourceHex(IResourceHex.ResourceHexType.MOUNTAINS, 3));
        hexMap[4][3].set(new ResourceHex(IResourceHex.ResourceHexType.FIELDS, 4));
        hexMap[4][4].set(new ResourceHex(IResourceHex.ResourceHexType.PASTURE, 5));
        hexMap[4][5].set(new WaterHex());
        hexMap[5][0].set(new WaterHex());
        hexMap[5][1].set(new ResourceHex(IResourceHex.ResourceHexType.HILLS, 5));
        hexMap[5][2].set(new ResourceHex(IResourceHex.ResourceHexType.FIELDS, 6));
        hexMap[5][3].set(new ResourceHex(IResourceHex.ResourceHexType.PASTURE, 11));
        hexMap[5][4].set(new HarborHex(hexMap[4][4], IHarborHex.HarborSide.NORTHWEST, IHarborHex.HarborResource.WOOL));
        hexMap[6][0].set(new HarborHex(hexMap[5][1], IHarborHex.HarborSide.NORTHEAST, IHarborHex.HarborResource.ANY));
        hexMap[6][1].set(new WaterHex());
        hexMap[6][2].set(new HarborHex(hexMap[5][2], IHarborHex.HarborSide.NORTHWEST, IHarborHex.HarborResource.ANY));
        hexMap[6][3].set(new WaterHex());

        //Create settlements
        intersectionMap[1][3].setOwnerAndState(Player.PLAYER_1, SETTLEMENT);
        intersectionMap[3][2].setOwnerAndState(Player.PLAYER_1, SETTLEMENT);
        intersectionMap[1][6].setOwnerAndState(Player.PLAYER_2, SETTLEMENT);
        intersectionMap[4][4].setOwnerAndState(Player.PLAYER_2, SETTLEMENT);
        intersectionMap[2][3].setOwnerAndState(Player.PLAYER_3, SETTLEMENT);
        intersectionMap[3][8].setOwnerAndState(Player.PLAYER_3, SETTLEMENT);
        intersectionMap[4][2].setOwnerAndState(Player.PLAYER_4, SETTLEMENT);
        intersectionMap[4][6].setOwnerAndState(Player.PLAYER_4, SETTLEMENT);

        //Create roads
        placeRoad(Player.PLAYER_1, edgeConnectingIntersections(intersectionMap[1][3], intersectionMap[1][4]));
        placeRoad(Player.PLAYER_1, edgeConnectingIntersections(intersectionMap[3][2], intersectionMap[3][3]));
        placeRoad(Player.PLAYER_2, edgeConnectingIntersections(intersectionMap[1][5], intersectionMap[1][6]));
        placeRoad(Player.PLAYER_2, edgeConnectingIntersections(intersectionMap[4][4], intersectionMap[4][5]));
        placeRoad(Player.PLAYER_3, edgeConnectingIntersections(intersectionMap[3][8], intersectionMap[2][8]));
        placeRoad(Player.PLAYER_3, edgeConnectingIntersections(intersectionMap[2][2], intersectionMap[2][3]));
        placeRoad(Player.PLAYER_4, edgeConnectingIntersections(intersectionMap[4][2], intersectionMap[4][3]));
        placeRoad(Player.PLAYER_4, edgeConnectingIntersections(intersectionMap[4][6], intersectionMap[3][7]));
    }

    @Override
    public IEdge edgeConnectingIntersections(IIntersection intersection1, IIntersection intersection2) {
        if (intersection1 == null || intersection2 == null) return null;
        return intersectionEdgeNetwork.edgeConnectingOrNull(intersection1, intersection2);
    }

    @Override
    public IGameHex getHex(MapPoint position) {
        return hexMap[position.getY()][position.getX()].get();
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
        return intersectionMap[position.getY()][position.getX()];
    }

    @Override
    public IIntersection[][] getIntersectionsAsJaggedArray() {
        return intersectionMap;
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
    public MapPoint getRobberPosition() {
        return robberPosition;
    }

    @Override
    public Set<IEdge> incidentEdges(IIntersection intersection) {
        return intersectionEdgeNetwork.incidentEdges(intersection);
    }

    @Override
    public void moveRobber(MapPoint newPosition) {
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
    public boolean placeSettlement(Player player, MapPoint position) {
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
        if (intersectionMap[position.getY()][position.getX()].getState() == SETTLEMENT && intersectionMap[position
                .getY()][position.getX()].getOwner() == player) {
            intersectionMap[position.getY()][position.getX()]
                    .setOwnerAndState(player, IIntersection.IntersectionState.CITY);
            return true;
        }
        return false;
    }

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

    void setHex(MapPoint position, IGameHex newHex) {
        hexMap[position.getY()][position.getX()].set(newHex);
    }
}
