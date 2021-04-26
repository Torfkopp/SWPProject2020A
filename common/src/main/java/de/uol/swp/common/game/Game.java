package de.uol.swp.common.game;

import de.uol.swp.common.game.map.Hexes.ResourceHex;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.management.IGameMapManagement;
import de.uol.swp.common.game.map.management.IIntersection;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.util.Triple;

import java.util.*;

/**
 * Class for a game
 *
 * @author Mario Fokken
 * @since 2021-01-21
 */
public class Game {

    private static final int[] dices = new int[2];
    private final Lobby lobby;
    private final IGameMapManagement map;
    private final InventoryMap players = new InventoryMap();
    private final List<String> bankInventory;
    private final Set<User> taxPayers = new HashSet<>();
    private UserOrDummy activePlayer;
    private boolean buildingAllowed = false;
    private boolean diceRolledAlready = false;
    private RoadBuildingCardPhase roadBuildingCardPhase = RoadBuildingCardPhase.NO_ROAD_BUILDING_CARD_PLAYED;
    private Player playerWithLongestRoad = null;
    private Player playerWithLargestArmy = null;
    private int longestRoadLength = 0;

    /**
     * Constructor
     *
     * @param lobby   The lobby the game is taking place in
     * @param first   The first player
     * @param gameMap The IGameMap the game will be using
     */
    public Game(Lobby lobby, UserOrDummy first, IGameMapManagement gameMap) {
        this.lobby = lobby;
        this.map = gameMap;
        {
            Player counterPlayer = Player.PLAYER_1;
            for (UserOrDummy userOrDummy : lobby.getUserOrDummies()) {
                players.put(userOrDummy, counterPlayer, new Inventory());
                counterPlayer = counterPlayer.nextPlayer(lobby.getUserOrDummies().size());
            }
        }
        activePlayer = first;
        BankInventory bankInvent = new BankInventory();
        bankInventory = bankInvent.getDevelopmentCards();
    }

    /**
     * Rolls two dices
     *
     * @return Array of two integers
     */
    public static int[] rollDice() {
        int dice1 = (int) (Math.random() * 6 + 1);
        int dice2 = (int) (Math.random() * 6 + 1);
        dices[0] = dice1;
        dices[1] = dice2;
        return (new int[]{dice1, dice2});
    }

    /**
     * Adds a taxPayer to the set
     *
     * @param user The user to add
     *
     * @since 2021-04-11
     */
    public void addTaxPayer(User user) {
        taxPayers.add(user);
    }

    /**
     * Calculates the player's victory points
     *
     * @param player The User object
     *
     * @return int The amount of points
     */
    public int calculateVictoryPoints(Player player) {
        int points = 0;
        //Points made with settlements & cities
        points += map.getPlayerPoints(player);
        //Points made with victory point cards
        points += players.get(player).getVictoryPointCards();
        //2 Points if player has the longest road
        if (Objects.equals(playerWithLongestRoad, player)) points += 2;
        //2 Points if player has the largest army
        if (Objects.equals(playerWithLargestArmy, player)) points += 2;
        return points;
    }

