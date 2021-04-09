package de.uol.swp.client.lobby;

import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.UserOrDummy;

import java.util.Map;

/**
 * An interface for all methods of the clientLobbyService
 *
 * @author Steven Luong
 * @see de.uol.swp.client.lobby.LobbyService
 * @since 2021-02-12
 */
public interface ILobbyService {

    /**
     * Posts a request to buy a Development Card onto the EventBus
     *
     * @param lobbyName The name of the lobby
     *
     * @author Phillip-André Suhr
     * @author Steven Luong
     * @since 2021-04-04
     */
    void buyDevelopmentCard(String lobbyName);

    /**
     * Posts a request to create a lobby onto the EventBus
     *
     * @param name      The name chosen for the new lobby
     * @param maxPlayer The maximum amount of players for the new lobby
     *
     * @see de.uol.swp.common.lobby.request.CreateLobbyRequest
     * @since 2019-11-20
     */
    void createNewLobby(String name, int maxPlayer);

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
     * Posts a request to join a specified lobby onto the EventBus
     *
     * @param name The name of the lobby the user wants to join
     *
     * @see de.uol.swp.common.lobby.request.LobbyJoinUserRequest
     * @since 2019-11-20
     */
    void joinLobby(String name);

    /**
     * Posts a request to kick a user
     *
     * @param lobbyName  The name of the lobby the user should be kicked out.
     * @param userToKick The user who should be kicked.
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @since 2021-03-23
     */
    void kickUser(String lobbyName, UserOrDummy userToKick);

    /**
     * Posts a request to leave a specified lobby onto the EventBus
     *
     * @param lobbyName The name of the lobby the User wants to leave
     *
     * @see de.uol.swp.common.lobby.request.LobbyLeaveUserRequest
     * @since 2020-12-05
     */
    void leaveLobby(String lobbyName);

    /**
     * Posts a request to offer a trade to a user onto the EventBus
     *
     * @param lobbyName           The name of the Lobby
     * @param respondingUser      The user to whom the offer is made
     * @param offeredResourceMap  Map of String and Integer of the resources offered
     * @param demandedResourceMap Map of String and Integer of the resources demanded
     *
     * @author Phillip-André Suhr
     * @author Steven Luong
     * @since 2021-04-04
     */
    void offerTrade(String lobbyName, UserOrDummy respondingUser, Map<String, Integer> offeredResourceMap,
                    Map<String, Integer> demandedResourceMap);

    /**
     * Posts a message to play a KnightCard
     *
     * @param lobbyName The name of the lobby
     */
    void playKnightCard(String lobbyName);

    /**
     * Posts a message to play a MonopolyCard
     *
     * @param lobbyName The name of the lobby
     * @param resource  The resource the user wants
     */
    void playMonopolyCard(String lobbyName, Resources resource);

    /**
     * Posts a message to play a RoadBuildingCard
     *
     * @param lobbyName The name of the lobby
     */
    void playRoadBuildingCard(String lobbyName);

    /**
     * Posts a message to play a YearOfPlentyCard
     *
     * @param lobbyName The name of the lobby
     * @param resource1 The resource the user wants
     * @param resource2 The resource the user wants
     */
    void playYearOfPlentyCard(String lobbyName, Resources resource1, Resources resource2);

    /**
     * Posts a new instance of the LobbyUpdateEvent onto the Eventbus
     * <p>
     * This ensures that a new LobbyPresenter will know what lobby it is
     * presenting and who the currently logged in User is.
     *
     * @param lobby The Lobby to present
     *
     * @since 2020-12-30
     */
    void refreshLobbyPresenterFields(Lobby lobby);

    /**
     * Posts a request to remove the user from all lobbies
     *
     * @see de.uol.swp.common.lobby.request.RemoveFromLobbiesRequest
     * @since 2021-01-28
     */
    void removeFromAllLobbies();

    /**
     * Posts a request to retrieve all lobby names
     *
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
     * @since 2020-12-12
     */
    void retrieveAllLobbies();

    /**
     * Posts a request to retrieve all members of a lobby
     *
     * @param lobbyName The name of the lobby whose member list to request
     *
     * @author Alwin Bossert
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbyMembersRequest
     * @since 2020-12-20
     */
    void retrieveAllLobbyMembers(String lobbyName);

    /**
     * Posts a request to return to the Pre-Game Lobby.
     *
     * @param lobbyName The name of the lobby.
     *
     * @author Steven Luong
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.ReturnToPreGameLobbyRequest
     * @since 2021-03-22
     */
    void returnToPreGameLobby(String lobbyName);

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
     * Posts a request to trade with the Bank onto the EventBus
     *
     * @param lobbyName The name of the lobby
     *
     * @author Phillip-André Suhr
     * @author Steven Luong
     * @since 2021-04-04
     */
    void tradeWithBank(String lobbyName);

    /**
     * Posts a request to trade with a user onto the EventBus
     *
     * @param lobbyName The name of the lobby
     * @param user      The user to offer a trade to
     *
     * @author Phillip-André Suhr
     * @author Steven Luong
     * @since 2021-04-04
     */
    void tradeWithUser(String lobbyName, UserOrDummy user);

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

    /**
     * Posts a request to update one's inventory after trading with the Bank
     * onto the EventBus
     *
     * @param lobbyName    The name of the lobby
     * @param getResource  The resource the user offered to the Bank
     * @param giveResource The resource the user got from the Bank
     *
     * @author Phillip-André Suhr
     * @author Steven Luong
     * @since 2021-04-04
     */
    void updateInventoryAfterTradeWithBank(String lobbyName, String getResource, String giveResource);

    /**
     * This method is used to update the pre-game settings of a specific lobby.
     *
     * @param lobbyName              The name of the lobby
     * @param maxPlayers             The maximum amount of players for a lobby
     * @param startUpPhaseEnabled    Whether the startUpPhase is allowed or not
     * @param commandsAllowed        Whether commands are allowed or not
     * @param moveTime               The maximum time of a move
     * @param randomPlayFieldEnabled Whether the randomPlayField is enabled or not
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    void updateLobbySettings(String lobbyName, int maxPlayers, boolean startUpPhaseEnabled, boolean commandsAllowed,
                             int moveTime, boolean randomPlayFieldEnabled);

    /**
     * Posts a request to change the ready status of a user
     *
     * @param lobbyName The name of the lobby the user wants to change his ready status in.
     * @param isReady   The ready status the user wants to change to.
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @since 2021-03-23
     */
    void userReady(String lobbyName, boolean isReady);
}
