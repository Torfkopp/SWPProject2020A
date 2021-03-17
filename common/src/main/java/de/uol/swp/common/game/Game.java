package de.uol.swp.common.game;

import de.uol.swp.common.game.map.GameMap;
import de.uol.swp.common.game.map.IGameMap;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.util.TwoKeyMap;

import java.util.List;

/**
 * Class for a game
 *
 * @author Mario Fokken
 * @since 2021-01-21
 */
public class Game {

    private final Lobby lobby;
    private final IGameMap map;
    private final TwoKeyMap<UserOrDummy, Player, Inventory> players = new TwoKeyMap<>();
    private final List<String> bankInventory;
    private UserOrDummy activePlayer;

    /**
     * Constructor
     *
     * @param lobby The lobby the game is taking place in
     * @param first The first player
     */
    public Game(Lobby lobby, UserOrDummy first) {
        this.lobby = lobby;
        {
            Player counterPlayer = Player.PLAYER_1;
            for (UserOrDummy userOrDummy : lobby.getUserOrDummies()) {
                players.put(userOrDummy, counterPlayer, new Inventory());
                counterPlayer = counterPlayer.nextPlayer(lobby.getUserOrDummies().size());
            }
        }
        activePlayer = first;
        map = new GameMap();
        map.createBeginnerMap();
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

    public UserOrDummy getUserFromPlayer(Player player) {
        return players.getKey1(player);
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
        points += players.getWithKey2(player).getVictoryPointCards();
        //2 Points if player has the longest road
        if (players.getWithKey2(player).isLongestRoad()) points += 2;
        //2 Points if player has the largest army
        if (players.getWithKey2(player).isLargestArmy()) points += 2;
        return points;
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
        return players.getWithKey2(player);
    }

    /**
     * Gets a specified player's inventory
     *
     * @param user The user whose inventory to get
     *
     * @return The player's inventory
     */
    public Inventory getInventory(UserOrDummy user) {
        return getInventory(players.getKey2(user));
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

    public Inventory[] getAllInventories() {
        return players.getValues().toArray(new Inventory[0]);
    }

    /**
     * Gets a user's player
     *
     * @param user The user
     *
     * @return A player
     */
    public Player getPlayer(UserOrDummy user) {
        return players.getKey1Key2Map().get(user);
    }

    /**
     * Gets an array of all participating players
     *
     * @return The array of Users participating in this game
     */
    public UserOrDummy[] getPlayers() {
        return players.getKey1Array();
    }

    /**
     * Gets the next player
     *
     * @return User object of the next player
     */
    public UserOrDummy getNextPlayer() {
        return players.getKey1(players.getKey2(activePlayer).nextPlayer(players.size()));
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
