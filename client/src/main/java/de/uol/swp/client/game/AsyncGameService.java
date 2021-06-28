package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.util.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An asynchronous wrapper for the IGameService implementation
 * <p>
 * This class handles putting calls to an injected GameService into
 * their own Task-Thread which is then executed away from the JavaFX
 * Application Thread, isolating non-UI calls onto their own threads.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.game.IGameService
 * @since 2021-05-23
 */
public class AsyncGameService implements IGameService {

    private static final Logger LOG = LogManager.getLogger(AsyncGameService.class);
    private final GameService syncGameService;

    /**
     * Constructor
     *
     * @param syncGameService The synchronous GameService (injected)
     */
    @Inject
    public AsyncGameService(GameService syncGameService) {
        this.syncGameService = syncGameService;
        LOG.debug("AsyncGameService initialised");
    }

    @Override
    public void buildRequest(LobbyName lobbyName, MapPoint mapPoint) {
        ThreadManager.runNow(() -> syncGameService.buildRequest(lobbyName, mapPoint));
    }

    @Override
    public void changeAutoRollState(LobbyName lobbyName, boolean autoRollEnabled) {
        ThreadManager.runNow(() -> syncGameService.changeAutoRollState(lobbyName, autoRollEnabled));
    }

    @Override
    public void endTurn(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncGameService.endTurn(lobbyName));
    }

    @Override
    public void pauseGame(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncGameService.pauseGame(lobbyName));
    }

    @Override
    public void playKnightCard(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncGameService.playKnightCard(lobbyName));
    }

    @Override
    public void playMonopolyCard(LobbyName lobbyName, ResourceType resource) {
        ThreadManager.runNow(() -> syncGameService.playMonopolyCard(lobbyName, resource));
    }

    @Override
    public void playRoadBuildingCard(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncGameService.playRoadBuildingCard(lobbyName));
    }

    @Override
    public void playYearOfPlentyCard(LobbyName lobbyName, ResourceType resource1, ResourceType resource2) {
        ThreadManager.runNow(() -> syncGameService.playYearOfPlentyCard(lobbyName, resource1, resource2));
    }

    @Override
    public void robberChooseVictim(LobbyName lobbyName, Actor victim) {
        ThreadManager.runNow(() -> syncGameService.robberChooseVictim(lobbyName, victim));
    }

    @Override
    public void robberNewPosition(LobbyName lobby, MapPoint mapPoint) {
        ThreadManager.runNow(() -> syncGameService.robberNewPosition(lobby, mapPoint));
    }

    @Override
    public void rollDice(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncGameService.rollDice(lobbyName));
    }

    @Override
    public void startSession(LobbyName lobbyName, int moveTime) {
        ThreadManager.runNow(() -> syncGameService.startSession(lobbyName, moveTime));
    }

    @Override
    public void taxPayed(LobbyName lobbyName, ResourceList selectedResources) {
        ThreadManager.runNow(() -> syncGameService.taxPayed(lobbyName, selectedResources));
    }

    @Override
    public void updateGameMap(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncGameService.updateGameMap(lobbyName));
    }

    @Override
    public void updateInventory(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncGameService.updateInventory(lobbyName));
    }
}
