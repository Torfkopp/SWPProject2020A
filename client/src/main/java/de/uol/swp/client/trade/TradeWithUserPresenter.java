package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.trade.event.TradeWithUserUpdateEvent;
import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.request.UnpauseTimerRequest;
import de.uol.swp.common.game.response.InventoryForTradeWithUserResponse;
import de.uol.swp.common.game.response.ResetOfferTradeButtonResponse;
import de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse;
import de.uol.swp.common.user.UserOrDummy;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Manages the TradingWithUser window
 *
 * @author Finn Haase
 * @author Maximilian Lindner
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-02-23
 */
@SuppressWarnings("UnstableApiUsage")
public class TradeWithUserPresenter extends AbstractTradePresenter {

    public static final String fxml = "/fxml/TradeWithUserView.fxml";
    public static final int MIN_HEIGHT = 680;
    public static final int MIN_WIDTH = 520;
    private static final Logger LOG = LogManager.getLogger(TradeWithUserPresenter.class);

    @FXML
    private Label statusLabel;
    @FXML
    private HBox tradingHBox;
    @FXML
    private Slider tradingPartnerLumberSlider, tradingPartnerWoolSlider, tradingPartnerGrainSlider, tradingPartnerOreSlider, tradingPartnerBrickSlider;
    @FXML
    private Slider ownLumberSlider, ownWoolSlider, ownGrainSlider, ownOreSlider, ownBrickSlider;
    @FXML
    private Button offerTradeButton;

    private String lobbyName;
    private UserOrDummy respondingUser;
    private int traderInventorySize;
    private List<Map<String, Object>> selectedOwnResourceList;
    private List<Map<String, Object>> selectedPartnersResourceList;

