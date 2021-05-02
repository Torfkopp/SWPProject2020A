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
}
