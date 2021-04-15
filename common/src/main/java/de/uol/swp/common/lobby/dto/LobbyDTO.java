package de.uol.swp.common.lobby.dto;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.map.configuration.IConfiguration;
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
 * its owner, who joined the lobby, and the settings that will be used in this
 * lobby's game session.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.Lobby
 * @since 2019-10-08
 */
public class LobbyDTO implements Lobby {

    private final LobbyName name;
    private final Set<UserOrDummy> users = new TreeSet<>();
    private final Set<UserOrDummy> readyUsers = new TreeSet<>();
    private boolean inGame;
    private UserOrDummy owner;
    private boolean commandsAllowed;
    private int maxPlayers;
    private int moveTime;
    private boolean startUpPhaseEnabled;
    private boolean randomPlayfieldEnabled;
    private IConfiguration configuration;

    /**
     * Constructor
     *
     * @param name    The requested name the lobby
     * @param creator The user who created the lobby and therefore its owner
     * @param inGame  Whether the lobby is currently in a game
     *
     * @since 2019-10-08
     */
    public LobbyDTO(LobbyName name, UserOrDummy creator, boolean inGame, int maxPlayers, boolean commandsAllowed,
                    int moveTime, boolean startUpPhaseEnabled, boolean randomPlayfieldEnabled) {
        this.name = name;
        this.owner = creator;
        this.users.add(creator);
        this.inGame = inGame;
        this.maxPlayers = maxPlayers;
        this.commandsAllowed = commandsAllowed;
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
        return new LobbyDTO(lobby.getName(), lobby.getOwner(), lobby.isInGame(), lobby.getMaxPlayers(),
                            lobby.commandsAllowed(), lobby.getMoveTime(), lobby.startUpPhaseEnabled(),
                            lobby.randomPlayfieldEnabled());
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
    public UserOrDummy getOwner() {
        return owner;
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
    public void setRandomPlayfieldEnabled(boolean randomPlayfieldEnabled) {
        this.randomPlayfieldEnabled = randomPlayfieldEnabled;
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

    @Override
    public void setConfiguration(IConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public IConfiguration getConfiguration() {
        return configuration;
    }
}
