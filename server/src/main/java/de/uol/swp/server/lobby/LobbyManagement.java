package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages creation, deletion, and storing of lobbies
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.Lobby
 * @see de.uol.swp.common.lobby.dto.LobbyDTO
 * @since 2019-10-08
 */
public class LobbyManagement implements ILobbyManagement {

    private final Map<String, Lobby> lobbies = new HashMap<>();

    @Override
    public void createLobby(String name, User owner) throws IllegalArgumentException {
        if (lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name " + name + " already exists!");
        }
        lobbies.put(name, new LobbyDTO(name, owner, false));
    }

    @Override
    public void dropLobby(String name) throws IllegalArgumentException {
        if (!lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name " + name + " not found!");
        }
        lobbies.remove(name);
    }

    @Override
    public Map<String, Lobby> getLobbies() {
        return lobbies;
    }

    @Override
    public Optional<Lobby> getLobby(String name) {
        Lobby lobby = lobbies.get(name);
        if (lobby != null) {
            return Optional.of(lobby);
        }
        return Optional.empty();
    }

    @Override
    public void setInGame(String lobbyName, boolean inGame) {
        Optional<Lobby> found = getLobby(lobbyName);
        if (found.isEmpty()) return;
        found.get().setInGame(inGame);
    }
}