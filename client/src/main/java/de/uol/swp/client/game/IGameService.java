package de.uol.swp.client.game;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.user.UserOrDummy;

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
    void buildRequest(LobbyName lobbyName, MapPoint mapPoint);

    /**
     * Posts a request to change the autoRoll-Status of a player
     *
     * @param lobbyName The name of the lobby where the auto roll status is changed
     *
     * @author Maximillian Lindner
     * @since 2021-04-26
     */
    void changeAutoRollState(LobbyName lobbyName, boolean autoRollEnabled);

    /**
     * Posts a request to end the turn onto the Event
     *
     * @param lobbyName The name of the lobby in which to end the turn
     *
     * @see de.uol.swp.common.game.request.EndTurnRequest
     * @since 2021-01-15
     */
    void endTurn(LobbyName lobbyName);

    /**
     * Posts a request to play a KnightCard
     *
     * @param lobbyName The name of the lobby
     */
    void playKnightCard(LobbyName lobbyName);

    /**
     * Posts a request to play a MonopolyCard
     *
     * @param lobbyName The name of the lobby
     * @param resource  The resource the user wants
     */
    void playMonopolyCard(LobbyName lobbyName, ResourceType resource);

    /**
     * Posts a request to play a RoadBuildingCard
     *
     * @param lobbyName The name of the lobby
     */
    void playRoadBuildingCard(LobbyName lobbyName);

    /**
     * Posts a request to play a YearOfPlentyCard
     *
     * @param lobbyName The name of the lobby
     * @param resource1 The resource the user wants
     * @param resource2 The resource the user wants
     */
    void playYearOfPlentyCard(LobbyName lobbyName, ResourceType resource1, ResourceType resource2);

    /**
     * Posts a RobberChosenVictimRequest
     *
     * @param lobbyName The name of the lobby
     * @param victim    The user to lose the resource card
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-06
     */
    void robberChooseVictim(LobbyName lobbyName, UserOrDummy victim);

    /**
     * Posts a RobberNewPositionMessage
     *
     * @param lobby    The name of the lobby
     * @param mapPoint The robber's new position
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-06
     */
    void robberNewPosition(LobbyName lobby, MapPoint mapPoint);

    /**
     * Posts a request to roll the dices
     *
     * @param lobbyName The Lobby in which to roll the dice
     *
     * @see de.uol.swp.common.game.request.RollDiceRequest
     * @since 2021-02-22
     */
    void rollDice(LobbyName lobbyName);

    /**
     * Posts a request to start the game session
     *
     * @param lobbyName The name of the lobby where the session should be started.
     * @param moveTime  The moveTime for the game
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @since 2021-03-23
     */
    void startSession(LobbyName lobbyName, int moveTime);

    /**
     * Posts a RobberTaxChosenRequest and a CloseRobberTaxView
     *
     * @param lobbyName         The lobby's name
     * @param selectedResources The user's selected resources
     *
     * @author Mario Fokken
     * @since 2021-04-12
     */
    void taxPayed(LobbyName lobbyName, ResourceList selectedResources);

    /**
     * Posts a request to update the game map
     *
     * @param lobbyName The name of the lobby
     *
     * @author Aldin Dervisi
     * @author Temmo Junkhoff
     * @since 2021-04-08
     */
    void updateGameMap(LobbyName lobbyName);

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
    void updateInventory(LobbyName lobbyName);
}