    /**
     * Constructor
     * <p>
     * Sets the eventBus
     *
     * @param eventBus The EventBus
     */
    @Inject
    public TradeWithUserPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Initialises the Presenter using its superclass
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        super.initialize();
        LOG.debug("TradeWithUserPresenter initialised");
    }

    /**
     * Helper Function
     * <p>
     * Checks if there is no selected resource at all or if too
     * many resources were demanded by the offering player.
     *
     * @return true if any resource is selected
     */
    private boolean checkResources() {
        int selectedOwnResourceMapCounter = 0;
        int selectedPartnersResourceMapCounter = 0;
        for (Map<String, Object> map : selectedOwnResourceList) {
            selectedOwnResourceMapCounter += (int) map.get("amount");
        }
        for (Map<String, Object> map : selectedPartnersResourceList) {
            selectedPartnersResourceMapCounter += (int) map.get("amount");
        }
        if (selectedPartnersResourceMapCounter > traderInventorySize) {
            tradeService.showTradeError(resourceBundle.getString("game.trade.error.demandtoohigh"));
        }
        //@formatter:off
        return ((selectedPartnersResourceMapCounter + selectedOwnResourceMapCounter == 0)
                || (selectedPartnersResourceMapCounter > traderInventorySize));
        //@formatter:on
    }

    /**
     * Helper function called if a unsuccessful trade happened.
     * <p>
     * Posts a TradeWithBankCancelEvent with its lobbyName to close the
     * trading window and a TradeWithUserCancelResponse to close the responding
     * trading window if existent.
     *
     * @see de.uol.swp.client.trade.event.TradeCancelEvent
     */
    private void closeWindow() {
        tradeService.closeUserTradeWindow(lobbyName);
        tradeService.cancelTrade(lobbyName, respondingUser);
    }

    /**
     * Handles a click on the Cancel Button
     * <p>
     * Method called when the CancelButton is pressed and uses
     * the helperFunction closeWindow to close the window
     * properly.
     */
    @FXML
    private void onCancelTradeButtonPressed() {
        closeWindow();
        eventBus.post(new UnpauseTimerRequest(lobbyName, userService.getLoggedInUser()));
    }

    /**
     * Handles a InventoryForTradeWithUserResponse found on the eventBus
     * <p>
     * If the InventoryForTradeWithUserResponse is directed to this lobby,
     * the TradeWithBankPresenter gets the inventory of the player
     * as a Map. Calls a function to fill the inventory and calls a function
     * to give the sliders the right labeling.
     * If the user has no resources and the amount of resources
     * of the other user is also 0, the trading possibility gets denied by
     * hiding the inventories and the button and showing the label instead.
     *
     * @param rsp InventoryForTradeResponse having the inventory
     *
     * @see de.uol.swp.common.game.response.InventoryForTradeWithUserResponse
     */
    @Subscribe
    private void onInventoryForTradeWithUserResponse(InventoryForTradeWithUserResponse rsp) {
        if (!rsp.getLobbyName().equals(this.lobbyName)) return;
        LOG.debug("Received InventoryForTradeResponse for Lobby {}", rsp.getLobbyName());
        respondingUser = rsp.getTradingUser();
        List<Map<String, Object>> resourceList = Collections.unmodifiableList(rsp.getResourceList());
        ownResourceTableView.getItems().addAll(resourceList);
        traderInventorySize = rsp.getTradingUsersInventorySize();
        int ownInventorySize = 0;
        for (Map<String, Object> map : resourceList) {
            ownInventorySize += (int) map.get("amount");
        }
        if (!(traderInventorySize == 0 && ownInventorySize == 0)) {
            setSliders(resourceList);
            Platform.runLater(() -> statusLabel
                    .setText(String.format(resourceBundle.getString("game.trade.status.makingoffer"), respondingUser)));
        } else {
            offerTradeButton.setDisable(true);
            tradingHBox.setVisible(false);
            Platform.runLater(() -> statusLabel
                    .setText(String.format(resourceBundle.getString("game.trade.error.noresources"), respondingUser)));
        }
    }

    /**
     * Handles a Click on the OfferTrade button
     * <p>
     * If the button is clicked, this method calls the setResourceMap method.
     * If there is no resource selected at all, nothing happens.
     * Otherwise, an OfferingTradeWithUserRequest is posted
     * onto the EventBus to get the needed information from
     * the server.
     * The offerTradeButton gets disabled and the user gets the message to wait
     * for the other user.
     *
     * @see de.uol.swp.common.game.request.OfferingTradeWithUserRequest
     */
    @FXML
    private void onOfferTradeButtonPressed() {
        setResourceLists();
        if (checkResources()) {
            LOG.debug("Failed sending the offer");
            return;
        }
        offerTradeButton.setDisable(true);
        statusLabel.setText(String.format(resourceBundle.getString("game.trade.status.waiting"), respondingUser));
        tradeService.offerTrade(lobbyName, respondingUser, selectedOwnResourceList, selectedPartnersResourceList);
        tradeService.closeTradeResponseWindow(lobbyName);
    }

    /**
     * Handles a ResetOfferTradeButtonResponse found on the EventBus
     * <p>
     * If a ResetOfferTradeButtonResponse is found on the EventBus, the offer trade button
     * is re-enabled and the trading user gets a hint that the other user
     * rejected the offer.
     *
     * @param event ResetOfferTradeButtonResponse found on the EventBus
     *
     * @see de.uol.swp.common.game.response.ResetOfferTradeButtonResponse
     */
    @Subscribe
    private void onResetOfferTradeButtonResponse(ResetOfferTradeButtonResponse event) {
        if (!lobbyName.equals(event.getLobbyName())) return;
        LOG.debug("Received ResetOfferTradeButtonResponse for Lobby {}", lobbyName);
        Platform.runLater(() -> {
            offerTradeButton.setDisable(false);
            statusLabel.setText(String.format(resourceBundle.getString("game.trade.status.rejected"), respondingUser));
        });
    }

    /**
     * Handles a TradeOfUsersAcceptedResponse found on the EventBus
     * If the trade is accepted this will close the trading window.
     *
     * @param rsp Response found on the EventBus
     *
     * @see de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse
     */
    @Subscribe
    private void onTradeOfUsersAcceptedResponse(TradeOfUsersAcceptedResponse rsp) {
        if (!rsp.getLobbyName().equals(this.lobbyName)) return;
        LOG.debug("Received TradeOfUsersAcceptedResponse for Lobby {}", lobbyName);
        closeWindow();
        eventBus.post(new UnpauseTimerRequest(lobbyName, userService.getLoggedInUser()));
    }

    /**
     * Handles a TradeWithUserUpdateEvent
     * <p>
     * If the lobbyName or the logged in user of the TradeWithUserPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * TradeWithUserPresenter is created. If a window is closed using e.g.
     * X(top-right-Button), the closeWindow method is called.
     *
     * @param event TradeUpdateEvent found on the event bus
     *
     * @see de.uol.swp.client.trade.event.TradeWithUserUpdateEvent
     */
    @Subscribe
    private void onTradeWithUserUpdateEvent(TradeWithUserUpdateEvent event) {
        LOG.debug("Received TradeWithUserUpdateEvent for Lobby {}", event.getLobbyName());
        if (lobbyName == null) lobbyName = event.getLobbyName();
        Window window = ownResourceTableView.getScene().getWindow();
        window.setOnCloseRequest(windowEvent -> closeWindow());
    }

    /**
     * Helper function to handle the slider readout
     * <p>
     * Sets the content of resource lists according to the selected resources
     * and the amount of resources as selected with the sliders.
     *
     * @author Phillip-André Suhr
     * @since 2021-04-20
     */
    @FXML
    private void setResourceLists() {
        selectedOwnResourceList = new ArrayList<>();
        selectedPartnersResourceList = new ArrayList<>();
        for (Resources resource : Resources.values()) {
            Map<String, Object> offeredResources = new HashMap<>();
            Map<String, Object> demandedResources = new HashMap<>();
            switch (resource) {
                case BRICK:
                    offeredResources.put("amount", (int) ownBrickSlider.getValue());
                    offeredResources.put("resource", new I18nWrapper("game.resources.brick"));
                    offeredResources.put("enumType", resource);
                    demandedResources.put("amount", (int) tradingPartnerBrickSlider.getValue());
                    demandedResources.put("resource", new I18nWrapper("game.resources.brick"));
                    demandedResources.put("enumType", resource);
                    selectedOwnResourceList.add(offeredResources);
                    selectedPartnersResourceList.add(demandedResources);
                    break;
                case GRAIN:
                    offeredResources.put("amount", (int) ownGrainSlider.getValue());
                    offeredResources.put("resource", new I18nWrapper("game.resources.grain"));
                    offeredResources.put("enumType", resource);
                    demandedResources.put("amount", (int) tradingPartnerGrainSlider.getValue());
                    demandedResources.put("resource", new I18nWrapper("game.resources.grain"));
                    demandedResources.put("enumType", resource);
                    selectedOwnResourceList.add(offeredResources);
                    selectedPartnersResourceList.add(demandedResources);
                    break;
                case LUMBER:
                    offeredResources.put("amount", (int) ownLumberSlider.getValue());
                    offeredResources.put("resource", new I18nWrapper("game.resources.lumber"));
                    offeredResources.put("enumType", resource);
                    demandedResources.put("amount", (int) tradingPartnerLumberSlider.getValue());
                    demandedResources.put("resource", new I18nWrapper("game.resources.lumber"));
                    demandedResources.put("enumType", resource);
                    selectedOwnResourceList.add(offeredResources);
                    selectedPartnersResourceList.add(demandedResources);
                    break;
                case ORE:
                    offeredResources.put("amount", (int) ownOreSlider.getValue());
                    offeredResources.put("resource", new I18nWrapper("game.resources.ore"));
                    offeredResources.put("enumType", resource);
                    demandedResources.put("amount", (int) tradingPartnerOreSlider.getValue());
                    demandedResources.put("resource", new I18nWrapper("game.resources.ore"));
                    demandedResources.put("enumType", resource);
                    selectedOwnResourceList.add(offeredResources);
                    selectedPartnersResourceList.add(demandedResources);
                    break;
                case WOOL:
                    offeredResources.put("amount", (int) ownWoolSlider.getValue());
                    offeredResources.put("resource", new I18nWrapper("game.resources.wool"));
                    offeredResources.put("enumType", resource);
                    demandedResources.put("amount", (int) tradingPartnerWoolSlider.getValue());
                    demandedResources.put("resource", new I18nWrapper("game.resources.wool"));
                    demandedResources.put("enumType", resource);
                    selectedOwnResourceList.add(offeredResources);
                    selectedPartnersResourceList.add(demandedResources);
                    break;
            }
        }
    }

    /**
     * Helper Function to handle the slider attributes
     *
     * @param resourceList List of resourceMaps to determine the Slider values
     */
    @FXML
    private void setSliders(List<Map<String, Object>> resourceList) {
        tradingPartnerBrickSlider.setMax(traderInventorySize);
        tradingPartnerOreSlider.setMax(traderInventorySize);
        tradingPartnerLumberSlider.setMax(traderInventorySize);
        tradingPartnerWoolSlider.setMax(traderInventorySize);
        tradingPartnerGrainSlider.setMax(traderInventorySize);

        for (Map<String, Object> map : resourceList) {
            if (Resources.GRAIN.equals(map.get("enumType"))) ownGrainSlider.setMax((int) map.get("amount"));
            if (Resources.BRICK.equals(map.get("enumType"))) ownBrickSlider.setMax((int) map.get("amount"));
            if (Resources.LUMBER.equals(map.get("enumType"))) ownLumberSlider.setMax((int) map.get("amount"));
            if (Resources.ORE.equals(map.get("enumType"))) ownOreSlider.setMax((int) map.get("amount"));
            if (Resources.WOOL.equals(map.get("enumType"))) ownWoolSlider.setMax((int) map.get("amount"));
        }
    }
}
