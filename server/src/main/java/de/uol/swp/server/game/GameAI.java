package de.uol.swp.server.game;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.hexes.*;
import de.uol.swp.common.game.map.management.IEdge;
import de.uol.swp.common.game.map.management.IIntersection;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.message.AbstractGameMessage;
import de.uol.swp.common.game.message.BuildingSuccessfulMessage;
import de.uol.swp.common.game.message.UpdateUniqueCardsListMessage;
import de.uol.swp.common.game.request.BuyDevelopmentCardRequest;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.Inventory;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.IResource;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.game.robber.RobberPositionMessage;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.server.specialisedUtil.ActorStartUpBuiltMap;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.util.Util;
import de.uol.swp.server.game.map.IGameMapManagement;
import de.uol.swp.server.lobby.LobbyService;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.uol.swp.common.game.message.BuildingSuccessfulMessage.Type.*;
import static de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType.*;

/**
 * Class for all AI related methods
 *
 * @author Mario Fokken
 * @since 2021-05-15
 */
public class GameAI {

    private final GameService gameService;
    private final IGameManagement gameManagement;
    private final LobbyService lobbyService;

    private final AIHarbourMap harbours = new AIHarbourMap();
    private final AIBuildPriorityMap aiBuildPriority = new AIBuildPriorityMap();

    /**
     * Constructor
     *
     * @param gameService    The GameService
     * @param gameManagement The GameService's gameManagement
     * @param lobbyService   The GameService's lobbyService
     *
     * @since 2021-05-15
     */
    public GameAI(GameService gameService, IGameManagement gameManagement, LobbyService lobbyService) {
        this.gameService = gameService;
        this.gameManagement = gameManagement;
        this.lobbyService = lobbyService;
    }

    /**
     * Method to move the robber when
     * an AI gets a seven.
     *
     * @author Mario Fokken
     * @since 2021-05-11
     */
    void robberMovementAI(AI ai, LobbyName lobby) {
        Game game = gameManagement.getGame(lobby);
        IGameMapManagement map = game.getMap();
        AI.Difficulty difficulty = ai.getDifficulty();
        writeChatMessageAI(ai, lobby, AI.WriteType.MOVE_ROBBER);

        //Pick place to put the robber on
        int y = 3;
        int x = 3;
        MapPoint mapPoint;
        switch (difficulty) {
            case EASY:
                y = Util.randomPositiveInt(5);
                x = (y == 1 || y == 5) ? (Util.randomPositiveInt(4)) :
                    ((y == 2 || y == 4) ? (Util.randomPositiveInt(5)) : (Util.randomPositiveInt(6)));
                break;
            case HARD:
                Map<MapPoint, Integer> position = new HashMap<>();
                Player player = game.getPlayer(ai);
                int rating;

                //Get every hex and give them a rating depending on the amount of players around it
                for (int i = 1; i < 6; i++) {
                    for (int j = 1; j < 6; j++) {
                        if (((i == 1 || i == 5) && j > 3) || ((i == 2 || i == 4) && j > 4)) break;
                        rating = 0;
                        mapPoint = MapPoint.HexMapPoint(i, j);
                        for (Player p : map.getPlayersAroundHex(mapPoint)) {
                            if (p.equals(player)) {
                                rating -= 2;
                                continue;
                            }
                            rating++;
                        }
                        position.put(mapPoint, rating);
                    }
                }

                //Filter every hex with a rating lower than the highest
                if (position.containsValue(3)) rating = 3;
                else if (position.containsValue(2)) rating = 2;
                else if (position.containsValue(1)) rating = 1;
                else rating = 0;
                List<MapPoint> points = new ArrayList<>(position.keySet());
                for (MapPoint mp : points) if (position.get(mp) < rating) position.remove(mp);

                //Pick the one(s) with the highest likeability to give resources (token nearest to 7)
                Map<MapPoint, Integer> tokens = new HashMap<>();
                IResourceHex resHex;
                for (MapPoint mp : position.keySet()) {
                    if (map.getHex(mp) instanceof DesertHex) {
                        tokens.put(mp, 13);
                        continue;
                    }
                    resHex = (IResourceHex) map.getHex(mp);
                    tokens.put(mp, resHex.getToken());
                }
                int token = 5;
                if (tokens.containsValue(6) || tokens.containsValue(8)) token = 1;
                else if (tokens.containsValue(5) || tokens.containsValue(9)) token = 2;
                else if (tokens.containsValue(4) || tokens.containsValue(10)) token = 3;
                else if (tokens.containsValue(3) || tokens.containsValue(11)) token = 4;
                for (MapPoint mp : tokens.keySet()) if (Math.abs(tokens.get(mp) - 7) > token) position.remove(mp);

                //Pick a random hex from the survivors
                if (!position.isEmpty()) {
                    mapPoint = new ArrayList<>(position.keySet()).get(Util.randomInt(position.keySet().size()));
                    y = mapPoint.getY();
                    x = mapPoint.getX();
                }
        }
        mapPoint = MapPoint.HexMapPoint(y, x);
        GameService.LOG.debug("{} moves the robber to position: {}|{}", ai, y, x);
        map.moveRobber(mapPoint);
        GameService.LOG.debug("Sending RobberPositionMessage for Lobby {}", lobby);
        AbstractGameMessage msg = new RobberPositionMessage(lobby, ai, mapPoint);
        lobbyService.sendToAllInLobby(lobby, msg);

        //Pick victim to steal random card from
        List<Player> player = map.getPlayersAroundHex(mapPoint);
        if (player.size() > 0) {
            Player victim = player.get(0);
            switch (difficulty) {
                case EASY:
                    victim = player.get(Util.randomInt(player.size()));
                    break;
                case HARD:
                    Map<Player, List<MapPoint>> victimRating = map.getPlayerSettlementsAndCities();
                    for (Player p : victimRating.keySet())
                        //Change victim when p has more cities/settlements or,
                        //if amount is the same, when p has more resources
                        if ((victimRating.get(p).size() > victimRating.get(victim).size()) || //
                            ((victimRating.get(p).size() == victimRating.get(victim).size()) && //
                             (game.getInventory(p).getResourceAmount() > game.getInventory(victim)
                                                                             .getResourceAmount()))) victim = p;
                    break;
            }
            gameService.robRandomResource(game, ai, game.getActorFromPlayer(victim));
        }
    }

