package de.uol.swp.client.trade.event;

/**
 * Event used to trigger close the trading window with the other user
 * when the cancel button got pressed
 * <p>
 * In order to close the Trading window when the cancel button got pressed, post an
 * instance of it onto the EventBus the SceneManager is subscribed to. The SceneManager
 * will need the Lobby Name to close the trading window
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.client.SceneManager
 * @since 2021-02-23
 */
public class TradeWithUserCancelEvent {

    private final String lobbyName;
    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby where the trading window should be closed
     */
    public TradeWithUserCancelEvent(String lobbyName) {
        this.lobbyName = lobbyName;
    }
    /**
     * Gets the name of the lobby where the trading window should be closed
     *
     * @return LobbyName object of the event
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
