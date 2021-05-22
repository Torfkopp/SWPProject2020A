package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.event.LobbyErrorEvent;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.game.request.CheckForGameRequest;
import de.uol.swp.common.game.request.ReturnToPreGameLobbyRequest;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.user.request.CheckUserInLobbyRequest;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The LobbyService is responsible for posting requests and events regarding
 * the state of a Lobby, like joining or Lobby setting updates.
 *
 * @author Marco Grawunder
 * @since 2019-11-20
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyService implements ILobbyService {

    private static final Logger LOG = LogManager.getLogger(LobbyService.class);

    private final EventBus eventBus;
    private final IUserService userService;

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
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.userService = userService;
        LOG.debug("LobbyService started");
    }

    @Override
    public void changeOwner(LobbyName lobbyName, UserOrDummy newOwner) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending ChangeOwnerRequest");
                Message req = new ChangeOwnerRequest(lobbyName, userService.getLoggedInUser(), newOwner);
                eventBus.post(req);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void checkForGame(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending CheckForGameRequest");
                Message request = new CheckForGameRequest(lobbyName, userService.getLoggedInUser());
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void checkUserInLobby() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending CheckUserInLobbyRequest");
                Message msg = new CheckUserInLobbyRequest(userService.getLoggedInUser());
                eventBus.post(msg);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void createNewLobby(LobbyName name, String password) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending CreateLobbyRequest");
                Message createLobbyRequest = new CreateLobbyRequest(name, userService.getLoggedInUser(), password);
                eventBus.post(createLobbyRequest);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void joinLobby(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending LobbyJoinUserRequest");
                Message joinUserRequest = new JoinLobbyRequest(lobbyName, userService.getLoggedInUser());
                eventBus.post(joinUserRequest);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void joinRandomLobby() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                Message joinRandomLobbyRequest = new JoinRandomLobbyRequest(null, userService.getLoggedInUser());
                eventBus.post(joinRandomLobbyRequest);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void kickUser(LobbyName lobbyName, UserOrDummy userToKick) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending KickUserRequest");
                Message kickUserRequest = new KickUserRequest(lobbyName, userService.getLoggedInUser(), userToKick);
                eventBus.post(kickUserRequest);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void leaveLobby(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending LobbyLeaveUserRequest");
                Message lobbyLeaveUserRequest = new LeaveLobbyRequest(lobbyName, userService.getLoggedInUser());
                eventBus.post(lobbyLeaveUserRequest);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void refreshLobbyPresenterFields(ISimpleLobby lobby) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending LobbyUpdateEvent");
                eventBus.post(new LobbyUpdateEvent(lobby));
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void removeFromAllLobbies() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending RemoveFromLobbiesRequest");
                Message removeFromLobbiesRequest = new RemoveFromLobbiesRequest(userService.getLoggedInUser());
                eventBus.post(removeFromLobbiesRequest);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void retrieveAllLobbies() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending RetrieveAllLobbiesRequest");
                Message retrieveAllLobbiesRequest = new RetrieveAllLobbiesRequest();
                eventBus.post(retrieveAllLobbiesRequest);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void retrieveAllLobbyMembers(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending RetrieveAllLobbyMembersRequest for Lobby {}", lobbyName);
                Message retrieveAllLobbyMembersRequest = new RetrieveAllLobbyMembersRequest(lobbyName);
                eventBus.post(retrieveAllLobbyMembersRequest);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void returnToPreGameLobby(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending ReturnToPreGameLobbyRequest for Lobby {}", lobbyName);
                Message returnToPreGameLobbyRequest = new ReturnToPreGameLobbyRequest(lobbyName);
                eventBus.post(returnToPreGameLobbyRequest);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void showLobbyError(String message) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending LobbyErrorEvent");
                eventBus.post(new LobbyErrorEvent(message));
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void updateLobbySettings(LobbyName lobbyName, int maxPlayers, boolean startUpPhaseEnabled,
                                    boolean commandsAllowed, int moveTime, boolean randomPlayFieldEnabled) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending ChangeLobbySettingsRequest");
                eventBus.post(new ChangeLobbySettingsRequest(lobbyName, userService.getLoggedInUser(), maxPlayers,
                                                             startUpPhaseEnabled, commandsAllowed, moveTime,
                                                             randomPlayFieldEnabled));
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void userReady(LobbyName lobbyName, boolean isReady) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending UserReadyRequest");
                Message userReadyRequest = new UserReadyRequest(lobbyName, userService.getLoggedInUser(), isReady);
                eventBus.post(userReadyRequest);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
}