    /**
     * Method to pay the tax for an AI
     *
     * @param game The game the AI is in
     * @param ai   The AI to pay the tax
     *
     * @author Mario Fokken
     * @since 2021-05-11
     */
    void taxPayAI(Game game, AI ai) {
        Inventory inv = game.getInventory(ai);
        int i = inv.getResourceAmount() / 2;
        writeChatMessageAI(ai, game.getLobby().getName(), AI.WriteType.TAX);

        GameService.LOG.debug("{} has to give up {} of their {} cards", ai, i, inv.getResourceAmount());
        switch (ai.getDifficulty()) {
            case EASY:
                while (i > 0) {
                    for (ResourceType resourceType : ResourceType.values()) {
                        if (inv.get(resourceType) > 0) {
                            inv.increase(resourceType, -1);
                            i--;
                            if (i == 0) break;
                        }
                    }
                }
                break;
            case HARD:
                ResourceType highest = BRICK;
                while (i > 0) {
                    for (IResource r : inv.getResources())
                        if (r.getAmount() > inv.getResources().getAmount(highest)) highest = r.getType();
                    inv.decrease(highest);
                    i--;
                }
        }
    }

    /**
     * Method to calculate if the AI
     * wants to accept the trade offer
     *
     * @param ai       The AI to decide
     * @param lobby    The lobby
     * @param offered  The offered Resources
     * @param demanded The demanded Resources
     *
     * @return If AI accepts
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    boolean tradeAcceptationAI(AI ai, LobbyName lobby, ResourceList offered, ResourceList demanded) {
        int difference = offered.getTotal() - demanded.getTotal();
        switch (ai.getDifficulty()) {
            case EASY:
                if (ai.getUsername().equals("Robert E. O. Speedwagon")) return true;
                //Difference:4-100%, 3-92%, 2-84%, 1-76%, 0-68%
                if (difference >= 0 && (Util.randomInt(100) < (68 + difference * 8))) return true;
                    //Difference:4-0%, 3-8%, 2-16%, 1-24%
                else return difference < 0 && (Util.randomInt(100) < (32 + difference * 8));
            case HARD:
                if (demanded.getTotal() == 0 || difference > 2) return true;
                if (offered.getTotal() == 0 || difference < -2) return false;
                int rating = 0;
                Game game = gameManagement.getGame(lobby);
                //If the AI has no resource left after trade, the rating goes down
                for (IResource r : offered)
                    if (game.getInventory(ai).get(r.getType()) - (r.getAmount() - demanded.getAmount(r.getType())) == 0)
                        rating--;
                List<ResourceType> priority = new ArrayList<>();
                //Prioritise Lumber, Brick early; ore later
                if (game.getRound() <= 4) {
                    priority.add(LUMBER);
                    priority.add(BRICK);
                } else priority.add(ORE);
                //Prioritise resource if low on it
                for (IResource r : game.getInventory(ai).getResources())
                    if (r.getAmount() <= 1) priority.add(r.getType());
                //If offered has a prioritised resource, the rating goes up
                for (ResourceType r : priority) rating += offered.getAmount(r) - demanded.getAmount(r);
                return Util.randomInt(100) < (50 + rating * 10);
            default:
                return false;
        }
    }

    /**
     * Method for an AI's turn
     *
     * @param game The game the AI is in
     * @param ai   The AI to make its turn
     *
     * @author Mario Fokken
     * @since 2021-05-11
     */
    void turnAI(Game game, AI ai) {
        switch (ai.getDifficulty()) {
            case EASY:
                turnPlayCardsAIEasy(game, ai);
                turnBuildAIEasy(game, ai);
                break;
            case HARD:
                if (game.getInventory(ai).get(DevelopmentCardType.KNIGHT_CARD) > 0)
                    playCardAI(game, ai, DevelopmentCardType.KNIGHT_CARD, null, null);
                turnBuildAIHard(game, ai);
                break;
        }
        //Trying to end the turn
        gameService.turnEndAI(game, ai);
    }

