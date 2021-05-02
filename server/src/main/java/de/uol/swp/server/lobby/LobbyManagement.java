package de.uol.swp.server.lobby;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.User;

import java.util.*;

/**
 * Manages creation, deletion, and storing of lobbies
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.Lobby
 * @see de.uol.swp.common.lobby.LobbyDTO
 * @since 2019-10-08
 */
public class LobbyManagement implements ILobbyManagement {

    private final Map<LobbyName, Lobby> lobbies = new HashMap<>();

    @Override
    public void createLobby(LobbyName name, User owner, int maxPlayer) throws IllegalArgumentException {
        if (lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name [" + name + "] already exists!");
        }
        lobbies.put(name, new LobbyDTO(name, owner));
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