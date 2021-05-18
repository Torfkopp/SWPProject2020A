package de.uol.swp.server.game;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.hexes.IHarbourHex;
import de.uol.swp.common.game.map.hexes.IResourceHex;
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
import de.uol.swp.common.user.AI;
import de.uol.swp.server.game.map.IGameMapManagement;
import de.uol.swp.server.lobby.LobbyService;

import java.util.*;

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

    private final Map<AI, List<IHarbourHex.HarbourResource>> harbours = new HashMap<>();
    private final Map<Game, Map<MapPoint, Integer>> aiBuildPriority = new HashMap<>();

    /**
     * Enum for all types of
     * ChatMessages an AI can write
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    protected enum WriteType {
        //Message, when...
        START, //the game starts
        TRADE_ACCEPT, //AI accepts a trade
        TRADE_DECLINE, //AI declines a trade
        GAME_WIN, //AI wins the game
        GAME_LOSE, //AI loses
        MOVE_ROBBER, //AI moves robber
        TAX, //AI has to pay tax
        MONOPOLY, //AI plays a monopoly card
    }

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
     * Helper method to move the robber when
     * an AI gets a seven.
     *
     * @author Mario Fokken
     * @since 2021-05-11
     */
    void robberMovementAI(AI uehara, LobbyName lobby) {
        Game game = gameManagement.getGame(lobby);
        IGameMapManagement map = game.getMap();
        AI.Difficulty difficulty = uehara.getDifficulty();
        writeChatMessageAI(uehara, lobby, WriteType.MOVE_ROBBER);

        //Pick place to put the robber on
        int y = 3;
        int x = 3;
        MapPoint mapPoint;
        switch (difficulty) {
            case EASY:
                y = (int) (Math.random() * 4 + 1);
                x = (y == 1 || y == 5) ? ((int) (Math.random() * 3 + 1)) :
                    ((y == 2 || y == 4) ? ((int) (Math.random() * 4 + 1)) : ((int) (Math.random() * 5 + 1)));
                break;
            case HARD:
                Map<MapPoint, Integer> position = new HashMap<>() {};
                Player ai = game.getPlayer(uehara);
                int rating = 0;
                //Get every hex and give them a rating depending on the amount of players around it
                for (int i = 1; i < 6; i++) {
                    for (int j = 1; j < 6; j++) {
                        if (((i == 1 || i == 5) && j >= 3) || ((i == 2 || i == 4) && j >= 4)) break;
                        mapPoint = MapPoint.HexMapPoint(i, j);
                        for (Player p : map.getPlayersAroundHex(mapPoint)) {
                            if (p.equals(ai)) {
                                rating -= 2;
                                continue;
                            }
                            rating++;
                        }
                        position.put(mapPoint, rating);
                    }
                }
                if (position.containsValue(3)) rating = 3;
                else if (position.containsValue(2)) rating = 2;
                else rating = 1;
                //Filter every hex with a rating lower than the highest
                for (MapPoint mp : position.keySet()) if (position.get(mp) < rating) position.remove(mp);
                //Pick a random hex from the survivors
                mapPoint = new ArrayList<>(position.keySet()).get((int) (Math.random() * position.keySet().size()));
                y = mapPoint.getY();
                x = mapPoint.getX();
        }
        mapPoint = MapPoint.HexMapPoint(y, x);
        GameService.LOG.debug("{} moves the robber to position: {}|{}", uehara, y, x);
        map.moveRobber(mapPoint);
        GameService.LOG.debug("Sending RobberPositionMessage for Lobby {}", lobby);
        AbstractGameMessage msg = new RobberPositionMessage(lobby, uehara, mapPoint);
        lobbyService.sendToAllInLobby(lobby, msg);

        //Pick victim to steal random card from
        List<Player> player = new ArrayList<>(map.getPlayersAroundHex(mapPoint));
        if (player.size() > 0) {
            Player victim = player.get(0);
            switch (difficulty) {
                case EASY:
                    victim = player.get((int) (Math.random() * player.size()));
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
            gameService.robRandomResource(lobby, uehara, game.getUserFromPlayer(victim));
        }
    }

    /**
     * Helper method to pay the tax for an AI
     *
     * @param game   The game the AI is in
     * @param uehara The AI to pay the tax
     *
     * @author Mario Fokken
     * @since 2021-05-11
     */
    void taxPayAI(Game game, AI uehara) {
        Inventory inv = game.getInventory(uehara);
        int i = inv.getResourceAmount() / 2;
        writeChatMessageAI(uehara, game.getLobby().getName(), WriteType.TAX);

        GameService.LOG.debug("{} has to give up {} of their {} cards", uehara, i, inv.getResourceAmount());
        switch (uehara.getDifficulty()) {
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
                ResourceList resources = inv.getResources();
                ResourceType highest = BRICK;
                while (i > 0) {
                    for (IResource r : resources)
                        if (r.getAmount() > resources.getAmount(highest)) highest = r.getType();
                    resources.decrease(highest);
                    i--;
                }
        }
    }

    /**
     * Helper method to calculate if the AI
     * wants to accept the trade offer
     *
     * @param uehara   The AI to decide
     * @param lobby    The lobby
     * @param offered  The offered Resources
     * @param demanded The demanded Resources
     *
     * @return If AI accepts
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    boolean tradeAcceptationAI(AI uehara, LobbyName lobby, ResourceList offered, ResourceList demanded) {
        int difference = offered.getTotal() - demanded.getTotal();
        switch (uehara.getDifficulty()) {
            case EASY:
                if (uehara.getUsername().equals("Robert E. O. Speedwagon")) return true;
                //Difference:4-100%, 3-92%, 2-84%, 1-76%, 0-68%
                if (difference >= 0 && ((int) (Math.random() * 100) < (68 + difference * 8))) return true;
                    //Difference:4-0%, 3-8%, 2-16%, 1-24%
                else return difference < 0 && ((int) (Math.random() * 100) < (32 - difference * 8));
            case HARD:
                //Resource prio: EarlyGame - Brick, Lumber - Wool, Grain - Ore - LateGame
                //If AI is low on that Ressource, it will prioritise it
                if (demanded.getTotal() == 0 || difference > 2) return true;
                if (offered.getTotal() == 0 || difference < -2) return false;
                int rating = 0;
                Game game = gameManagement.getGame(lobby);
                for (IResource r : offered)
                    if (game.getInventory(uehara).get(r.getType()) - (r.getAmount() - demanded
                            .getAmount(r.getType())) == 0) rating--;
                List<ResourceType> priority = new ArrayList<>();
                if (game.getRound() <= 4) {
                    priority.add(LUMBER);
                    priority.add(BRICK);
                } else priority.add(ORE);
                for (IResource r : game.getInventory(uehara).getResources())
                    if (r.getAmount() <= 1) priority.add(r.getType());
                for (ResourceType r : priority) rating += offered.getAmount(r) + demanded.getAmount(r);

                return Math.random() * 100 < (50 + rating * 10);
            default:
                return false;
        }
    }

    /**
     * Helper method for an AI's turn
     *
     * @param game   The game the AI is in
     * @param uehara The AI to make its turn
     *
     * @author Mario Fokken
     * @since 2021-05-11
     */
    void turnAI(Game game, AI uehara) {
        switch (uehara.getDifficulty()) {
            case EASY:
                turnPlayCardsAIEasy(game, uehara);
                turnBuildAIEasy(game, uehara);
                break;
            case HARD:
                if (game.getInventory(uehara).get(DevelopmentCardType.KNIGHT_CARD) > 0)
                    playCardAI(game, uehara, DevelopmentCardType.KNIGHT_CARD, null, null);
                turnBuildAIHard(game, uehara);
                break;
        }
        //Trying to end the turn
        gameService.turnEndAI(game, uehara);
    }

    /**
     * Helper method to make a chat
     * message for an AI
     *
     * @param uehara    The AI to send the message
     * @param lobbyName The lobby the AI is in
     * @param type      The type of chat message
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    void writeChatMessageAI(AI uehara, LobbyName lobbyName, WriteType type) {
        String msg = "";
        String name = uehara.getUsername();
        //todo nach AI verlegen

        /* BAUSTEINE
        SPRACHEN: British, German, French, English Italian Accent, -TIbetan accent, -Japanese Accent
        if(uehara.getUsername().equals("")) msg = "";
        EASY-----
        Bri'ish: if(uehara.getAINameEasy().subList(0,6).contains(uehara.getUsername()))
        US-American: if(uehara.getAINameEasy().subList(18,28).contains(uehara.getUsername()))
        Japanese: if(uehara.getAINameEasy().subList(31, 38).contains(uehara.getUsername()))
        Italian: if(uehara.getAINameEasy().subList(38, ende(45)).contains(uehara.getUsername()))
        Generic: if(uehara.getAINameEasy().subList(9, 19).contains(uehara.getUsername()))
        German, French, Animal, Arabic
        HARD-----
        British: if(uehara.getAINameHard().subList(0,6).contains(uehara.getUsername()))
        Arabic: if(uehara.getAINameHard().subList(10,17).contains(uehara.getUsername()))
        'Merican: if(uehara.getAINameHard().subList(17,23).contains(uehara.getUsername()))
        Aztec, Italian, Japanese, Animal
        */
        switch (uehara.getDifficulty()) {
            case EASY:
                switch (type) {
                    case START:
                        if (name.equals("Giorno Giovanna")) msg = "I, Giorno Giovanna, have a dream!";
                        break;
                    case TRADE_ACCEPT:
                        break;
                    case TRADE_DECLINE:
                        if (name.equals("Giorno Giovanna"))
                            msg = "Oh please. Did you really think you're that lucky. A piece of shit like you?";
                        break;
                    case GAME_WIN:
                        if (name.equals("Giorno Giovanna")) msg = "This is... Requiem.";
                        break;
                    case GAME_LOSE:
                        break;
                    case TAX:
                        break;
                    case MONOPOLY:
                        break;
                    case MOVE_ROBBER:
                        break;
                    default:
                }
                break;
            case HARD:
                switch (type) {
                    case START:
                        if (name.equals("Doppio")) msg = "Moshi moshi, Doppio desu";
                        else if (name.equals("Temmo")) msg = "Enums Enums ENUMS ENUMS EEEEEENUUUUUUMS";
                        break;
                    case TRADE_ACCEPT:
                        if (name.equals("Temmo")) msg = "Wow! Nicht erwartet, dass etwas Vernünftiges von dir kommt";
                        break;
                    case TRADE_DECLINE:
                        if (name.equals("Temmo")) msg = "Als würde ich sowas annehmen";
                        break;
                    case GAME_WIN:
                        if (name.equals("Temmo")) msg = "Dein Code ist Müll";
                        break;
                    case GAME_LOSE:
                        if (name.equals("Temmo")) msg = "Hax!";
                        break;
                    case TAX:
                        if (name.equals("Temmo")) msg = "Aufploppende Fenster sind schrecklich";
                        break;
                    case MONOPOLY:
                        if (name.equals("Temmo")) msg = "Gib her, damit kannst du eh nix anfangen";
                        break;
                    case MOVE_ROBBER:
                        if (name.equals("Temmo")) msg = "Das sollte man in der Config-Datei ausschalten können";
                        break;
                }
                break;
        }
        gameService.postAI(uehara, msg, lobbyName);
    }

    /**
     * Helper method to build a
     * city for a hard AI
     * <p>
     * It upgrades the settlement on the most
     * lucrative spot.
     *
     * @param game   The game the AI is in
     * @param uehara The AI to build the city
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private boolean buildCity(Game game, AI uehara) {
        Inventory inv = game.getInventory(uehara);
        if (inv.get(DevelopmentCardType.MONOPOLY_CARD) > 1) {
            if (inv.get(ORE) < 3) playCardAI(game, uehara, DevelopmentCardType.MONOPOLY_CARD, ORE, null);
            if (inv.get(GRAIN) < 2) playCardAI(game, uehara, DevelopmentCardType.MONOPOLY_CARD, GRAIN, null);
        }
        if (!((inv.get(GRAIN) >= 2) && (inv.get(ORE) >= 3))) return false;
        IGameMapManagement map = game.getMap();
        LobbyName lobbyName = game.getLobby().getName();
        Player ai = game.getPlayer(uehara);
        Map<MapPoint, Integer> settlements = new HashMap<>();
        //Fill settlements with every settlement of the AI
        for (MapPoint mp : map.getPlayerSettlementsAndCities().get(ai))
            if (map.getIntersection(mp).getState() == IIntersection.IntersectionState.SETTLEMENT)
                settlements.put(mp, aiBuildPriority.get(game).get(mp));

        //Pick most lucrative spot and upgrade the settlement
        for (int i = 0; i < 15; i++) {
            if (settlements.containsValue(i)) for (MapPoint mp : settlements.keySet())
                if (settlements.get(mp) == i) {
                    map.upgradeSettlement(ai, mp);
                    inv.decrease(GRAIN, 2);
                    inv.decrease(ORE, 3);
                    lobbyService
                            .sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara, mp, CITY));
                    return true;
                }
        }
        return false;
    }

    /**
     * Helper method to build a
     * road for a hard AI
     * <p>
     * It builds roads to reach
     * the most lucrative spot in vicinity
     *
     * @param game   The game the AI is in
     * @param uehara The AI to build the city
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private boolean buildRoad(Game game, AI uehara) {
        Inventory inv = game.getInventory(uehara);
        int roadsPlaceable = Math.min(inv.get(LUMBER), inv.get(BRICK));
        if (roadsPlaceable == 0) return false;
        IGameMapManagement map = game.getMap();
        LobbyName lobbyName = game.getLobby().getName();
        Player ai = game.getPlayer(uehara);
        MapPoint mapPoint;
        List<MapPoint> intersections = new ArrayList<>(map.getPlayerSettlementsAndCities().get(ai));
        //Gets all intersections "under AI's control" (settlement, city, or placeable settlement)
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 11; j++) {
                if ((i == 0 || i == 5) && j > 6) break;
                else if ((i == 1 || i == 4) && j > 8) break;
                mapPoint = MapPoint.IntersectionMapPoint(i, j);
                if (map.settlementPlaceable(ai, mapPoint)) intersections.add(mapPoint);
            }

        Map<MapPoint, Integer> lucrativeSpots = new HashMap<>();
        int i = 0;
        //Gets lucrative spots from priority map
        do {
            for (MapPoint mp : aiBuildPriority.get(game).keySet())
                if (aiBuildPriority.get(game).get(mp) <= 8 + i && map.getIntersection(mp)
                                                                     .getState() == IIntersection.IntersectionState.FREE)
                    lucrativeSpots.put(mp, aiBuildPriority.get(game).get(mp));
            i++;
        } while (lucrativeSpots.isEmpty());

        Map<MapPoint, MapPoint> road = new HashMap<>();
        Map<MapPoint, MapPoint> roadRBCNeeded = new HashMap<>();
        Map<MapPoint, MapPoint> roadYOPNeeded = new HashMap<>();

        //Filters all lucrative spots by theoretical reachability
        for (MapPoint mp : intersections) {
            for (MapPoint mp2 : lucrativeSpots.keySet()) {
                int distance = Math.abs(mp.getY() - mp2.getY()) + Math.abs(mp.getX() - mp2.getX());
                if (distance <= roadsPlaceable) road.put(mp, mp2);
                else if (distance <= roadsPlaceable + 1 && lucrativeSpots.get(mp2) < 5) roadYOPNeeded.put(mp, mp2);
                else if (distance <= roadsPlaceable + 2 && lucrativeSpots.get(mp2) < 5) roadRBCNeeded.put(mp, mp2);
            }
        }

        List<MapPoint> best = null;
        List<MapPoint> temp;

        if (inv.get(DevelopmentCardType.ROAD_BUILDING_CARD) > 0) {
            for (MapPoint mp : roadRBCNeeded.keySet()) {
                temp = findPath(game, uehara, mp, roadRBCNeeded.get(mp));
                if (best == null) best = temp;
                else if (temp.size() > best.size()) best = temp;
            }
            if (best != null) {
                playCardAI(game, uehara, DevelopmentCardType.ROAD_BUILDING_CARD, null, null);
                buildRoadOnPath(game, uehara, best);
                return true;
            }
        }

        if (inv.get(DevelopmentCardType.YEAR_OF_PLENTY_CARD) > 0 && !roadYOPNeeded.isEmpty()) {
            for (MapPoint mp : roadYOPNeeded.keySet()) {
                temp = findPath(game, uehara, mp, roadYOPNeeded.get(mp));
                if (best == null) best = temp;
                else if (temp.size() > best.size()) best = temp;
            }
            if (best != null) {
                playCardAI(game, uehara, DevelopmentCardType.YEAR_OF_PLENTY_CARD, BRICK, LUMBER);
                buildRoadOnPath(game, uehara, best);
                return true;
            }
        }

        if (!road.isEmpty()) {
            for (MapPoint mp : road.keySet()) {
                temp = findPath(game, uehara, mp, roadYOPNeeded.get(mp));
                if (best == null) best = temp;
                else if (temp.size() > best.size()) best = temp;
            }
            if (best != null) {
                buildRoadOnPath(game, uehara, best);
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method to build the roads
     * on a path
     *
     * @param game   The game the AI is in
     * @param uehara The AI to build the roads
     * @param path   The path to build the roads on
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private void buildRoadOnPath(Game game, AI uehara, List<MapPoint> path) {
        Inventory inv = game.getInventory(uehara);
        LobbyName lobbyName = game.getLobby().getName();
        MapPoint mapPoint;
        for (int i = 0; i < path.size(); i++) {
            mapPoint = MapPoint.EdgeMapPoint(path.get(i), path.get(i + 1));
            if (game.getMap().roadPlaceable(game.getPlayer(uehara), mapPoint)) {
                game.getMap().placeRoad(game.getPlayer(uehara), mapPoint);
                inv.decrease(BRICK);
                inv.decrease(LUMBER);
                lobbyService
                        .sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara, mapPoint, ROAD));
            }
        }
    }

    /**
     * Helper method to build a
     * settlement for a hard AI
     * <p>
     * It builds a settlement on the most
     * lucrative spot available.
     *
     * @param game   The game the AI is in
     * @param uehara The AI to build the city
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private boolean buildSettlement(Game game, AI uehara) {
        Inventory inv = game.getInventory(uehara);
        if (!(inv.get(LUMBER) >= 1 && inv.get(BRICK) >= 1 && inv.get(GRAIN) >= 1 && inv.get(WOOL) >= 1)) return false;
        IGameMapManagement map = game.getMap();
        List<MapPoint> intersections = new ArrayList<>();
        LobbyName lobbyName = game.getLobby().getName();
        Player ai = game.getPlayer(uehara);
        MapPoint mapPoint;
        //Fill intersections with all possible settlement locations
        for (int i = 0; i <= 5; i++)
            for (int j = 0; j <= 10; j++) {
                if ((i == 1 || i == 4) && j >= 9) break;
                else if ((i == 0 || i == 5) && j >= 7) break;
                mapPoint = MapPoint.IntersectionMapPoint(i, j);
                if (map.settlementPlaceable(ai, mapPoint)) intersections.add(mapPoint);
            }
        //Pick the most lucrative location and build the settlement
        for (int i = 0; i < 15; i++) {
            for (MapPoint mp : intersections) {
                if (aiBuildPriority.get(game).get(mp) == i) {
                    try {
                        map.placeSettlement(ai, mp);
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
                        harbours.putIfAbsent(uehara, new ArrayList<>());
                        harbours.get(uehara).add(map.getHarbourResource(mp));
                    }
                    inv.decrease(BRICK);
                    inv.decrease(LUMBER);
                    inv.decrease(GRAIN);
                    inv.decrease(WOOL);
                    lobbyService.sendToAllInLobby(lobbyName,
                                                  new BuildingSuccessfulMessage(lobbyName, uehara, mp, SETTLEMENT));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Helper method to fill the
     * aiBuildPriority map used by the
     * hard AI
     *
     * @param game The game the AI is in
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private void createBuildPriority(Game game) {
        IGameMapManagement map = game.getMap();
        MapPoint mapPoint;
        int rating = 0;
        Map<MapPoint, Integer> priority = new HashMap<>();
        for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 10; j++) {
                if ((i == 0 || i == 5) && j >= 7) break;
                else if ((i == 1 || i == 4) && j >= 9) break;
                mapPoint = MapPoint.IntersectionMapPoint(i, j);

                //Rate all locations
                for (MapPoint mp : game.getMap().getResourceHexesFromIntersection(mapPoint)) {
                    IResourceHex hex = (IResourceHex) game.getMap().getHex(mp);
                    rating += Math.abs(hex.getToken() - 7);
                }
                //Special rating for coast intersections
                if (i == 0 || i == 5 || j == 0 || j > 9 || ((i == 1 || i == 4) && j > 7)) {
                    if (map.getHarbourResource(mapPoint) == null) rating += 10;
                    else rating += 5;
                }
                priority.put(mapPoint, rating);
            }
        }
        aiBuildPriority.put(game, priority);
    }

    /**
     * Method to find a path
     * between two Intersections
     *
     * @param game   The Game the AI is in
     * @param uehara The AI to build the roads
     * @param start  The road's start
     * @param end    The road's end
     *
     * @return List of MapPoints; empty if no path is found
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private List<MapPoint> findPath(Game game, AI uehara, MapPoint start, MapPoint end) {
        List<MapPoint> path = new ArrayList<>(Collections.singletonList(start));
        return findPathRecursive(game.getMap(), game.getPlayer(uehara), path, start, end);
    }

    /**
     * Helper method for the helper method
     * to find a path between two Intersections
     *
     * @param map   The IGameMapManagement
     * @param ai    The AI wanting to build the road
     * @param path  The taken path
     * @param start The start point
     * @param end   The end point
     *
     * @return List of MapPoints
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private List<MapPoint> findPathRecursive(IGameMapManagement map, Player ai, List<MapPoint> path, MapPoint start,
                                             MapPoint end) {
        List<IIntersection> inters = new ArrayList<>(
                map.adjacentIntersections(map.getIntersection(path.get(path.size() - 1))));

        List<MapPoint> path1 = new ArrayList<>(path);
        List<MapPoint> path2 = new ArrayList<>(path);

        int yDiff = start.getY() - end.getY();
        int xDiff = start.getX() - end.getX();
        if (yDiff == 0 && xDiff == 0) return path;

        MapPoint mp1 = null;
        if (yDiff > 0) mp1 = MapPoint.IntersectionMapPoint(start.getY() - 1, start.getX());
        else if (yDiff < 0) mp1 = MapPoint.IntersectionMapPoint(start.getY() + 1, start.getX());
        MapPoint mp2 = null;
        if (xDiff > 0) mp2 = MapPoint.IntersectionMapPoint(start.getY(), start.getX() - 1);
        else if (xDiff < 0) mp2 = MapPoint.IntersectionMapPoint(start.getY(), start.getX() + 1);

        if (mp1 != null && inters.contains(map.getIntersection(mp1)) && //
            (map.getIntersection(mp1).getState() == IIntersection.IntersectionState.FREE || //
             map.getIntersection(mp1).getOwner() == ai) && //
            (map.getEdge(MapPoint.EdgeMapPoint(start, mp1)).getOwner() == null || //
             map.getEdge(MapPoint.EdgeMapPoint(start, mp1)).getOwner() == ai)) {
            path1.add(mp1);
            path1 = findPathRecursive(map, ai, path, mp1, end);
        } else path1 = null;
        if (mp2 != null && inters.contains(map.getIntersection(mp2)) && //
            (map.getIntersection(mp2).getState() == IIntersection.IntersectionState.FREE || //
             map.getIntersection(mp2).getOwner() == ai) && //
            (map.getEdge(MapPoint.EdgeMapPoint(start, mp2)).getOwner() == null || //
             map.getEdge(MapPoint.EdgeMapPoint(start, mp2)).getOwner() == ai)) {
            path2.add(mp2);
            path2 = findPathRecursive(map, ai, path, mp2, end);
        } else path2 = null;

        if (path1 == null) return path2;
        if (path2 == null) return path1;
        return path1.size() >= path2.size() ? path1 : path2;
    }

    /**
     * Helper method to let a hard AI
     * play a card
     *
     * @param game   The game the AI is in
     * @param uehara The AI to do the card playing
     * @param type   The type of card to play
     * @param res1   Needed for Monopoly, YearOfPlenty
     * @param res2   Only needed for YearOfPlenty
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private void playCardAI(Game game, AI uehara, DevelopmentCardType type, ResourceType res1, ResourceType res2) {
        Inventory inv = game.getInventory(uehara);
        LobbyName lobbyName = game.getLobby().getName();
        switch (type) {
            case KNIGHT_CARD:
                inv.decrease(DevelopmentCardType.KNIGHT_CARD);
                inv.increaseKnights();
                gameService.checkLargestArmy(lobbyName, uehara);
                robberMovementAI(uehara, lobbyName);
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
                writeChatMessageAI(uehara, lobbyName, WriteType.MONOPOLY);
                break;
        }
    }

    /**
     * Helper method for an easy AI's
     * building phase
     *
     * @param game   The game the AI is in
     * @param uehara The AI to do the building
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    private void turnBuildAIEasy(Game game, AI uehara) {
        Player ai = game.getPlayer(uehara);
        Inventory inv = game.getInventory(uehara);
        IGameMapManagement map = game.getMap();
        LobbyName lobbyName = game.getLobby().getName();

        List<MapPoint> cities = new ArrayList<>();
        List<MapPoint> settlements = new ArrayList<>();
        List<MapPoint> roads = new ArrayList<>();

        for (MapPoint mp : map.getPlayerSettlementsAndCities().get(ai))
            if (map.settlementUpgradeable(ai, mp)) cities.add(mp);

        MapPoint mp;
        for (int i = 0; i <= 5; i++)
            for (int j = 0; j <= 10; j++) {
                //Why? see GameMapManagement.createIntersectionEdgeNetwork
                if ((i == 0 || i == 5) && j >= 7) break;
                else if ((i == 1 || i == 4) && j >= 9) break;
                mp = MapPoint.IntersectionMapPoint(i, j);
                //Settlement Stuff
                if (map.settlementPlaceable(ai, mp)) settlements.add(mp);
                //Road Stuff
                for (IEdge e : map.incidentEdges(map.getIntersection(mp)))
                    if (map.roadPlaceable(ai, e)) switch (e.getOrientation()) {
                        case EAST:
                            roads.add(
                                    MapPoint.EdgeMapPoint(mp, MapPoint.IntersectionMapPoint(mp.getY(), mp.getX() + 1)));
                            break;
                        case WEST:
                            roads.add(
                                    MapPoint.EdgeMapPoint(mp, MapPoint.IntersectionMapPoint(mp.getY(), mp.getX() - 1)));
                            break;
                        case SOUTH:
                            roads.add(
                                    MapPoint.EdgeMapPoint(mp, MapPoint.IntersectionMapPoint(mp.getY() - 1, mp.getX())));
                            break;
                    }
            }
        //Build City for Rock 'n' Roll
        while (inv.get(GRAIN) >= 2 && inv.get(ORE) >= 3) {
            mp = cities.remove((int) (Math.random() * cities.size()));
            map.upgradeSettlement(ai, mp);
            inv.decrease(GRAIN, 2);
            inv.decrease(ORE, 3);
            lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara, mp, CITY));
        }

        //Build Settlement
        while (inv.get(BRICK) >= 1 && inv.get(LUMBER) >= 1 && inv.get(GRAIN) >= 1 && inv.get(WOOL) >= 1) {
            mp = settlements.remove((int) (Math.random() * settlements.size()));
            try {
                map.placeSettlement(ai, mp);
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
            lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara, mp, SETTLEMENT));
        }

        //Build Street
        while (inv.get(BRICK) >= 1 && inv.get(LUMBER) >= 1) {
            mp = roads.remove((int) (Math.random() * roads.size()));
            map.placeRoad(ai, mp);
            inv.decrease(BRICK);
            inv.decrease(LUMBER);
            lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara, mp, ROAD));
        }

        //Buy Dev Card
        while (inv.get(WOOL) >= 1 && inv.get(GRAIN) >= 1 && inv.get(ORE) >= 1)
            gameService.onBuyDevelopmentCardRequest(new BuyDevelopmentCardRequest(uehara, lobbyName));
    }

    /**
     * Helper method for a hard AI's
     * building phase
     *
     * @param game   The game the AI is in
     * @param uehara The AI to do the building
     *
     * @author Mario Fokken
     * @since 2021-05-15
     */
    private void turnBuildAIHard(Game game, AI uehara) {
        LobbyName lobbyName = game.getLobby().getName();
        Inventory inv = game.getInventory(uehara);
        if (!aiBuildPriority.containsValue(game)) createBuildPriority(game);

        useHarbour(game, uehara);

        //Random chance of buying a card increases steadily
        if (inv.get(GRAIN) > 0 && inv.get(ORE) > 0 && inv.get(WOOL) > 0 && //
            ((int) (Math.random() * 100) < (10 + game.getRound() * 8)))
            gameService.onBuyDevelopmentCardRequest(new BuyDevelopmentCardRequest(uehara, lobbyName));

        //The numbers may not be optimal; not enough data
        if (game.getRound() < 3) {
            //Focus on expanding like crazy
            buildRoad(game, uehara);
            buildSettlement(game, uehara);
        } else if (game.getRound() < 6) {
            //Build a settlement, before making a road
            buildSettlement(game, uehara);
            buildRoad(game, uehara);
            buildCity(game, uehara);
        } else if (game.getRound() < 9) {
            //Twice the building, double the settlements
            while (buildSettlement(game, uehara)) buildSettlement(game, uehara);
            buildCity(game, uehara);
            buildRoad(game, uehara);
        } else {
            //Cities are good
            while (buildCity(game, uehara)) buildCity(game, uehara);
            while (buildSettlement(game, uehara)) buildSettlement(game, uehara);
            buildRoad(game, uehara);
        }
    }

    /**
     * Helper method for an easy AI's
     * card playing phase
     *
     * @param game   The game the AI is in
     * @param uehara The AI to do the card playing
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    private void turnPlayCardsAIEasy(Game game, AI uehara) {
        DevelopmentCardList cards = game.getInventory(uehara).getDevelopmentCards();
        LobbyName lobbyName = game.getLobby().getName();
        Inventory inv = game.getInventory(uehara);

        /**
         * Local class
         *
         * @author Mario Fokken
         * @since 2021-05-13
         */
        class randomResource {

            /**
             * Returns a random resource
             *
             * @return random Resource
             */
            private ResourceType randomResource() {
                switch ((int) (Math.random() * 4)) {
                    case 0:
                        return BRICK;
                    case 1:
                        return GRAIN;
                    case 2:
                        return LUMBER;
                    case 3:
                        return ORE;
                    default:
                        return WOOL;
                }
            }
        }
        randomResource r = new randomResource();

        if (cards.getAmount(DevelopmentCardType.MONOPOLY_CARD) > 0) {
            playCardAI(game, uehara, DevelopmentCardType.MONOPOLY_CARD, r.randomResource(), null);
            return;
        }
        if (cards.getAmount(DevelopmentCardType.ROAD_BUILDING_CARD) > 0) {
            List<IEdge> roads = new ArrayList<>();
            Player ai = game.getPlayer(uehara);
            for (int i = 0; i <= 5; i++) {
                for (int j = 0; j <= 5; j++) {
                    MapPoint mp = MapPoint.HexMapPoint(i, j);
                    for (IEdge edge : game.getMap().getEdgesFromHex(mp))
                        if (game.getMap().roadPlaceable(ai, edge)) roads.add(edge);
                }
            }
            if (roads.size() > 1) {
                game.getMap().placeRoad(ai, roads.remove((int) (Math.random() * roads.size())));
                inv.decrease(DevelopmentCardType.ROAD_BUILDING_CARD);
                if (roads.size() > 2) game.getMap().placeRoad(ai, roads.remove((int) (Math.random() * roads.size())));
            }
            return;
        }
        if (cards.getAmount(DevelopmentCardType.YEAR_OF_PLENTY_CARD) > 0) {
            playCardAI(game, uehara, DevelopmentCardType.YEAR_OF_PLENTY_CARD, r.randomResource(), r.randomResource());
            return;
        }
        if (cards.getAmount(DevelopmentCardType.KNIGHT_CARD) > 1)
            playCardAI(game, uehara, DevelopmentCardType.KNIGHT_CARD, null, null);
    }

    /**
     * Helper method for a hard AI's
     * harbour usage
     *
     * @param game   The game the AI is in
     * @param uehara The AI to use the harbour
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    private void useHarbour(Game game, AI uehara) {
        Inventory inv = game.getInventory(uehara);
        if (!harbours.containsKey(uehara)) return;
        ResourceType tradeGive = null;
        ResourceType tradeGet = null;
        //Check if any resource amount is relatively high
        for (ResourceType res : ResourceType.values())
            if (1.0 * inv.get(res) / inv.getResourceAmount() > 0.6) {
                tradeGive = res;
                break;
            }
        if (tradeGive == null) return;
        //Early Game Brick/ Lumber focus, Later Ore/ Grain focus
        if (game.getRound() < 6) {
            if (harbours.get(uehara).contains(IHarbourHex.HarbourResource.LUMBER)) tradeGet = LUMBER;
            if (harbours.get(uehara).contains(IHarbourHex.HarbourResource.BRICK)) tradeGet = BRICK;
        } else {
            if (harbours.get(uehara).contains(IHarbourHex.HarbourResource.GRAIN)) tradeGet = GRAIN;
            if (harbours.get(uehara).contains(IHarbourHex.HarbourResource.ORE)) tradeGet = ORE;
        }
        //Wool
        if (tradeGet == null && harbours.get(uehara).contains(IHarbourHex.HarbourResource.WOOL)) tradeGet = WOOL;
        //Use harbour
        if (tradeGet != null) {
            inv.decrease(tradeGive, 2);
            inv.increase(tradeGet);
            return;
        }
        //Use "any" harbour as last resort
        if (harbours.get(uehara).contains(IHarbourHex.HarbourResource.ANY)) {
            tradeGet = game.getRound() > 6 ? ORE : inv.get(BRICK) >= inv.get(LUMBER) ? LUMBER : BRICK;
            inv.decrease(tradeGive, 3);
            inv.increase(tradeGet);
        }
    }
}