package de.uol.swp.common.lobby;

import de.uol.swp.common.specialisedUtil.UserOrDummySet;
import de.uol.swp.common.user.User;

/**
 * The class Simple lobby.
 * Used to transfer data about a lobby over the network
 *
 * @author Temmo Junkhoff
 * @since 2021-05-03
 */
public class SimpleLobby implements ISimpleLobby {

    private final LobbyName name;
    private final UserOrDummySet users;
    private final UserOrDummySet readyUsers;
    private final boolean inGame;
    private final User owner;
    private final int maxPlayers;
    private final int moveTime;
    private final boolean startUpPhaseEnabled;
    private final boolean randomPlayFieldEnabled;
    private final boolean hasPassword;
    private final int maxTradeDiff;

    /**
     * Constructor.
     *
     * @param name                   The name
     * @param inGame                 The in game
     * @param owner                  The owner
     * @param maxPlayers             The max players
     * @param moveTime               The move time
     * @param startUpPhaseEnabled    Whether the start up phase is enabled or not
     * @param randomPlayFieldEnabled Whether the random play field is enabled or not
     * @param maxTradeDiff           The maximum Ressource Difference a Trade can have
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    public SimpleLobby(LobbyName name, boolean inGame, User owner, int maxPlayers, int moveTime,
                       boolean startUpPhaseEnabled, boolean randomPlayFieldEnabled, boolean hasPassword,
                       UserOrDummySet users, UserOrDummySet readyUsers, int maxTradeDiff) {
        this.name = name;
        this.inGame = inGame;
        this.owner = owner;
        this.maxPlayers = maxPlayers;
        this.moveTime = moveTime;
        this.startUpPhaseEnabled = startUpPhaseEnabled;
        this.randomPlayFieldEnabled = randomPlayFieldEnabled;
        this.hasPassword = hasPassword;
        this.users = users;
        this.readyUsers = readyUsers;
        this.maxTradeDiff = maxTradeDiff;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public int getMoveTime() {
        return moveTime;
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
    public UserOrDummySet getReadyUsers() {
        return readyUsers;
    }

    @Override
    public UserOrDummySet getUserOrDummies() {
        return users;
    }

    @Override
    public int getMaxTradeDiff() {return maxTradeDiff;}

    @Override
    public boolean hasPassword() {
        return hasPassword;
    }

    @Override
    public boolean isInGame() {
        return inGame;
    }

    @Override
    public boolean isRandomPlayFieldEnabled() {
        return randomPlayFieldEnabled;
    }

    @Override
    public boolean isStartUpPhaseEnabled() {
        return startUpPhaseEnabled;
    }
}
