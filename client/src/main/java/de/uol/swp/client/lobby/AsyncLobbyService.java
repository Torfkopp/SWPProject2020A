package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.util.ThreadManager;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * An asynchronous wrapper for the ILobbyService implementation
 * <p>
 * This class handles putting calls to an injected ChatService into
 * their own Task-Thread which is then executed away from the JavaFX
 * Application Thread, isolating non-UI calls onto their own threads.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.lobby.ILobbyService
 * @since 2021-05-23
 */
public class AsyncLobbyService implements ILobbyService {

    private final LobbyService syncLobbyService;

    @Inject
    public AsyncLobbyService(LobbyService syncLobbyService) {
        this.syncLobbyService = syncLobbyService;
    }

    @Override
    public void changeOwner(LobbyName lobbyName, UserOrDummy newOwner) {
        ThreadManager.runNow(() -> syncLobbyService.changeOwner(lobbyName, newOwner));
    }

    @Override
    public void checkForGame(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncLobbyService.checkForGame(lobbyName));
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
    public void kickUser(LobbyName lobbyName, UserOrDummy userToKick) {
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
    public void showLobbyError(String message) {
        ThreadManager.runNow(() -> syncLobbyService.showLobbyError(message));
    }

    @Override
    public void updateLobbySettings(LobbyName lobbyName, int maxPlayers, boolean startUpPhaseEnabled,
                                    boolean commandsAllowed, int moveTime, boolean randomPlayFieldEnabled) {
        ThreadManager.runNow(() -> syncLobbyService
                .updateLobbySettings(lobbyName, maxPlayers, startUpPhaseEnabled, commandsAllowed, moveTime,
                                     randomPlayFieldEnabled));
    }

    @Override
    public void userReady(LobbyName lobbyName, boolean isReady) {
        ThreadManager.runNow(() -> syncLobbyService.userReady(lobbyName, isReady));
    }
}
