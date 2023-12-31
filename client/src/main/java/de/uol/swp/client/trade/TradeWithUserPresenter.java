package de.uol.swp.client.trade;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.trade.event.TradeWithUserUpdateEvent;
import de.uol.swp.common.game.request.PauseTimerRequest;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.*;
import de.uol.swp.common.game.response.InventoryForTradeWithUserResponse;
import de.uol.swp.common.game.response.ResetOfferTradeButtonResponse;
import de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.util.ResourceManager;
import de.uol.swp.common.util.Util;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

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
    private int maxTradeDiff;

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

    private LobbyName lobbyName;
    private Actor respondingUser;
    private int traderInventorySize;
    private ResourceList selectedOwnResourceList;
    private ResourceList selectedPartnersResourceList;
    private boolean counterOffer;

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
        for (IResource entry : selectedOwnResourceList) {
            selectedOwnResourceMapCounter += entry.getAmount();
        }
        for (IResource entry : selectedPartnersResourceList) {
            selectedPartnersResourceMapCounter += entry.getAmount();
        }
        if (selectedPartnersResourceMapCounter > traderInventorySize) {
            sceneService.showError(ResourceManager.get("game.trade.error.demandtoohigh"));
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
     */
    private void closeWindow() {
        sceneService.closeUserTradeWindow(lobbyName);
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
        soundService.button();
        closeWindow();
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
        if (!Util.equals(lobbyName, rsp.getLobbyName())) return;
        LOG.debug("Received InventoryForTradeResponse for Lobby {}", rsp.getLobbyName());
        respondingUser = rsp.getTradingUser();
        counterOffer = rsp.isCounterOffer();
        maxTradeDiff = rsp.getMaxTradeDiff();
        IResourceList resourceList = rsp.getResourceMap();
        for (IResource resource : resourceList)
            ownResourceTableView.getItems().add(resource);
        traderInventorySize = rsp.getTradingUsersInventorySize();
        int ownInventorySize = 0;
        for (IResource entry : resourceList) {
            ownInventorySize += entry.getAmount();
        }
        if (!(traderInventorySize == 0 && ownInventorySize == 0)) {
            setSliders(resourceList);
            String status = ResourceManager.get("game.trade.status.makingoffer", respondingUser);
            Platform.runLater(() -> statusLabel.setText(status));
        } else {
            String text = ResourceManager.get("game.trade.error.noresources", respondingUser);
            Platform.runLater(() -> {
                offerTradeButton.setDisable(true);
                tradingHBox.setVisible(false);
                statusLabel.setText(text);
            });
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
     * It also posts a new PauseTimerRequest onto the EventBus.
     *
     * @see de.uol.swp.common.game.request.OfferingTradeWithUserRequest
     */
    @FXML
    private void onOfferTradeButtonPressed() {
        if (offerTradeButton.isDisabled()) {
            LOG.trace("onOfferTradeButtonPressed called with disabled offerTradeButton, returning");
            return;
        }
        soundService.button();
        setResourceLists();
        if (checkResources()) {
            LOG.debug("Failed sending the offer");
            return;
        }
        if (tradeIsFair()) {
            offerTradeButton.setDisable(true);
            statusLabel.setText(ResourceManager.get("game.trade.status.waiting", respondingUser));
            tradeService.offerTrade(lobbyName, respondingUser, selectedOwnResourceList, selectedPartnersResourceList,
                                    counterOffer);
            sceneService.closeAcceptTradeWindow(lobbyName);
            LOG.debug("Sending PauseTimerRequest for Lobby {}", lobbyName);
            post(new PauseTimerRequest(lobbyName, userService.getLoggedInUser()));
        }
    }

    /**
     * Handles a ResetOfferTradeButtonResponse found on the EventBus
     * <p>
     * If a ResetOfferTradeButtonResponse is found on the EventBus, the offer trade button
     * is re-enabled and the trading user gets a hint that the other user
     * rejected the offer.
     *
     * @param rsp ResetOfferTradeButtonResponse found on the EventBus
     *
     * @see de.uol.swp.common.game.response.ResetOfferTradeButtonResponse
     */
    @Subscribe
    private void onResetOfferTradeButtonResponse(ResetOfferTradeButtonResponse rsp) {
        if (!Util.equals(lobbyName, rsp.getLobbyName())) return;
        LOG.debug("Received ResetOfferTradeButtonResponse for Lobby {}", rsp.getLobbyName());
        if (rsp.isTradeRejectedByActivePlayer()) {
            closeWindow();
            return;
        }
        String text = ResourceManager.get("game.trade.status.rejected", respondingUser);
        Platform.runLater(() -> {
            offerTradeButton.setDisable(false);
            statusLabel.setText(text);
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
        if (!Util.equals(lobbyName, rsp.getLobbyName())) return;
        LOG.debug("Received TradeOfUsersAcceptedResponse for Lobby {}", rsp.getLobbyName());
        soundService.coins();
        closeWindow();
    }

    /**
     * Handles a TradeWithUserUpdateEvent
     * <p>
     * If the lobbyName or the logged in user of the TradeWithUserPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * TradeWithUserPresenter is created. If a window is closed using e.g.
     * X(top-right-Button), the closeWindow method is called.
     * <p>
     * This method also sets the accelerators for the TradeWithUserPresenter, namely
     * <ul>
     *     <li> CTRL/META + O = Make Offer button
     *     <li> ESC           = Cancel button
     *
     * @param event TradeUpdateEvent found on the event bus
     *
     * @see de.uol.swp.client.trade.event.TradeWithUserUpdateEvent
     */
    @Subscribe
    private void onTradeWithUserUpdateEvent(TradeWithUserUpdateEvent event) {
        if (lobbyName != null) return;
        LOG.debug("Received TradeWithUserUpdateEvent for Lobby {}", event.getLobbyName());
        lobbyName = event.getLobbyName();

        Map<KeyCombination, Runnable> accelerators = new HashMap<>();
        accelerators.put(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN), // CTRL/META + O
                         this::onOfferTradeButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.ESCAPE), // ESC to close window
                         this::onCancelTradeButtonPressed);
        ownResourceTableView.getScene().getAccelerators().putAll(accelerators);
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
    private void setResourceLists() {
        selectedOwnResourceList = new ResourceList();
        selectedOwnResourceList.set(ResourceType.BRICK, ((int) (ownBrickSlider.getValue())));
        selectedOwnResourceList.set(ResourceType.ORE, ((int) (ownOreSlider.getValue())));
        selectedOwnResourceList.set(ResourceType.LUMBER, ((int) (ownLumberSlider.getValue())));
        selectedOwnResourceList.set(ResourceType.GRAIN, ((int) (ownGrainSlider.getValue())));
        selectedOwnResourceList.set(ResourceType.WOOL, ((int) (ownWoolSlider.getValue())));

        selectedPartnersResourceList = new ResourceList();
        selectedPartnersResourceList.set(ResourceType.BRICK, ((int) (tradingPartnerBrickSlider.getValue())));
        selectedPartnersResourceList.set(ResourceType.ORE, ((int) (tradingPartnerOreSlider.getValue())));
        selectedPartnersResourceList.set(ResourceType.WOOL, ((int) (tradingPartnerWoolSlider.getValue())));
        selectedPartnersResourceList.set(ResourceType.LUMBER, ((int) (tradingPartnerLumberSlider.getValue())));
        selectedPartnersResourceList.set(ResourceType.GRAIN, ((int) (tradingPartnerGrainSlider.getValue())));
    }

    /**
     * Helper Function to handle the slider attributes
     *
     * @param resourceList List of resourceMaps to determine the Slider values
     */
    private void setSliders(IResourceList resourceList) {
        Platform.runLater(() -> {
            tradingPartnerBrickSlider.setMax(traderInventorySize);
            tradingPartnerOreSlider.setMax(traderInventorySize);
            tradingPartnerLumberSlider.setMax(traderInventorySize);
            tradingPartnerWoolSlider.setMax(traderInventorySize);
            tradingPartnerGrainSlider.setMax(traderInventorySize);

            ownGrainSlider.setMax(resourceList.getAmount(ResourceType.GRAIN));
            ownOreSlider.setMax(resourceList.getAmount(ResourceType.ORE));
            ownLumberSlider.setMax(resourceList.getAmount(ResourceType.LUMBER));
            ownWoolSlider.setMax(resourceList.getAmount(ResourceType.WOOL));
            ownBrickSlider.setMax(resourceList.getAmount(ResourceType.BRICK));
        });
    }

    /**
     * Method which blocks unfair trades
     * <p>
     * If the onOfferTradeButtonPressed Method has been called, this
     * method checks if the difference in the amount of Ressources between
     * offering and demanding Trade is the current MAX_TRADE_DIFF or lower.
     * If not, the Method will return false and with that, the onOfferTradeButtonPressed
     * will not send the offer
     */
    private boolean tradeIsFair() {
        statusLabel.setText(ResourceManager.get("game.trade.status.toomanyresources"));
        int counterOwnResource = selectedOwnResourceList.getTotal();
        int counterPartnersResource = selectedPartnersResourceList.getTotal();
        return Math.abs(counterOwnResource - counterPartnersResource) <= maxTradeDiff;
    }
}
