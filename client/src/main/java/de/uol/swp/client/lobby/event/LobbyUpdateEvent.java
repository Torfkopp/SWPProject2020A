package de.uol.swp.client.lobby.event;

import de.uol.swp.common.lobby.Lobby;

/**
 * Event used to communicate Lobby details to new LobbyPresenter instances
 * <p>
 * This event is dispatched when the User creates or joins a Lobby from the Main Menu
 * in order to tell the LobbyPresenter of that Lobby the details about the lobby.
 * <p>
 * In order to communicate the Lobby details, post an instance of this event to the
 * EventBus the LobbyPresenter instance(s) are subscribed to.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.main.MainMenuPresenter
 * @see de.uol.swp.client.lobby.LobbyPresenter
 * @since 2020-12-30
 */
public class LobbyUpdateEvent {

    private final Lobby lobby;

    /**
     * Constructor
     *
     * @param lobby The Lobby-object of the lobby the user wants to join
     */
    public LobbyUpdateEvent(Lobby lobby) {
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
}
