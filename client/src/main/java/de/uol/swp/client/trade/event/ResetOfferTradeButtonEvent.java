package de.uol.swp.client.trade.event;

/**
 * Event used to re-enable the offer trade button.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.client.SceneManager
 * @since 2021-02-24
 */
public class ResetOfferTradeButtonEvent {

    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby where the offer trade button should be re-enabled
     */
    public ResetOfferTradeButtonEvent(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the name of the lobby where the offer trade button should be re-enabled
     *
     * @return name of the lobby
     */
    public String getLobbyName() {
        return lobbyName;
    }
}