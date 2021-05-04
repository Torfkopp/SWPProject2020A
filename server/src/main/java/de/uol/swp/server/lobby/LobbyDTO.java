package de.uol.swp.server.lobby;

import de.uol.swp.common.LobbyName;
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
 * its owner, who joined the lobby, and the settings that will be used in this
 * lobby's game session.
 *
 * @author Marco Grawunder
 * @see Lobby
 * @since 2019-10-08
 */
public class LobbyDTO implements Lobby {

    private final LobbyName name;
    private final Set<UserOrDummy> users = new TreeSet<>();
    private final Set<UserOrDummy> readyUsers = new TreeSet<>();
    private final String password;
    private boolean inGame;
    private boolean hasPassword;
    private User owner;
    private boolean commandsAllowed;
    private int maxPlayers;
    private int moveTime;
    private boolean startUpPhaseEnabled;
    private boolean randomPlayfieldEnabled;

    /**
     * Constructor
     *
     * @param name     The requested name the lobby
     * @param creator  The user who created the lobby and therefore its owner
     * @param password The requested password of the lobby
     *
     * @since 2019-10-08
     */
    public LobbyDTO(LobbyName name, User creator, String password) {
        this.name = name;
        this.owner = creator;
        this.password = password;
        this.hasPassword = password != null;
        this.users.add(creator);
        this.inGame = false;
        this.maxPlayers = 3;
        this.commandsAllowed = true;
        this.moveTime = 60;
        this.startUpPhaseEnabled = false;
        this.randomPlayfieldEnabled = false;
    }

    /**
     * Constructor to create a lobby without a password
     *
     * @param name    The requested name the lobby
     * @param creator The user who created the lobby and therefore its owner
     *
     * @since 2019-10-08
     */
    public LobbyDTO(LobbyName name, User creator) {
        this.name = name;
        this.owner = creator;
        this.password = null;
        this.hasPassword = false;
        this.users.add(creator);
        this.inGame = false;
        this.maxPlayers = 3;
        this.commandsAllowed = true;
        this.moveTime = 60;
        this.startUpPhaseEnabled = false;
        this.randomPlayfieldEnabled = false;
    }

    /**
     * Private constructor for copying the class
     *
     * @param name                   The name
     * @param password               The password
     * @param inGame                 The in game
     * @param hasPassword            The has password
     * @param owner                  The owner
     * @param commandsAllowed        The commands allowed
     * @param maxPlayers             The max players
     * @param moveTime               The move time
     * @param startUpPhaseEnabled    The start up phase enabled
     * @param randomPlayfieldEnabled The random playfield enabled
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    private LobbyDTO(LobbyName name, String password, boolean inGame, boolean hasPassword, User owner,
                     boolean commandsAllowed, int maxPlayers, int moveTime, boolean startUpPhaseEnabled,
                     boolean randomPlayfieldEnabled) {
        this.name = name;
        this.password = password;
        this.inGame = inGame;
        this.hasPassword = password != null;
        this.owner = owner;
        this.commandsAllowed = commandsAllowed;
        this.maxPlayers = maxPlayers;
        this.moveTime = moveTime;
        this.startUpPhaseEnabled = startUpPhaseEnabled;
        this.randomPlayfieldEnabled = randomPlayfieldEnabled;
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
        return new LobbyDTO(lobby.getName(), lobby.getPassword(), lobby.isInGame(), lobby.hasPassword(),
                            lobby.getOwner(), lobby.commandsAllowed(), lobby.getMaxPlayers(), lobby.getMoveTime(),
                            lobby.startUpPhaseEnabled(), lobby.randomPlayfieldEnabled());
    }

    @Override
    public boolean commandsAllowed() {
        return commandsAllowed;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public int getMoveTime() {
        return moveTime;
    }

    @Override
    public void setMoveTime(int moveTime) {
        this.moveTime = moveTime;
    }

    @Override
    public LobbyName getName() {
        return name;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Set<UserOrDummy> getReadyUsers() {
        return readyUsers;
    }

    @Override
    public Set<User> getRealUsers() {
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
    public boolean hasPassword() {
        return hasPassword;
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
    public boolean isStartUpPhaseEnabled() {
        return startUpPhaseEnabled;
    }

    @Override
    public void setStartUpPhaseEnabled(boolean startUpPhaseEnabled) {
        this.startUpPhaseEnabled = startUpPhaseEnabled;
    }

    @Override
    public void joinUser(UserOrDummy user) {
        this.users.add(user);
        if (user instanceof Dummy) {
            readyUsers.add(user);
        }
    }

    @Override
    public void leaveUser(UserOrDummy user) {
        if (users.size() == 1) {
            throw new IllegalArgumentException("Lobby must contain at least one user!");
        }
        if (users.contains(user)) {
            this.users.remove(user);
            unsetUserReady(user);
            if (this.owner.equals(user)) {
                boolean foundUser = false;
                for (UserOrDummy nextOwner : users) {
                    if (nextOwner instanceof User) {
                        foundUser = true;
                        updateOwner((User) nextOwner);
                        break;
                    }
                }
                if (!foundUser) throw new IllegalArgumentException("Lobby must contain at least one real user!");
            }
        }
    }

    @Override
    public boolean randomPlayfieldEnabled() {
        return randomPlayfieldEnabled;
    }

    @Override
    public void setCommandsAllowed(boolean commandsAllowed) {
        this.commandsAllowed = commandsAllowed;
    }

    @Override
    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    @Override
    public void setRandomPlayfieldEnabled(boolean randomPlayfieldEnabled) {
        this.randomPlayfieldEnabled = randomPlayfieldEnabled;
    }

    @Override
    public void setUserReady(UserOrDummy user) {
        this.readyUsers.add(user);
    }

    @Override
    public boolean startUpPhaseEnabled() {
        return startUpPhaseEnabled;
    }

    @Override
    public void unsetUserReady(UserOrDummy user) {
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
