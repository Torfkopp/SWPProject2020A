package de.uol.swp.common.lobby.dto;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Object to transfer the information of a game lobby
 * <p>
 * This object is used to communicate the current state of game lobbies between
 * the server and clients. It contains information about the lobby's name,
 * its owner, and who joined the lobby.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.Lobby
 * @since 2019-10-08
 */
public class LobbyDTO implements Lobby {

    private final String name;
    private final Set<User> users = new TreeSet<>();
    private final Set<User> readyUsers = new TreeSet<>();
    private User owner;

    /**
     * Constructor
     *
     * @param name    The requested name the lobby
     * @param creator The user who created the lobby and therefore its owner
     *
     * @since 2019-10-08
     */
    public LobbyDTO(String name, User creator) {
        this.name = name;
        this.owner = creator;
        this.users.add(creator);
    }

    /**
     * Copy constructor
     *
     * @param lobby Lobby object to copy the values of
     *
     * @return Lobby copy of Lobby object
     *
     * @since 2020-11-29
     */
    public static Lobby create(Lobby lobby) {
        return new LobbyDTO(lobby.getName(), lobby.getOwner());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public Set<User> getReadyUsers() {
        return readyUsers;
    }

    @Override
    public Set<User> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    @Override
    public void joinUser(User user) {
        this.users.add(user);
    }

    @Override
    public void leaveUser(User user) {
        if (users.size() == 1) {
            throw new IllegalArgumentException("Lobby must contain at least one user!");
        }
        if (users.contains(user)) {
            this.users.remove(user);
            if (this.owner.equals(user)) {
                updateOwner(users.iterator().next());
            }
        }
    }

    @Override
    public void setUserReady(User user) {
        this.readyUsers.add(user);
    }

    @Override
    public void unsetUserReady(User user) {
        this.readyUsers.remove(user);
    }

    @Override
    public void updateOwner(User user) {
        if (!this.users.contains(user)) {
            throw new IllegalArgumentException(
                    "User " + user.getUsername() + " not found. Owner must be member of lobby!");
        }
        this.owner = user;
    }
}
