package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.sun.javafx.scene.control.IntegerField;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.game.IGameService;
import de.uol.swp.client.lobby.event.ShowRobberTaxUpdateEvent;
import de.uol.swp.common.game.request.UnpauseTimerRequest;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.IResource;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.Resource;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.lobby.LobbyName;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType.*;

/**
 * Manages the RobberTax window
 *
 * @author Mario Fokken
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-04-08
 */
@SuppressWarnings("UnstableApiUsage")
public class RobberTaxPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/RobberTaxView.fxml";
    public static final int MIN_HEIGHT = 650;
    public static final int MIN_WIDTH = 550;
    private static final Logger LOG = LogManager.getLogger(RobberTaxPresenter.class);
    private static final String GREEN_BAR = "green-bar", BLUE_BAR = "blue-bar", RED_BAR = "red-bar";
    private static final String[] barColourClasses = {RED_BAR, BLUE_BAR, GREEN_BAR};
    private final ResourceList selectedResources = new ResourceList();

    @Inject
    protected IGameService gameService;

    @FXML
    private Label resourceAmount;
    @FXML
    private Slider brickSlider, grainSlider, lumberSlider, oreSlider, woolSlider;
    @FXML
    private IntegerField brickField, grainField, lumberField, oreField, woolField;
    @FXML
    private ListView<Resource> ownInventoryView;
    @FXML
    private Button taxPay;
    @FXML
    private ProgressBar progress;

    private LobbyName lobbyName;
    private int taxAmount;
    private ResourceList inventory;
    private ObservableList<Resource> ownInventoryList;

    /**
     * Listener for the brickSlider
     */
    @FXML
    private void brickSliderListener() {
        selectedResources.set(BRICK, (int) brickSlider.getValue());
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
        for (IResource resource : selectedResources) {
            selectedAmount += resource.getAmount();
        }
        if (selectedAmount < taxAmount) {
            progress.setProgress((double) selectedAmount / taxAmount);
            progress.getStyleClass().removeAll(barColourClasses);
            progress.getStyleClass().add(BLUE_BAR);
        } else if (selectedAmount == taxAmount) {
            progress.setProgress(100);
            progress.getStyleClass().removeAll(barColourClasses);
            progress.getStyleClass().add(GREEN_BAR);
        } else progress.getStyleClass().add(RED_BAR);

        taxPay.setDisable(selectedAmount != taxAmount);
    }

    /**
     * Listener for the grainSlider
     */
    @FXML
    private void grainSliderListener() {
        selectedResources.set(GRAIN, (int) grainSlider.getValue());
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
            protected void updateItem(Resource item, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getAmount() + " " + item.getType());
                });
            }
        });
        Platform.runLater(() -> brickSlider.requestFocus());

        brickSlider.valueProperty().addListener((obs, oldValue, newValue) -> brickSliderListener());
        grainSlider.valueProperty().addListener((obs, oldValue, newValue) -> grainSliderListener());
        lumberSlider.valueProperty().addListener((obs, oldValue, newValue) -> lumberSliderListener());
        oreSlider.valueProperty().addListener((obs, oldValue, newValue) -> oreSliderListener());
        woolSlider.valueProperty().addListener((obs, oldValue, newValue) -> woolSliderListener());

        brickSlider.valueProperty().bindBidirectional(brickField.valueProperty());
        grainSlider.valueProperty().bindBidirectional(grainField.valueProperty());
        lumberSlider.valueProperty().bindBidirectional(lumberField.valueProperty());
        oreSlider.valueProperty().bindBidirectional(oreField.valueProperty());
        woolSlider.valueProperty().bindBidirectional(woolField.valueProperty());

        LOG.debug("RobberTaxPresenter initialised");
    }

    /**
     * Listener for the lumberSlider
     */
    @FXML
    private void lumberSliderListener() {
        selectedResources.set(LUMBER, (int) lumberSlider.getValue());
        dragMethod();
    }

    /**
     * Handles a ShowRobberTaxUpdateEvent
     * <p>
     * The event is sent when a new RobberTaxPresenter is created and
     * contains the relevant data to be displayed in the RobberTaxPresenter.
     * <p>
     * This method also sets the accelerators for the RobberTaxPresenter, namely
     * <ul>
     *     <li> CTRL/META + P = Pay Tax button
     *
     * @param event ShowRobberTaxUpdateEvent found on the EventBus
     *
     * @see de.uol.swp.client.lobby.event.ShowRobberTaxUpdateEvent
     */
    @Subscribe
    private void onShowRobberTaxUpdateEvent(ShowRobberTaxUpdateEvent event) {
        LOG.debug("Received ShowRobberTaxUpdateEvent");
        lobbyName = event.getLobbyName();
        taxAmount = event.getTaxAmount();
        inventory = event.getInventory();

        resourceAmount.setText(String.valueOf(taxAmount));
        setInventoryList();
        setSliders(event.getInventory());

        Map<KeyCombination, Runnable> accelerators = new HashMap<>();
        accelerators.put(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN), // CTRL/META + P
                         this::onTaxPayButtonPressed);
        resourceAmount.getScene().getAccelerators().putAll(accelerators);
    }

    /**
     * Method called when the TaxPayButton is pressed
     * <p>
     * Posts a request to reduce the resources, posts an event to close the window
     * and then updates the inventory
     * It also posts a new UnpauseTimerRequest onto the EventBus
     */
    @FXML
    private void onTaxPayButtonPressed() {
        if (taxPay.isDisabled()) {
            LOG.trace("onTaxPayButton called with disabled button, returning");
            return;
        }
        soundService.button();
        LOG.debug("Sending RobberTaxChosenRequest");
        gameService.taxPayed(lobbyName, selectedResources);
        gameService.updateInventory(lobbyName);
        eventBus.post(new UnpauseTimerRequest(lobbyName, userService.getLoggedInUser()));
    }

    /**
     * Listener for the oreSlider
     */
    @FXML
    private void oreSliderListener() {
        selectedResources.set(ORE, (int) oreSlider.getValue());
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
        for (IResource entry : inventory) {
            ownInventoryList.add(entry.create());
        }
    }

    /**
     * Helper method to handle the slider attributes
     */
    @FXML
    private void setSliders(ResourceList inventory) {
        inventory = inventory.create();
        brickSlider.setMax(inventory.getAmount(BRICK));
        grainSlider.setMax(inventory.getAmount(GRAIN));
        lumberSlider.setMax(inventory.getAmount(LUMBER));
        oreSlider.setMax(inventory.getAmount(ORE));
        woolSlider.setMax(inventory.getAmount(WOOL));
    }

    /**
     * Listener for the woolSlider
     */
    @FXML
    private void woolSliderListener() {
        selectedResources.set(WOOL, (int) woolSlider.getValue());
        dragMethod();
    }
}
