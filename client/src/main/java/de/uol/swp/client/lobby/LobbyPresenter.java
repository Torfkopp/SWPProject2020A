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
import de.uol.swp.common.chat.response.SystemMessageResponse;
import de.uol.swp.common.game.map.GameMap;
import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.game.map.IGameMap;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.message.DiceCastMessage;
import de.uol.swp.common.game.message.NextPlayerMessage;
import de.uol.swp.common.game.request.TradeWithBankRequest;
import de.uol.swp.common.game.request.TradeWithUserRequest;
import de.uol.swp.common.game.response.*;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.KickUserRequest;
import de.uol.swp.common.lobby.request.StartSessionRequest;
import de.uol.swp.common.lobby.request.UserReadyRequest;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.lobby.response.KickUserResponse;
import de.uol.swp.common.lobby.response.RemoveFromLobbiesResponse;
import de.uol.swp.common.message.RequestMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.response.ChangeAccountDetailsSuccessfulResponse;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.UnaryOperator;

import static de.uol.swp.common.game.map.MapPoint.Type.*;

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
    public static final int LOBBY_HEIGHT_PRE_GAME = 700;
    public static final int LOBBY_WIDTH_PRE_GAME = 535;
    public static final int LOBBY_HEIGHT_IN_GAME = 740;
    public static final int LOBBY_WIDTH_IN_GAME = 1285;
    private static final CloseLobbiesViewEvent closeLobbiesViewEvent = new CloseLobbiesViewEvent();
    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<Pair<Integer, User>> lobbyMembers;
    private ObservableList<Pair<String, String>> resourceList;
    private User owner;
    private Set<User> readyUsers;
    private Integer dice1;
    private Integer dice2;
    @FXML
    private ListView<Pair<Integer, User>> membersView;
    @FXML
    private CheckBox readyCheckBox;
    @FXML
    private Button kickUserButton;
    @FXML
    private Button startSession;
    @FXML
    private Button rollDice;
    @FXML
    private Button endTurn;
    @FXML
    private Button playCard;
    @FXML
    private Button tradeWithUserButton;
    @FXML
    private Button tradeWithBankButton;
    @FXML
    private Label turnIndicator;
    @FXML
    private Label moveTimeLabel;
    @FXML
    private Canvas gameMapCanvas;
    @FXML
    private ListView<Pair<String, String>> inventoryView;
    @FXML
    private VBox preGameSettingBox;
    @FXML
    private TextField moveTimeTextfield;
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

    private IGameMap gameMap;
    private int moveTime;
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
                        //if the background should be in colour you need to use setBackground
                        int i = lobbyMembers.size();
                        if (i >= 1 && getText().contains(lobbyMembers.get(0).getValue().getUsername())) {
                            setTextFill(GameRendering.PLAYER_1_COLOUR);
                        }
                        if (i >= 2 && getText().contains(lobbyMembers.get(1).getValue().getUsername())) {
                            setTextFill(GameRendering.PLAYER_2_COLOUR);
                        }
                        if (i >= 3 && getText().contains(lobbyMembers.get(2).getValue().getUsername())) {
                            setTextFill(GameRendering.PLAYER_3_COLOUR);
                        }
                        if (i >= 4 && getText().contains(lobbyMembers.get(3).getValue().getUsername())) {
                            setTextFill(GameRendering.PLAYER_4_COLOUR);
                        }
                    }
                });
            }
        });
        membersView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            String name = newValue.getValue().getUsername();
            boolean isSelf = newValue.getValue().equals(this.loggedInUser);
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
        moveTimeTextfield.setTextFormatter(new TextFormatter<>(integerFilter));
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

    public void onEnter(ActionEvent actionEvent) {
        super.onSendMessageButtonPressed();
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
        if (!this.lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received AllLobbyMembersResponse");
        LOG.debug("---- Update of lobby member list");
        LOG.debug("---- Owner of this lobby: " + rsp.getOwner().getUsername());
        LOG.debug("---- Update of ready users");
        this.owner = rsp.getOwner();
        if (this.readyUsers == null) this.readyUsers = new HashSet<>();
        this.readyUsers.addAll(rsp.getReadyUsers());
        Platform.runLater(() -> {
            updateUsersList(rsp.getUsers());
            setStartSessionButtonState();
            setKickUserButtonState();
            setPreGameSettings();
        });
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
        setPlayCardButtonState(rsp.getUser());
    }

    /**
     * Handles a click on the change-move-time Button which allows the ChangeMove
     * Timer to be edited. Default: 60s
     * <p>
     * A TextField allows to edit the timer, which restricts the time frame between each Move.
     * It can be set to any integer value.
     *
     * @author Maximilian Lindner
     * @author AldinDervisi
     * @since 2021-03-14
     */
    @FXML
    private void onChangeMoveTimeButtonPressed() {
        prepareLobbyUpdate();
    }

    /**
     * Handles a click on the Commands-Activated-ChechBox to allow or disable commands in
     * the specific lobby.
     * <p>
     * By enabling this option, players will be able to use quick commands in the
     * chat to get access to specific in-game methods.
     * Calls the prepareLobbyUpdate Method to send the new settings to the server.
     *
     * @author Maximilian Lindner
     * @author AldinDervisi
     * @since 2021-03-14
     */
    @FXML
    private void onCommandsActivatedPressed() {
        prepareLobbyUpdate();
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
        if (!this.lobbyName.equals(msg.getLobbyName())) return;
        LOG.debug("Received DiceCastMessage");
        LOG.debug("---- The dices show: " + msg.getDice1() + " and " + msg.getDice2());
        setEndTurnButtonState(msg.getUser());
        setTradeWithBankButtonState(msg.getUser());
        setTradeWithUserButtonState(msg.getUser());
        setPlayCardButtonState(msg.getUser());
        this.dice1 = msg.getDice1();
        this.dice2 = msg.getDice2();
        gameRendering.drawDice(msg.getDice1(), msg.getDice2());
        lobbyService.updateInventory(lobbyName, loggedInUser);
    }

    /**
     * Method called when the EndTurnButton is pressed
     * <p>
     * If the EndTurnButton is pressed, this method disables all appropriate
     * buttons and then requests the LobbyService to end the current turn.
     *
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2021-01-15
     */
    @FXML
    private void onEndTurnButtonPressed() {
        disableButtonsAfterTurn();
        lobbyService.endTurn(loggedInUser, lobbyName);
    }

    /**
     * Handles a click on the fourPlayers-RadioButton.
     * <p>
     * By having this Box unchecked, the Button will
     * automatically switch the Lobby to have three players enabled.
     * Calls the prepareLobbyUpdate Method to send the new settings to the server.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-14
     */
    @FXML
    private void onFourPlayersRadioButtonSelected() {
        prepareLobbyUpdate();
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
        Pair<Integer, User> selectedUser = membersView.getSelectionModel().getSelectedItem();
        if ((selectedUser.getValue()) == this.loggedInUser) return;
        eventBus.post(new KickUserRequest(lobbyName, this.loggedInUser, selectedUser.getValue().getUsername()));
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
        if (lobbyName.equals(rsp.getLobbyName()) && this.loggedInUser.equals(rsp.getToBeKickedUser())) {
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
        this.window.setOnCloseRequest(windowEvent -> closeWindow(false));
        kickUserButton.setText(String.format(resourceBundle.getString("lobby.buttons.kickuser"), ""));
        tradeWithUserButton.setText(resourceBundle.getString("lobby.game.buttons.playertrade.noneselected"));

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
        //just to trigger the heightProperty ChangeListener and make the canvas have actual dimensions
        window.setHeight(window.getHeight() + 0.01);
        window.setHeight(window.getHeight() - 0.01);

        lobbyService.retrieveAllLobbyMembers(this.lobbyName);
        setAllowedPlayers(event.getLobby().getMaxPlayers());
        this.commandsActivated.setSelected(event.getLobby().commandsAllowed());
        this.randomPlayFieldCheckbox.setSelected(event.getLobby().randomPlayfieldEnabled());
        this.setStartUpPhaseCheckBox.setSelected(event.getLobby().startUpPhaseEnabled());
        this.moveTime = event.getLobby().getMoveTime();
        this.moveTimeLabel.setText(String.format(resourceBundle.getString("lobby.labels.movetime"), this.moveTime));
        this.moveTimeTextfield.setText(String.valueOf(this.moveTime));
        setPreGameSettings();
    }

    /**
     * Handles a click on the gameMapCanvas
     * <p>
     * This method calls on the GameRendering to map the x,y coordinates of the
     * mouse click to the proper element located in that location, e.g. a Hex.
     *
     * @param mouseEvent The Event produced by the mouse clicking on the Canvas
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.GameRendering#coordinatesToHex(double, double)
     * @since 2021-03-14
     */
    @FXML
    private void onMouseClickedOnCanvas(MouseEvent mouseEvent) {
        // TODO: extend documentation to final implementation behaviour
        MapPoint mapPoint = gameRendering.coordinatesToHex(mouseEvent.getX(), mouseEvent.getY());
        if (mapPoint.getType() == INVALID) {
            System.out.println("INVALID");
        } else if (mapPoint.getType() == HEX) {
            System.out.println("HEX");
            System.out.println("mapPoint.getY() = " + mapPoint.getY());
            System.out.println("mapPoint.getX() = " + mapPoint.getX());
        } else if (mapPoint.getType() == INTERSECTION) {
            System.out.println("INTERSECTION");
            System.out.println("mapPoint.getY() = " + mapPoint.getY());
            System.out.println("mapPoint.getX() = " + mapPoint.getX());
        } else if (mapPoint.getType() == EDGE) {
            System.out.println("EDGE");
            System.out.println("left:");
            System.out.println("mapPoint.getL().getY() = " + mapPoint.getL().getY());
            System.out.println("mapPoint.getL().getX() = " + mapPoint.getL().getX());
            System.out.println("right:");
            System.out.println("mapPoint.getR().getY() = " + mapPoint.getR().getY());
            System.out.println("mapPoint.getR().getX() = " + mapPoint.getR().getX());
        }
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
     * Handles a click on the PlayCardButton
     * <p>
     * Method called when the PlayCardButton is pushed
     * It opens a dialogue to allow the player to choose
     * which card is to be played.
     *
     * @author Eric Vuong
     * @author Mario Fokken
     * @since 2021-02-25
     */
    @FXML
    private void onPlayCardButtonPressed() {
        //Create a new alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("game.playcards.alert.title"));
        alert.setHeaderText(resourceBundle.getString("game.playcards.alert.header"));
        alert.setContentText(resourceBundle.getString("game.playcards.alert.content"));
        //Create the buttons
        ButtonType bKnight = new ButtonType(resourceBundle.getString("game.resources.cards.knight"));
        ButtonType bMonopoly = new ButtonType(resourceBundle.getString("game.resources.cards.monopoly"));
        ButtonType bRoadBuilding = new ButtonType(resourceBundle.getString("game.resources.cards.roadbuilding"));
        ButtonType bYearOfPlenty = new ButtonType(resourceBundle.getString("game.resources.cards.yearofplenty"));
        ButtonType bCancel = new ButtonType(resourceBundle.getString("button.cancel"),
                                            ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(bKnight, bMonopoly, bRoadBuilding, bYearOfPlenty, bCancel);
        //Show the dialogue and get the result
        Optional<ButtonType> result = alert.showAndWait();
        //Create Strings based on the languages name for the resources
        String ore = resourceBundle.getString("game.resources.ore");
        String grain = resourceBundle.getString("game.resources.grain");
        String brick = resourceBundle.getString("game.resources.brick");
        String lumber = resourceBundle.getString("game.resources.lumber");
        String wool = resourceBundle.getString("game.resources.wool");
        //Make a list with aforementioned Strings
        List<String> choices = new ArrayList<>();
        choices.add(ore);
        choices.add(grain);
        choices.add(brick);
        choices.add(lumber);
        choices.add(wool);
        //Result is the button the user has clicked on
        if (result.isEmpty()) return;
        if (result.get() == bKnight) { //Play a Knight Card
            lobbyService.playKnightCard(lobbyName, loggedInUser);
        } else if (result.get() == bMonopoly) { //Play a Monopoly Card
            //Creating a dialogue
            ChoiceDialog<String> dialogue = new ChoiceDialog<>(brick, choices);
            dialogue.setTitle(resourceBundle.getString("game.playcards.monopoly.title"));
            dialogue.setHeaderText(resourceBundle.getString("game.playcards.monopoly.header"));
            dialogue.setContentText(resourceBundle.getString("game.playcards.monopoly.context"));
            //Creating a new DialogPane so the button text can be customised
            DialogPane pane = new DialogPane();
            pane.setContent(dialogue.getDialogPane().getContent());
            ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"),
                                                ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType(resourceBundle.getString("button.cancel"),
                                               ButtonBar.ButtonData.CANCEL_CLOSE);
            dialogue.setDialogPane(pane);
            dialogue.getDialogPane().getButtonTypes().addAll(confirm, cancel);
            //Show the dialogue and get the result
            Optional<String> rst = dialogue.showAndWait();
            //Convert String to Resources and send the request
            Resources resource = Resources.BRICK;
            if (rst.isPresent()) {
                if (rst.get().equals(ore)) resource = Resources.ORE;
                else if (rst.get().equals(grain)) resource = Resources.GRAIN;
                else if (rst.get().equals(lumber)) resource = Resources.LUMBER;
                else if (rst.get().equals(wool)) resource = Resources.WOOL;
                lobbyService.playMonopolyCard(lobbyName, loggedInUser, resource);
            }
        } else if (result.get() == bRoadBuilding) { //Play a Road Building Card
            lobbyService.playRoadBuildingCard(lobbyName, loggedInUser);
        } else if (result.get() == bYearOfPlenty) { //Play a Year Of Plenty Card
            //Create a dialogue
            Dialog<String> dialogue = new Dialog<>();
            dialogue.setTitle(resourceBundle.getString("game.playcards.yearofplenty.title"));
            dialogue.setHeaderText(resourceBundle.getString("game.playcards.yearofplenty.header"));
            //Create its buttons
            ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"),
                                                ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType(resourceBundle.getString("button.cancel"),
                                               ButtonBar.ButtonData.CANCEL_CLOSE);
            dialogue.getDialogPane().getButtonTypes().addAll(confirm, cancel);
            //Make a grid to put the ChoiceBoxes and labels on
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            //Make ChoiceBoxes and the choices
            ChoiceBox<String> c1 = new ChoiceBox<>();
            ChoiceBox<String> c2 = new ChoiceBox<>();
            for (String s : choices) {
                c1.getItems().add(s);
                c2.getItems().add(s);
            }
            //Set which choice is shown first
            c1.setValue(brick);
            c2.setValue(brick);
            //Add ChoiceBoxes and labels to the grid
            grid.add(new Label(resourceBundle.getString("game.playcards.yearofplenty.label1")), 0, 0);
            grid.add(c1, 1, 0);
            grid.add(new Label(resourceBundle.getString("game.playcards.yearofplenty.label2")), 0, 1);
            grid.add(c2, 1, 1);
            //Put the grid into the dialogue and let it appear
            dialogue.getDialogPane().setContent(grid);
            //Get the pressed button
            Optional<String> rst = dialogue.showAndWait();
            Optional<String> button1 = Optional.of(confirm.toString());
            //Checks if the pressed button is the same as the confirm button
            if (rst.toString().equals(button1.toString())) {
                //Create two resource variables
                Resources resource1 = Resources.BRICK;
                Resources resource2 = Resources.BRICK;
                //Convert String to Resource
                if (c1.getValue().equals(ore)) resource1 = Resources.ORE;
                else if (c1.getValue().equals(grain)) resource1 = Resources.GRAIN;
                else if (c1.getValue().equals(lumber)) resource1 = Resources.LUMBER;
                else if (c1.getValue().equals(wool)) resource1 = Resources.WOOL;
                //Second ChoiceBox's conversion
                if (c2.getValue().equals(ore)) resource2 = Resources.ORE;
                else if (c2.getValue().equals(grain)) resource2 = Resources.GRAIN;
                else if (c2.getValue().equals(lumber)) resource2 = Resources.LUMBER;
                else if (c2.getValue().equals(wool)) resource2 = Resources.WOOL;
                //Send Request
                lobbyService.playYearOfPlentyCard(lobbyName, loggedInUser, resource1, resource2);
            }
        }
    }

    /**
     * Handles a PlayCardFailureResponse found on the EventBus
     *
     * @param rsp The PlayCardFailureResponse found on the EventBus
     *
     * @see de.uol.swp.common.game.response.PlayCardFailureResponse
     */
    @Subscribe
    private void onPlayCardFailureResponse(PlayCardFailureResponse rsp) {
        if (lobbyName.equals(rsp.getLobbyName())) {
            LOG.debug("Received PlayCardFailureResponse");
            if (loggedInUser.equals(rsp.getUser())) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(resourceBundle.getString("game.playcards.failure.title"));
                    alert.setHeaderText(resourceBundle.getString("game.playcards.failure.header"));
                    ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"),
                                                        ButtonBar.ButtonData.OK_DONE);
                    alert.getButtonTypes().setAll(confirm);
                    if (rsp.getReason().equals(PlayCardFailureResponse.Reasons.NO_CARDS))
                        alert.setContentText(resourceBundle.getString("game.playcards.failure.context.noCards"));
                    alert.showAndWait();
                });
            }
        }
    }

    /**
     * Handles a PlayCardSuccessResponse found on the EventBus
     *
     * @param rsp The PlayCardSuccessResponse found on the EventBus
     *
     * @see de.uol.swp.common.game.response.PlayCardSuccessResponse
     */
    @Subscribe
    private void onPlayCardSuccessResponse(PlayCardSuccessResponse rsp) {
        if (lobbyName.equals(rsp.getLobbyName())) {
            LOG.debug("Received PlayCardSuccessResponse");
            playCard.setDisable(true);
            lobbyService.updateInventory(rsp.getLobbyName(), rsp.getUser());
        }
    }

    /**
     * Handles the Button to enable a randomly generated play field instead
     * of the fixed one.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    @FXML
    private void onRandomPlayFieldPressed() {
        prepareLobbyUpdate();
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
    private void onResetTradeWithBankButtonEvent(ResetTradeWithBankButtonEvent event) {
        if (super.lobbyName.equals(event.getLobbyName()) && super.loggedInUser.equals(event.getUser())) {
            setTradeWithBankButtonState(event.getUser());
            setEndTurnButtonState(event.getUser());
            setTradeWithUserButtonState(event.getUser());
            setPlayCardButtonState(event.getUser());
        }
    }

    /**
     * Handles a TradeWithUserCancelResponse found on the event bus
     * <p>
     * If a TradeWithUserCancelResponse is posted onto the EventBus the
     * the possible options for the active player are re-enabled.
     *
     * @param rsp The TradeWithUserCancelResponse seen on the EventBus
     *
     * @author Aldin Dervisi
     * @author Maximilian Lindner
     * @since 2021-03-19
     */
    @Subscribe
    private void onTradeWithUserCancelResponse(TradeWithUserCancelResponse rsp) {
        if (!rsp.getActivePlayer().equals(this.loggedInUser)) return;
        setTradeWithBankButtonState(this.loggedInUser);
        setTradeWithUserButtonState(this.loggedInUser);
        setEndTurnButtonState(this.loggedInUser);
        setPlayCardButtonState(this.loggedInUser);
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
    private void onRollDiceButtonPressed() {
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
            this.preGameSettingBox.setVisible(false);
            this.preGameSettingBox.setPrefHeight(0);
            this.preGameSettingBox.setMaxHeight(0);
            this.preGameSettingBox.setMinHeight(0);
            //This Line needs to be changed/ removed in the Future
            this.gameRendering = new GameRendering(gameMapCanvas);
            this.gameMap = new GameMap();
            gameMap.createBeginnerMap();
            gameRendering.drawGameMap(gameMap);
            setTurnIndicatorText(msg.getUser());
            lobbyService.updateInventory(lobbyName, loggedInUser);
            this.window.setWidth(LOBBY_WIDTH_IN_GAME);
            this.window.setHeight(LOBBY_HEIGHT_IN_GAME);
            ((Stage) this.window).setMinWidth(LOBBY_WIDTH_IN_GAME);
            ((Stage) this.window).setMinHeight(LOBBY_HEIGHT_IN_GAME);
            this.inventoryView.setMaxHeight(280);
            this.inventoryView.setMinHeight(280);
            this.inventoryView.setPrefHeight(280);
            this.inventoryView.setVisible(true);
            this.readyCheckBox.setVisible(false);
            this.startSession.setVisible(false);
            this.rollDice.setVisible(true);
            this.endTurn.setVisible(true);
            this.tradeWithUserButton.setVisible(true);
            this.tradeWithUserButton.setDisable(true);
            this.tradeWithBankButton.setVisible(true);
            setRollDiceButtonState(msg.getUser());
            this.kickUserButton.setVisible(false);
            this.playCard.setVisible(true);
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
        Pair<Integer, User> selectedUser = membersView.getSelectionModel().getSelectedItem();
        if (membersView.getSelectionModel().isEmpty() || selectedUser.getValue() == null) {
            eventBus.post(new TradeErrorEvent(resourceBundle.getString("game.trade.error.noplayer")));
        } else if (selectedUser.getKey() == this.loggedInUser.getID()) {
            eventBus.post(new TradeErrorEvent(resourceBundle.getString("game.trade.error.selfplayer")));
        } else {
            User user = selectedUser.getValue();
            tradeWithUserButton.setDisable(true);
            tradeWithBankButton.setDisable(true);
            playCard.setDisable(true);
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
        if (!this.lobbyName.equals(msg.getName())) return;
        setAllowedPlayers(msg.getLobby().getMaxPlayers() == 3 ? 3 : 4);
        this.setStartUpPhaseCheckBox.setSelected(msg.getLobby().startUpPhaseEnabled());
        this.randomPlayFieldCheckbox.setSelected(msg.getLobby().randomPlayfieldEnabled());
        this.commandsActivated.setSelected(msg.getLobby().commandsAllowed());
        this.moveTimeTextfield.setText(String.valueOf(msg.getLobby().getMoveTime()));
        this.moveTime = msg.getLobby().getMoveTime();
        Platform.runLater(() -> this.moveTimeLabel
                .setText(String.format(resourceBundle.getString("lobby.labels.movetime"), this.moveTime)));
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
            setPreGameSettings();
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
     * Helper method to handle the overall update of the LobbySettings in the pre-game menu
     * <p>
     * This method takes the content of all the specific CheckBoxes, RadioButtons, and Textfields
     * for the pre-game settings which the Owner selected, and calls the updateLobbySettings
     * method of the LobbyService.
     *
     * @author Maximilian Lindner
     * @author Aldin Devisi
     * @since 2021-03-15
     */
    private void prepareLobbyUpdate() {
        if (!this.loggedInUser.equals(owner)) return;
        int moveTime =
                !moveTimeTextfield.getText().equals("") ? Integer.parseInt(moveTimeTextfield.getText()) : this.moveTime;
        int maxPlayers = maxPlayersToggleGroup.getSelectedToggle() == threePlayerRadioButton ? 3 : 4;
        lobbyService.updateLobbySettings(this.lobbyName, this.loggedInUser, maxPlayers,
                                         setStartUpPhaseCheckBox.isSelected(), commandsActivated.isSelected(), moveTime,
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
        this.threePlayerRadioButton.setSelected(allowedPlayers == 3);
        this.fourPlayerRadioButton.setSelected(allowedPlayers == 4);
    }

    /**
     * Helper function that sets the disable state of the endTurnButton.
     * <p>
     * The button is only enabled to the active player when the
     * obligatory part of the turn is done.
     *
     * @param player The User whose turn it is currently
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
            this.kickUserButton.setVisible(this.loggedInUser.equals(this.owner));
            this.kickUserButton.setDisable(this.loggedInUser.equals(this.owner));
        });
    }

    /**
     * Helper function that sets the disable state of the PlayCardButton
     * The button is only enabled to the active player
     *
     * @param player The User whose turn it currently is
     *
     * @author Mario Fokken
     * @since 2021-02-25
     */
    private void setPlayCardButtonState(User player) {
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
        this.moveTimeTextfield.setDisable(!this.loggedInUser.equals(owner));
        this.changeMoveTimeButton.setDisable(!this.loggedInUser.equals(owner));
        this.setStartUpPhaseCheckBox.setDisable(!this.loggedInUser.equals(owner));
        this.commandsActivated.setDisable(!this.loggedInUser.equals(owner));
        this.randomPlayFieldCheckbox.setDisable(!this.loggedInUser.equals(owner));
        this.fourPlayerRadioButton.setDisable(!this.loggedInUser.equals(owner));
        this.threePlayerRadioButton.setDisable(!this.loggedInUser.equals(owner) || lobbyMembers.size() == 4);
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
     * @param player The User whose turn it is currently
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
     * @param player The User whose turn it is currently
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
        if (lobbyMembers == null) {
            lobbyMembers = FXCollections.observableArrayList();
            membersView.setItems(lobbyMembers);
        }
        lobbyMembers.clear();
        userLobbyList.forEach(u -> lobbyMembers.add(new Pair<>(u.getID(), u)));
    }
}
