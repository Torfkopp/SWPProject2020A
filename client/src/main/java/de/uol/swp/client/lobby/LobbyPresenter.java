package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenterWithChat;
import de.uol.swp.client.GameRendering;
import de.uol.swp.client.lobby.event.CloseLobbiesViewEvent;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.client.trade.event.*;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.game.map.GameMapManagement;
import de.uol.swp.common.game.message.DiceCastMessage;
import de.uol.swp.common.game.message.NextPlayerMessage;
import de.uol.swp.common.game.request.TradeWithBankRequest;
import de.uol.swp.common.game.request.TradeWithUserRequest;
import de.uol.swp.common.game.response.*;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.StartSessionRequest;
import de.uol.swp.common.lobby.request.UserReadyRequest;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.lobby.response.RemoveFromLobbiesResponse;
import de.uol.swp.common.message.RequestMessage;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Manages the lobby's menu
 *
 * @see de.uol.swp.client.AbstractPresenter
 * @see de.uol.swp.client.AbstractPresenterWithChat
 * @since 2020-11-21
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyPresenter extends AbstractPresenterWithChat {

    public static final String fxml = "/fxml/LobbyView.fxml";
    private static final CloseLobbiesViewEvent closeLobbiesViewEvent = new CloseLobbiesViewEvent();
    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<Pair<Integer, User>> lobbyMembers;
    private ObservableList<Pair<String, String>> resourceList;
    private User owner;
    private Set<User> readyUsers;
    @FXML
    private ListView<Pair<Integer, User>> membersView;
    @FXML
    private CheckBox readyCheckBox;
    @FXML
    private Button startSession;
    @FXML
    private Button rollDice;
    @FXML
    private Button endTurn;
    @FXML
    private Button tradeWithUserButton;
    @FXML
    private Button tradeWithBankButton;
    @FXML
    private Label turnIndicator;
    @FXML
    private Canvas gameMapCanvas;
    @FXML
    private VBox playField;
    @FXML
    private ListView<Pair<String, String>> inventoryView;

    private GameRendering gameRendering;
    private Window window;

    /**
     * Constructor
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.AbstractPresenterWithChat
     * @since 2021-01-02
     */
    public LobbyPresenter() {
        super.init(LogManager.getLogger(LobbyPresenter.class));
    }

    /**
     * Initialises the Presenter by setting up the membersView.
     *
     * @implNote Called automatically by JavaFX
     * @author Temmo Junkhoff
     * @author Timo Gerken
     * @since 2021-01-18
     */
    @Override
    @FXML
    public void initialize() {
        super.initialize();
        membersView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<Integer, User> item, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(item, empty);
                    if (empty || item == null) setText("");
                    else {
                        User user = item.getValue();
                        String name = user.getUsername();
                        if (readyUsers.contains(user))
                            name = String.format(resourceBundle.getString("lobby.members.ready"), name);
                        if (user.getID() == owner.getID())
                            name = String.format(resourceBundle.getString("lobby.members.owner"), name);
                        setText(name);
                    }
                });
            }
        });
        inventoryView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, String> item, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getValue() + " " + resourceBundle
                            .getString("game.resources." + item.getKey())); // looks like: "1 Brick"
                });
            }
        });

        gameRendering = new GameRendering(gameMapCanvas);
        LOG.debug("LobbyPresenter initialised");
    }

    @Override
    @Subscribe
    protected void onAskLatestChatMessageResponse(AskLatestChatMessageResponse rsp) {
        LOG.debug("Received AskLatestChatMessageResponse");
        if (rsp.getLobbyName() != null && rsp.getLobbyName().equals(super.lobbyName)) {
            super.onAskLatestChatMessageResponse(rsp);
        }
    }

    @Override
    @Subscribe
    protected void onCreatedChatMessageMessage(CreatedChatMessageMessage msg) {
        LOG.debug("Received CreatedChatMessageMessage");
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(super.lobbyName)) {
            super.onCreatedChatMessageMessage(msg);
        }
    }

    @Override
    @Subscribe
    protected void onDeletedChatMessageMessage(DeletedChatMessageMessage msg) {
        LOG.debug("Received DeletedChatMessageMessage");
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(super.lobbyName)) {
            super.onDeletedChatMessageMessage(msg);
        }
    }

    @Override
    @Subscribe
    protected void onEditedChatMessageMessage(EditedChatMessageMessage msg) {
        LOG.debug("Received EditedChatMessageMessage");
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(super.lobbyName)) {
            super.onEditedChatMessageMessage(msg);
        }
    }

    /**
     * Helper function to let the user leave the lobby and close the window
     * Also clears the EventBus of the instance to avoid NullPointerExceptions.
     *
     * @author Temmo Junkhoff
     * @since 2021-01-06
     */
    private void closeWindow() {
        if (lobbyName != null || loggedInUser != null) {
            lobbyService.leaveLobby(lobbyName, loggedInUser);
        }
        ((Stage) window).close();
        clearEventBus();
    }

    /**
     * Helper function to find the Pair for a given key
     *
     * @param id The key of the pair that should be returned
     *
     * @return The pair matched by the ID
     *
     * @author Temmo Junkhoff
     * @author Timo Gerken
     * @since 2021-01-19
     */
    private Pair<Integer, User> findMember(int id) {
        for (Pair<Integer, User> lobbyMember : lobbyMembers) {
            if (lobbyMember.getKey() == id) return lobbyMember;
        }
        return null;
    }

    /**
     * Handles a new list of users
     * <p>
     * If a new AllOnlineUsersResponse object is posted onto the EventBus,
     * and it is intended for the current Lobby, the names of the currently
     * logged in members are put into the list of lobby members. The owner
     * attribute is set and the set of ready Users is updated. Additionally,
     * the state of the "Start Session" button is set appropriately.
     * <p>
     * Furthermore, if the LOG-Level is set to DEBUG, the messages "Update of user
     * list" with the names of all currently logged in users, "Owner of this
     * lobby: " with the name of the lobby's owner, and "Update of ready users "
     * are displayed in the log.
     *
     * @param rsp The AllLobbyMembersResponse object seen on the EventBus
     *
     * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
     * @since 2021-01-19
     */
    @Subscribe
    private void onAllLobbyMembersResponse(AllLobbyMembersResponse rsp) {
        if (!super.lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received AllLobbyMembersResponse");
        LOG.debug("---- Update of lobby member list");
        LOG.debug("---- Owner of this lobby: " + rsp.getOwner().getUsername());
        LOG.debug("---- Update of ready users");
        this.owner = rsp.getOwner();
        this.readyUsers = rsp.getReadyUsers();
        updateUsersList(rsp.getUsers());
        setStartSessionButtonState();
    }

    /**
     * If a BuyDevelopmentCardResponse is found on the EventBus,
     * this method calls 2 methods to reset the trade with bank button
     * and the trade with user button for the users in the response.
     *
     * @param rsp BuyDevelopmentCardResponse found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.response.BuyDevelopmentCardResponse
     * @since 2021-02-28
     */
    @Subscribe
    private void onBuyDevelopmentCardResponse(BuyDevelopmentCardResponse rsp) {
        if (!lobbyName.equals(rsp.getLobbyName())) return;
        setTradeWithUserButtonState(rsp.getUser());
        setTradeWithBankButtonState(rsp.getUser());
    }

    /**
     * Handles a DiceCastMessage
     * <p>
     * If a new DiceCastMessage object is posted onto the EventBus,
     * this method is called.
     * It enables the endTurnButton, the Trade with User Button and
     * the trade with Bank Button.
     *
     * @param msg The DiceCastMessage object seen on the EventBus
     *
     * @see de.uol.swp.common.game.message.DiceCastMessage
     * @since 2021-01-15
     */
    @Subscribe
    private void onDiceCastMessage(DiceCastMessage msg) {
        LOG.debug("Received DiceCastMessage");
        LOG.debug("---- The dices show: " + msg.getDice1() + " and " + msg.getDice2());
        setEndTurnButtonState(msg.getUser());
        setTradeWithBankButtonState(msg.getUser());
        setTradeWithUserButtonState(msg.getUser());
        gameRendering.drawDice(msg.getDice1(), msg.getDice2());
    }

    /**
     * Method called when the EndTurnButton is pressed
     * <p>
     * If the EndTurnButton is pressed, this method requests the LobbyService
     * to end the current turn.
     *
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2021-01-15
     */
    @FXML
    private void onEndTurnButtonPressed() {
        this.endTurn.setDisable(true);
        this.rollDice.setDisable(true);
        this.tradeWithBankButton.setDisable(true);
        this.tradeWithUserButton.setDisable(true);
        lobbyService.endTurn(loggedInUser, lobbyName);
        lobbyService.updateInventory(lobbyName, loggedInUser);
    }

    /**
     * Handles a click on the LeaveLobby button
     * <p>
     * Method called when the leaveLobby button is pressed.
     * If the leaveLobby button is pressed this method requests
     * the lobby service to leave the lobby.
     *
     * @since 2020-12-14
     */
    @FXML
    private void onLeaveLobbyButtonPressed() {
        closeWindow();
    }

    /**
     * Handles LobbyUpdateEvents on the EventBus
     * <p>
     * If a new LobbyUpdateEvent is posted to the EventBus, this method checks
     * whether the lobbyName, loggedInUser, or readyUsers attributes of the current
     * LobbyPresenter are null. If they are, it sets these attributes to the
     * values found in the LobbyUpdateEvent or creates a new, empty instance.
     * Also makes sure that the lobby will be left gracefully should the window
     * be closed without using the Leave Lobby button.
     *
     * @param event The LobbyUpdateEvent found on the EventBus
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.lobby.event.LobbyUpdateEvent
     * @since 2021-01-19
     */
    @Subscribe
    private void onLobbyUpdateEvent(LobbyUpdateEvent event) {
        LOG.debug("Received LobbyUpdateEvent for lobby " + event.getLobbyName());
        if (super.lobbyName == null || loggedInUser == null) {
            super.lobbyName = event.getLobbyName();
            super.loggedInUser = event.getUser();
            super.chatService.askLatestMessages(10, super.lobbyName);
        }
        if (this.window == null) {
            this.window = membersView.getScene().getWindow();
        }
        if (this.readyUsers == null) {
            this.readyUsers = new HashSet<>();
        }
        this.window.setOnCloseRequest(windowEvent -> closeWindow());
        lobbyService.retrieveAllLobbyMembers(this.lobbyName);
    }

    /**
     * Handles a NextPlayerMessage
     * <p>
     * If a new NextPlayerMessage object is posted onto the EventBus,
     * this method is called.
     * It changes the text of a textField to state whose turn it is.
     *
     * @param msg The NextPlayerMessage object seen on the EventBus
     */
    @Subscribe
    private void onNextPlayerMessage(NextPlayerMessage msg) {
        if (!msg.getLobbyName().equals(this.lobbyName)) return;
        LOG.debug("Received NextPlayerMessage for Lobby " + msg.getLobbyName());
        setTurnIndicatorText(msg.getActivePlayer());
        setRollDiceButtonState(msg.getActivePlayer());
    }

    /**
     * Handles the click on the ReadyCheckBox
     * <p>
     * Method called when the Ready Checkbox is clicked. It checks whether the
     * CheckBox is selected or not and then posts a UserReadyRequest onto the
     * EventBus with the appropriate parameters.
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-19
     */
    @FXML
    private void onReadyCheckBoxClicked() {
        boolean isReady = readyCheckBox.isSelected();
        RequestMessage userReadyRequest = new UserReadyRequest(this.lobbyName, this.loggedInUser, isReady);
        eventBus.post(userReadyRequest);
    }

    /**
     * Handles leaving all Lobbies when a user logged out
     * <p>
     * If a new RemoveFromLobbiesResponse is posted onto the EventBus the
     * method leaveLobby in LobbyService is called for every Lobby the user
     * is in.
     *
     * @param rsp The RemoveFromLobbiesResponse seen on the EventBus
     *
     * @author Finn Haase
     * @author Aldin Dervisi
     * @see de.uol.swp.common.lobby.response.RemoveFromLobbiesResponse
     * @since 2021-01-28
     */
    @Subscribe
    private void onRemoveFromLobbiesResponse(RemoveFromLobbiesResponse rsp) {
        LOG.debug("Received RemoveFromLobbiesResponse");
        for (Map.Entry<String, Lobby> entry : rsp.getLobbiesWithUser().entrySet()) {
            lobbyService.leaveLobby(entry.getKey(), loggedInUser);
        }
    }

    /**
     * Handles an ResetTradeWithBankButtonEvent found on the EventBus
     * <p>
     * If the ResetTradeWithBankButtonEvent is intended for the current Lobby
     * the trade With Bank button is enabled again and the end turn button
     * as well.
     *
     * @param event The TradeLobbyButtonUpdateEvent found on the event bus
     *
     * @author Alwin Bossert
     * @author Maximilian Lindner
     * @see de.uol.swp.client.trade.event.ResetTradeWithBankButtonEvent
     * @since 2021-02-22
     */
    @Subscribe
    public void onResetTradeWithBankButtonEvent(ResetTradeWithBankButtonEvent event) {
        if (super.lobbyName.equals(event.getLobbyName()) && super.loggedInUser.equals(event.getUser())) {
            setTradeWithBankButtonState(event.getUser());
            setEndTurnButtonState(event.getUser());
            setTradeWithUserButtonState(event.getUser());
        }
    }

    /**
     * Handles a ResetTradeWithUserButtonEvent found on the event bus
     * <p>
     * If a new ResetTradeWithUserButtonEvent is posted onto the EventBus the
     * tradeWithUserButton is enabled again.
     *
     * @param event The ResetTradeWithUserButtonEvent seen on the EventBus
     *
     * @author Finn Haase
     * @author Maximilian Lindner
     * @since 2021-02-23
     */
    @Subscribe
    private void onResetTradeWithUserButtonEvent(ResetTradeWithUserButtonEvent event) {
        setTradeWithBankButtonState(event.getUser());
        setTradeWithUserButtonState(event.getUser());
    }

    /**
     * Method called when the rollDice Button is pressed
     * <p>
     * If the rollDice Button is pressed, this method requests the LobbyService
     * to roll the dices.
     *
     * @author Mario Fokken
     * @author Sven Ahrens
     * @see LobbyService
     * @since 2021-02-22
     */
    @FXML
    public void onRollDiceButtonPressed() {
        lobbyService.rollDice(lobbyName, loggedInUser);
        this.rollDice.setDisable(true);
    }

    /**
     * Handles a click on the StartSession Button
     * <p>
     * Method called when the StartSessionButton is pressed.
     * The Method posts a StartSessionRequest including the lobby name and the
     * logged in user onto the EventBus.
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-20
     */
    @FXML
    private void onStartSessionButtonPressed() {
        RequestMessage startSessionRequest = new StartSessionRequest(this.lobbyName, this.loggedInUser);
        eventBus.post(startSessionRequest);
    }

    /**
     * Handles a StartSessionMessage found on the EventBus
     * <p>
     * Sets the play field visible.
     * The startSessionButton and every readyCheckbox are getting invisible for
     * the lobby members.
     *
     * @param msg The StartSessionMessage found on the EventBus
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-02-04
     */
    @Subscribe
    private void onStartSessionMessage(StartSessionMessage msg) {
        if (!msg.getName().equals(this.lobbyName)) return;
        LOG.debug("Received StartSessionMessage for Lobby " + this.lobbyName);
        Platform.runLater(() -> {
            playField.setVisible(true);
            //This Line needs to be changed/ removed in the Future
            gameRendering.drawGameMap(new GameMapManagement());
            setTurnIndicatorText(msg.getUser());
            lobbyService.updateInventory(lobbyName, loggedInUser);
            this.readyCheckBox.setVisible(false);
            this.startSession.setVisible(false);
            this.rollDice.setVisible(true);
            this.tradeWithUserButton.setVisible(true);
            this.tradeWithBankButton.setVisible(true);
            setRollDiceButtonState(msg.getUser());
        });
    }

    /**
     * Handles an TradeLobbyButtonUpdateEvent found on the EventBus
     * <p>
     * If the TradeLobbyButtonUpdateEvent is intended for the current Lobby
     * the trade With Bank button is disabled. The end turn button gets
     * enabled again.
     *
     * @param event The TradeLobbyButtonUpdateEvent found on the event bus
     *
     * @author Alwin Bossert
     * @author Maximilian Lindner
     * @see de.uol.swp.client.trade.event.TradeLobbyButtonUpdateEvent
     * @since 2021-02-22
     */
    @Subscribe
    public void onTradeLobbyButtonUpdateEvent(TradeLobbyButtonUpdateEvent event) {
        if (super.lobbyName.equals(event.getLobbyName()) && super.loggedInUser.equals(event.getUser())) {
            endTurn.setDisable(false);
        }
    }

    /**
     * Handles a TradeOfUsersAcceptedResponse found on the EventBus
     * Updates the Inventories of the trading User.
     *
     * @param rsp The TradeOfUsersAcceptedResponse found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse
     * @since 2021-02-25
     */
    @Subscribe
    private void onTradeOfUsersAcceptedResponse(TradeOfUsersAcceptedResponse rsp) {
        lobbyService.updateInventory(this.lobbyName, this.loggedInUser);
    }

    /**
     * If a TradeWithBankAcceptedResponse is found on the EventBus,
     * this method calls 2 methods to reset the trade with bank button
     * and the trade with user button for the users in the response.
     *
     * @param rsp TradeWithBankButtonAcceptedResponse found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.response.TradeWithBankAcceptedResponse
     * @since 2021-02-28
     */
    @Subscribe
    private void onTradeWithBankAcceptedResponse(TradeWithBankAcceptedResponse rsp) {
        if (!lobbyName.equals(rsp.getLobbyName())) return;
        setTradeWithUserButtonState(rsp.getUser());
        setTradeWithBankButtonState(rsp.getUser());
    }

    /**
     * Handles a click on the TradeWithBank Button
     * <p>
     * Method called when the TradeWithBankButton is pressed. It posts a
     * ShowTradeWithViewEvent and a TradeWithBankRequest onto the event bus.
     *
     * @author Alwin Bossert
     * @author Maximilian Lindner
     * @see de.uol.swp.client.trade.event.ShowTradeWithBankViewEvent
     * @see de.uol.swp.common.game.request.TradeWithBankRequest
     * @since 2021-02-20
     */
    @FXML
    private void onTradeWithBankButtonPressed() {
        this.tradeWithBankButton.setDisable(true);
        this.endTurn.setDisable(true);
        this.tradeWithUserButton.setDisable(true);
        eventBus.post(new ShowTradeWithBankViewEvent(this.loggedInUser, this.lobbyName));
        LOG.debug("Sending a ShowTradeWithBankViewEvent for Lobby " + this.lobbyName);
        eventBus.post(new TradeWithBankRequest(lobbyName, loggedInUser));
        LOG.debug("Sending a TradeWithBankRequest for Lobby " + this.lobbyName);
    }

    /**
     * Handles a Click on the TradeWithUserButton
     * <p>
     * If another player of the lobby-member-list is selected and the button gets pressed,
     * this button gets disabled, a new ShowTradeWithUserViewEvent is posted onto the
     * EventBus to show the trading window and a TradeWithUserRequest is posted
     * onto the EventBus to get the necessary inventory information.
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.client.trade.event.ShowTradeWithUserViewEvent
     * @see de.uol.swp.common.game.request.TradeWithUserRequest
     * @see de.uol.swp.client.lobby.event.LobbyErrorEvent
     * @since 2021-02-23
     */
    @FXML
    private void onTradeWithUserButtonPressed() {
        membersView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        Pair<Integer, User> selectedUser = membersView.getSelectionModel().getSelectedItem();
        User user = selectedUser.getValue();
        if (membersView.getSelectionModel().isEmpty()) {
            eventBus.post(new TradeErrorEvent(resourceBundle.getString("game.trade.error.noplayer")));
        } else if (selectedUser.getKey() == this.loggedInUser.getID()) {
            eventBus.post(new TradeErrorEvent(resourceBundle.getString("game.trade.error.selfplayer")));
        } else {
            tradeWithUserButton.setDisable(true);
            tradeWithBankButton.setDisable(true);
            endTurn.setDisable(true);
            LOG.debug("Sending ShowTradeWithUserViewEvent");
            eventBus.post(new ShowTradeWithUserViewEvent(this.loggedInUser, this.lobbyName, user.getUsername()));
            LOG.debug("Sending a TradeWithUserRequest for Lobby " + this.lobbyName);
            eventBus.post(new TradeWithUserRequest(this.lobbyName, this.loggedInUser, user.getUsername()));
        }
    }

    /**
     * Handles the TradeWithUserOfferResponse found on the EventBus
     * If a user gets a trading offer a new ShowTradeWithUserRespondViewEvent is posted onto
     * the  EventBus to show the AcceptView.
     *
     * @param rsp The TradeWithUserOfferResponse found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.response.TradeWithUserOfferResponse
     * @since 2021-02-25
     */
    @Subscribe
    private void onTradeWithUserOfferResponse(TradeWithUserOfferResponse rsp) {
        if (!rsp.getLobbyName().equals(this.lobbyName)) return;
        LOG.debug("Sending ShowTradeWithUserRespondViewEvent");
        eventBus.post(new ShowTradeWithUserRespondViewEvent(rsp.getOfferingUser().getUsername(),
                                                            this.loggedInUser.getUsername(), this.lobbyName, rsp));
    }

    /**
     * Handles an UpdateInventoryResponse found on the EventBus
     * <p>
     * If the UpdateInventoryResponse is intended for the current Lobby, the
     * resourceList linked to the inventoryView is cleared and updated with the
     * items as listed in the maps contained in the UpdateInventoryResponse.
     * The item names are localised with the ResourceBundle injected into the
     * LobbyPresenter.
     *
     * @param rsp The UpdateInventoryResponse found on the EventBus
     *
     * @author Finn Haase
     * @author Sven Ahrens
     * @author Phillip-André Suhr
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore, it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.game.response.UpdateInventoryResponse
     * @since 2021-01-27
     */
    @Subscribe
    private void onUpdateInventoryResponse(UpdateInventoryResponse rsp) {
        if (!rsp.getLobbyName().equals(this.lobbyName)) return;
        LOG.debug("Received UpdateInventoryResponse for Lobby " + this.lobbyName);
        Platform.runLater(() -> {
            if (resourceList == null) {
                resourceList = FXCollections.observableArrayList();
                inventoryView.setItems(resourceList);
            }
            resourceList.clear();
            for (Map.Entry<String, Integer> entry : rsp.getResourceMap().entrySet()) {
                resourceList.add(new Pair<>(entry.getKey(), entry.getValue().toString()));
            }
            for (Map.Entry<String, Boolean> entry : rsp.getArmyAndRoadMap().entrySet()) {
                resourceList.add(new Pair<>(entry.getKey(),
                                            entry.getValue() ? resourceBundle.getString("game.property.has") :
                                            resourceBundle.getString("game.property.hasnot")));
            }
        });
    }

    /**
     * Handles new joined users
     * <p>
     * If a new UserJoinedLobbyMessage object is posted onto the EventBus, the name of the newly
     * joined user is appended to the user list in the lobby menu.
     * The state of the "Start Session" button is updated accordingly.
     * Furthermore, if the LOG-Level is set to DEBUG, the message "New user {@literal
     * <Username>} joined Lobby." is displayed in the log.
     *
     * @param msg the UserJoinedLobbyMessage object seen on the EventBus
     *
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-11-22
     */
    @Subscribe
    private void onUserJoinedLobbyMessage(UserJoinedLobbyMessage msg) {
        if (!msg.getName().equals(this.lobbyName)) return;
        LOG.debug("Received UserJoinedLobbyMessage for Lobby " + this.lobbyName);
        User user = msg.getUser();
        LOG.debug("---- User " + user.getUsername() + " joined");
        Pair<Integer, User> pair = new Pair<>(user.getID(), user);
        Platform.runLater(() -> {
            if (lobbyMembers != null && loggedInUser != null && loggedInUser != user && !lobbyMembers.contains(pair))
                lobbyMembers.add(pair);
            setStartSessionButtonState();
        });
    }

    /**
     * Handles users leaving a lobby
     * <p>
     * If a new UserLeftLobbyMessage object is posted onto the EventBus and
     * the leaving user was the lobby owner, the entire list of member is
     * requested from the server to ensure a new owner is decided. If the
     * leaving user wasn't the owner, their name is removed from the list
     * of members. If they were marked as ready, they are removed from the
     * Set of ready users. Afterwards, the "Start Session" button is set to
     * the appropriate state.
     * <p>
     * Furthermore, if the LOG-Level is set to DEBUG, the message "Owner/User
     * {@literal <Username>} left Lobby {@literal <Lobbyname>}" is displayed
     * in the log, depending on whether the owner or a normal user left.
     *
     * @param msg The UserLeftLobbyMessage object seen on the EventBus
     *
     * @author Temmo Junkhoff
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2021-01-20
     */
    @Subscribe
    private void onUserLeftLobbyMessage(UserLeftLobbyMessage msg) {
        if (!msg.getName().equals(this.lobbyName)) return;
        LOG.debug("Received UserLeftLobbyMessage for Lobby " + this.lobbyName);
        User user = msg.getUser();
        if (user.getID() == owner.getID()) {
            LOG.debug("---- Owner " + user.getUsername() + " left");
            lobbyService.retrieveAllLobbyMembers(lobbyName);
        } else LOG.debug("---- User " + user.getUsername() + " left");
        Platform.runLater(() -> {
            lobbyMembers.remove(findMember(user.getID()));
            readyUsers.remove(user);
            setStartSessionButtonState();
        });
    }

    /**
     * Handles the UserReadyMessage
     * <p>
     * If the UserReadyMessage belongs to this lobby, it calls the LobbyService
     * to retrieve all lobby members, which will also mark all ready users as
     * such.
     *
     * @param msg The UserReadyMessage found on the EventBus
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-19
     */
    @Subscribe
    private void onUserReadyMessage(UserReadyMessage msg) {
        if (!msg.getName().equals(this.lobbyName)) return;
        LOG.debug("Received UserReadyMessage for Lobby " + this.lobbyName);
        lobbyService.retrieveAllLobbyMembers(this.lobbyName); // for updateUserList
    }

    /**
     * Helper function that sets the disable state of the endTurnButton.
     * <p>
     * The button is only enabled to the active player when the
     * obligatory part of the turn is done.
     *
     * @author Alwin Bossert
     * @author Mario Fokken
     * @author Marvin Drees
     * @since 2021-01-23
     */
    private void setEndTurnButtonState(User player) {
        this.endTurn.setDisable(!super.loggedInUser.equals(player));
    }

    /**
     * Helper function that sets the disable state of the rollDiceButton
     *
     * @author Sven Ahrens
     * @author Mario Fokken
     * @since 2021-02-22
     */
    private void setRollDiceButtonState(User player) {
        this.rollDice.setDisable(!super.loggedInUser.equals(player));
    }

    /**
     * Helper function that sets the Visible and Disable states of the "Start
     * Session" button.
     * <p>
     * The button is only ever visible to the lobby owner, and is only enabled
     * if there are 3 or more lobby members, and all members are marked as ready.
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-20
     */
    private void setStartSessionButtonState() {
        if (super.loggedInUser.equals(this.owner)) {
            this.startSession.setVisible(true);
            this.startSession
                    .setDisable(this.readyUsers.size() < 3 || this.lobbyMembers.size() != this.readyUsers.size());
        } else {
            this.startSession.setDisable(true);
            this.startSession.setVisible(false);
        }
    }

    /**
     * Helper function that sets the Visible and Disable states of the "Trade
     * With Bank" button.
     * <p>
     * The button is only visible if the logged in user is the player.
     *
     * @author Alwin Bossert
     * @author Maximilian Lindner
     * @since 2021-02-21
     */
    private void setTradeWithBankButtonState(User player) {
        this.tradeWithBankButton.setDisable(!super.loggedInUser.equals(player));
    }

    /**
     * Helper function that sets the Visible and Disable states of the "Trade
     * With User" button.
     * <p>
     * The button is only visible if the logged in user is the player.
     *
     * @author Finn Haase
     * @author Maximilian Lindner
     * @since 2021-02-21
     */
    private void setTradeWithUserButtonState(User player) {
        this.tradeWithUserButton.setDisable(!super.loggedInUser.equals(player));
    }

    /**
     * Helper function that sets the text's text.
     * <p>
     * The text states whose turn it is.
     *
     * @author Alwin Bossert
     * @author Mario Fokken
     * @author Marvin Drees
     * @since 2021-01-23
     */
    private void setTurnIndicatorText(User player) {
        Platform.runLater(() -> turnIndicator.setText(
                String.format(resourceBundle.getString("lobby.game.text.turnindicator"), player.getUsername())));
    }

    /**
     * Updates the lobby's member list according to the list given
     * <p>
     * This method clears the entire member list and then adds the name of each user
     * in the list given to the lobby's member list.
     * If there is no member list, it creates one.
     * <p>
     * If a user is marked as ready in the readyUsers Set, their name is prepended
     * with a checkmark.
     * If the owner is found amongst the users, their username is appended with a
     * crown symbol.
     *
     * @param userLobbyList A list of User objects including all currently logged in
     *                      users
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore, it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.user.User
     * @since 2021-01-05
     */
    private void updateUsersList(List<User> userLobbyList) {
        Platform.runLater(() -> {
            if (lobbyMembers == null) {
                lobbyMembers = FXCollections.observableArrayList();
                membersView.setItems(lobbyMembers);
            }
            lobbyMembers.clear();
            userLobbyList.forEach(u -> lobbyMembers.add(new Pair<>(u.getID(), u)));
        });
    }
}
