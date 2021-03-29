package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.GameRendering;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.client.trade.event.*;
import de.uol.swp.common.chat.message.*;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.chat.response.SystemMessageForTradeWithBankResponse;
import de.uol.swp.common.chat.response.SystemMessageResponse;
import de.uol.swp.common.game.map.*;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.TradeWithBankRequest;
import de.uol.swp.common.game.request.TradeWithUserRequest;
import de.uol.swp.common.game.response.*;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.UpdateLobbyMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.lobby.response.RemoveFromLobbiesResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.user.response.ChangeAccountDetailsSuccessfulResponse;
import de.uol.swp.common.util.Triple;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import javafx.scene.control.ListCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static de.uol.swp.common.game.map.MapPoint.Type.*;

/**
 * Manages the lobby's menu
 *
 * @see de.uol.swp.client.AbstractPresenter
 * @see de.uol.swp.client.AbstractPresenterWithChat
 * @since 2020-11-21
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyPresenter extends AbstractPresenterWithChatWithGameWithPreGamePhase {

    public static final String fxml = "/fxml/LobbyView.fxml";
    public static final int LOBBY_HEIGHT_PRE_GAME = 700;
    public static final int LOBBY_WIDTH_PRE_GAME = 685;
    public static final int LOBBY_HEIGHT_IN_GAME = 740;
    public static final int LOBBY_WIDTH_IN_GAME = 1435;
    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<Triple<String, UserOrDummy, Integer>> uniqueCardList;

    @FXML
    private ListView<Triple<String, UserOrDummy, Integer>> uniqueCardView;
    @FXML
    private Button returnToLobby;

    private List<Triple<UserOrDummy, Integer, Integer>> cardAmountTripleList;
    private boolean inGame;
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
        prepareMembersView();
        LOG.debug("LobbyPresenter initialised");
    }

    /**
     * Prepares the MembersView
     * Adds listeners for the MembersView
     */
    private void prepareMembersView() {
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
                        if (inGame) {
                            if (cardAmountTripleList == null) {
                                cardAmountTripleList = new ArrayList<>();
                                // At the start of the game, nobody has any cards, so add 0s for each user
                                for (UserOrDummy u : lobbyMembers) cardAmountTripleList.add(new Triple<>(u, 0, 0));
                            }
                            for (Triple<UserOrDummy, Integer, Integer> triple : cardAmountTripleList) {
                                if (triple.getValue1().equals(user)) {
                                    name = String.format(resourceBundle.getString("lobby.members.amount"), name,
                                                         triple.getValue2(), triple.getValue3());
                                    break;
                                }
                            }
                        }
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
            if (newValue == null) {
                kickUserButton.setText(String.format(resourceBundle.getString("lobby.buttons.kickuser"), ""));
                return;
            }
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
    }


    @Override
    @Subscribe
    protected void onSystemMessageForTradeMessage(SystemMessageForTradeMessage msg) {
        LOG.debug("Received SystemMessageForTradeResponse");
        if (msg.getName().equals(super.lobbyName)) super.onSystemMessageForTradeMessage(msg);
    }

    @Override
    @Subscribe
    protected void onSystemMessageForTradeWithBankMessage(SystemMessageForTradeWithBankMessage msg) {
        LOG.debug("Received SystemMessageForTradeWithBankResponse");
        if (msg.getName().equals(super.lobbyName) && !this.loggedInUser.equals(msg.getUser()))
            super.onSystemMessageForTradeWithBankMessage(msg);
    }

    @Override
    @Subscribe
    protected void onSystemMessageForTradeWithBankResponse(SystemMessageForTradeWithBankResponse rsp) {
        LOG.debug("Received SystemMessageForTradeWithBankResponse");
        if (rsp.getLobbyName().equals(super.lobbyName)) super.onSystemMessageForTradeWithBankResponse(rsp);
    }

    @Override
    @Subscribe
    protected void onSystemMessageForPlayingCardsMessage(SystemMessageForPlayingCardsMessage msg) {
        LOG.debug("Received SystemMessageForPlayingCardsMessage");
        if (msg.getName().equals(super.lobbyName)) super.onSystemMessageForPlayingCardsMessage(msg);
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
        this.owner = (User) rsp.getOwner();
        if (this.readyUsers == null) this.readyUsers = new HashSet<>();
        this.readyUsers.clear();
        this.readyUsers.addAll(rsp.getReadyUsers());
        Platform.runLater(() -> {
            updateUsersList(rsp.getUsers());
            setStartSessionButtonState();
            setKickUserButtonState();
            setPreGameSettings();
        });
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
        MapPoint mapPoint = gameRendering.coordinatesToHex(mouseEvent.getX(), mouseEvent.getY());
        // TODO: Replace this placeholder code with handling the results in context of e.g. building, info, etc
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
     * Handles the click on the ReturnToLobby-Button.
     *
     * @author Finn Haase
     * @author Steven Luong
     * @since 2021-03-22
     */
    @FXML
    private void onReturnToLobbyButtonPressed() {
        inGame = false;
        lobbyService.returnToPreGameLobby(this.lobbyName);
    }

    /**
     * Handles a ReturnToPreGameLobbyMessage
     * <p>
     * If a new ReturnToLobbyMessage is posted onto the EventBus the
     * Settings and visibility of the Buttons will be set to their
     * Pre-Game states.
     *
     * @param msg The ReturnToPreGameLobbyMessage seen on the EventBus
     *
     * @author Steven Luong
     * @author Finn Haase
     * @since 2021-03-2021
     */
    @Subscribe
    private void onReturnToPreGameLobbyMessage(ReturnToPreGameLobbyMessage msg) {
        Platform.runLater(() -> {
            this.returnToLobby.setVisible(false);
            this.returnToLobby.setPrefHeight(0);
            this.returnToLobby.setPrefWidth(0);
            this.window.setWidth(LOBBY_WIDTH_PRE_GAME);
            this.window.setHeight(LOBBY_HEIGHT_PRE_GAME);
            ((Stage) this.window).setMinWidth(LOBBY_WIDTH_PRE_GAME);
            ((Stage) this.window).setMinHeight(LOBBY_HEIGHT_PRE_GAME);
            this.preGameSettingBox.setVisible(true);
            this.preGameSettingBox.setPrefHeight(190);
            this.preGameSettingBox.setMaxHeight(190);
            this.turnIndicator.setText("");
            this.preGameSettingBox.setMinHeight(190);
            this.uniqueCardView.setMaxHeight(0);
            this.uniqueCardView.setMinHeight(0);
            this.uniqueCardView.setPrefHeight(0);
            this.uniqueCardView.setVisible(false);
            this.inventoryView.setMaxHeight(0);
            this.inventoryView.setMinHeight(0);
            this.inventoryView.setPrefHeight(0);
            this.inventoryView.setVisible(false);
            this.readyCheckBox.setVisible(true);
            this.readyCheckBox.setSelected(false);
            lobbyService.retrieveAllLobbyMembers(this.lobbyName);
            setStartSessionButtonState();
            this.rollDice.setVisible(false);
            this.endTurn.setVisible(false);
            this.tradeWithUserButton.setVisible(false);
            this.tradeWithUserButton.setDisable(false);
            this.tradeWithBankButton.setVisible(false);
            this.rollDice.setVisible(false);
            this.kickUserButton.setVisible(true);
            this.playCard.setVisible(false);
        });
    }
    /**
     * Handles a RefreshCardAmountMessage found on the EventBus
     * <p>
     * If a RefreshCardAmountMessage is found on the EventBus, this method
     * stores the contained cardAmountTripleList in the class attribute and
     * then calls the LobbyService to refresh the member list (which will
     * then contain the new card amounts).
     *
     * @param msg The RefreshCardAmountMessage found on the EventBus
     *
     * @author Alwin Bossert
     * @author Eric Vuong
     * @see de.uol.swp.common.game.message.RefreshCardAmountMessage
     * @since 2021-03-27
     */
    @Subscribe
    private void onRefreshCardAmountMessage(RefreshCardAmountMessage msg) {
        if (!lobbyName.equals(msg.getLobbyName())) return;
        cardAmountTripleList = msg.getCardAmountTriples();
        lobbyService.retrieveAllLobbyMembers(lobbyName);
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
        lobbyService.retrieveAllLobbyMembers(lobbyName);
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
     * Handles the PlayerWonGameMessage
     * <p>
     * If the Message belongs to this Lobby, the GameMap gets cleared and a Text
     * with the Player that won is shown. For the owner of the Lobby appears a
     * ReturnToPreGameLobbyButton that resets the Lobby to its Pre-Game state.
     *
     * @param msg The CheckVictoryPointsMessage found on the EventBus
     *
     * @author Steven Luong
     * @author Finn Haase
     * @since 2021-03-22
     */
    @Subscribe
    private void onPlayerWonGameMessage(PlayerWonGameMessage msg) {
        if (!msg.getLobbyName().equals(this.lobbyName)) return;
        GraphicsContext ctx = gameMapCanvas.getGraphicsContext2D();
        ctx.clearRect(0, 0, gameMapCanvas.getWidth(), gameMapCanvas.getHeight());
        this.gameMap = null;
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.fillText(msg.getUser().getUsername() + " " + resourceBundle.getString("game.won.info"),
                     gameMapCanvas.getWidth() / 2, gameMapCanvas.getHeight() / 2);
        ctx.setFill(Color.BLACK);
        ctx.setFont(Font.font(25));
        ChangeListener<Number> listener;
        if (this.loggedInUser.getID() == this.owner.getID()) {
            listener = (observable, oldValue, newValue) -> {
                double hexFactor = 10.0 / 11.0; // <~0.91 (ratio of tiled hexagons (less high than wide))
                double heightValue = (gameMapCanvas.getScene().getWindow().getHeight() - 60) / hexFactor;
                double widthValue = gameMapCanvas.getScene().getWindow().getWidth() - LOBBY_WIDTH_PRE_GAME;
                double dimension = Math.min(heightValue, widthValue);
                gameMapCanvas.setHeight((dimension * hexFactor) - 40);
                gameMapCanvas.setWidth(dimension);
                // gameMap exists, so redraw map to fit the new canvas dimensions
                gameMapCanvas.getGraphicsContext2D()
                             .clearRect(0, 0, gameMapCanvas.getWidth(), gameMapCanvas.getHeight());
                gameMapCanvas.getGraphicsContext2D().fillText(msg.getUser().getUsername() + " " + resourceBundle.getString("game.won.info"),
                                                              gameMapCanvas.getWidth() / 2,
                                                              gameMapCanvas.getHeight() / 2);

            };
            this.returnToLobby.setVisible(true);
            this.returnToLobby.setPrefHeight(30);
            this.returnToLobby.setPrefWidth(250);
        } else {
            listener = (observable, oldValue, newValue) -> {
                double hexFactor = 10.0 / 11.0; // <~0.91 (ratio of tiled hexagons (less high than wide))
                double heightValue = (gameMapCanvas.getScene().getWindow().getHeight() - 60) / hexFactor;
                double widthValue = gameMapCanvas.getScene().getWindow().getWidth() - LOBBY_WIDTH_PRE_GAME;
                double dimension = Math.min(heightValue, widthValue);
                gameMapCanvas.setHeight(dimension * hexFactor);
                gameMapCanvas.setWidth(dimension);
                // gameMap exists, so redraw map to fit the new canvas dimensions
                gameMapCanvas.getGraphicsContext2D()
                             .clearRect(0, 0, gameMapCanvas.getWidth(), gameMapCanvas.getHeight());
                gameMapCanvas.getGraphicsContext2D()
                             .fillText(msg.getUser().getUsername() + " " + resourceBundle.getString("game.won.info"), gameMapCanvas.getWidth() / 2, gameMapCanvas.getHeight() / 2);
            };
        }
        this.window.widthProperty().removeListener(canvasResizeListener);
        this.window.heightProperty().removeListener(canvasResizeListener);
        this.window.widthProperty().addListener(listener);
        this.window.heightProperty().addListener(listener);
        this.window.setHeight(this.window.getHeight() - 1);
        this.window.setHeight(this.window.getHeight() + 1);
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
        if (lobbyMembers == null) {
            lobbyMembers = FXCollections.observableArrayList();
            membersView.setItems(lobbyMembers);
        }
        lobbyMembers.clear();
        lobbyMembers.addAll(userLobbyList);
    }
}
