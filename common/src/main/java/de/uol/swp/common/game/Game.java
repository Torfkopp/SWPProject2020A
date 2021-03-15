package de.uol.swp.common.game;

import de.uol.swp.common.game.map.*;
import de.uol.swp.common.game.map.Hexes.ResourceHex;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

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
    private final Inventory[] inventories;
    private final IGameMap map;
    private final User[] players;
    private final List<String> bankInventory;
    private int activePlayer;

    /**
     * Constructor
     *
     * @param lobby The lobby the game is taking place in
     * @param first The first player
     */
    public Game(Lobby lobby, User first) {
        this.lobby = lobby;
        players = lobby.getUsers().toArray(new User[0]);
        for (int i = 0; i < players.length; i++) {
            if (players[i].equals(first)) {
                activePlayer = i;
                break;
            }
        }
        map = new GameMap();
        map.createBeginnerMap();
        inventories = new Inventory[players.length];
        int i = 0;
        for (User u : players) {
            inventories[i++] = new Inventory(u);
        }
        BankInventory bankInvent = new BankInventory();
        bankInventory = bankInvent.getDevelopmentCards();
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
        int num = 0;
        switch (player) {
            case PLAYER_2:
                num = 1;
            case PLAYER_3:
                num = 2;
            case PLAYER_4:
                num = 3;
        }
        //Points made with settlements & cities
        points += map.getPlayerPoints(player);
        //Points made with victory point cards
        points += inventories[num].getVictoryPointCards();
        //2 Points if player has the longest road
        if (inventories[num].isLongestRoad()) points += 2;
        //2 Points if player has the largest army
        if (inventories[num].isLargestArmy()) points += 2;
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
            for (IIntersection i : map.getIntersectionFromHex(mapPoint)) {
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
    public User getActivePlayer() {
        return players[activePlayer];
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
     * Gets an array of all inventories in this game
     *
     * @return The array of inventories in this game
     */
    public Inventory[] getInventories() {
        return inventories;
    }

    /**
     * Gets a specified player's inventory
     *
     * @param player The player whose inventory to get
     *
     * @return The player's inventory
     */
    public Inventory getInventory(Player player) {
        return inventories[player.toString().charAt(7) - 49];
    }

    /**
     * Gets a specified player's inventory
     *
     * @param user The user whose inventory to get
     *
     * @return The player's inventory
     */
    public Inventory getInventory(User user) {
        return getInventory(getPlayer(user));
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
     * @return The IGameMapManagement this game is using
     */
    public IGameMap getMap() {
        return map;
    }

    /**
     * Gets a user's player
     *
     * @param user The user
     *
     * @return A player
     */
    public Player getPlayer(User user) {
        int i = 0;
        for (User u : players) {
            if (u.equals(user)) break;
            i++;
        }
        switch (i) {
            case 1:
                return Player.PLAYER_2;
            case 2:
                return Player.PLAYER_3;
            case 3:
                return Player.PLAYER_4;
        }
        return Player.PLAYER_1;
    }

    /**
     * Gets a user's player
     *
     * @param name The user, but as string
     *
     * @return A player
     */
    public Player getPlayer(String name) {
        int i = 0;
        for (User u : players) {
            if (u.getUsername().equals(name)) break;
            i++;
        }
        switch (i) {
            case 1:
                return Player.PLAYER_2;
            case 2:
                return Player.PLAYER_3;
            case 3:
                return Player.PLAYER_4;
        }
        return Player.PLAYER_1;
    }

    /**
     * Gets an array of all participating players
     *
     * @return The array of Users participating in this game
     */
    public User[] getPlayers() {
        return players;
    }

    /**
     * Gets the next player
     *
     * @return User object of the next player
     */
    public User nextPlayer() {
        activePlayer = (activePlayer + 1) % players.length;
        return players[activePlayer];
    }

    /**
     * Rolls two dices
     *
     * @return Array of two integers
     */
    public int[] rollDice() {
        int dice1 = (int) (Math.random() * 6 + 1);
        int dice2 = (int) (Math.random() * 6 + 1);

        return (new int[]{dice1, dice2});
    }
}