    /**
     * Distributes resources
     * Gets the result of the dices to distribute
     * the resource to the players.
     *
     * @param token Integer between 2 and 12
     *
     * @author Mario Fokken
     * @since 2021-03-15
     */
    public void distributeResources(int token) {
        if (token < 2 || token > 12) return;
        Set<MapPoint> mapPoints = map.getHex(token);
        int amount = 1;
        //Hexes can have the same token
        for (MapPoint mapPoint : mapPoints) {
            //No resources if the robber is on the hex
            if (mapPoint.equals(map.getRobberPosition())) return;
            ResourceHex hex = (ResourceHex) map.getHex(mapPoint);
            //Checks every intersection around the hex
            for (IIntersection i : map.getIntersectionsFromHex(mapPoint)) {
                if (i.getState().equals(IIntersection.IntersectionState.SETTLEMENT)) amount = 1;
                else if (i.getState().equals(IIntersection.IntersectionState.CITY)) amount = 2;
                if (i.getOwner() != null) {
                    switch (hex.getResource()) {
                        case HILLS:
                            getInventory(i.getOwner()).increaseBrick(amount);
                            break;
                        case FIELDS:
                            getInventory(i.getOwner()).increaseGrain(amount);
                            break;
                        case FOREST:
                            getInventory(i.getOwner()).increaseLumber(amount);
                            break;
                        case PASTURE:
                            getInventory(i.getOwner()).increaseWool(amount);
                            break;
                        case MOUNTAINS:
                            getInventory(i.getOwner()).increaseOre(amount);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Gets the active player.
     *
     * @return The currently active player
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @since 2021-03-01
     */
    public UserOrDummy getActivePlayer() {
        return activePlayer;
    }

    /**
     * Gets all the inventories in the game
     *
     * @return An array of all inventories
     */
    public Inventory[] getAllInventories() {
        return players.getInventories().toArray(new Inventory[0]);
    }

    /**
     * Gets the List of the items of the bank.
     *
     * @return The List of the bank inventory
     *
     * @since 2021-02-21
     */
    public List<String> getBankInventory() {
        return bankInventory;
    }

    /**
     * Gets a list of triples consisting of the UserOrDummy, the amount of
     * resource cards they have, and the amount of development cards they have
     *
     * @return List of Triples of UserOrDummy, Integer, Integer
     *
     * @author Alwin Bossert
     * @author Eric Vuong
     * @see de.uol.swp.common.util.Triple
     * @since 2021-03-27
     */
    public List<Triple<UserOrDummy, Integer, Integer>> getCardAmounts() {
        List<Triple<UserOrDummy, Integer, Integer>> list = new ArrayList<>();
        for (UserOrDummy u : lobby.getUserOrDummies()) {
            list.add(new Triple<>(u, players.get(u).getResourceAmount(), players.get(u).getAmountOfDevelopmentCards()));
        }
        return list;
    }

    /**
     * Return the current state of the rolled dices as an array
     *
     * @return Current state of dices
     *
     * @author Marvin Drees
     * @author Maximilian Lindner
     * @since 2021-04-09
     */
    public int[] getDices() {
        return dices;
    }

    /**
     * Gets a specified player's inventory
     *
     * @param player The player whose inventory to get
     *
     * @return The player's inventory
     */
    public Inventory getInventory(Player player) {
        return players.get(player);
    }

    /**
     * Gets a specified player's inventory
     *
     * @param user The user whose inventory to get
     *
     * @return The player's inventory
     */
    public Inventory getInventory(UserOrDummy user) {
        return players.get(user);
    }

    /**
     * Gets the lobby this game is taking place in
     *
     * @return The Lobby this game is taking place in
     */
    public Lobby getLobby() {
        return lobby;
    }

    /**
     * Gets the length of the longest road
     *
     * @return The length of the longest road
     */
    public int getLongestRoadLength() {
        return longestRoadLength;
    }

    /**
     * Set the length of the longest road
     *
     * @param longestRoadLength The new length
     */
    public void setLongestRoadLength(int longestRoadLength) {
        this.longestRoadLength = longestRoadLength;
    }

    /**
     * Gets this game's map
     *
     * @return The IGameMap this game is using
     */
    public IGameMapManagement getMap() {
        return map;
    }

    /**
     * Gets the next player
     *
     * @return User object of the next player
     */
    public UserOrDummy getNextPlayer() {
        return players
                .getUserOrDummyFromPlayer(players.getPlayerFromUserOrDummy(activePlayer).nextPlayer(players.size()));
    }

    /**
     * Gets a user's player
     *
     * @param user The user
     *
     * @return A player
     */
    public Player getPlayer(UserOrDummy user) {
        return players.getPlayerFromUserOrDummy(user);
    }

    /**
     * Gets a mapping of Players to Users
     *
     * @return The player user mapping
     */
    public Map<Player, UserOrDummy> getPlayerUserMapping() {
        Map<Player, UserOrDummy> temp = new HashMap<>();
        for (Player player : Player.values()) {
            temp.put(player, getUserFromPlayer(player));
        }
        return temp;
    }

    /**
     * Gets the player with the largest army
     *
     * @return The player with the largest army
     */
    public Player getPlayerWithLargestArmy() {
        return playerWithLargestArmy;
    }

    /**
     * Sets the player with the largest army
     *
     * @param playerWithLargestArmy The player with the largest army
     */
    public void setPlayerWithLargestArmy(Player playerWithLargestArmy) {
        this.playerWithLargestArmy = playerWithLargestArmy;
    }

    /**
     * Gets the player with the longest road
     *
     * @return The player with the longest road
     */
    public Player getPlayerWithLongestRoad() {
        return playerWithLongestRoad;
    }

    /**
     * Sets the player with the longest road
     *
     * @param playerWithLongestRoad The player with the longest road
     */
    public void setPlayerWithLongestRoad(Player playerWithLongestRoad) {
        this.playerWithLongestRoad = playerWithLongestRoad;
    }

    /**
     * Gets an array of all participating players
     *
     * @return The array of Users participating in this game
     */
    public UserOrDummy[] getPlayers() {
        return players.getUserOrDummyArray();
    }

    /**
     * Gets the roadBuildingCardPhase
     *
     * @return NO_ROAD_BUILDING, FIRST_ROAD, SECOND_ROAD
     *
     * @author Mario Fokken
     * @since 2021-04-20
     */
    public RoadBuildingCardPhase getRoadBuildingCardPhase() {
        return roadBuildingCardPhase;
    }

    /**
     * Sets the roadBuildingCardPhase
     *
     * @param roadBuildingCardPhase NO_ROAD_BUILDING, FIRST_ROAD, SECOND_ROAD
     *
     * @author Mario Fokken
     * @since 2021-04-20
     */
    public void setRoadBuildingCardPhase(RoadBuildingCardPhase roadBuildingCardPhase) {
        this.roadBuildingCardPhase = roadBuildingCardPhase;
    }

    /**
     * Gets the taxPayer Set
     *
     * @return Set of the taxPayer
     *
     * @author Mario Fokken
     * @since 2021-04-11
     */
    public Set<User> getTaxPayers() {
        return taxPayers;
    }

    /**
     * Gets a List of Triples with information about the unique cards
     * (largest army and longest road)
     *
     * @return A List of Triples with information about the unique cards
     *
     * @author Eric Vuong
     * @author Temmo Junkhoff
     * @since 2021-04-10
     */
    public List<Triple<String, UserOrDummy, Integer>> getUniqueCardsList() {
        Map<Boolean, Triple<String, UserOrDummy, Integer>> returnMap = new HashMap<>();
        returnMap.put(false, new Triple<>("game.resources.whohas.longestroad", getUserFromPlayer(playerWithLongestRoad),
                                          longestRoadLength));
        returnMap.put(true, new Triple<>("game.resources.whohas.largestarmy", getUserFromPlayer(playerWithLargestArmy),
                                         playerWithLargestArmy == null ? 0 :
                                         getInventory(playerWithLargestArmy).getKnights()));
        return new LinkedList<>(returnMap.values());
    }

    /**
     * Returns the user corresponding with the given player
     *
     * @param player The player whose User is required
     *
     * @return The user needed
     */
    public UserOrDummy getUserFromPlayer(Player player) {
        return players.getUserOrDummyFromPlayer(player);
    }

    /**
     * Gets whether building is currently allowed or not
     *
     * @return If Building is currently allowed
     *
     * @author Maximilian Lindner
     * @author Marvin Drees
     * @since 2021-04-11
     */
    public boolean isBuildingAllowed() {
        return buildingAllowed;
    }

    /**
     * Set the BuildingAllowed Attribute
     *
     * @param buildingAllowed The new buildingAllowed status
     *
     * @author Maximilian Lindner
     * @author Marvin Drees
     * @since 2021-04-11
     */
    public void setBuildingAllowed(boolean buildingAllowed) {
        this.buildingAllowed = buildingAllowed;
    }

    /**
     * Gets whether the player rolled the dice in the current turn or not
     *
     * @return If Player rolled the dice
     *
     * @author Maximilian Lindner
     * @author Marvin Drees
     * @since 2021-04-11
     */
    public boolean isDiceRolledAlready() {
        return diceRolledAlready;
    }

    /**
     * Set the diceRolledAlready Attribute
     *
     * @param diceRolledAlready The new diceRolledAlready status
     *
     * @author Maximilian Lindner
     * @author Marvin Drees
     * @since 2021-04-11
     */
    public void setDiceRolledAlready(boolean diceRolledAlready) {
        this.diceRolledAlready = diceRolledAlready;
    }

    /**
     * Gets the next player and sets it as the new active player
     *
     * @return User object of the next player
     */
    public UserOrDummy nextPlayer() {
        activePlayer = getNextPlayer();
        return activePlayer;
    }

    /**
     * Removes a user from the taxPayers
     *
     * @param user User to remove
     *
     * @author Mario Fokken
     * @since 2021-04-11
     */
    public void removeTaxPayer(User user) {
        taxPayers.remove(user);
    }
}
