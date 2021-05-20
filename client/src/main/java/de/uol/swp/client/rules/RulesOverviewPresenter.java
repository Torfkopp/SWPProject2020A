package de.uol.swp.client.rules;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.SetAcceleratorsEvent;
import de.uol.swp.client.rules.event.ResetRulesOverviewEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

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
        rulesTabPane.getSelectionModel().select(basicsTab);
    }

    /**
     * Handles a SetAcceleratorEvent found on the EventBus
     * <p>
     * This method sets the accelerators for the RulesOverviewPresenter, namely
     * <ul>
     *     <li> CTRL/META + I = Select Basics tab
     *     <li> CTRL/META + B = Select Building tab
     *     <li> CTRL/META + T = Select Trading tab
     *     <li> CTRL/META + A = Select Bank tab
     *     <li> CTRL/META + R = Select Robber tab
     *     <li> CTRL/META + D = Select DevelopmentCard tab
     *     <li> CTRL/META + F = Select Founding Phase tab
     *     <li> ESC           = Close window
     *
     * @param event The SetAcceleratorEvent found on the EventBus
     *
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.SetAcceleratorsEvent
     * @since 2021-05-20
     */
    @Subscribe
    private void onSetAcceleratorsEvent(SetAcceleratorsEvent event) {
        LOG.debug("Received SetAcceleratorsEvent");
        Map<KeyCombination, Runnable> accelerators = new HashMap<>();
        accelerators.put(new KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN), // CTRL/META + I
                         () -> rulesTabPane.getSelectionModel().select(basicsTab));
        accelerators.put(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN), // CTRL/META + B
                         () -> rulesTabPane.getSelectionModel().select(buildingTab));
        accelerators.put(new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN), // CTRL/META + T
                         () -> rulesTabPane.getSelectionModel().select(tradingTab));
        accelerators.put(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN), // CTRL/META + A
                         () -> rulesTabPane.getSelectionModel().select(bankTab));
        accelerators.put(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN), // CTRL/META + R
                         () -> rulesTabPane.getSelectionModel().select(robberTab));
        accelerators.put(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN), // CTRL/META + D
                         () -> rulesTabPane.getSelectionModel().select(developmentCardTab));
        accelerators.put(new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN), // CTRL/META + F
                         () -> rulesTabPane.getSelectionModel().select(founderPhaseTab));
        accelerators.put(new KeyCodeCombination(KeyCode.ESCAPE), // ESC to close window
                         () -> {
                             Window window = rulesTabPane.getScene().getWindow();
                             // WINDOW_CLOSE_REQUEST to trigger the routine set by the SceneManager properly
                             window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
                         });
        rulesTabPane.getScene().getAccelerators().putAll(accelerators);
    }
}
