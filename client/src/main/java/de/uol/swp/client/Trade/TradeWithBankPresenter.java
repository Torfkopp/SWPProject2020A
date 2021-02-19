package de.uol.swp.client.Trade;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
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
public class TradeWithBankPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/TradeWithBankView.fxml";
    private final Logger LOG = LogManager.getLogger(TradeWithBankPresenter.class);
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
     * the EventBus.
     */
    @FXML
    private void onCancelButtonPressed() {
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
}

