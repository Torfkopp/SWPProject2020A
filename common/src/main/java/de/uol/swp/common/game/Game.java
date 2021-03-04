package de.uol.swp.common.game;

import de.uol.swp.common.game.map.GameMapManagement;
import de.uol.swp.common.game.map.IGameMapManagement;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

import java.util.List;

/**
 * Class for a game
 *
 * @author Mario Fokken
 * @since 2021-01-21
 */
public class Game {

    private final Lobby lobby;
    private final Inventory[] inventories;
    private final IGameMapManagement map;
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
        map = new GameMapManagement();
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

    public User getActivePlayer() {
        return players[activePlayer]; //TODO: docs, maybe remove if not needed
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
    public IGameMapManagement getMap() {
        return map;
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
     * Gets a user's player
     *
     * @return A player
     */
    public Player getPlayer(User user) {
        int i = 0;
        for (User u : players) {
            if(u.equals(user)) break;
            i++;
        }
        switch (i){
            case 1: return Player.PLAYER_2;
            case 2: return Player.PLAYER_3;
            case 3: return Player.PLAYER_4;
        }
        return Player.PLAYER_1;
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
