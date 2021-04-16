package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.game.IGameService;
import de.uol.swp.client.lobby.event.ShowRobberTaxUpdateEvent;
import de.uol.swp.common.game.map.Resources;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the RobberTax window
 *
 * @author Mario Fokken
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-04-08
 */
@SuppressWarnings("UnstableApiUsage")
public class RobberTaxPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/RobberTaxPresenter.fxml";
    public static final int MIN_HEIGHT = 650;
    public static final int MIN_WIDTH = 550;
    private static final Logger LOG = LogManager.getLogger(RobberTaxPresenter.class);
    private final Map<Resources, Integer> selectedResources = new HashMap<>();

    @Inject
    protected IGameService gameService;

    @FXML
    private Label resourceAmount;
    @FXML
    private Slider brickSlider, grainSlider, lumberSlider, oreSlider, woolSlider;
    @FXML
    private ListView<Pair<Resources, Integer>> ownInventoryView;
    @FXML
    private Button taxPay;
    @FXML
    private ProgressBar progress;

    private String lobbyName;
    private int taxAmount;
    private Map<Resources, Integer> inventory;
    private ObservableList<Pair<Resources, Integer>> ownInventoryList;

    /**
     * Constructor
     * <p>
     * Sets the eventBus
     *
     * @param eventBus The EventBus
     */
    @Inject
    public RobberTaxPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Listener for the brickSlider
     */
    @FXML
    private void brickSliderListener() {
        selectedResources.put(Resources.BRICK, (int) brickSlider.getValue());
        dragMethod();
    }

    /**
     * Helper method for every slider
     * <p>
     * It sets the progressBar according to the amount of selected resources
     * and enables/ disables the taxPay button depending on said amount.
     */
    private void dragMethod() {
        int selectedAmount = 0;
        for (Integer i : selectedResources.values()) {
            selectedAmount += i;
        }
        if (selectedAmount <= taxAmount) progress.setProgress((double) selectedAmount / taxAmount);
        else progress.setProgress(1.0 - (selectedAmount % taxAmount) / (double) taxAmount);
        taxPay.setDisable(selectedAmount != taxAmount);
    }

    /**
     * Listener for the grainSlider
     */
    @FXML
    private void grainSliderListener() {
        selectedResources.put(Resources.GRAIN, (int) grainSlider.getValue());
        dragMethod();
    }

    /**
     * Initialises the Presenter by setting up the ownInventoryView.
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    private void initialize() {
        ownInventoryView.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Pair<Resources, Integer> item, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getValue().toString() + " " + resourceBundle
                            .getString("game.resources." + item.getKey().toString().toLowerCase()));
                });
            }
        });
        brickSlider.valueProperty().addListener((obs, oldValue, newValue) -> brickSliderListener());
        grainSlider.valueProperty().addListener((obs, oldValue, newValue) -> grainSliderListener());
        lumberSlider.valueProperty().addListener((obs, oldValue, newValue) -> lumberSliderListener());
        oreSlider.valueProperty().addListener((obs, oldValue, newValue) -> oreSliderListener());
        woolSlider.valueProperty().addListener((obs, oldValue, newValue) -> woolSliderListener());
        LOG.debug("RobberTaxPresenter initialised");
    }

    /**
     * Listener for the lumberSlider
     */
    @FXML
    private void lumberSliderListener() {
        selectedResources.put(Resources.LUMBER, (int) lumberSlider.getValue());
        dragMethod();
    }

    /**
     * Handles a ShowRobberTaxUpdateEvent
     * <p>
     * The event is sent when a new RobberTaxPresenter is created
     *
     * @param event ShowRobberTaxUpdateEvent found on the EventBus
     *
     * @see de.uol.swp.client.lobby.event.ShowRobberTaxUpdateEvent
     */
    @Subscribe
    private void onShowRobberTaxUpdateEvent(ShowRobberTaxUpdateEvent event) {
        lobbyName = event.getLobbyName();
        taxAmount = event.getTaxAmount();
        inventory = event.getInventory();

        resourceAmount.setText(String.valueOf(taxAmount));
        setInventoryList();
        setSliders();
    }

    /**
     * Method called when the TaxPayButton is pressed
     * <p>
     * Posts a request to reduce the resources, posts an event to close the window
     * and then updates the inventory
     */
    @FXML
    private void onTaxPayButtonPressed() {
        LOG.debug("Sending RobberTaxChosenRequest");
        gameService.taxPayed(lobbyName, selectedResources);
        gameService.updateInventory(lobbyName);
    }

    /**
     * Listener for the oreSlider
     */
    @FXML
    private void oreSliderListener() {
        selectedResources.put(Resources.ORE, (int) oreSlider.getValue());
        dragMethod();
    }

    /**
     * Helper method to set the ownInventoryView
     * <p>
     * If there is no ownInventoryList, one gets created and cleared.
     * then it gets updated with the items as listed in the inventory map.
     */
    private void setInventoryList() {
        if (ownInventoryList == null) {
            ownInventoryList = FXCollections.observableArrayList();
            ownInventoryView.setItems(ownInventoryList);
        }
        ownInventoryList.clear();
        for (Map.Entry<Resources, Integer> entry : inventory.entrySet()) {
            Pair<Resources, Integer> ownResource = new Pair<>(entry.getKey(), entry.getValue());
            ownInventoryList.add(ownResource);
        }
    }

    /**
     * Helper method to handle the slider attributes
     */
    @FXML
    private void setSliders() {
        brickSlider.setMax(inventory.get(Resources.BRICK));
        grainSlider.setMax(inventory.get(Resources.GRAIN));
        lumberSlider.setMax(inventory.get(Resources.LUMBER));
        oreSlider.setMax(inventory.get(Resources.ORE));
        woolSlider.setMax(inventory.get(Resources.WOOL));
    }

    /**
     * Listener for the woolSlider
     */
    @FXML
    private void woolSliderListener() {
        selectedResources.put(Resources.WOOL, (int) woolSlider.getValue());
        dragMethod();
    }
}