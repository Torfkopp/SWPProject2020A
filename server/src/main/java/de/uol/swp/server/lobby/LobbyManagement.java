package de.uol.swp.server.lobby;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages creation, deletion, and storing of lobbies
 *
 * @author Marco Grawunder
 * @see Lobby
 * @see LobbyDTO
 * @since 2019-10-08
 */
public class LobbyManagement implements ILobbyManagement {

    private final Map<LobbyName, Lobby> lobbies = new HashMap<>();

    @Override
    public void createLobby(LobbyName name, User owner, String lobbyPassword) throws IllegalArgumentException {
        if (lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name [" + name + "] already exists!");
        }
        lobbies.put(name, new LobbyDTO(name, owner, lobbyPassword));
    }

    @Override
    public void dropLobby(LobbyName name) throws IllegalArgumentException {
        if (!lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name [" + name + "] not found!");
        }
        lobbies.remove(name);
    }

    @Override
    public Map<LobbyName, Lobby> getLobbies() {
        return lobbies;
    }

    @Override
    public Optional<Lobby> getLobby(LobbyName lobbyName) {
        Lobby lobby = lobbies.get(lobbyName);
        if (lobby != null) {
            return Optional.of(lobby);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Lobby> getLobby(LobbyName name, String password) {
        Lobby lobby = lobbies.get(name);
        if (lobby != null) {
            return Optional.of(lobby);
        }
        return Optional.empty();
    }

    @Override
    public Map<LobbyName, ISimpleLobby> getSimpleLobbies() {
        Map<LobbyName, ISimpleLobby> temp = new HashMap<>();
        lobbies.forEach((key, value) -> temp.put(key, Lobby.getSimpleLobby(value)));
        return temp;
    }

    @Override
    public void setHasPassword(LobbyName lobbyName, boolean hasPassword) {
        Optional<Lobby> lobby = getLobby(lobbyName);
        if (lobby.isEmpty()) return;
        lobby.get().setHasPassword(hasPassword);
    }

    @Override
    public void setInGame(LobbyName lobbyName, boolean inGame) {
        Optional<Lobby> found = getLobby(lobbyName);
        if (found.isEmpty()) return;
        found.get().setInGame(inGame);
    }

    @Override
    public void updateLobbySettings(LobbyName lobbyName, int maxPlayers, boolean commandsAllowed, int moveTime,
                                    boolean startUpPhaseEnabled, boolean randomPlayfieldEnabled) {
        lobbies.get(lobbyName).setMaxPlayers(maxPlayers);
        lobbies.get(lobbyName).setCommandsAllowed(commandsAllowed);
        lobbies.get(lobbyName).setMoveTime(moveTime);
        lobbies.get(lobbyName).setStartUpPhaseEnabled(startUpPhaseEnabled);
        lobbies.get(lobbyName).setRandomPlayfieldEnabled(randomPlayfieldEnabled);
    }
}