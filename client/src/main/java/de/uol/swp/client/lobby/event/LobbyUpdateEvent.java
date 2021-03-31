package de.uol.swp.client.lobby.event;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Event used to communicate Lobby details to new LobbyPresenter instances
 * <p>
 * This event is dispatched when the User creates or joins a Lobby from the Main Menu
 * in order to tell the LobbyPresenter of that Lobby the details about its name and
 * the currently logged in user.
 * <p>
 * In order to communicate the Lobby details, post an instance of this event to the
 * EventBus the LobbyPresenter instance(s) are subscribed to.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @implNote The User can be the Creator of the Lobby or a normal User joining
 * an existing Lobby, so it cannot be used to determine the Lobby owner.
 * @see de.uol.swp.client.main.MainMenuPresenter
 * @see de.uol.swp.client.lobby.LobbyPresenter
 * @since 2020-12-30
 */
public class LobbyUpdateEvent {

    private final Lobby lobby;
    private final String lobbyName;
    private final UserOrDummy user;

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby to update
     * @param user      The name of the User who caused this Event (Creator or Joining User)
     * @param lobby     The Lobby-object of the lobby the user wants to join
     */
    public LobbyUpdateEvent(String lobbyName, UserOrDummy user, Lobby lobby) {
        this.lobbyName = lobbyName;
        this.user = user;
        this.lobby = lobby;
    }

    /**
     * The Lobby-object of the lobby the user wants to join
     *
     * @return The lobby object
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    public Lobby getLobby() {
        return lobby;
    }

    /**
     * Getter for the lobbyName attribute
     *
     * @return The lobbyName
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Getter for the user attribute
     *
     * @return The User
     */
    public UserOrDummy getUser() {
        return user;
    }
}
