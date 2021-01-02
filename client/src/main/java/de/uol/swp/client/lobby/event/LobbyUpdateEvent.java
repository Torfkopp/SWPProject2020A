package de.uol.swp.client.lobby.event;

import de.uol.swp.common.user.User;

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
    private final String lobbyName;
    private final User user;

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby to update
     * @param user      The name of the User who caused this Event (Creator or Joining User)
     */
    public LobbyUpdateEvent(String lobbyName, User user) {
        this.lobbyName = lobbyName;
        this.user = user;
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
    public User getUser() {
        return user;
    }
}
