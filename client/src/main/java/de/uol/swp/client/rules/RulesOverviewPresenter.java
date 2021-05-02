package de.uol.swp.client.rules;

import de.uol.swp.client.AbstractPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Manages display of the Rules Overview
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-04-24
 */
public class RulesOverviewPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/RulesOverviewView.fxml";
    public static final int MIN_HEIGHT = 550;
    public static final int MIN_WIDTH = 610;

    private static final int MIN_TRADE_TAB_HEIGHT = 700;

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
            if (newValue.equals(tradingTab)) {
                rulesTabPane.getScene().getWindow().setHeight(MIN_TRADE_TAB_HEIGHT);
            } else if (oldValue.equals(tradingTab) && !newValue.equals(tradingTab)) {
                rulesTabPane.getScene().getWindow().setHeight(MIN_HEIGHT);
            }
        });
    }
}
