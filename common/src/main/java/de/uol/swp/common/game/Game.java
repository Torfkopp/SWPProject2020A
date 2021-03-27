package de.uol.swp.common.game;

import de.uol.swp.common.game.map.Hexes.ResourceHex;
import de.uol.swp.common.game.map.*;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.UserOrDummy;

import java.util.List;
import java.util.Set;

/**
 * Class for a game
 *
 * @author Mario Fokken
 * @since 2021-01-21
 */
public class Game {

    private final Lobby lobby;
    private final IGameMap map;
    private final InventoryMap players = new InventoryMap();
    private final List<String> bankInventory;
    private UserOrDummy activePlayer;

    /**
     * Constructor
     *
     * @param lobby   The lobby the game is taking place in
     * @param first   The first player
     * @param gameMap The IGameMap the game will be using
     */
    public Game(Lobby lobby, UserOrDummy first, IGameMap gameMap) {
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

        return (new int[]{dice1, dice2});
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
        if (players.get(player).isLongestRoad()) points += 2;
        //2 Points if player has the largest army
        if (players.get(player).isLargestArmy()) points += 2;
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
     * Gets this game's map
     *
     * @return The IGameMap this game is using
     */
    public IGameMap getMap() {
        return map;
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
     * Gets an array of all participating players
     *
     * @return The array of Users participating in this game
     */
    public UserOrDummy[] getPlayers() {
        return players.getUserOrDummyArray();
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
     * Gets the next player and sets it as the new active player
     *
     * @return User object of the next player
     */
    public UserOrDummy nextPlayer() {
        activePlayer = getNextPlayer();
        return activePlayer;
    }
}
