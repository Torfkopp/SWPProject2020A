package de.uol.swp.common.lobby.response;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.dto.ReadySystemMessageDTO;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.UserOrDummy;

import java.util.*;

/**
 * Response to the AllLobbyMembersRequest
 * <p>
 * This response includes all lobby members as well as the owner of the lobby.
 *
 * @author Alwin Bossert
 * @see de.uol.swp.common.lobby.request.RetrieveAllLobbyMembersRequest
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2020-12-20
 */
public class AllLobbyMembersResponse extends AbstractLobbyResponse {

    private final List<UserOrDummy> users = new ArrayList<>();
    private final Set<UserOrDummy> readyUsers = new TreeSet<>();
    private final UserOrDummy owner;
    private final ReadySystemMessageDTO ownerNotice;

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of the lobby members from the given
     * Collection. The significant difference between the two is that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     * The same is done for the Set of ready users in the lobby.
     * The same method is used to provide the User object for the lobby owner.
     *
     * @param lobbyName  The name of the lobby
     * @param users      Collection of all lobby members
     * @param owner      Owner of the lobby
     * @param readyUsers Set of all ready lobby members
     * @param maxPlayers Maximum number of players that will play in the Lobby
     *
     * @since 2021-01-19
     */
    public AllLobbyMembersResponse(LobbyName lobbyName, Set<UserOrDummy> users, UserOrDummy owner,
                                   Set<UserOrDummy> readyUsers, int maxPlayers) {
        super(lobbyName);
        for (UserOrDummy user : users) {
            if (user instanceof User) this.users.add(UserDTO.createWithoutPassword((User) user));
            else this.users.add(user);
        }
        if (owner instanceof User) this.owner = UserDTO.createWithoutPassword((User) owner);
        else this.owner = owner;
        for (UserOrDummy user : readyUsers) {
            if (user instanceof User) this.readyUsers.add(UserDTO.createWithoutPassword((User) user));
            else this.readyUsers.add(user);
        }
        boolean areEqualSize = this.readyUsers.size() == this.users.size();
        boolean ownerReady = this.readyUsers.contains(this.owner);
        if (ownerReady && areEqualSize && this.users.size() == maxPlayers) {
            this.ownerNotice = new ReadySystemMessageDTO(new I18nWrapper("lobby.ready.everyone"));
        } else if (!ownerReady && this.readyUsers.size() == maxPlayers - 1) {
            this.ownerNotice = new ReadySystemMessageDTO((new I18nWrapper("lobby.ready.everyoneelse")));
        } else this.ownerNotice = null;
    }

    /**
     * Getter for the Owner attribute
     *
     * @return Owner/Creator of the lobby
     *
     * @since 2021-01-05
     */
    public UserOrDummy getOwner() {
        return owner;
    }

    /**
     * Gets the owner notice message
     * <p>
     * The owner notice message is a SystemMessage notifying the owner
     * that either all or all users except the owner are ready and that
     * the owner should use the Start Session button to start the game.
     *
     * @return null if not enough users are ready, SystemMessage if all or
     * all users except the owner are marked as ready
     *
     * @author Phillip-André Suhr
     * @since 2021-04-26
     */
    public ReadySystemMessageDTO getOwnerNotice() {
        return ownerNotice;
    }

    /**
     * Gets the set of all ready users
     *
     * @return A Set of ready Users
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-19
     */
    public Set<UserOrDummy> getReadyUsers() {
        return this.readyUsers;
    }

    /**
     * Gets the list of lobby members
     *
     * @return List of lobby members
     *
     * @since 2020-12-21
     */
    public List<UserOrDummy> getUsers() {
        return users;
    }
}
