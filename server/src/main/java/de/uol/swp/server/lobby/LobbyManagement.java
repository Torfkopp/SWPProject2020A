package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.specialisedUtil.SimpleLobbyMap;
import de.uol.swp.common.user.User;

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

    private final LobbyMap lobbies = new LobbyMap();

    @Override
    public void createLobby(LobbyName name, User owner, String lobbyPassword) throws IllegalArgumentException {
        lobbies.create(name, owner, lobbyPassword);
    }

    @Override
    public void dropLobby(LobbyName name) throws IllegalArgumentException { lobbies.drop(name); }

    @Override
    public LobbyMap getLobbies() {
        return lobbies;
    }

    @Override
    public Optional<ILobby> getLobby(LobbyName lobbyName) { return lobbies.getLobby(lobbyName); }

    @Override
    public Optional<ILobby> getLobby(LobbyName name, String password) { return lobbies.getLobby(name, password); }

    @Override
    public SimpleLobbyMap getSimpleLobbies() { return lobbies.getSimpleLobbies(); }

    @Override
    public void setInGame(LobbyName lobbyName, boolean inGame) {
        Optional<ILobby> found = getLobby(lobbyName);
        if (found.isEmpty()) return;
        found.get().setInGame(inGame);
    }

    @Override
    public void updateLobbySettings(LobbyName lobbyName, int maxPlayers, int moveTime, boolean startUpPhaseEnabled,
                                    boolean randomPlayfieldEnabled, int maxTradeDiff) {
        ILobby lobby = lobbies.get(lobbyName);
        lobby.setMaxPlayers(maxPlayers);
        lobby.setMoveTime(moveTime);
        lobby.setStartUpPhaseEnabled(startUpPhaseEnabled);
        lobby.setRandomPlayFieldEnabled(randomPlayfieldEnabled);
        lobby.setMaxTradeDiff(maxTradeDiff);
    }
}