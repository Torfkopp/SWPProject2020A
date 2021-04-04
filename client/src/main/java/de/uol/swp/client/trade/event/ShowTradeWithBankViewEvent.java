package de.uol.swp.client.trade.event;

/**
 * Event used to show the window for the trading with the bank
 * <p>
 * In order to show the previous window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Maximilian Lindner
 * @author Alwin Bossert
 * @see de.uol.swp.client.SceneManager
 * @since 2021-02-19
 */
public class ShowTradeWithBankViewEvent {

    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName Lobby name of the lobby where the player wants to trade
     */
    public ShowTradeWithBankViewEvent(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the lobby name of the lobby where the player want to
     * trade with the bank
     *
     * @return Lobby name of the lobby where the player wants to trade
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
