package de.uol.swp.server.game;

import de.uol.swp.common.game.CardsAmount;
import de.uol.swp.common.game.RoadBuildingCardPhase;
import de.uol.swp.common.game.StartUpPhaseBuiltStructures;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.hexes.ResourceHex;
import de.uol.swp.common.game.map.management.IIntersection;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.BankInventory;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.Inventory;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.InventoryMap;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.uniqueCards.UniqueCard;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.uniqueCards.UniqueCardsType;
import de.uol.swp.common.specialisedUtil.*;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.user.User;
import de.uol.swp.common.util.Util;
import de.uol.swp.server.game.map.IGameMapManagement;
import de.uol.swp.server.lobby.ILobby;
import de.uol.swp.server.specialisedUtil.ActorBooleanMap;
import de.uol.swp.server.specialisedUtil.ActorStartUpBuiltMap;

import java.util.*;

/**
 * Class for a game
 *
 * @author Mario Fokken
 * @since 2021-01-21
 */
public class Game {

    private static final int[] dices = new int[2];
    private final ILobby lobby;
    private final IGameMapManagement map;
    private final InventoryMap players = new InventoryMap();
    private final BankInventory bankInventory;
    private final LinkedList<Actor> startUpPlayerOrder = new LinkedList<>();
    private final ActorSet taxPayers = new ActorSet();
    private final ActorBooleanMap autoRollEnabled;
    private final ActorBooleanMap pauseGameMap = new ActorBooleanMap(); //true if the user wants to change the current pause status of the game
    private final ActorStartUpBuiltMap playersStartUpBuiltMap;
    private final VictoryPointOverTimeMap victoryPointsOverTimeMap = new VictoryPointOverTimeMap();
    private final Actor first;
    private final int maxTradeDiff;
    private final ActorSet playerList;
    private Actor activePlayer;
    private boolean buildingAllowed = false;
    private boolean diceRolledAlready = false;
    private RoadBuildingCardPhase roadBuildingCardPhase = RoadBuildingCardPhase.NO_ROAD_BUILDING_CARD_PLAYED;
    private StartUpPhase startUpPhase;
    private Player playerWithLongestRoad = null;
    private Player playerWithLargestArmy = null;
    private int longestRoadLength = 0;
    private boolean pausedByTrade = false;
    private boolean pausedByVoting = false;
    private int round = 1;
    private ActorPair robResourceReceiverVictimPair = null;
    private Actor robberMover = null;

    public enum StartUpPhase {
        PHASE_1,
        PHASE_2,
        NOT_IN_STARTUP_PHASE
    }

    /**
     * Constructor
     *
     * @param lobby   The lobby the game is taking place in
     * @param first   The first player
     * @param gameMap The IGameMap the game will be using
     */
    public Game(ILobby lobby, Actor first, IGameMapManagement gameMap) {
        this.lobby = lobby;
        this.map = gameMap;
        this.first = first;
        this.maxTradeDiff = getLobby().getMaxTradeDiff();
        playersStartUpBuiltMap = new ActorStartUpBuiltMap();
        autoRollEnabled = new ActorBooleanMap();
        {
            ActorSet playerList = new ActorSet();
            playerList.addAll(lobby.getActors());
            preparePausedMembers();
            victoryPointsOverTimeMap.put(first, new HashMap<>());
            victoryPointsOverTimeMap.get(first).put(0, 0);
            startUpPlayerOrder.addLast(first);
            playersStartUpBuiltMap.put(first);
            players.put(first, Player.PLAYER_1, new Inventory());
            playerList.remove(first);
            Player counterPlayer = Player.PLAYER_2;
            while (playerList.size() > 0) {
                int randomNumber = Util.randomInt(playerList.size());
                Actor randomUser = playerList.get(randomNumber);
                victoryPointsOverTimeMap.put(randomUser, new HashMap<>());
                victoryPointsOverTimeMap.get(randomUser).put(0, 0);
                startUpPlayerOrder.addLast(randomUser);
                playersStartUpBuiltMap.put(randomUser);
                players.put(randomUser, counterPlayer, new Inventory());
                counterPlayer = counterPlayer.nextPlayer(lobby.getActors().size());
                playerList.remove(randomUser);
                autoRollEnabled.put(randomUser, false);
            }
        }
        startUpPhase = lobby.isStartUpPhaseEnabled() ? StartUpPhase.PHASE_1 : StartUpPhase.NOT_IN_STARTUP_PHASE;
        activePlayer = first;
        bankInventory = new BankInventory();
        playerList = createPlayerList();
    }

