package de.uol.swp.client.trade;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.trade.event.TradeWithUserResponseUpdateEvent;
import de.uol.swp.common.game.request.UnpauseTimerRequest;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.IResource;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.response.InvalidTradeOfUsersResponse;
import de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.util.ResourceManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages the tradingAccept menu
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-02-25
 */
@SuppressWarnings("UnstableApiUsage")
public class TradeWithUserAcceptPresenter extends AbstractTradePresenter {

    public static final String fxml = "/fxml/TradeWithUserAcceptView.fxml";
    public static final int MIN_HEIGHT = 340;
    public static final int MIN_WIDTH = 380;
    private static final Logger LOG = LogManager.getLogger(TradeWithUserAcceptPresenter.class);

    @FXML
    protected Label acceptTradeTimerLabel;

    protected Timer tradeAcceptTimer;
    protected boolean paused;
    @FXML
    private Button acceptTradeButton;
    @FXML
    private Label tradeNotPossibleLabel;
    @FXML
    private Label tradeResponseLabel;
    private LobbyName lobbyName;
    private Actor offeringUser;
    private ResourceList offeringResourceMap;
    private ResourceList respondingResourceMap;

    /**
     * Initialises the Presenter using the superclass.
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        super.initialize();
        LOG.debug("TradeWithUserAcceptPresenter initialised");
        setAcceptTradeTimer(30);
    }

    /**
     * Helper method to set the timer for the players round.
     * The user gets forced to end his turn, if the timer gets zero.
     * It also closes all the opened windows.
     * If paused is true, the timer is paused.
     *
     * @param moveTime The moveTime for the Lobby
     *
     * @author Alwin Bossert
     * @since 2021-05-01
     */
    public void setAcceptTradeTimer(int moveTime) {
        tradeAcceptTimer = new Timer();
        AtomicInteger moveTimeToDecrement = new AtomicInteger(moveTime);
        tradeAcceptTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!paused) {
                    int i = moveTimeToDecrement.getAndDecrement();
                    String moveTimeText = String.format(ResourceManager.get("game.labels.movetime"), i);
                    Platform.runLater(() -> acceptTradeTimerLabel.setText(moveTimeText));
                    if (moveTimeToDecrement.get() == 0) {
                        tradeService.resetOfferTradeButton(lobbyName, offeringUser);
                        tradeService.closeTradeResponseWindow(lobbyName);
                        post(new UnpauseTimerRequest(lobbyName, userService.getLoggedInUser()));
                    }
                }
            }
        }, 0, 1000);
    }

    /**
     * Handles a click on the accept button
     * If the accept button is pressed a new AcceptUserTradeRequest is posted onto
     * the EventBus.
     */
    @FXML
    private void onAcceptTradeButtonPressed() {
        if (acceptTradeButton.isDisabled()) {
            LOG.trace("onAcceptTradeButtonPressed called with disabled acceptTradeButton, returning");
            return;
        }
        soundService.button();
        tradeService.acceptUserTrade(lobbyName, offeringUser, respondingResourceMap, offeringResourceMap);
    }

    /**
     * Handles a InvalidTradeOfUsersResponse found on the EventBus
     * <p>
     * If an InvalidTradeOfUsersResponse is found on the EventBus, this
     * method disables the accept button and shows an info text.
     *
     * @param rsp The InvalidTradeOfUsersResponse found on the EventBus
     */
    @Subscribe
    private void onInvalidTradeOfUsersResponse(InvalidTradeOfUsersResponse rsp) {
        LOG.debug("Received InvalidTradeOfUsersResponse for Lobby {}", lobbyName);
        String invalid = String.format(ResourceManager.get("game.trade.status.invalid"), rsp.getOfferingUser());
        Platform.runLater(() -> {
            acceptTradeButton.setDisable(true);
            tradeNotPossibleLabel.setText(invalid);
        });
    }

    /**
     * Handles a click on the MakeCounterOfferButton
     * <p>
     * When the buttons gets pressed, this method calls the
     * ShowTradeWithUserViewEvent to open up the trading window and
     * a TradeWithUserRequest to get the needed information from the
     * server for the trade.
     * It also posts a new UnpauseTimerRequest onto the EventBus.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @see de.uol.swp.client.trade.event.ShowTradeWithUserViewEvent
     * @see de.uol.swp.common.game.request.TradeWithUserRequest
     * @since 2021-03-19
     */
    @FXML
    private void onMakeCounterOfferButtonPressed() {
        soundService.button();
        tradeService.showUserTradeWindow(lobbyName, offeringUser, true);
        post(new UnpauseTimerRequest(lobbyName, userService.getLoggedInUser()));
    }

    /**
     * Handles a click on the reject button
     * <p>
     * If the lobbyName or the logged in user of the TradeWithUserPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * TradeWithUserPresenter is created. If a window is closed using, e.g.
     * X(top-right-Button), the closeWindowAfterNotSuccessfulTrade method is called.
     * It also posts a new UnpauseTimerRequest onto the EventBus.
     */
    @FXML
    private void onRejectTradeButtonPressed() {
        soundService.button();
        tradeService.resetOfferTradeButton(lobbyName, offeringUser);
        tradeService.closeTradeResponseWindow(lobbyName);
        tradeService.cancelTrade(lobbyName, offeringUser);
        post(new UnpauseTimerRequest(lobbyName, userService.getLoggedInUser()));
    }

    /**
     * This method calls the closeWindow function to close the
     * according window.
     * It also posts a new UnpauseTimerRequest onto the EventBus.
     *
     * @param rsp TradeOfUsersAcceptedResponse found on the EventBus
     *
     * @see de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse
     */
    @Subscribe
    private void onTradeOfUsersAcceptedResponse(TradeOfUsersAcceptedResponse rsp) {
        LOG.debug("Received TradeOfUsersAcceptedResponse for Lobby {}", lobbyName);
        tradeService.closeTradeResponseWindow(lobbyName);
        post(new UnpauseTimerRequest(lobbyName, userService.getLoggedInUser()));
    }

    /**
     * Handles a TradeWithUserResponseUpdateEvent found on the EventBus
     * <p>
     * If a TradeWithUserResponseUpdateEvent is found on the EventBus
     * and it is directed to this lobby, this TradeWithUserAcceptPresenter
     * gets multiple Parameters and calls the setOfferLabel method to
     * set the offer label according to the offer and show the Users' own
     * inventory.
     * <p>
     * This method also sets the accelerators for the TradeWithUserAcceptPresenter, namely
     * <ul>
     *     <li> CTRL/META + A = Accept Trade Offer
     *     <li> CTRL/META + C = Make Counter Offer
     *     <li> CTRL/META + R = Reject Trade Offer
     *
     * @param event TradeWithUserResponseUpdateEvent found on the EventBus
     */
    @Subscribe
    private void onTradeWithUserResponseUpdateEvent(TradeWithUserResponseUpdateEvent event) {
        TradeWithUserOfferResponse rsp = event.getRsp();
        lobbyName = rsp.getLobbyName();
        if (!lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received TradeWithUserResponseUpdateEvent for Lobby {}", lobbyName);
        offeringUser = rsp.getOfferingUser();
        respondingResourceMap = rsp.getDemandedResources();
        offeringResourceMap = rsp.getOfferedResources();
        for (IResource resource : rsp.getResourceList())
            ownResourceTableView.getItems().add(resource);
        setOfferLabel();
        Window window = ownResourceTableView.getScene().getWindow();
        window.setOnCloseRequest(windowEvent -> {
            tradeService.resetOfferTradeButton(lobbyName, offeringUser);
            tradeService.closeTradeResponseWindow(lobbyName);
        });

        Map<KeyCombination, Runnable> accelerators = new HashMap<>();
        accelerators.put(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN), // CTRL/META + A
                         this::onAcceptTradeButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), // CTRL/META + C
                         this::onMakeCounterOfferButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN), // CTRL/META + R
                         this::onRejectTradeButtonPressed);
        ownResourceTableView.getScene().getAccelerators().putAll(accelerators);
    }

    /**
     * Helper Function
     * <p>
     * Sets the content of the tradeResponseLabel to the offers and demands
     */
    private void setOfferLabel() {
        String offered = tallyUpOfferOrDemand(offeringResourceMap);
        String demanded = tallyUpOfferOrDemand(respondingResourceMap);
        String bundleString = ResourceManager.get("game.trade.offer.proposed");
        String text = String.format(bundleString, offeringUser, offered, demanded);
        Platform.runLater(() -> tradeResponseLabel.setText(text));
    }

    /**
     * Helper method to tally up the offered/demanded resources
     * <p>
     * Returns a String containing the offered and demanded resources.
     *
     * @param resourceList The List of resources to tally up
     *
     * @return String containing the offer
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-04-05
     */
    private String tallyUpOfferOrDemand(ResourceList resourceList) {
        boolean nothing = true;
        StringBuilder content = new StringBuilder();
        for (IResource entry : resourceList) {
            int amount = entry.getAmount();
            if (amount > 0) {
                nothing = false;
                content.append(entry.getAmount()).append(" ").append(entry.getType()).append(", ");
            }
        }
        if (nothing) content.append(ResourceManager.get("game.trade.offer.nothing"));
        if (content.substring(content.length() - 2, content.length()).equals(", "))
            content.delete(content.length() - 2, content.length());
        return content.toString();
    }
}
