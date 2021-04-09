package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.UserOrDummy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Class that manages lobbies
 *
 * @author Marco Grawunder
 * @since 2019-11-20
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyService implements ILobbyService {

    private final EventBus eventBus;
    private final IUserService userService;
    private final Logger LOG = LogManager.getLogger(LobbyService.class);

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2019-11-20
     */
    @Inject
    public LobbyService(EventBus eventBus, IUserService userService) {
        LOG.debug("LobbyService started");
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.userService = userService;
    }

    @Override
    public void buyDevelopmentCard(String lobbyName) {
        LOG.debug("Sending BuyDevelopmentCardRequest");
        Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(userService.getLoggedInUser(), lobbyName);
        eventBus.post(buyDevelopmentCardRequest);
    }

    @Override
    public void createNewLobby(String name, int maxPlayers) {
        LOG.debug("Sending CreateLobbyRequest");
        Message createLobbyRequest = new CreateLobbyRequest(name, userService.getLoggedInUser(), maxPlayers);
        eventBus.post(createLobbyRequest);
    }

    @Override
    public void endTurn(String lobbyName) {
        LOG.debug("Sending EndTurnRequest");
        Message endTurnRequest = new EndTurnRequest(userService.getLoggedInUser(), lobbyName);
        eventBus.post(endTurnRequest);
    }

    @Override
    public void joinLobby(String name) {
        LOG.debug("Sending LobbyJoinUserRequest");
        Message joinUserRequest = new LobbyJoinUserRequest(name, userService.getLoggedInUser());
        eventBus.post(joinUserRequest);
    }

    @Override
    public void kickUser(String lobbyName, UserOrDummy userToKick) {
        LOG.debug("Sending KickUserRequest");
        Message kickUserRequest = new KickUserRequest(lobbyName, userService.getLoggedInUser(), userToKick);
        eventBus.post(kickUserRequest);
    }

    @Override
    public void leaveLobby(String lobbyName) {
        LOG.debug("Sending LobbyLeaveUserRequest");
        Message lobbyLeaveUserRequest = new LobbyLeaveUserRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(lobbyLeaveUserRequest);
    }

    @Override
    public void offerTrade(String lobbyName, UserOrDummy respondingUser, Map<String, Integer> offeredResourceMap,
                           Map<String, Integer> demandedResourceMap) {
        LOG.debug("Sending an OfferingTradeWithUserRequest");
        Message offeringTradeWithUserRequest = new OfferingTradeWithUserRequest(userService.getLoggedInUser(),
                                                                                respondingUser, lobbyName,
                                                                                offeredResourceMap,
                                                                                demandedResourceMap);
        eventBus.post(offeringTradeWithUserRequest);
    }

    @Override
    public void playKnightCard(String lobbyName) {
        LOG.debug("Sending PlayKnightCardRequest");
        Message msg = new PlayKnightCardRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(msg);
    }

    @Override
    public void playMonopolyCard(String lobbyName, Resources resource) {
        LOG.debug("Sending PlayMonopolyCardRequest");
        Message msg = new PlayMonopolyCardRequest(lobbyName, userService.getLoggedInUser(), resource);
        eventBus.post(msg);
    }

    @Override
    public void playRoadBuildingCard(String lobbyName) {
        LOG.debug("Sending PlayRoadBuildingCardRequest");
        Message msg = new PlayRoadBuildingCardRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(msg);
    }

    @Override
    public void playYearOfPlentyCard(String lobbyName, Resources resource1, Resources resource2) {
        LOG.debug("Sending PlayYearOfPlentyCardRequest");
        Message msg = new PlayYearOfPlentyCardRequest(lobbyName, userService.getLoggedInUser(), resource1, resource2);
        eventBus.post(msg);
    }

    @Override
    public void refreshLobbyPresenterFields(Lobby lobby) {
        LOG.debug("Sending LobbyUpdateEvent");
        eventBus.post(new LobbyUpdateEvent(lobby));
    }

    @Override
    public void removeFromAllLobbies() {
        LOG.debug("Sending RemoveFromLobbiesRequest");
        Message removeFromLobbiesRequest = new RemoveFromLobbiesRequest(userService.getLoggedInUser());
        eventBus.post(removeFromLobbiesRequest);
    }

    @Override
    public void retrieveAllLobbies() {
        LOG.debug("Sending RetrieveAllLobbiesRequest");
        Message retrieveAllLobbiesRequest = new RetrieveAllLobbiesRequest();
        eventBus.post(retrieveAllLobbiesRequest);
    }

    @Override
    public void retrieveAllLobbyMembers(String lobbyName) {
        LOG.debug("Sending RetrieveAllLobbyMembersRequest for Lobby " + lobbyName);
        Message retrieveAllLobbyMembersRequest = new RetrieveAllLobbyMembersRequest(lobbyName);
        eventBus.post(retrieveAllLobbyMembersRequest);
    }

    @Override
    public void returnToPreGameLobby(String lobbyName) {
        LOG.debug("Sending ReturnToPreGameLobbyRequest for Lobby " + lobbyName);
        Message returnToPreGameLobbyRequest = new ReturnToPreGameLobbyRequest(lobbyName);
        eventBus.post(returnToPreGameLobbyRequest);
    }

    @Override
    public void rollDice(String lobbyName) {
        LOG.debug("Sending RollDiceRequest for Lobby " + lobbyName);
        Message rollDiceRequest = new RollDiceRequest(userService.getLoggedInUser(), lobbyName);
        eventBus.post(rollDiceRequest);
    }

    @Override
    public void startSession(String lobbyName) {
        LOG.debug("Sending StartSessionRequest");
        Message startSessionRequest = new StartSessionRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(startSessionRequest);
    }

    @Override
    public void tradeWithBank(String lobbyName) {
        LOG.debug("Sending TradeWithBankRequest for Lobby " + lobbyName);
        eventBus.post(new TradeWithBankRequest(lobbyName, userService.getLoggedInUser()));
    }

    @Override
    public void tradeWithUser(String lobbyName, UserOrDummy user) {
        LOG.debug("Sending TradeWithUserRequest for Lobby " + lobbyName);
        eventBus.post(new TradeWithUserRequest(lobbyName, userService.getLoggedInUser(), user));
    }

    @Override
    public void updateInventory(String lobbyName) {
        LOG.debug("Sending UpdateInventoryRequest");
        Message updateInventoryRequest = new UpdateInventoryRequest(userService.getLoggedInUser(), lobbyName);
        eventBus.post(updateInventoryRequest);
    }

    @Override
    public void updateInventoryAfterTradeWithBank(String lobbyName, String getResource, String giveResource) {
        LOG.debug("Sending UpdateInventoryAfterTradeWithBankRequest");
        Message updateInventoryAfterTradeWithBankRequest = new UpdateInventoryAfterTradeWithBankRequest(
                userService.getLoggedInUser(), lobbyName, getResource, giveResource);
        eventBus.post(updateInventoryAfterTradeWithBankRequest);
    }

    @Override
    public void updateLobbySettings(String lobbyName, int maxPlayers, boolean startUpPhaseEnabled,
                                    boolean commandsAllowed, int moveTime, boolean randomPlayFieldEnabled) {
        LOG.debug("Sending ChangeLobbySettingsRequest");
        eventBus.post(new ChangeLobbySettingsRequest(lobbyName, userService.getLoggedInUser(), maxPlayers,
                                                     startUpPhaseEnabled, commandsAllowed, moveTime,
                                                     randomPlayFieldEnabled));
    }

    @Override
    public void userReady(String lobbyName, boolean isReady) {
        LOG.debug("Sending UserReadyRequest");
        Message userReadyRequest = new UserReadyRequest(lobbyName, userService.getLoggedInUser(), isReady);
        eventBus.post(userReadyRequest);
    }
}
