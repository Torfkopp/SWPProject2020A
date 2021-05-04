package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.trade.event.TradeWithUserResponseUpdateEvent;
import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.IResource;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.response.InvalidTradeOfUsersResponse;
import de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.user.UserOrDummy;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private Button acceptTradeButton;
    @FXML
    private Label tradeNotPossibleLabel;
    @FXML
    private Label tradeResponseLabel;

    private LobbyName lobbyName;
    private UserOrDummy offeringUser;
    private ResourceList offeringResourceMap;
    private ResourceList respondingResourceMap;

    /**
     * Constructor
     *
     * @param eventBus The EventBus
     */
    @Inject
    public TradeWithUserAcceptPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Initialises the Presenter using the superclass.
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        super.initialize();
        LOG.debug("TradeWithUserAcceptPresenter initialised");
    }

    /**
     * Handles a click on the accept button
     * If the accept button is pressed a new AcceptUserTradeRequest is posted onto
     * the EventBus.
     */
    @FXML
    private void onAcceptTradeButtonPressed() {
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
        Platform.runLater(() -> {
            acceptTradeButton.setDisable(true);
            tradeNotPossibleLabel.setText(
                    String.format(resourceBundle.getString("game.trade.status.invalid"), rsp.getOfferingUser()));
        });
    }

    /**
     * Handles a click on the MakeCounterOfferButton
     * <p>
     * When the buttons gets pressed, this method calls the
     * ShowTradeWithUserViewEvent to open up the trading window and
     * a TradeWithUserRequest to get the needed information from the
     * server for the trade.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @see de.uol.swp.client.trade.event.ShowTradeWithUserViewEvent
     * @see de.uol.swp.common.game.request.TradeWithUserRequest
     * @since 2021-03-19
     */
    @FXML
    private void onMakeCounterOfferButtonPressed() {
        tradeService.showUserTradeWindow(lobbyName, offeringUser);
        tradeService.tradeWithUser(lobbyName, offeringUser);
    }

    /**
     * Handles a click on the reject button
     * <p>
     * If the lobbyName or the logged in user of the TradeWithUserPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * TradeWithUserPresenter is created. If a window is closed using, e.g.
     * X(top-right-Button), the closeWindowAfterNotSuccessfulTrade method is called.
     */
    @FXML
    private void onRejectTradeButtonPressed() {
        tradeService.resetOfferTradeButton(lobbyName, offeringUser);
        tradeService.closeTradeResponseWindow(lobbyName);
    }

    /**
     * This method calls the closeWindow function to close the
     * according window.
     *
     * @param rsp TradeOfUsersAcceptedResponse found on the EventBus
     *
     * @see de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse
     */
    @Subscribe
    private void onTradeOfUsersAcceptedResponse(TradeOfUsersAcceptedResponse rsp) {
        LOG.debug("Received TradeOfUsersAcceptedResponse for Lobby {}", lobbyName);
        tradeService.closeTradeResponseWindow(lobbyName);
    }

    /**
     * Handles a TradeWithUserResponseUpdateEvent found on the EventBus
     * <p>
     * If a TradeWithUserResponseUpdateEvent is found on the EventBus
     * and it is directed to this lobby, this TradeWithUserAcceptPresenter
     * gets multiple Parameters and calls the setOfferLabel method to
     * set the offer label according to the offer and show the Users' own
     * inventory.
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
        window.setOnCloseRequest(windowEvent -> tradeService.closeTradeResponseWindow(lobbyName));
    }

    /**
     * Helper Function
     * <p>
     * Sets the content of the tradeResponseLabel to the offers and demands
     */
    private void setOfferLabel() {
        String offered = tallyUpOfferOrDemand(offeringResourceMap);
        String demanded = tallyUpOfferOrDemand(respondingResourceMap);
        Platform.runLater(() -> tradeResponseLabel.setText(
                String.format(resourceBundle.getString("game.trade.offer.proposed"), offeringUser, offered, demanded)));
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
        if (nothing) content.append(resourceBundle.getString("game.trade.offer.nothing"));
        if (content.substring(content.length() - 2, content.length()).equals(", "))
            content.delete(content.length() - 2, content.length());
        return content.toString();
    }
}
