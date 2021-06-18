package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.specialisedUtil.SimpleLobbyMap;
import de.uol.swp.common.user.User;

import java.util.HashMap;
import java.util.Optional;

/**
 * Specialised class to map a
 * LobbyName to its ILobby object
 *
 * @author Mario Fokken
 * @since 2021-06-18
 */
class LobbyMap extends HashMap<LobbyName, ILobby> {

    /**
     * Creates a new lobby and adds it to the list
     *
     * @param name          The name of the lobby to create
     * @param owner         The user wanting to create a lobby
     * @param lobbyPassword The lobby's password
     *
     * @throws java.lang.IllegalArgumentException Name already taken
     * @implNote The primary key of the lobbies is the name, therefore the name has
     * to be unique
     * @see de.uol.swp.common.user.User
     */
    void create(LobbyName name, User owner, String lobbyPassword) throws IllegalArgumentException {
        if (containsKey(name)) {
            throw new IllegalArgumentException("Lobby name [" + name + "] already exists!");
        }
        put(name, new LobbyDTO(name, owner, lobbyPassword));
    }

    /**
     * Deletes a lobby with a requested name
     *
     * @param name String containing the name of the lobby to delete
     *
     * @throws java.lang.IllegalArgumentException There is no lobby with the requested name
     */
    void drop(LobbyName name) throws IllegalArgumentException {
        if (!containsKey(name)) {
            throw new IllegalArgumentException("Lobby name [" + name + "] not found!");
        }
        remove(name);
    }

    /**
     * Searches for the lobby with the requested name
     *
     * @param lobbyName String containing the name of the lobby to search for
     *
     * @return Either an empty Optional or an Optional containing the lobby
     *
     * @see java.util.Optional
     */
    Optional<ILobby> getLobby(LobbyName lobbyName) {
        ILobby lobby = get(lobbyName);
        if (lobby != null) {
            return Optional.of(lobby);
        }
        return Optional.empty();
    }

    /**
     * Searches for the lobby with the requested name and password
     *
     * @param lobbyName String containing the name of the lobby to search for
     * @param password  String containing the password of the lobby to search for
     *
     * @return Either an empty Optional or an Optional containing the lobby
     *
     * @author Alwin Bossert
     * @see java.util.Optional
     */
    Optional<ILobby> getLobby(LobbyName lobbyName, String password) { return getLobby(lobbyName); }

    /**
     * Gets the map with simple lobbies
     *
     * @return Map with the lobby's name and its SimpleLobby object
     */
    SimpleLobbyMap getSimpleLobbies() {
        SimpleLobbyMap temp = new SimpleLobbyMap();
        forEach((key, value) -> temp.put(key, ILobby.getSimpleLobby(value)));
        return temp;
    }

    /**
     * Checks if the specified user is
     * in one of the lobbies
     *
     * @param user The user to check presence of
     *
     * @return Whether the user is in one of the lobbies
     */
    boolean isInALobby(User user) {
        return values().stream().anyMatch(l -> l.getUserOrDummies().contains(user));
    }
}
