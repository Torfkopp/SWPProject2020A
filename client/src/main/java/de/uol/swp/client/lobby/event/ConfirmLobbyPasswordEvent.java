package de.uol.swp.client.lobby.event;

/**
 * Event used to trigger the confirmation of the ConfirmLobbyPasswordPresenter
 * <p>
 * In order to give a ConfirmLobbyPasswordPresenter its name and User who wants to join the lobby, post an
 * instance of it onto the EventBus the ConfirmLobbyPasswordPresenter is subscribed to.
 *
 * @author Alwin Bossert
 * @see de.uol.swp.client.lobby.ConfirmLobbyPasswordPresenter
 * @since 2021-04-22
 */
public class ConfirmLobbyPasswordEvent {

    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby
     */
    public ConfirmLobbyPasswordEvent(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the name of the lobby
     *
     * @return The lobbyName
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