    /**
     * Rolls two dices
     *
     * @return Array of two integers
     */
    public static int[] rollDice() {
        dices[0] = Util.randomPositiveInt(7);
        dices[1] = Util.randomPositiveInt(7);
        return (dices);
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
        points += players.get(player).get(DevelopmentCardType.VICTORY_POINT_CARD);
        //2 Points if player has the longest road
        if (Objects.equals(playerWithLongestRoad, player)) points += 2;
        //2 Points if player has the largest army
        if (Objects.equals(playerWithLargestArmy, player)) points += 2;
        return points;
    }

    /**
     * Changes status of a user in the pauseGameMap
     * to true.
     *
     * @param user A User who wants to change the pause status of a game
     *
     * @author Maximilian Lindner
     * @since 2021-05-21
     */
    public void changePauseStatus(Actor user) {
        pauseGameMap.replace(user, !pauseGameMap.get(user));
    }

    /**
     * Prepares a ordered list of players in the game
     *
     * @return An ordered list of the players
     *
     * @author Maximilian Lindner
     * @since 2021-06-11
     */
    public ActorSet createPlayerList() {
        ActorSet playerList = new ActorSet();
        Actor player = activePlayer;
        playerList.add(activePlayer);
        for (int i = 0; i < players.size() - 1; i++) {
            player = players.getActorFromPlayer(players.getPlayerFromActor(player).nextPlayer(players.size()));
            playerList.add(player);
        }
        return playerList;
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
                // NotEnoughResourcesExceptions can be ignored here because amount gets
                // defined above as +1 or +2, which won't take the Inventory into the negative
                if (i.getOwner() != null) {
                    getInventory(i.getOwner()).increase(hex.getResource(), amount);
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
    public Actor getActivePlayer() {
        return activePlayer;
    }

    /**
     * Sets the active player
     *
     * @param newActivePlayer The active player who gets set
     *
     * @author Eric Vuong
     * @since 2021-06-10
     */
    private void setActivePlayer(Actor newActivePlayer) {
        activePlayer = newActivePlayer;
    }

    /**
     * Returns the user corresponding with the given player
     *
     * @param player The player whose User is required
     *
     * @return The user needed
     */
    public Actor getActorFromPlayer(Player player) {
        return players.getActorFromPlayer(player);
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
     * Gets the autoRoll Status of a player
     *
     * @return All autoRoll States
     *
     * @author Maximilian Lindner
     * @since 2021-04-26
     */
    public Boolean getAutoRollEnabled(Actor actor) {
        return autoRollEnabled.get(actor);
    }

    /**
     * Gets the List of the items of the bank.
     *
     * @return The List of the bank inventory
     *
     * @since 2021-02-21
     */
    public BankInventory getBankInventory() {
        return bankInventory;
    }

    /**
     * Gets a list of triples consisting of the Actor, the amount of
     * resource cards they have, and the amount of development cards they have
     *
     * @return List of Triples of Actor, Integer, Integer
     *
     * @author Alwin Bossert
     * @author Eric Vuong
     * @since 2021-03-27
     */
    public List<CardsAmount> getCardAmounts() {
        List<CardsAmount> list = new ArrayList<>();
        for (Actor u : lobby.getActors()) {
            list.add(new CardsAmount(u, players.get(u).getResourceAmount(),
                                     players.get(u).getAmountOfDevelopmentCards()));
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
     * Gets the Actor who made the current game's first turn
     *
     * @author Aldin Dervisi
     * @since 2021-05-01
     */
    public Actor getFirst() {
        return first;
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
    public Inventory getInventory(Actor user) {
        return players.get(user);
    }

    /**
     * Gets the lobby this game is taking place in
     *
     * @return The Lobby this game is taking place in
     */
    public ILobby getLobby() {
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
     * Gets this game's maximum resource difference
     *
     * @return The Game's maximum resource difference
     */
    public int getMaxTradeDiff() {
        return maxTradeDiff;
    }

    /**
     * Gets the next player
     *
     * @return User object of the next player
     */
    public Actor getNextPlayer() {
        return players.getActorFromPlayer(players.getPlayerFromActor(activePlayer).nextPlayer(players.size()));
    }

    /**
     * Gets how many players in the lobby want to change the paused
     * status.
     *
     * @return How many players want to change the paused status
     *
     * @author Maximilian Lindner
     * @since 2021-05-21
     */
    public int getPausedMembers() {
        int pausedMembers = 0;
        for (Map.Entry<Actor, Boolean> entry : pauseGameMap.entrySet()) {
            if (entry.getValue()) pausedMembers++;
        }
        return pausedMembers;
    }

    /**
     * Gets a user's player
     *
     * @param user The user
     *
     * @return A player
     */
    public Player getPlayer(Actor user) {
        return players.getPlayerFromActor(user);
    }

    /**
     * Gets the player list
     *
     * @return The order of the players in the game
     *
     * @author Maximilian Lindner
     * @since 2021-06-11
     */
    public ActorSet getPlayerList() {
        return playerList;
    }

    /**
     * Gets a mapping of Players to Users
     *
     * @return The player user mapping
     */
    public Map<Player, Actor> getPlayerUserMapping() {
        Map<Player, Actor> temp = new HashMap<>();
        for (Player player : Player.values()) {
            temp.put(player, getActorFromPlayer(player));
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
    public Actor[] getPlayers() {
        return players.getActorArray();
    }

    /**
     * Gets a map of the Users and what part of the founding phase they already finished
     *
     * @return playersStartUpBuiltMap
     *
     * @author Sven Ahrens
     * @since 2021-05-03
     */
    public ActorStartUpBuiltMap getPlayersStartUpBuiltMap() {
        return playersStartUpBuiltMap;
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
     * Gets the robResourceReceiverVictimPair
     *
     * @return ActorPair of receiver and victim
     *
     * @author Mario Fokken
     * @since 2021-06-11
     */
    public ActorPair getRobResourceReceiverVictimPair() {
        return robResourceReceiverVictimPair;
    }

    /**
     * Sets the robResourceReceiverVictimPair
     *
     * @param robResourceReceiverVictimPair ActorPair of receiver and victim
     *
     * @author Mario Fokken
     * @since 2021-06-11
     */
    public void setRobResourceReceiverVictimPair(ActorPair robResourceReceiverVictimPair) {
        this.robResourceReceiverVictimPair = robResourceReceiverVictimPair;
    }

    /**
     * Gets the user who currently has to move the robber
     *
     * @return The user who has to move the robber, null if not applicable
     *
     * @author Eric Vuong
     * @since 2021-07-03
     */
    public Actor getRobberMover() {
        return robberMover;
    }

    /**
     * Sets the user who currently has to move the robber
     *
     * @param robberMover The user who has to move the robber
     *
     * @author Eric Vuong
     * @since 2021-07-03
     */
    public void setRobberMover(Actor robberMover) {
        this.robberMover = robberMover;
    }

    /**
     * Gets the current Round the Game is in
     *
     * @author Aldin Dervisi
     * @since 2021-05-01
     */
    public int getRound() {return round;}

    /**
     * Gets the Founding Phase Enum
     *
     * @return startUpPhase
     *
     * @author Sven Ahrens
     * @since 2021-05-03
     */
    public StartUpPhase getStartUpPhase() {
        return startUpPhase;
    }

    /**
     * Sets the StartUpPhase Enum
     *
     * @param startUpPhase the Enum which is used in order to grasp the current phase
     *
     * @author Sven Ahrens
     * @since 2021-05-03
     */
    public void setStartUpPhase(StartUpPhase startUpPhase) {
        this.startUpPhase = startUpPhase;
    }

    /**
     * Gets the StartUpPlayerOrder Deque which is used for setting the order
     * in which the players take turns during the founding phase
     *
     * @return startUpPlayerOrder
     *
     * @author Sven Ahrens
     * @since 2021-05-03
     */
    public Deque<Actor> getStartUpPlayerOrder() {
        return startUpPlayerOrder;
    }

    /**
     * Gets the taxPayer Set
     *
     * @return Set of the taxPayer
     *
     * @author Mario Fokken
     * @since 2021-04-11
     */
    public ActorSet getTaxPayers() {
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
    public List<UniqueCard> getUniqueCardsList() {
        List<UniqueCard> returnList = new LinkedList<>();
        returnList.add(new UniqueCard(UniqueCardsType.LONGEST_ROAD, getActorFromPlayer(playerWithLongestRoad),
                                      longestRoadLength));
        returnList.add(new UniqueCard(UniqueCardsType.LARGEST_ARMY, getActorFromPlayer(playerWithLargestArmy),
                                      playerWithLargestArmy == null ? 0 :
                                      getInventory(playerWithLargestArmy).getKnights()));
        return returnList;
    }

    /**
     * Gets a map of users or dummies and their chosen colour
     *
     * @return A map containing users or dummies and their chosen colour
     *
     * @author Mario Fokken
     * @since 2021-06-02
     */
    public ActorColourMap getUserColoursMap() {
        return lobby.getUserColourMap();
    }

    /**
     * Gets a map of users or dummies and their corresponding players
     *
     * @return A map containing users or dummies and their corresponding players
     *
     * @since 2021-05-20
     */
    public ActorPlayerMap getUserToPlayerMap() {
        return players.getUserToPlayerMap();
    }

    /**
     * Gets a map of users or dummies and their corresponding players
     *
     * @return A map containing users or dummies and their corresponding players
     *
     * @author Aldin Dervisi
     * @since 2021-05-20
     */
    public VictoryPointOverTimeMap getVictoryPointsOverTimeMap() {
        return victoryPointsOverTimeMap;
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
     * Gets the paused status
     *
     * @return Whether the game is paused or not
     *
     * @author Maximilian Lindner
     * @since 2021-05-21
     */
    public boolean isPausedByVoting() {
        return pausedByVoting;
    }

    /**
     * Gets the next player and sets it as the new active player
     *
     * @return User object of the next player
     */
    public Actor nextPlayer() {
        activePlayer = getNextPlayer();
        if (activePlayer.equals(first)) round++;
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

    /**
     * Replace a User who leaves a lobby in Game with an AI
     *
     * @param userToReplace     The user who left the lobby and should be replaced
     * @param userToReplaceWith The AI that replaces the user who left the lobby
     *
     * @author Eric Vuong
     * @since 2021-06-10
     */
    public void replaceUser(Actor userToReplace, Actor userToReplaceWith) {
        if (Objects.equals(getActivePlayer(), userToReplace)) setActivePlayer(userToReplaceWith);
        players.replace(userToReplace, userToReplaceWith);
        pauseGameMap.remove(userToReplace);
        pauseGameMap.put(userToReplaceWith, true);
        StartUpPhaseBuiltStructures build = playersStartUpBuiltMap.remove(userToReplace);
        playersStartUpBuiltMap.put(userToReplaceWith, build);
        Map<Integer, Integer> currentVictoryPoints = victoryPointsOverTimeMap.remove(userToReplace);
        victoryPointsOverTimeMap.put(userToReplaceWith, currentVictoryPoints);
        if (Objects.equals(robberMover, userToReplace)) robberMover = userToReplaceWith;
        for (int i = 0; i < startUpPlayerOrder.size(); i++) {
            Actor user = startUpPlayerOrder.get(i);
            if (Objects.equals(user, userToReplace)) {
                startUpPlayerOrder.remove(user);
                startUpPlayerOrder.add(i, userToReplaceWith);
            }
        }
    }

    /**
     * Replaces the autoRoll status for a specific player
     *
     * @param actor             The user who wants to change the status
     * @param isAutoRollEnabled The new autoRoll status
     *
     * @author Maximilian Lindner
     * @since 2021-04-26
     */
    public void setAutoRollEnabled(Actor actor, boolean isAutoRollEnabled) {
        autoRollEnabled.replace(actor, isAutoRollEnabled);
    }

    /**
     * Sets the boolean paused for the game.
     *
     * @param pausedByTrade True if paused by trade
     *
     * @author Alwin Bossert
     * @since 2021-05-02
     */
    public void setPausedByTrade(boolean pausedByTrade) {
        this.pausedByTrade = pausedByTrade;
    }

    /**
     * Checks if all player want to change the pause status of the game.
     * Returns false if not everyone want to change the status. Otherwise
     * the pause status gets changed and the preparePausedMembers method is
     * called.
     *
     * @author Maximilian Lindner
     * @since 2021-05-21
     */
    public void updatePauseByVotingStatus() {
        for (Map.Entry<Actor, Boolean> entry : pauseGameMap.entrySet()) {
            if (!entry.getValue()) return;
        }
        pausedByVoting = !pausedByVoting;
        preparePausedMembers();
    }

    /**
     * Helper method to prepare the pauseGameMap.
     *
     * @author Maximilian Lindner
     * @since 2021-05-21
     */
    private void preparePausedMembers() {
        ActorSet playerList = lobby.getActors();
        for (Actor actor : playerList) {
            if (actor instanceof User) pauseGameMap.put(actor, false);
            else pauseGameMap.put(actor, true);
        }
    }
}
