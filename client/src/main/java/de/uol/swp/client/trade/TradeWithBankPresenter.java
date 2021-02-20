package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.trade.event.TradeLobbyButtonUpdateEvent;
import de.uol.swp.client.trade.event.TradeUpdateEvent;
import de.uol.swp.client.trade.event.TradeWithBankCancelEvent;
import de.uol.swp.common.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the Trading with the bank window
 *
 * @author Alwin Bossert
 * @author Maximilian Lindner
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-02-19
 */
@SuppressWarnings("UnstableApiUsage")
public class TradeWithBankPresenter extends AbstractPresenter {
    public static final String fxml = "/fxml/TradeWithBankView.fxml";
    private final Logger LOG = LogManager.getLogger(TradeWithBankPresenter.class);
    private String lobbyName;
    private User loggedInUser;
    @FXML
    private Button buyEntwicklungskarteButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button tradeRessourceWithBankButton;

    @Inject
    public TradeWithBankPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Handles a click on the Buy Button
     * <p>
     * Method called when the BuyBankButton is pressed.
     * The Method posts a BuyBankRequest including logged in user
     * the EventBus.
     */
    @FXML
    private void onBuyEntwicklungskarteButtonPressed() {
    }

    /**
     * Handles a click on the Cancel Button
     * <p>
     * Method called when the CancelButton is pressed.
     * The Method posts a CancelBankTradeRequest including logged in user
     * the EventBus and a TradeLobbyButtonUpdateEvent including the
     * logged in user and the lobbyname.
     */
    @FXML
    private void onCancelButtonPressed() {
        eventBus.post(new TradeWithBankCancelEvent(lobbyName));
        eventBus.post(new TradeLobbyButtonUpdateEvent(loggedInUser, lobbyName));
    }

    /**
     * Handles a click on the Trade Button
     * <p>
     * Method called when the TradeBankButton is pressed.
     * The Method posts a TradeBankRequest including logged in user
     * the EventBus.
     */
    @FXML
    private void onTradeRessourceWithBankButtonPressed() {
    }

    /**
     * Handles a TradeUpdateEvent
     * <p>
     * If the lobbyname and the logged in user of the TradeWithBankPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * TradeWithBankPresenter is created.
     *
     * @param event TradeUpdateEvent found on the event bus
     */
    @Subscribe
    private void onTradeUpdateEvent(TradeUpdateEvent event) {
        if (lobbyName == null && loggedInUser == null) {
            lobbyName = event.getLobbyName();
            loggedInUser = event.getUser();
        }
    }
}

