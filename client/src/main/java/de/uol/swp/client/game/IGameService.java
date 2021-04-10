package de.uol.swp.client.game;

import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.user.User;

/**
 * An interface for all methods of the GameService
 *
 * @author Maximilian Lindner
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.game.GameService
 * @since 2021-04-07
 */
public interface IGameService {

    /**
     * Posts a request to end the turn onto the Event
     *
     * @param lobbyName The name of the lobby in which to end the turn
     * @param user      The user who wants to end the turn
     *
     * @see de.uol.swp.common.game.request.EndTurnRequest
     * @since 2021-01-15
     */
    void endTurn(String lobbyName, User user);

    /**
     * Posts a request to play a KnightCard
     *
     * @param lobbyName The name of the lobby
     * @param user      The user
     */
    void playKnightCard(String lobbyName, User user);

    /**
     * Posts a request to play a MonopolyCard
     *
     * @param lobbyName The name of the lobby
     * @param user      The user
     * @param resource  The resource the user wants
     */
    void playMonopolyCard(String lobbyName, User user, Resources resource);

    /**
     * Posts a request to play a RoadBuildingCard
     *
     * @param lobbyName The name of the lobby
     * @param user      The user
     */
    void playRoadBuildingCard(String lobbyName, User user);

    /**
     * Posts a request to play a YearOfPlentyCard
     *
     * @param lobbyName The name of the lobby
     * @param user      The user
     * @param resource1 The resource the user wants
     * @param resource2 The resource the user wants
     */
    void playYearOfPlentyCard(String lobbyName, User user, Resources resource1, Resources resource2);

    /**
     * Posts a request to roll the dices
     *
     * @param lobbyName The Lobby in which to roll the dice
     * @param user      The User who wants to roll the dice
     *
     * @see de.uol.swp.common.game.request.RollDiceRequest
     * @since 2021-02-22
     */
    void rollDice(String lobbyName, User user);

    /**
     * Posts a request to start the game session
     *
     * @param lobbyName The name of the lobby where the session should be started.
     * @param user      The user who wants to start the session.
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @since 2021-03-23
     */
    void startSession(String lobbyName, User user);

    /**
     * Posts a request to update ones Inventory
     *
     * @param lobbyName The name of the lobby the user wants to update his Inventory in
     * @param user      The user who wants to update his Inventory.
     *
     * @author Sven Ahrens
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.UpdateInventoryRequest
     * @since 2021-01-25
     */
    void updateInventory(String lobbyName, User user);
}
