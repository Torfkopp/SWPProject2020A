package de.uol.swp.common.lobby;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

import java.util.Set;
import java.util.TreeSet;

/**
 * The type Simple lobby.
 *
 * @author Temmo Junkhoff
 * @since 2021-05-03
 */
public class SimpleLobby implements ISimpleLobby {

    private final LobbyName name;
    private final Set<UserOrDummy> users = new TreeSet<>();
    private final Set<UserOrDummy> readyUsers = new TreeSet<>();
    private final boolean inGame;
    private final User owner;
    private final boolean commandsAllowed;
    private final int maxPlayers;
    private final int moveTime;
    private final boolean startUpPhaseEnabled;
    private final boolean randomPlayfieldEnabled;

    /**
     * Instantiates a new Simple lobby.
     *
     * @param name                   the name
     * @param inGame                 the in game
     * @param owner                  the owner
     * @param commandsAllowed        the commands allowed
     * @param maxPlayers             the max players
     * @param moveTime               the move time
     * @param startUpPhaseEnabled    the start up phase enabled
     * @param randomPlayfieldEnabled the random playfield enabled
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    public SimpleLobby(LobbyName name, boolean inGame, User owner, boolean commandsAllowed, int maxPlayers,
                       int moveTime, boolean startUpPhaseEnabled, boolean randomPlayfieldEnabled) {
        this.name = name;
        this.inGame = inGame;
        this.owner = owner;
        this.commandsAllowed = commandsAllowed;
        this.maxPlayers = maxPlayers;
        this.moveTime = moveTime;
        this.startUpPhaseEnabled = startUpPhaseEnabled;
        this.randomPlayfieldEnabled = randomPlayfieldEnabled;
    }

    @Override
    public boolean areCommandsAllowed() {
        return commandsAllowed;
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
    public Set<UserOrDummy> getReadyUsers() {
        return readyUsers;
    }

    @Override
    public Set<UserOrDummy> getUserOrDummies() {
        return users;
    }

    @Override
    public boolean isInGame() {
        return inGame;
    }

    @Override
    public boolean isRandomPlayfieldEnabled() {
        return randomPlayfieldEnabled;
    }

    @Override
    public boolean isStartUpPhaseEnabled() {
        return startUpPhaseEnabled;
    }
}
