package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages creation, deletion, and storing of lobbies
 *
 * @author Marco Grawunder
 * @see ILobby
 * @see LobbyDTO
 * @since 2019-10-08
 */
public class LobbyManagement implements ILobbyManagement {

    private final Map<LobbyName, ILobby> lobbies = new HashMap<>();

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
    public Map<LobbyName, ILobby> getLobbies() {
        return lobbies;
    }

    @Override
    public Optional<ILobby> getLobby(LobbyName lobbyName) {
        ILobby lobby = lobbies.get(lobbyName);
        if (lobby != null) {
            return Optional.of(lobby);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ILobby> getLobby(LobbyName name, String password) {
        ILobby lobby = lobbies.get(name);
        if (lobby != null) {
            return Optional.of(lobby);
        }
        return Optional.empty();
    }

    @Override
    public Map<LobbyName, ISimpleLobby> getSimpleLobbies() {
        Map<LobbyName, ISimpleLobby> temp = new HashMap<>();
        lobbies.forEach((key, value) -> temp.put(key, ILobby.getSimpleLobby(value)));
        return temp;
    }

    @Override
    public void setInGame(LobbyName lobbyName, boolean inGame) {
        Optional<ILobby> found = getLobby(lobbyName);
        if (found.isEmpty()) return;
        found.get().setInGame(inGame);
    }

    @Override
    public void updateLobbySettings(LobbyName lobbyName, int maxPlayers, int moveTime, boolean startUpPhaseEnabled,
                                    boolean randomPlayfieldEnabled, int maxTradeDiff) {
        lobbies.get(lobbyName).setMaxPlayers(maxPlayers);
        lobbies.get(lobbyName).setMoveTime(moveTime);
        lobbies.get(lobbyName).setStartUpPhaseEnabled(startUpPhaseEnabled);
        lobbies.get(lobbyName).setRandomPlayFieldEnabled(randomPlayfieldEnabled);
        lobbies.get(lobbyName).setMaxTradeDiff(maxTradeDiff);
        System.err.println(lobbies.get(lobbyName).getMaxTradeDiff());
    }
}