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
public class LobbyManagement {

    private final Map<String, Lobby> lobbies = new HashMap<>();

    /**
     * Creates a new lobby and adds it to the list
     *
     * @param name  The name of the lobby to create
     * @param owner The user who wants to create a lobby
     * @throws java.lang.IllegalArgumentException Name already taken
     * @implNote The primary key of the lobbies is the name, therefore the name has
     * to be unique
     * @see de.uol.swp.common.user.User
     * @since 2019-10-08
     */
    public void createLobby(String name, User owner) throws IllegalArgumentException {
        if (lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name " + name + " already exists!");
        }
        lobbies.put(name, new LobbyDTO(name, owner));
    }

    /**
     * Deletes a lobby with a requested name
     *
     * @param name String containing the name of the lobby to delete
     * @throws java.lang.IllegalArgumentException There is no lobby with the requested name
     * @since 2019-10-08
     */
    public void dropLobby(String name) {
        if (!lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name " + name + " not found!");
        }
        lobbies.remove(name);
    }

    /**
     * Searches for the lobby with the requested name
     *
     * @param name String containing the name of the lobby to search for
     * @return Either an empty Optional or an Optional containing the lobby
     * @see java.util.Optional
     * @since 2019-10-08
     */
    public Optional<Lobby> getLobby(String name) {
        Lobby lobby = lobbies.get(name);
        if (lobby != null) {
            return Optional.of(lobby);
        }
        return Optional.empty();
    }

    /**
     * Gets the map
     *
     * @return Map with the lobby's name and its Lobby object
     * @since 2020-12-12
     */
    public Map<String, Lobby> getLobbies() {
        return lobbies;
    }
}