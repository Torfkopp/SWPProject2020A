package de.uol.swp.client.rules;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.rules.event.ResetRulesOverviewEvent;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages display of the Rules Overview
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-04-24
 */
@SuppressWarnings("UnstableApiUsage")
public class RulesOverviewPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/RulesOverviewView.fxml";
    public static final int MIN_HEIGHT = 550;
    public static final int MIN_WIDTH = 625;

    private static final int MIN_LONG_TEXT_HEIGHT = 700;
    private static final int MIN_BANK_TAB_WIDTH = 650;
    private static final Logger LOG = LogManager.getLogger(RulesOverviewPresenter.class);

    @FXML
    private Tab standardDevCardTab;
    @FXML
    private Tab uniqueDevCardTab;
    @FXML
    private TabPane bankTabPane;
    @FXML
    private Tab bankTab;
    @FXML
    private Tab resourceExchangeTab;
    @FXML
    private Tab devCardSaleTab;
    @FXML
    private Tab basicsTab;
    @FXML
    private Tab buildingTab;
    @FXML
    private Tab developmentCardTab;
    @FXML
    private Tab founderPhaseTab;
    @FXML
    private Tab robberTab;
    @FXML
    private Tab tradingTab;
    @FXML
    private TabPane rulesTabPane;

    /**
     * Initialises the rulesTabPane to resize when selecting the tradingTab, which has a lot
     * more text to display than the others.
     */
    @FXML
    private void initialize() {
        rulesTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(tradingTab) || newValue.equals(robberTab)) {
                rulesTabPane.getScene().getWindow().setHeight(MIN_LONG_TEXT_HEIGHT);
                rulesTabPane.getScene().getWindow().setWidth(MIN_WIDTH);
            } else if (newValue.equals(bankTab)) {
                rulesTabPane.getScene().getWindow().setWidth(MIN_BANK_TAB_WIDTH);
                rulesTabPane.getScene().getWindow().setHeight(MIN_LONG_TEXT_HEIGHT);
            } else {
                rulesTabPane.getScene().getWindow().setHeight(MIN_HEIGHT);
                rulesTabPane.getScene().getWindow().setWidth(MIN_WIDTH);
            }
        });
        bankTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            rulesTabPane.getScene().getWindow()
                        .setHeight(newValue.equals(resourceExchangeTab) ? MIN_LONG_TEXT_HEIGHT : MIN_HEIGHT);
            rulesTabPane.getScene().getWindow().setWidth(MIN_BANK_TAB_WIDTH);
        });
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("RulesOverviewPresenter initialised");
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Handles a ResetRulesOverviewEvent found on the EventBus
     * <p>
     * This method resets the selected tab to the introductory "Basics" tab
     * in order to avoid sizing issues.
     *
     * @param event The ResetRulesOverviewEvent found on the EventBus
     *
     * @since 2021-05-03
     */
    @Subscribe
    private void onResetRulesOverviewEvent(ResetRulesOverviewEvent event) {
        Platform.runLater(() -> rulesTabPane.getSelectionModel().select(basicsTab));
    }
}
