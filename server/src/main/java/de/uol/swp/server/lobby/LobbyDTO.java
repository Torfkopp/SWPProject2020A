package de.uol.swp.server.lobby;

import de.uol.swp.common.Colour;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.specialisedUtil.ActorColourMap;
import de.uol.swp.common.specialisedUtil.ActorSet;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.user.Computer;
import de.uol.swp.common.user.User;
import de.uol.swp.common.util.Util;

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
 * @see de.uol.swp.server.lobby.ILobby
 * @since 2019-10-08
 */
public class LobbyDTO implements ILobby {

    private final LobbyName name;
    private final ActorSet users = new ActorSet();
    private final ActorSet readyUsers = new ActorSet();
    private final String password;
    private final ActorColourMap userColours = new ActorColourMap();
    private boolean inGame;
    private boolean hasPassword;
    private User owner;
    private int maxPlayers;
    private int moveTime;
    private boolean startUpPhaseEnabled;
    private boolean randomPlayFieldEnabled;
    private int maxTradeDiff;

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
        this.users.add(creator);
        this.inGame = false;
        this.maxPlayers = 3;
        this.moveTime = 120;
        this.startUpPhaseEnabled = false;
        this.randomPlayFieldEnabled = false;
        userColours.put(creator);
        this.maxTradeDiff = 2;
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
        this(name, creator, null);
    }

    /**
     * Private constructor for copying the class
     *
     * @param name                   The name
     * @param password               The password
     * @param inGame                 The in game
     * @param hasPassword            The has password
     * @param owner                  The owner
     * @param maxPlayers             The max players
     * @param moveTime               The move time
     * @param startUpPhaseEnabled    The start up phase enabled
     * @param randomPlayFieldEnabled The random playfield enabled
     * @param maxTradeDiff           The maximum Resource difference
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    private LobbyDTO(LobbyName name, String password, boolean inGame, boolean hasPassword, User owner, int maxPlayers,
                     int moveTime, boolean startUpPhaseEnabled, boolean randomPlayFieldEnabled, int maxTradeDiff) {
        this.name = name;
        this.password = password;
        this.inGame = inGame;
        this.hasPassword = hasPassword;
        this.owner = owner;
        this.users.add(owner);
        this.maxPlayers = maxPlayers;
        this.moveTime = moveTime;
        this.startUpPhaseEnabled = startUpPhaseEnabled;
        this.randomPlayFieldEnabled = randomPlayFieldEnabled;
        this.maxTradeDiff = maxTradeDiff;
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
    public static ILobby create(ILobby lobby) {
        return new LobbyDTO(lobby.getName(), lobby.getPassword(), lobby.isInGame(), lobby.hasPassword(),
                            lobby.getOwner(), lobby.getMaxPlayers(), lobby.getMoveTime(), lobby.isStartUpPhaseEnabled(),
                            lobby.isRandomPlayFieldEnabled(), lobby.getMaxTradeDiff());
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
    public int getMaxTradeDiff() {
        return maxTradeDiff;
    }

    @Override
    public void setMaxTradeDiff(int newTradeDiff) {
        this.maxTradeDiff = newTradeDiff;
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
    public ActorSet getReadyUsers() {
        return readyUsers;
    }

    @Override
    public Set<User> getRealUsers() {
        Set<User> userSet = new TreeSet<>();
        for (Actor actor : users) {
            if (actor instanceof User) {
                userSet.add((User) actor);
            }
        }
        return Collections.unmodifiableSet(userSet);
    }

    @Override
    public ActorColourMap getUserColourMap() {
        return userColours;
    }

    @Override
    public ActorSet getActors() { return users; }

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
    public boolean isRandomPlayFieldEnabled() {
        return randomPlayFieldEnabled;
    }

    @Override
    public void setRandomPlayFieldEnabled(boolean randomPlayFieldEnabled) {
        this.randomPlayFieldEnabled = randomPlayFieldEnabled;
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
    public void joinUser(Actor user) {
        this.users.add(user);
        //Give a new user a random colour (except Gold)
        Colour colour = Util.randomColour();
        while (userColours.containsValue(colour)) colour = Util.randomColour();
        userColours.put(user, colour);
        if (user instanceof Computer) {
            if (user.getUsername().equals("Temmo")) userColours.put(user, Colour.TEMMO);
            readyUsers.add(user);
        }
    }

    @Override
    public void leaveUser(Actor user) {
        if (users.size() == 1) {
            throw new IllegalArgumentException("Lobby must contain at least one user!");
        }
        if (users.contains(user)) {
            this.users.remove(user);
            userColours.remove(user);
            unsetUserReady(user);
            if (this.owner.equals(user)) {
                boolean foundUser = false;
                for (Actor nextOwner : users) {
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
    public void setUserColour(Actor user, Colour colour) {
        userColours.put(user, colour);
    }

    @Override
    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    @Override
    public void setUserReady(Actor user) {
        this.readyUsers.add(user);
    }

    @Override
    public void unsetUserReady(Actor user) {
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
