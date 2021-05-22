package de.uol.swp.client.game;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.lobby.event.CloseRobberTaxViewEvent;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.game.robber.RobberChosenVictimRequest;
import de.uol.swp.common.game.robber.RobberNewPositionChosenRequest;
import de.uol.swp.common.game.robber.RobberTaxChosenRequest;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.request.StartSessionRequest;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.UserOrDummy;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The GameService is responsible for posting requests and events regarding
 * the state of a Game, like playing Development Cards or rolling dice.
 *
 * @author Maximilian Lindner
 * @author Phillip-André Suhr
 * @since 2021-04-07
 */
@SuppressWarnings("UnstableApiUsage")
public class GameService implements IGameService {

    private static final Logger LOG = LogManager.getLogger(LobbyService.class);
    private final EventBus eventBus;
    private final IUserService userService;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2021-04-07
     */
    @Inject
    public GameService(EventBus eventBus, IUserService userService) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.userService = userService;
        LOG.debug("GameService started");
    }

    @Override
    public void buildRequest(LobbyName lobbyName, MapPoint mapPoint) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending BuildRequest");
                Message request = new BuildRequest(lobbyName, userService.getLoggedInUser(), mapPoint);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void changeAutoRollState(LobbyName lobbyName, boolean autoRollEnabled) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending ChangeAutoRollStateRequest");
                Message request = new ChangeAutoRollStateRequest(lobbyName, userService.getLoggedInUser(),
                                                                 autoRollEnabled);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void endTurn(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending EndTurnRequest");
                Message request = new EndTurnRequest(userService.getLoggedInUser(), lobbyName);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void playKnightCard(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending PlayKnightCardRequest");
                Message request = new PlayKnightCardRequest(lobbyName, userService.getLoggedInUser());
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void playMonopolyCard(LobbyName lobbyName, ResourceType resource) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending PlayMonopolyCardRequest");
                Message request = new PlayMonopolyCardRequest(lobbyName, userService.getLoggedInUser(), resource);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void playRoadBuildingCard(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending PlayRoadBuildingCardRequest");
                Message request = new PlayRoadBuildingCardRequest(lobbyName, userService.getLoggedInUser());
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void playYearOfPlentyCard(LobbyName lobbyName, ResourceType resource1, ResourceType resource2) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending PlayYearOfPlentyCardRequest");
                Message request = new PlayYearOfPlentyCardRequest(lobbyName, userService.getLoggedInUser(), resource1,
                                                                  resource2);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void robberChooseVictim(LobbyName lobbyName, UserOrDummy victim) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending RobberChosenVictimRequest");
                Message msg = new RobberChosenVictimRequest(lobbyName, userService.getLoggedInUser(), victim);
                eventBus.post(msg);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void robberNewPosition(LobbyName lobbyName, MapPoint mapPoint) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending RobberNewPositionChosenRequest");
                Message msg = new RobberNewPositionChosenRequest(lobbyName, userService.getLoggedInUser(), mapPoint);
                eventBus.post(msg);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void rollDice(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending RollDiceRequest");
                Message request = new RollDiceRequest(userService.getLoggedInUser(), lobbyName);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void startSession(LobbyName lobbyName, int moveTime) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending StartSessionRequest");
                Message request = new StartSessionRequest(lobbyName, userService.getLoggedInUser(), moveTime);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void taxPayed(LobbyName lobbyName, ResourceList selectedResources) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending RobberTaxChosenRequest");
                Message request = new RobberTaxChosenRequest(selectedResources, userService.getLoggedInUser(),
                                                             lobbyName);
                eventBus.post(request);
                LOG.debug("Sending CloseRobberTaxViewEvent");
                eventBus.post(new CloseRobberTaxViewEvent(lobbyName));
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void updateGameMap(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending UpdateGameMapRequest");
                Message request = new UpdateGameMapRequest(lobbyName);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void updateInventory(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending UpdateInventoryRequest");
                Message request = new UpdateInventoryRequest(userService.getLoggedInUser(), lobbyName);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
}
