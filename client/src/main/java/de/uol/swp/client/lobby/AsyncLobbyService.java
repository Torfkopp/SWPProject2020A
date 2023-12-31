package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.common.Colour;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.util.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An asynchronous wrapper for the ILobbyService implementation
 * <p>
 * This class handles putting calls to an injected LobbyService into
 * their own Task-Thread which is then executed away from the JavaFX
 * Application Thread, isolating non-UI calls onto their own threads.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.lobby.ILobbyService
 * @since 2021-05-23
 */
public class AsyncLobbyService implements ILobbyService {

    private static final Logger LOG = LogManager.getLogger(AsyncLobbyService.class);
    private final LobbyService syncLobbyService;

    /**
     * Constructor
     *
     * @param syncLobbyService The synchronous LobbyService (injected)
     */
    @Inject
    public AsyncLobbyService(LobbyService syncLobbyService) {
        this.syncLobbyService = syncLobbyService;
        LOG.debug("AsyncLobbyService initialised");
    }

    @Override
    public void addAI(LobbyName name, AI ai) {
        ThreadManager.runNow(() -> syncLobbyService.addAI(name, ai));
    }

    @Override
    public void changeOwner(LobbyName lobbyName, Actor newOwner) {
        ThreadManager.runNow(() -> syncLobbyService.changeOwner(lobbyName, newOwner));
    }

    @Override
    public void checkUserInLobby() {
        ThreadManager.runNow(syncLobbyService::checkUserInLobby);
    }

    @Override
    public void createNewLobby(LobbyName name, String password) {
        ThreadManager.runNow(() -> syncLobbyService.createNewLobby(name, password));
    }

    @Override
    public void joinLobby(LobbyName name) {
        ThreadManager.runNow(() -> syncLobbyService.joinLobby(name));
    }

    @Override
    public void joinRandomLobby() {
        ThreadManager.runNow(syncLobbyService::joinRandomLobby);
    }

    @Override
    public void kickUser(LobbyName lobbyName, Actor userToKick) {
        ThreadManager.runNow(() -> syncLobbyService.kickUser(lobbyName, userToKick));
    }

    @Override
    public void leaveLobby(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncLobbyService.leaveLobby(lobbyName));
    }

    @Override
    public void refreshLobbyPresenterFields(ISimpleLobby lobby) {
        ThreadManager.runNow(() -> syncLobbyService.refreshLobbyPresenterFields(lobby));
    }

    @Override
    public void removeFromAllLobbies() {
        ThreadManager.runNow(syncLobbyService::removeFromAllLobbies);
    }

    @Override
    public void retrieveAllLobbies() {
        ThreadManager.runNow(syncLobbyService::retrieveAllLobbies);
    }

    @Override
    public void retrieveAllLobbyMembers(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncLobbyService.retrieveAllLobbyMembers(lobbyName));
    }

    @Override
    public void returnToPreGameLobby(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncLobbyService.returnToPreGameLobby(lobbyName));
    }

    @Override
    public void setColour(LobbyName lobbyName, Colour colour) {
        ThreadManager.runNow(() -> syncLobbyService.setColour(lobbyName, colour));
    }

    @Override
    public void updateLobbySettings(LobbyName lobbyName, int maxPlayers, boolean startUpPhaseEnabled, int moveTime,
                                    boolean randomPlayFieldEnabled, int maxTradeDiff) {
        ThreadManager.runNow(() -> syncLobbyService
                .updateLobbySettings(lobbyName, maxPlayers, startUpPhaseEnabled, moveTime, randomPlayFieldEnabled,
                                     maxTradeDiff));
    }

    @Override
    public void userReady(LobbyName lobbyName, boolean isReady) {
        ThreadManager.runNow(() -> syncLobbyService.userReady(lobbyName, isReady));
    }

    @Override
    public void replaceUserWithAI(LobbyName lobbyName, Colour oldColour) {
        ThreadManager.runNow(() -> syncLobbyService.replaceUserWithAI(lobbyName, oldColour));
    }
}
