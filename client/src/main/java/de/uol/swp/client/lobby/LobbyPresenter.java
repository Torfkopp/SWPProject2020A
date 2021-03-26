package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.GameRendering;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.chat.response.SystemMessageResponse;
import de.uol.swp.common.game.map.GameMap;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.lobby.response.KickUserResponse;
import de.uol.swp.common.lobby.response.RemoveFromLobbiesResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.user.response.ChangeAccountDetailsSuccessfulResponse;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Manages the lobby's menu
 *
 * @see de.uol.swp.client.AbstractPresenter
 * @see de.uol.swp.client.AbstractPresenterWithChat
 * @since 2020-11-21
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyPresenter extends AbstractPresenterWithChatWithGame {

    public static final String fxml = "/fxml/LobbyView.fxml";
    public static final int LOBBY_HEIGHT_PRE_GAME = 700;
    public static final int LOBBY_WIDTH_PRE_GAME = 535;
    public static final int LOBBY_HEIGHT_IN_GAME = 740;
    public static final int LOBBY_WIDTH_IN_GAME = 1285;
    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<UserOrDummy> lobbyMembers;
    private Set<UserOrDummy> readyUsers;
    @FXML
    private CheckBox readyCheckBox;
    @FXML
    private Button kickUserButton;
    @FXML
    private Button startSession;
    @FXML
    private Label moveTimeLabel;
    @FXML
    private VBox preGameSettingBox;
    @FXML
    private TextField moveTimeTextField;
    @FXML
    private ToggleGroup maxPlayersToggleGroup;
    @FXML
    private RadioButton threePlayerRadioButton;
    @FXML
    private RadioButton fourPlayerRadioButton;
    @FXML
    private CheckBox setStartUpPhaseCheckBox;
    @FXML
    private CheckBox randomPlayFieldCheckbox;
    @FXML
    private CheckBox commandsActivated;
    @FXML
    private Button changeMoveTimeButton;

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
     * Initialises the Presenter by setting up the membersView, the
     * inventory view, the kickUserButton and the tradeWithUserButton.
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
            protected void updateItem(UserOrDummy user, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(user, empty);
                    if (empty || user == null) setText("");
                    else {
                        String name = user.getUsername();
                        if (readyUsers.contains(user))
                            name = String.format(resourceBundle.getString("lobby.members.ready"), name);
                        if (user.equals(owner))
                            name = String.format(resourceBundle.getString("lobby.members.owner"), name);
                        setText(name);
                        //if the background should be in colour you need to use setBackground
                        int i = lobbyMembers.size();
                        if (i >= 1 && user.equals(lobbyMembers.get(0))) {
                            setTextFill(GameRendering.PLAYER_1_COLOUR);
                        }
                        if (i >= 2 && user.equals(lobbyMembers.get(1))) {
                            setTextFill(GameRendering.PLAYER_2_COLOUR);
                        }
                        if (i >= 3 && user.equals(lobbyMembers.get(2))) {
                            setTextFill(GameRendering.PLAYER_3_COLOUR);
                        }
                        if (i >= 4 && user.equals(lobbyMembers.get(3))) {
                            setTextFill(GameRendering.PLAYER_4_COLOUR);
                        }
                    }
                });
            }
        });
        membersView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            String name = newValue.getUsername();
            boolean isSelf = newValue.equals(this.loggedInUser);
            kickUserButton.setDisable(isSelf);
            tradeWithUserButton.setDisable(isSelf);
            if (isSelf) {
                kickUserButton.setText(String.format(resourceBundle.getString("lobby.buttons.kickuser"), ""));
                tradeWithUserButton.setText(resourceBundle.getString("lobby.game.buttons.playertrade.noneselected"));
            } else {
                kickUserButton.setText(String.format(resourceBundle.getString("lobby.buttons.kickuser"), name));
                tradeWithUserButton
                        .setText(String.format(resourceBundle.getString("lobby.game.buttons.playertrade"), name));
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
        UnaryOperator<TextFormatter.Change> integerFilter = (s) ->
                s.getText().matches("\\d") || s.isDeleted() || s.getText().equals("") ? s : null;
        moveTimeTextField.setTextFormatter(new TextFormatter<>(integerFilter));
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
    protected void onChangeAccountDetailsSuccessfulResponse(ChangeAccountDetailsSuccessfulResponse rsp) {
        super.onChangeAccountDetailsSuccessfulResponse(rsp);
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

    @Override
    @Subscribe
    protected void onSystemMessageResponse(SystemMessageResponse rsp) {
        LOG.debug("Received SystemMessageResponse");
        if (rsp.isLobbyChatMessage() && rsp.getLobbyName().equals(super.lobbyName)) {
            super.onSystemMessageResponse(rsp);
        }
    }

    /**
     * Helper function to let the user leave the lobby and close the window
     * Also clears the EventBus of the instance to avoid NullPointerExceptions.
     *
     * @param kicked Whether the user was kicked (true) or is leaving
     *               voluntarily (false)
     *
     * @author Temmo Junkhoff
     * @since 2021-01-06
     */
    private void closeWindow(boolean kicked) {
        if (lobbyName != null || loggedInUser != null || !kicked) {
            lobbyService.leaveLobby(lobbyName, loggedInUser);
        }
        ((Stage) window).close();
        clearEventBus();
    }

    /**
     * Helper method to handle disabling the endTurn, rollDice, playCard,
     * tradeWithBank, and tradeWithUser buttons after a turn was ended (either
     * forcibly or voluntarily).
     * Also calls on the LobbyService to update the player's inventory.
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @since 2021-03-07
     */
    private void disableButtonsAfterTurn() {
        this.endTurn.setDisable(true);
        this.rollDice.setDisable(true);
        this.playCard.setDisable(true);
        this.tradeWithBankButton.setDisable(true);
        this.tradeWithUserButton.setDisable(true);
        lobbyService.updateInventory(lobbyName, loggedInUser);
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
        if (!this.lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received AllLobbyMembersResponse");
        LOG.debug("---- Update of lobby member list");
        LOG.debug("---- Owner of this lobby: " + rsp.getOwner().getUsername());
        LOG.debug("---- Update of ready users");
        this.owner = rsp.getOwner();
        this.readyUsers = rsp.getReadyUsers();
        updateUsersList(rsp.getUsers());
        Platform.runLater(() -> {
            setStartSessionButtonState();
            setKickUserButtonState();
            setPreGameSettings();
        });
    }

    /**
     * Method called when the KickUserButton is pressed
     * <p>
     * If the EndTurnButton is pressed, this method requests to kick
     * the selected User of the members view.
     *
     * @author Maximilian Lindner
     * @author Sven Ahrens
     * @see de.uol.swp.common.lobby.request.KickUserRequest
     * @since 2021-03-02
     */
    @FXML
    private void onKickUserButtonPressed() {
        membersView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        UserOrDummy selectedUser = membersView.getSelectionModel().getSelectedItem();
        if (selectedUser == loggedInUser) return;
        eventBus.post(new KickUserRequest(lobbyName, loggedInUser, selectedUser));
    }

    /**
     * Handles a KickUserResponse found on the EventBus
     * <p>
     * If a KickUserResponse is detected on the EventBus and its
     * directed to this lobby and this player, the according lobby
     * window is closed.
     *
     * @param rsp The KickUserResponse found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Sven Ahrens
     * @see de.uol.swp.common.lobby.response.KickUserResponse
     * @since 2021-03-02
     */
    @Subscribe
    private void onKickUserResponse(KickUserResponse rsp) {
        if (lobbyName.equals(rsp.getLobbyName()) && loggedInUser.equals(rsp.getToBeKickedUser())) {
            Platform.runLater(() -> closeWindow(true));
        }
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
        closeWindow(false);
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
     * It also sets the pre-game Setting according to the Lobby.
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
        if (lobbyName == null || loggedInUser == null) {
            lobbyName = event.getLobbyName();
            loggedInUser = (User) event.getUser();
            chatService.askLatestMessages(10, lobbyName);
        }
        if (window == null) {
            window = membersView.getScene().getWindow();
        }
        if (readyUsers == null) {
            readyUsers = new HashSet<>();
        }
        this.window.setOnCloseRequest(windowEvent -> closeWindow(false));
        kickUserButton.setText(String.format(resourceBundle.getString("lobby.buttons.kickuser"), ""));
        tradeWithUserButton.setText(resourceBundle.getString("lobby.game.buttons.playertrade.noneselected"));

        addSizeChangeListener();
        //just to trigger the heightProperty ChangeListener and make the canvas have actual dimensions
        window.setHeight(window.getHeight() + 0.01);
        window.setHeight(window.getHeight() - 0.01);

        lobbyService.retrieveAllLobbyMembers(lobbyName);
        setAllowedPlayers(event.getLobby().getMaxPlayers());
        commandsActivated.setSelected(event.getLobby().commandsAllowed());
        randomPlayFieldCheckbox.setSelected(event.getLobby().randomPlayfieldEnabled());
        setStartUpPhaseCheckBox.setSelected(event.getLobby().startUpPhaseEnabled());
        moveTime = event.getLobby().getMoveTime();
        moveTimeLabel.setText(String.format(resourceBundle.getString("lobby.labels.movetime"), moveTime));
        moveTimeTextField.setText(String.valueOf(moveTime));
        setPreGameSettings();
    }

    private void addSizeChangeListener() {
        ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
            double hexFactor = 10.0 / 11.0; // <~0.91 (ratio of tiled hexagons (less high than wide))
            double heightValue = (gameMapCanvas.getScene().getWindow().getHeight() - 60) / hexFactor;
            double widthValue = gameMapCanvas.getScene().getWindow().getWidth() - 535;
            double dimension = Math.min(heightValue, widthValue);
            gameMapCanvas.setHeight(dimension * hexFactor);
            gameMapCanvas.setWidth(dimension);
            if (gameMap == null) return;
            // gameMap exists, so redraw map to fit the new canvas dimensions
            gameRendering = new GameRendering(gameMapCanvas);
            gameMapCanvas.getGraphicsContext2D().clearRect(0, 0, gameMapCanvas.getWidth(), gameMapCanvas.getHeight());
            gameRendering.drawGameMap(gameMap);
            if (dice1 != null && dice2 != null) gameRendering.drawDice(dice1, dice2);
        };
        window.widthProperty().addListener(listener);
        window.heightProperty().addListener(listener);
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
        lobbyService.userReady(lobbyName, loggedInUser, isReady);
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
        lobbyService.startSession(lobbyName, loggedInUser);
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
        if (!msg.getName().equals(lobbyName)) return;
        LOG.debug("Received StartSessionMessage for Lobby " + lobbyName);
        Platform.runLater(() -> {
            preGameSettingBox.setVisible(false);
            preGameSettingBox.setPrefHeight(0);
            preGameSettingBox.setMaxHeight(0);
            preGameSettingBox.setMinHeight(0);
            //This Line needs to be changed/ removed in the Future
            gameRendering = new GameRendering(gameMapCanvas);
            gameMap = new GameMap();
            gameMap.createBeginnerMap();
            gameRendering.drawGameMap(gameMap);
            setTurnIndicatorText(msg.getUser());
            lobbyService.updateInventory(lobbyName, loggedInUser);
            window.setWidth(LOBBY_WIDTH_IN_GAME);
            window.setHeight(LOBBY_HEIGHT_IN_GAME);
            ((Stage) window).setMinWidth(LOBBY_WIDTH_IN_GAME);
            ((Stage) window).setMinHeight(LOBBY_HEIGHT_IN_GAME);
            inventoryView.setMaxHeight(280);
            inventoryView.setMinHeight(280);
            inventoryView.setPrefHeight(280);
            inventoryView.setVisible(true);
            readyCheckBox.setVisible(false);
            startSession.setVisible(false);
            rollDice.setVisible(true);
            endTurn.setVisible(true);
            tradeWithUserButton.setVisible(true);
            tradeWithUserButton.setDisable(true);
            tradeWithBankButton.setVisible(true);
            setRollDiceButtonState(msg.getUser());
            kickUserButton.setVisible(false);
            playCard.setVisible(true);
        });
    }

    /**
     * Handles a click on the StartUpPhase-CheckBox to allow or disable the
     * start up phase in the specific lobby.
     * <p>
     * By enabling this option the start up phase will start after the game started.
     * Otherwise the first building will be created by its own.
     * Calls the prepareLobbyUpdate Method to send the new settings to the server.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    @FXML
    private void onStartUpPhasePressed() {
        prepareLobbyUpdate();
    }

    /**
     * Handles a click on the ThreePlayers-RadioButton to restrict the lobby to
     * three players.
     * <p>
     * Calls the prepareLobbyUpdate Method to send the new settings to the server.
     *
     * @author Maximilian Lindner
     * @author Aldin Devisi
     * @since 2021-03-15
     */
    @FXML
    private void onThreePlayersRadioButtonSelected() {
        prepareLobbyUpdate();
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
    private void onTradeLobbyButtonUpdateEvent(TradeLobbyButtonUpdateEvent event) {
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
        setPlayCardButtonState(rsp.getUser());
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
        this.playCard.setDisable(true);
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
        UserOrDummy user = membersView.getSelectionModel().getSelectedItem();
        if (membersView.getSelectionModel().isEmpty() || user == null) {
            eventBus.post(new TradeErrorEvent(resourceBundle.getString("game.trade.error.noplayer")));
        } else if (Objects.equals(user, this.loggedInUser)) {
            eventBus.post(new TradeErrorEvent(resourceBundle.getString("game.trade.error.selfplayer")));
        } else {
            tradeWithUserButton.setDisable(true);
            tradeWithBankButton.setDisable(true);
            playCard.setDisable(true);
            endTurn.setDisable(true);
            LOG.debug("Sending ShowTradeWithUserViewEvent");
            eventBus.post(new ShowTradeWithUserViewEvent(this.loggedInUser, this.lobbyName, user));
            LOG.debug("Sending a TradeWithUserRequest for Lobby " + this.lobbyName);
            eventBus.post(new TradeWithUserRequest(this.lobbyName, this.loggedInUser, user));
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
        eventBus.post(
                new ShowTradeWithUserRespondViewEvent(rsp.getOfferingUser(), this.loggedInUser, this.lobbyName, rsp));
    }

    /**
     * Handles a TurnSkippedResponse found on the EventBus
     * <p>
     * This method calls {@link #disableButtonsAfterTurn()} to make sure all
     * buttons that a player would have access to when it is their turn are
     * properly disabled even though the player's turn was forcibly skipped.
     *
     * @param rsp The TurnSkippedResponse found on the EventBus
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @since 2021-03-07
     */
    @Subscribe
    private void onTurnSkippedResponse(TurnSkippedResponse rsp) {
        if (!this.lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received TurnSkippedResponse");
        disableButtonsAfterTurn();
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
     * Handles an UpdateLobbyMessage found on the EventBus
     * <p>
     * If an UpdateLobbyMessage is found on the EventBus, this method applies
     * the settings contained therein to this lobby and updates the different
     * TextFields, CheckBoxes, and RadioButtons accordingly.
     *
     * @param msg The UpdateLobbyMessage found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @see de.uol.swp.common.lobby.message.UpdateLobbyMessage
     * @since 2021-03-14
     */
    @Subscribe
    private void onUpdateLobbyMessage(UpdateLobbyMessage msg) {
        LOG.debug("Received a AllowedAmountOfPlayersMessage");
        if (!lobbyName.equals(msg.getName())) return;
        setAllowedPlayers(msg.getLobby().getMaxPlayers() == 3 ? 3 : 4);
        setStartUpPhaseCheckBox.setSelected(msg.getLobby().startUpPhaseEnabled());
        randomPlayFieldCheckbox.setSelected(msg.getLobby().randomPlayfieldEnabled());
        commandsActivated.setSelected(msg.getLobby().commandsAllowed());
        moveTimeTextField.setText(String.valueOf(msg.getLobby().getMoveTime()));
        moveTime = msg.getLobby().getMoveTime();
        Platform.runLater(() -> moveTimeLabel
                .setText(String.format(resourceBundle.getString("lobby.labels.movetime"), moveTime)));
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
        if (!msg.getName().equals(lobbyName)) return;
        LOG.debug("Received UserJoinedLobbyMessage for Lobby " + lobbyName);
        UserOrDummy user = msg.getUser();
        LOG.debug("---- User " + user.getUsername() + " joined");
        Platform.runLater(() -> {
            if (lobbyMembers != null && loggedInUser != null && loggedInUser != user && !lobbyMembers.contains(user))
                lobbyMembers.add(user);
            setStartSessionButtonState();
            setPreGameSettings();
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
        LOG.debug("Received UserLeftLobbyMessage for Lobby " + lobbyName);
        UserOrDummy user = msg.getUser();
        if (Objects.equals(user, owner)) {
            LOG.debug("---- Owner " + user.getUsername() + " left");
        } else LOG.debug("---- User " + user.getUsername() + " left");
        Platform.runLater(() -> {
            lobbyMembers.remove(user);
            readyUsers.remove(user);
            setStartSessionButtonState();
            setPreGameSettings();
        });
        lobbyService.retrieveAllLobbyMembers(lobbyName);
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
        if (!msg.getName().equals(lobbyName)) return;
        LOG.debug("Received UserReadyMessage for Lobby " + lobbyName);
        lobbyService.retrieveAllLobbyMembers(lobbyName); // for updateUserList
    }

    /**
     * Method to handle the overall update of the LobbySettings in the pre-game menu
     * <p>
     * This method takes the content of all the specific CheckBoxes, RadioButtons, and TextFields
     * for the pre-game settings which the Owner selected, and calls the updateLobbySettings
     * method of the LobbyService.
     *
     * @author Maximilian Lindner
     * @author Aldin Devisi
     * @since 2021-03-15
     */
    @FXML
    private void prepareLobbyUpdate() {
        if (!loggedInUser.equals(owner)) return;
        int moveTime =
                !moveTimeTextField.getText().equals("") ? Integer.parseInt(moveTimeTextField.getText()) : this.moveTime;
        int maxPlayers = maxPlayersToggleGroup.getSelectedToggle() == threePlayerRadioButton ? 3 : 4;
        lobbyService.updateLobbySettings(lobbyName, loggedInUser, maxPlayers, setStartUpPhaseCheckBox.isSelected(),
                                         commandsActivated.isSelected(), moveTime,
                                         randomPlayFieldCheckbox.isSelected());
    }

    /**
     * Helper method to set the allowed players RadioButton
     * according to the allowed players attribute of the lobby.
     *
     * @param allowedPlayers The maximum amount of players for a lobby.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    private void setAllowedPlayers(int allowedPlayers) {
        threePlayerRadioButton.setSelected(allowedPlayers == 3);
        fourPlayerRadioButton.setSelected(allowedPlayers == 4);
    }

    /**
     * Helper function that sets the disable state of the endTurnButton.
     * <p>
     * The button is only enabled to the active player when the
     * obligatory part of the turn is done.
     *
     * @param player
     *
     * @author Alwin Bossert
     * @author Mario Fokken
     * @author Marvin Drees
     * @since 2021-01-23
     */
    private void setEndTurnButtonState(UserOrDummy player) {
        this.endTurn.setDisable(!super.loggedInUser.equals(player));
    }

    /**
     * Helper function that sets the disable and visible state of the kickUserButton.
     * <p>
     * The button is only enabled the lobby owner when a game
     * has not started yet and if the logged in user is the
     * owner
     *
     * @author Maximilian Lindner
     * @author Sven Ahrens
     * @since 2021-03-03
     */
    private void setKickUserButtonState() {
        Platform.runLater(() -> {
            kickUserButton.setVisible(loggedInUser.equals(owner));
            kickUserButton.setDisable(loggedInUser.equals(owner));
        });
    }

    /**
     * Helper function that sets the disable state of the PlayCardButton
     * The button is only enabled to the active player
     *
     * @param player
     *
     * @author Mario Fokken
     * @since 2021-02-25
     */
    private void setPlayCardButtonState(UserOrDummy player) {
        this.playCard.setDisable(!super.loggedInUser.equals(player));
    }

    /**
     * Helper method to disable pre-game Buttons and Checkboxes
     * for everyone, expect the owner.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    private void setPreGameSettings() {
        moveTimeTextField.setDisable(!loggedInUser.equals(owner));
        changeMoveTimeButton.setDisable(!loggedInUser.equals(owner));
        setStartUpPhaseCheckBox.setDisable(!loggedInUser.equals(owner));
        commandsActivated.setDisable(!loggedInUser.equals(owner));
        randomPlayFieldCheckbox.setDisable(!loggedInUser.equals(owner));
        fourPlayerRadioButton.setDisable(!loggedInUser.equals(owner));
        threePlayerRadioButton.setDisable(!loggedInUser.equals(owner) || lobbyMembers.size() == 4);
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
        if (loggedInUser.equals(owner)) {
            startSession.setVisible(true);
            startSession.setDisable(readyUsers.size() < 3 || lobbyMembers.size() != readyUsers.size());
        } else {
            startSession.setDisable(true);
            startSession.setVisible(false);
        }
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
     * @see de.uol.swp.common.user.UserOrDummy
     * @since 2021-01-05
     */
    private void updateUsersList(List<UserOrDummy> userLobbyList) {
        Platform.runLater(() -> {
            if (lobbyMembers == null) {
                lobbyMembers = FXCollections.observableArrayList();
                membersView.setItems(lobbyMembers);
            }
            lobbyMembers.clear();
            lobbyMembers.addAll(userLobbyList);
        });
    }
}
