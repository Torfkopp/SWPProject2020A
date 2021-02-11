package de.uol.swp.common.game;

import de.uol.swp.common.game.map.GameMapManagement;
import de.uol.swp.common.game.map.IGameMapManagement;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

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
    private int activePlayer;

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
        int num = 1;
        switch(player){
            case PLAYER_2: num= 2;
            case PLAYER_3: num= 3;
            case PLAYER_4: num = 4;
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

    public Inventory[] getInventories() {
        return inventories;
    }

    public Inventory getInventory(Player player) {
        return inventories[player.toString().charAt(7) - 49];
    }

    public Lobby getLobby() {
        return lobby;
    }

    public IGameMapManagement getMap() {
        return map;
    }

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
}
