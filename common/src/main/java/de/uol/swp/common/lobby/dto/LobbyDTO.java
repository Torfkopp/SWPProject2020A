package de.uol.swp.common.lobby.dto;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.Dummy;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

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
    private final Set<UserOrDummy> users = new TreeSet<>();
    private final Set<UserOrDummy> readyUsers = new TreeSet<>();
    private boolean inGame;
    private User owner;

    /**
     * Constructor
     *
     * @param name    The requested name the lobby
     * @param creator The user who created the lobby and therefore its owner
     * @param inGame  Whether the lobby is currently in a game
     *
     * @since 2019-10-08
     */
    public LobbyDTO(String name, User creator, boolean inGame) {
        this.name = name;
        this.owner = creator;
        this.users.add(creator);
        this.inGame = inGame;
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
        return new LobbyDTO(lobby.getName(), lobby.getOwner(), lobby.isInGame());
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
    public Set<UserOrDummy> getReadyUsers() {
        return readyUsers;
    }

    @Override
    public Set<User> getUsers() {
        Set<User> userSet = new TreeSet<>();
        for (UserOrDummy userOrDummy : users) {
            if (userOrDummy instanceof User) {
                userSet.add((User) userOrDummy);
            }
        }
        return Collections.unmodifiableSet(userSet);
    }

    @Override
    public Set<UserOrDummy> getUserOrDummies() {
        return Collections.unmodifiableSet(users);
    }

    @Override
    public boolean isInGame() {
        return inGame;
    }

    @Override
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    @Override
    public void joinUser(UserOrDummy user) {
        this.users.add(user);
        if (user instanceof Dummy){
        readyUsers.add(user);
        }
        System.out.println("User joined Lobby");
        for (UserOrDummy i : users)
            System.out.println(i.getUsername());
    }

    @Override
    public void leaveUser(UserOrDummy user) {
        if (users.size() == 1) {
            throw new IllegalArgumentException("Lobby must contain at least one user!");
        }
        if (users.contains(user)) {
            this.users.remove(user);
            if (this.owner.equals(user)) {
                while (true) {
                    UserOrDummy nextOwner = users.iterator().next();
                    if (nextOwner instanceof User) {
                        updateOwner((User) nextOwner);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void setUserReady(UserOrDummy user) {
        this.readyUsers.add(user);
    }

    @Override
    public void unsetUserReady(UserOrDummy user) {
        this.readyUsers.remove(user);
    }

    @Override
    public void updateOwner(User user) {
        //TODO: Check for dummy
        if (!this.users.contains(user)) {
            throw new IllegalArgumentException(
                    "User " + user.getUsername() + " not found. Owner must be member of lobby!");
        }
        this.owner = user;
    }
}
