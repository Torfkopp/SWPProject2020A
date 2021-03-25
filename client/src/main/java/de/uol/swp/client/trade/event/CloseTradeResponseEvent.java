package de.uol.swp.client.trade.event;

/**
 * This event is used to cancel a Response window of a possible
 * previous trade.
 *
 * @author Maximilian Lindner
 * @author Aldin Dervisi
 * @since 2021 -03-19
 */
public class CloseTradeResponseEvent {

    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName The name of the lobby
     */
    public CloseTradeResponseEvent(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the lobby name.
     *
     * @return The lobby name
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