    /**
     * Method for an AI's turn in the set up phase
     *
     * @param game The game the AI is in
     * @param ai   The AI to make its turn
     *
     * @author Mario Fokken
     * @since 2021-06-07
     */
    void turnAISetUp(Game game, AI ai) {
        switch (ai.getDifficulty()) {
            case EASY:
                startUpPhaseAIEasy(game, ai);
                break;
            case HARD:
                startUpPhaseAIHard(game, ai);
                break;
        }
        ActorStartUpBuiltMap startUpBuiltMap = game.getPlayersStartUpBuiltMap();
        startUpBuiltMap.nextPhase(ai);

        gameService.turnEndAI(game, ai);
    }

    /**
     * Method to make a chat
     * message for an AI
     *
     * @param ai        The AI to send the message
     * @param lobbyName The lobby the AI is in
     * @param type      The type of chat message
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    void writeChatMessageAI(AI ai, LobbyName lobbyName, AI.WriteType type) {
        String msg = ai.writeMessage(type);
        if (!msg.equals("")) gameService.postAI(ai, msg, lobbyName);
    }

    /**
     * Method to build a
     * city for a hard AI
     * <p>
     * It upgrades the settlement on the most
     * lucrative spot.
     *
     * @param game The game the AI is in
     * @param ai   The AI to build the city
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private boolean buildCity(Game game, AI ai) {
        Inventory inv = game.getInventory(ai);

        //Play a monopoly card if possible
        if (inv.get(DevelopmentCardType.MONOPOLY_CARD) > 1) {
            if (inv.get(ORE) < 3) playCardAI(game, ai, DevelopmentCardType.MONOPOLY_CARD, ORE, null);
            if (inv.get(GRAIN) < 2) playCardAI(game, ai, DevelopmentCardType.MONOPOLY_CARD, GRAIN, null);
        }

        //Not enough Resources
        if (inv.get(GRAIN) < 2 || inv.get(ORE) < 3) return false;

        IGameMapManagement map = game.getMap();
        LobbyName lobbyName = game.getLobby().getName();
        Player player = game.getPlayer(ai);
        Map<MapPoint, Integer> settlements = new HashMap<>();

        //Fill settlements with every settlement of the AI
        MapPoint mapPoint = null;
        for (MapPoint mp : map.getPlayerSettlementsAndCities().get(player)) {
            for (MapPoint mp2 : aiBuildPriority.get(game).keySet())
                if (mp.getY() == mp2.getY() && mp.getX() == mp2.getX()) mapPoint = mp2;
            if (mapPoint == null) continue;
            if (map.getIntersection(mp).getState() == IIntersection.IntersectionState.SETTLEMENT)
                settlements.put(mp, aiBuildPriority.get(game).get(mapPoint));
        }

        //Pick most lucrative spot and upgrade the settlement
        for (int i = 0; i < 15; i++) {
            if (settlements.containsValue(i)) for (MapPoint mp : settlements.keySet())
                if (settlements.get(mp) == i) {
                    map.upgradeSettlement(player, mp);
                    inv.decrease(GRAIN, 2);
                    inv.decrease(ORE, 3);
                    lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, ai, mp, CITY));
                    return true;
                }
        }
        return false;
    }

    /**
     * Method to build a
     * road for a hard AI
     * <p>
     * It builds roads to reach
     * the most lucrative spot in vicinity
     *
     * @param game The game the AI is in
     * @param ai   The AI to build the city
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private boolean buildRoad(Game game, AI ai) {
        Inventory inv = game.getInventory(ai);
        int roadsPlaceable = Math.min(inv.get(LUMBER), inv.get(BRICK));
        if (roadsPlaceable == 0) return false;

        IGameMapManagement map = game.getMap();
        Player player = game.getPlayer(ai);
        MapPoint mapPoint;
        List<MapPoint> intersections = new ArrayList<>(map.getPlayerSettlementsAndCities().get(player));
        //Gets all intersections "under AI's control" (settlement, city, or adjacent road)
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 11; j++) {
                if ((i == 0 || i == 5) && j > 6) break;
                else if ((i == 1 || i == 4) && j > 8) break;
                mapPoint = MapPoint.IntersectionMapPoint(i, j);
                for (IEdge edge : map.getEdgesAroundIntersection(map.getIntersection(mapPoint))) {
                    if (edge.getOwner() == player) intersections.add(mapPoint);
                }
            }

        Map<MapPoint, Integer> lucrativeSpots = new HashMap<>();
        int i = 0;
        //Gets lucrative spots from priority map
        do {
            for (MapPoint mp : aiBuildPriority.get(game).keySet())
                if (aiBuildPriority.get(game).get(mp) <= 3 + i && map.getIntersection(mp)
                                                                     .getState() == IIntersection.IntersectionState.FREE)
                    lucrativeSpots.put(mp, aiBuildPriority.get(game).get(mp));
            i++;
        } while (lucrativeSpots.isEmpty());

        List<List<MapPoint>> roads = new ArrayList<>();
        List<MapPoint> path;

        //Get all path to the lucrativeSpot(s)
        for (MapPoint mp : intersections) {
            for (MapPoint mp2 : lucrativeSpots.keySet()) {
                path = findPath(game, ai, mp, mp2);
                if (path != null) roads.add(path);
            }
        }

        //Play a card if possible
        if (inv.get(DevelopmentCardType.ROAD_BUILDING_CARD) > 0)
            playCardAI(game, ai, DevelopmentCardType.ROAD_BUILDING_CARD, null, null);
        if (inv.get(DevelopmentCardType.YEAR_OF_PLENTY_CARD) > 0)
            playCardAI(game, ai, DevelopmentCardType.YEAR_OF_PLENTY_CARD, BRICK, LUMBER);

        //Chooses the best path; best as in longest buildable road
        path = null;
        for (List<MapPoint> list : roads) {
            if (path == null) path = list;
            else if (list.size() > path.size() && list.size() <= roadsPlaceable) path = list;
        }

        //Build the road
        if (path != null) {
            buildRoadOnPath(game, ai, path);
            return true;
        }

        return false;
    }

    /**
     * Method to build the roads
     * on a path
     *
     * @param game The game the AI is in
     * @param ai   The AI to build the roads
     * @param path The path to build the roads on
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private void buildRoadOnPath(Game game, AI ai, List<MapPoint> path) {
        Inventory inv = game.getInventory(ai);
        LobbyName lobbyName = game.getLobby().getName();
        MapPoint mapPoint;
        for (int i = 0; i < path.size() - 1; i++) {
            if (inv.get(BRICK) == 0 || inv.get(LUMBER) == 0) break;
            mapPoint = MapPoint.EdgeMapPoint(path.get(i), path.get(i + 1));
            if (game.getMap().roadPlaceable(game.getPlayer(ai), mapPoint)) {
                game.getMap().placeRoad(game.getPlayer(ai), mapPoint);
                inv.decrease(BRICK);
                inv.decrease(LUMBER);
                lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, ai, mapPoint, ROAD));
            }
        }
    }

    /**
     * Method to build a
     * settlement for a hard AI
     * <p>
     * It builds a settlement on the most
     * lucrative spot available.
     *
     * @param game The game the AI is in
     * @param ai   The AI to build the settlement
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private boolean buildSettlement(Game game, AI ai) {
        Inventory inv = game.getInventory(ai);
        if (!(inv.get(LUMBER) >= 1 && inv.get(BRICK) >= 1 && inv.get(GRAIN) >= 1 && inv.get(WOOL) >= 1)) return false;
        IGameMapManagement map = game.getMap();
        List<MapPoint> intersections = new ArrayList<>();
        LobbyName lobbyName = game.getLobby().getName();
        Player player = game.getPlayer(ai);
        MapPoint mapPoint;

        //Fill intersections with all possible settlement locations
        for (int i = 0; i <= 5; i++)
            for (int j = 0; j <= 10; j++) {
                if ((i == 1 || i == 4) && j >= 9) break;
                else if ((i == 0 || i == 5) && j >= 7) break;
                mapPoint = MapPoint.IntersectionMapPoint(i, j);
                if (map.settlementPlaceable(player, mapPoint)) intersections.add(mapPoint);
            }

        //Pick the most lucrative location and build the settlement
        MapPoint mp = null;
        for (int i = 0; i < 20; i++) {
            for (MapPoint mpI : intersections) {
                for (MapPoint mpP : aiBuildPriority.get(game).keySet())
                    if (mpI.getY() == mpP.getY() && mpI.getX() == mpP.getX()) {
                        mp = mpP;
                        break;
                    }
                if (aiBuildPriority.get(game).get(mp) == i) {
                    try {
                        map.placeSettlement(player, mp);
                    } catch (GameMapManagement.SettlementMightInterfereWithLongestRoadException e) {
                        GameMapManagement.PlayerWithLengthOfLongestRoad a = map.findLongestRoad();
                        if (a.getLength() > 4) {
                            game.setPlayerWithLongestRoad(a.getPlayer());
                            game.setLongestRoadLength(a.getLength());
                        } else {
                            game.setPlayerWithLongestRoad(null);
                            game.setLongestRoadLength(0);
                        }
                        lobbyService.sendToAllInLobby(lobbyName, new UpdateUniqueCardsListMessage(lobbyName,
                                                                                                  game.getUniqueCardsList()));
                    }
                    if (map.getHarbourResource(mp) != null) {
                        harbours.putIfAbsent(ai, new ArrayList<>());
                        harbours.get(ai).add(map.getHarbourResource(mp));
                    }
                    inv.decrease(BRICK);
                    inv.decrease(LUMBER);
                    inv.decrease(GRAIN);
                    inv.decrease(WOOL);
                    lobbyService
                            .sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, ai, mp, SETTLEMENT));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method to fill the aiBuildPriority map
     * used by the hard AI.
     * It gives every intersection a rating
     *
     * @param game The game the AI is in
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private void createBuildPriority(Game game) {
        IGameMapManagement map = game.getMap();
        MapPoint mapPoint;
        int rating;
        Map<MapPoint, Integer> priority = new HashMap<>();
        for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 10; j++) {
                if ((i == 0 || i == 5) && j >= 7) break;
                else if ((i == 1 || i == 4) && j >= 9) break;
                mapPoint = MapPoint.IntersectionMapPoint(i, j);
                rating = 0;

                //Rate all locations
                for (MapPoint mp : game.getMap().getResourceHexesFromIntersection(mapPoint)) {
                    IResourceHex hex = (IResourceHex) game.getMap().getHex(mp);
                    rating += Math.abs(hex.getToken() - 7);
                }
                //↓ Special rating for coast intersections
                if (j == 0 || ((i == 0 || i == 5) && (j == 1 || j == 3 || j == 5)) //
                    || ((i == 1 || i == 4) && j == 8) || ((i == 2 || i == 3) && j == 10))
                    //Two Water Hexes
                    if (map.getHarbourResource(mapPoint) == null) rating += 20;
                    else rating += 15;
                else if (i == 0 || i == 5 || j == 1 || ((i == 1 || i == 4) && j == 7) || ((i == 2 || i == 3) && j == 9))
                    //One Water Hex
                    if (map.getHarbourResource(mapPoint) == null) rating += 10;
                    else rating += 5;
                else if (map.getHex(MapPoint.HexMapPoint(3, 3)).getType() == IGameHex.HexType.DESERT //
                         && (i == 2 || i == 3) && (j == 4 || j == 5 || j == 6)) rating += 7;
                //↑ Special rating for the desert field (only if desert field is the in the middle)
                priority.put(mapPoint, rating);
            }
        }
        aiBuildPriority.put(game, priority);
    }

    /**
     * Method to find a path
     * between two Intersections
     *
     * @param game  The Game the AI is in
     * @param ai    The AI to build the roads
     * @param start The road's start
     * @param end   The road's end
     *
     * @return List of MapPoints; null if no path is found
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private List<MapPoint> findPath(Game game, AI ai, MapPoint start, MapPoint end) {
        List<MapPoint> path = new ArrayList<>(Collections.singletonList(start));
        return findPathRecursive(game.getMap(), game.getPlayer(ai), path, end);
    }

    /**
     * Method for the helper method
     * to find a path between two Intersections
     *
     * @param map    The IGameMapManagement
     * @param player The AI wanting to build the road
     * @param path   The taken path
     * @param end    The end point
     *
     * @return List of MapPoints (null if no path is found)
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private List<MapPoint> findPathRecursive(IGameMapManagement map, Player player, List<MapPoint> path, MapPoint end) {
        if (path.size() > 11) return null;
        List<MapPoint> adjacent = new ArrayList<>();
        MapPoint start = path.get(path.size() - 1);
        MapPoint mapP;
        //Get the MapPoints of the adjacent Intersections
        for (IIntersection in : map.adjacentIntersections(map.getIntersection(start))) {
            mapP = map.getIntersectionMapPoint(in);
            //If end is near, take it
            if (mapP.getY() == end.getY() && mapP.getX() == end.getX()) {
                path.add(end);
                return path;
            }
            adjacent.add(mapP);
        }

        Map<MapPoint, List<MapPoint>> paths = new HashMap<>();

        //Remove all Intersection further away than the start or already used
        int yDiff = Math.abs(start.getY() - end.getY());
        int xDiff = Math.abs(start.getX() - end.getX());
        int distance = yDiff + xDiff;
        //If the distance is 0, we're at the end
        if (distance == 0) return path;

        List<MapPoint> removals = new ArrayList<>();
        for (MapPoint mapPoint : adjacent) {
            for (MapPoint mp : path) {
                if (mp.getY() == mapPoint.getY() && mp.getX() == mapPoint.getX()) {
                    removals.add(mapPoint);
                    break;
                }
            }
            if (Math.abs(mapPoint.getY() - end.getY()) > yDiff) removals.add(mapPoint);
            else if (mapPoint.getY() == end.getY())
                if (Math.abs(mapPoint.getX() - end.getX()) > xDiff) removals.add(mapPoint);
        }

        for (MapPoint mp : removals) adjacent.remove(mp);

        //Recursive call
        for (MapPoint mp : adjacent) {
            if ((map.getIntersection(mp).getState() == IIntersection.IntersectionState.FREE || //
                 map.getIntersection(mp).getOwner() == player) && //
                (map.getEdge(MapPoint.EdgeMapPoint(start, mp)).getOwner() == null || //
                 map.getEdge(MapPoint.EdgeMapPoint(start, mp)).getOwner() == player)) {
                paths.put(mp, path);
                paths.get(mp).add(mp);
                paths.put(mp, findPathRecursive(map, player, paths.get(mp), end));
            } else paths.put(mp, null);
        }

        //Choose the best path
        List<MapPoint> path1 = null;
        List<MapPoint> path2;
        for (MapPoint mp : paths.keySet()) {
            if (path1 == null) path1 = paths.get(mp);
            else {
                path2 = paths.get(mp);
                if (path2 != null) path1 = path1.size() >= path2.size() ? path2 : path1;
            }
        }
        return path1;
    }

    /**
     * Method to let a hard AI
     * play a card
     *
     * @param game The game the AI is in
     * @param ai   The AI to do the card playing
     * @param type The type of card to play
     * @param res1 Needed for Monopoly, YearOfPlenty
     * @param res2 Only needed for YearOfPlenty
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private void playCardAI(Game game, AI ai, DevelopmentCardType type, ResourceType res1, ResourceType res2) {
        Inventory inv = game.getInventory(ai);
        LobbyName lobbyName = game.getLobby().getName();
        switch (type) {
            case KNIGHT_CARD:
                inv.decrease(DevelopmentCardType.KNIGHT_CARD);
                inv.increaseKnights();
                gameService.checkLargestArmy(lobbyName, ai);
                gameService.updateVictoryPoints(lobbyName);
                robberMovementAI(ai, lobbyName);
                break;
            case ROAD_BUILDING_CARD:
                inv.decrease(DevelopmentCardType.ROAD_BUILDING_CARD);
                inv.increase(BRICK, 2);
                inv.increase(LUMBER, 2);
                break;
            case YEAR_OF_PLENTY_CARD:
                inv.decrease(DevelopmentCardType.YEAR_OF_PLENTY_CARD);
                inv.increase(res1);
                inv.increase(res2);
                break;
            case MONOPOLY_CARD:
                inv.decrease(DevelopmentCardType.MONOPOLY_CARD);
                Inventory[] inventories = game.getAllInventories();
                for (Inventory i : inventories)
                    if (i.get(res1) > 0) {
                        inv.increase(res1, i.get(res1));
                        i.decrease(res1, i.get(res1));
                    }
                writeChatMessageAI(ai, lobbyName, AI.WriteType.MONOPOLY);
                break;
        }
    }

    /**
     * Method to let an Easy AI build
     * in the start up phase
     *
     * @param game The game the AI is in
     * @param ai   The AI building
     *
     * @author Mario Fokken
     * @since 2021-06-05
     */
    private void startUpPhaseAIEasy(Game game, AI ai) {
        Player player = game.getPlayer(ai);
        IGameMapManagement map = game.getMap();
        LobbyName lobbyName = game.getLobby().getName();

        boolean built = false;
        int y, xmax, x;
        MapPoint mp = null;

        //Choose random place to build settlement upon
        while (mp == null || !built) {
            y = Util.randomInt(5);
            xmax = (y == 0 || y == 5) ? 6 : (y == 1 || y == 4) ? 8 : 10;
            x = Util.randomInt(xmax);
            mp = MapPoint.IntersectionMapPoint(y, x);
            System.err.println(mp.getY() + " " + mp.getX());
            built = map.placeFoundingSettlement(player, mp);
        }
        lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, ai, mp, SETTLEMENT));

        List<IEdge> edges = new ArrayList<>(map.getEdgesAroundIntersection(map.getIntersection(mp)));
        built = false;
        IEdge edge = null;

        //Choose random place to build road upon
        while (!built) {
            edge = edges.get(Util.randomInt(edges.size()));
            built = map.placeRoad(player, edge);
        }

        lobbyService.sendToAllInLobby(lobbyName,
                                      new BuildingSuccessfulMessage(lobbyName, ai, map.getEdgeMapPoint(edge), ROAD));
    }

    /**
     * Method to let a Hard AI build
     * in the start up phase
     *
     * @param game The game the AI is in
     * @param ai   The AI building
     *
     * @author Mario Fokken
     * @since 2021-06-05
     */
    private void startUpPhaseAIHard(Game game, AI ai) {
        if (!aiBuildPriority.containsKey(game)) createBuildPriority(game);
        Player player = game.getPlayer(ai);
        IGameMapManagement map = game.getMap();
        LobbyName lobbyName = game.getLobby().getName();

        //Sorts the aiBuildPriority map by Value
        List<Entry<MapPoint, Integer>> list = new LinkedList<>(aiBuildPriority.get(game).entrySet());
        list.sort((o1, o2) -> o1.getValue().compareTo(o2.getValue()) == 0 //
                              ? o1.getKey().getY() == o2.getKey().getY() //
                                ? Integer.compare(o1.getKey().getX(), o2.getKey().getX()) //
                                : Integer.compare(o1.getKey().getY(), o2.getKey().getY()) //
                              : o1.getValue().compareTo(o2.getValue()));
        Map<MapPoint, Integer> priority = list.stream().collect(
                Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));

        MapPoint mapPoint = null;
        //Goes from best to worst priority to choose the settlement point
        for (MapPoint mp : priority.keySet())
            if (map.placeFoundingSettlement(player, mp)) {
                mapPoint = mp;
                break;
            }
        lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, ai, mapPoint, SETTLEMENT));

        //Build road in the direction of the next best rated point
        List<MapPoint> roads = new LinkedList<>(priority.keySet());
        List<MapPoint> path = null;

        MapPoint road;
        int i = 1;
        while (path == null) {
            road = roads.get(roads.indexOf(mapPoint) + i++);
            path = findPath(game, ai, mapPoint, road);
        }

        road = MapPoint.EdgeMapPoint(path.get(0), path.get(1));

        map.placeRoad(player, road);

        lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, ai, road, ROAD));
    }

    /**
     * Method for an easy AI's
     * building phase
     *
     * @param game The game the AI is in
     * @param ai   The AI to do the building
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    private void turnBuildAIEasy(Game game, AI ai) {
        Player player = game.getPlayer(ai);
        Inventory inv = game.getInventory(ai);
        IGameMapManagement map = game.getMap();
        LobbyName lobbyName = game.getLobby().getName();

        List<MapPoint> cities = new ArrayList<>();
        List<MapPoint> settlements = new ArrayList<>();
        Set<IEdge> edges = new HashSet<>();

        MapPoint mp;
        for (int i = 0; i <= 5; i++)
            for (int j = 0; j <= 10; j++) {
                //Why? see GameMapManagement.createIntersectionEdgeNetwork
                if ((i == 0 || i == 5) && j >= 7) break;
                else if ((i == 1 || i == 4) && j >= 9) break;
                mp = MapPoint.IntersectionMapPoint(i, j);
                //Settlement Stuff
                if (map.settlementPlaceable(player, mp)) settlements.add(mp);
                if (map.settlementUpgradeable(player, mp)) cities.add(mp);
                //Road Stuff
                for (IEdge e : map.incidentEdges(map.getIntersection(mp)))
                    if (map.roadPlaceable(player, e)) edges.add(e);
            }

        //Build City for Rock 'n' Roll
        while (inv.get(GRAIN) >= 2 && inv.get(ORE) >= 3 && !cities.isEmpty()) {
            mp = cities.remove(Util.randomInt(cities.size()));
            map.upgradeSettlement(player, mp);
            inv.decrease(GRAIN, 2);
            inv.decrease(ORE, 3);
            lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, ai, mp, CITY));
        }

        //Build Settlement
        while (inv.get(BRICK) >= 1 && inv.get(LUMBER) >= 1 && inv.get(GRAIN) >= 1 && inv.get(WOOL) >= 1 && !settlements
                .isEmpty()) {
            mp = settlements.remove(Util.randomInt(settlements.size()));
            try {
                map.placeSettlement(player, mp);
            } catch (GameMapManagement.SettlementMightInterfereWithLongestRoadException e) {
                GameMapManagement.PlayerWithLengthOfLongestRoad a = map.findLongestRoad();
                if (a.getLength() >= 5) {
                    game.setPlayerWithLongestRoad(a.getPlayer());
                    game.setLongestRoadLength(a.getLength());
                } else {
                    game.setPlayerWithLongestRoad(null);
                    game.setLongestRoadLength(0);
                }
                lobbyService.sendToAllInLobby(lobbyName,
                                              new UpdateUniqueCardsListMessage(lobbyName, game.getUniqueCardsList()));
            }
            inv.decrease(BRICK);
            inv.decrease(LUMBER);
            inv.decrease(GRAIN);
            inv.decrease(WOOL);
            lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, ai, mp, SETTLEMENT));
        }

        List<IEdge> roads = new ArrayList<>(edges);
        //Build Road
        while (inv.get(BRICK) >= 1 && inv.get(LUMBER) >= 1 && !roads.isEmpty()) {
            IEdge edge = roads.remove(Util.randomInt(roads.size()));
            map.placeRoad(player, edge);
            inv.decrease(BRICK);
            inv.decrease(LUMBER);
            mp = map.getEdgeMapPoint(edge);
            lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, ai, mp, ROAD));
        }

        //Buy Dev Card
        while (inv.get(WOOL) >= 1 && inv.get(GRAIN) >= 1 && inv.get(ORE) >= 1 && !game.getBankInventory()
                                                                                      .getDevelopmentCards().isEmpty())
            gameService.onBuyDevelopmentCardRequest(new BuyDevelopmentCardRequest(ai, lobbyName));

        //Update Victory Points
        gameService.updateVictoryPoints(lobbyName);
    }

    /**
     * Method for a hard AI's
     * building phase
     *
     * @param game The game the AI is in
     * @param ai   The AI to do the building
     *
     * @author Mario Fokken
     * @since 2021-05-15
     */
    private void turnBuildAIHard(Game game, AI ai) {
        LobbyName lobbyName = game.getLobby().getName();
        Inventory inv = game.getInventory(ai);
        if (!aiBuildPriority.containsKey(game)) createBuildPriority(game);

        useHarbour(game, ai);

        //Random chance of buying a card increases steadily
        if (inv.get(GRAIN) > 0 && inv.get(ORE) > 0 && inv.get(WOOL) > 0 && //
            Util.randomInt(100) < (10 + game.getRound() * 5))
            gameService.onBuyDevelopmentCardRequest(new BuyDevelopmentCardRequest(ai, lobbyName));

        //The numbers may not be optimal; not enough data
        if (game.getRound() < 7) {
            //Focus on expanding like crazy
            buildRoad(game, ai);
            buildSettlement(game, ai);
        } else if (game.getRound() < 14) {
            //Build a settlement, before making a road
            buildSettlement(game, ai);
            buildRoad(game, ai);
            buildCity(game, ai);
        } else if (game.getRound() < 21) {
            //Twice the building, double the settlements
            while (buildSettlement(game, ai)) buildSettlement(game, ai);
            buildCity(game, ai);
            buildRoad(game, ai);
        } else {
            //Cities are good
            while (buildCity(game, ai)) buildCity(game, ai);
            while (buildSettlement(game, ai)) buildSettlement(game, ai);
            buildRoad(game, ai);
        }

        //Update Victory Points
        gameService.updateVictoryPoints(lobbyName);
    }

    /**
     * Method for an easy AI's
     * card playing phase
     *
     * @param game The game the AI is in
     * @param ai   The AI to do the card playing
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    private void turnPlayCardsAIEasy(Game game, AI ai) {
        DevelopmentCardList cards = game.getInventory(ai).getDevelopmentCards();
        LobbyName lobbyName = game.getLobby().getName();
        Inventory inv = game.getInventory(ai);

        Supplier<ResourceType> getRandomResource = Util::randomResourceType;

        if (cards.getAmount(DevelopmentCardType.MONOPOLY_CARD) > 0) {
            playCardAI(game, ai, DevelopmentCardType.MONOPOLY_CARD, getRandomResource.get(), null);
            return;
        }
        if (cards.getAmount(DevelopmentCardType.ROAD_BUILDING_CARD) > 0) {
            Set<IEdge> edges = new HashSet<>();
            Player player = game.getPlayer(ai);
            IGameMapManagement map = game.getMap();
            for (int i = 1; i <= 5; i++) {
                for (int j = 1; j <= 5; j++) {
                    if ((i == 1 || i == 5) && j > 3) break;
                    else if ((i == 2 || i == 4) && j > 4) break;
                    MapPoint mp = MapPoint.HexMapPoint(i, j);
                    for (IEdge edge : map.getEdgesFromHex(mp))
                        if (map.roadPlaceable(player, edge)) edges.add(edge);
                }
            }
            List<IEdge> roads = new ArrayList<>(edges);
            if (roads.size() > 1) {
                IEdge edge = roads.remove(Util.randomInt(roads.size()));
                map.placeRoad(player, edge);
                inv.decrease(DevelopmentCardType.ROAD_BUILDING_CARD);
                lobbyService.sendToAllInLobby(lobbyName,
                                              new BuildingSuccessfulMessage(lobbyName, ai, map.getEdgeMapPoint(edge),
                                                                            ROAD));
                if (roads.size() > 2) {
                    edge = roads.remove(Util.randomInt(roads.size()));
                    game.getMap().placeRoad(player, roads.remove(Util.randomInt(roads.size())));
                    lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, ai,
                                                                                           map.getEdgeMapPoint(edge),
                                                                                           ROAD));
                }
            }
            return;
        }
        if (cards.getAmount(DevelopmentCardType.YEAR_OF_PLENTY_CARD) > 0) {
            playCardAI(game, ai, DevelopmentCardType.YEAR_OF_PLENTY_CARD, getRandomResource.get(),
                       getRandomResource.get());
            return;
        }
        if (cards.getAmount(DevelopmentCardType.KNIGHT_CARD) > 1)
            playCardAI(game, ai, DevelopmentCardType.KNIGHT_CARD, null, null);
    }

    /**
     * Method for a hard AI's
     * harbour usage
     *
     * @param game The game the AI is in
     * @param ai   The AI to use the harbour
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    private void useHarbour(Game game, AI ai) {
        Inventory inv = game.getInventory(ai);
        if (!harbours.containsKey(ai)) return;
        ResourceType tradeGive = null;
        ResourceType tradeGet;
        //Check if any resource amount is relatively high
        for (ResourceType res : ResourceType.values())
            if (1.0 * inv.get(res) / inv.getResourceAmount() > 0.6) {
                tradeGive = res;
                break;
            }
        if (tradeGive == null) return;
        //Early Game Brick/ Lumber focus, Later Ore/ Grain focus
        tradeGet = harbours.tradeGet(ai, game.getRound());
        //Use harbour
        if (tradeGet != null) {
            inv.decrease(tradeGive, 2);
            inv.increase(tradeGet);
            return;
        }
        //Use "any" harbour as last resort
        if (harbours.get(ai).contains(IHarbourHex.HarbourResource.ANY)) {
            tradeGet = game.getRound() > 6 ? ORE : inv.get(BRICK) >= inv.get(LUMBER) ? LUMBER : BRICK;
            inv.decrease(tradeGive, 3);
            inv.increase(tradeGet);
        }
    }
}