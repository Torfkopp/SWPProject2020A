package de.uol.swp.client.game;

import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.game.map.Resources;

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
     * Posts a request to build something
     *
     * @param lobbyName The name of the lobby
     * @param mapPoint  The map point at which something should be build
     *
     * @author Aldin Dervisi
     * @author Temmo Junkhoff
     * @since 2021-04-08
     */
    void buildRequest(String lobbyName, MapPoint mapPoint);

    /**
     * Posts a request to end the turn onto the Event
     *
     * @param lobbyName The name of the lobby in which to end the turn
     *
     * @see de.uol.swp.common.game.request.EndTurnRequest
     * @since 2021-01-15
     */
    void endTurn(String lobbyName);

    /**
     * Posts a request to play a KnightCard
     *
     * @param lobbyName The name of the lobby
     */
    void playKnightCard(String lobbyName);

    /**
     * Posts a request to play a MonopolyCard
     *
     * @param lobbyName The name of the lobby
     * @param resource  The resource the user wants
     */
    void playMonopolyCard(String lobbyName, Resources resource);

    /**
     * Posts a request to play a RoadBuildingCard
     *
     * @param lobbyName The name of the lobby
     */
    void playRoadBuildingCard(String lobbyName);

    /**
     * Posts a request to play a YearOfPlentyCard
     *
     * @param lobbyName The name of the lobby
     * @param resource1 The resource the user wants
     * @param resource2 The resource the user wants
     */
    void playYearOfPlentyCard(String lobbyName, Resources resource1, Resources resource2);

    /**
     * Posts a request to roll the dices
     *
     * @param lobbyName The Lobby in which to roll the dice
     *
     * @see de.uol.swp.common.game.request.RollDiceRequest
     * @since 2021-02-22
     */
    void rollDice(String lobbyName);

    /**
     * Posts a request to start the game session
     *
     * @param lobbyName The name of the lobby where the session should be started.
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @since 2021-03-23
     */
    void startSession(String lobbyName);

    /**
     * Posts a request to update the game map
     *
     * @param lobbyName The name of the lobby
     *
     * @author Aldin Dervisi
     * @author Temmo Junkhoff
     * @since 2021-04-08
     */
    void updateGameMap(String lobbyName);

    /**
     * Posts a request to update ones Inventory
     *
     * @param lobbyName The name of the lobby the user wants to update his Inventory in
     *
     * @author Sven Ahrens
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.UpdateInventoryRequest
     * @since 2021-01-25
     */
    void updateInventory(String lobbyName);
}
