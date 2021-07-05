package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uol.swp.client.GameRendering;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
import de.uol.swp.common.game.CardsAmount;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.message.UpdateLobbyMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.lobby.response.RemoveFromLobbiesResponse;
import de.uol.swp.common.specialisedUtil.ActorSet;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.user.User;
import de.uol.swp.common.util.ResourceManager;
import de.uol.swp.common.util.Util;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
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
public class LobbyPresenter extends AbstractPresenterWithChatWithGameWithPreGamePhase {

    public static final String fxml = "/fxml/LobbyView.fxml";
    public static final int MIN_HEIGHT_PRE_GAME = 825;
    public static final int HELP_MIN_WIDTH = 350;
    public static final int MIN_HEIGHT_IN_GAME = 905;
    public static final int MIN_WIDTH_PRE_GAME = 695;
    public static final int MIN_WIDTH_IN_GAME = 1435;

    private boolean leftGame = false;
    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private final boolean joinLeaveMsgsOn;
    private final boolean ownerReadyNotificationsOn;
    private final boolean ownerTransferNotificationsOn;

    /**
     * Constructor
     *
     * @param joinLeaveMsgsOn              Boolean whether to show join/leave messages.
     * @param ownerReadyNotificationsOn    Boolean whether to show ready messages.
     * @param ownerTransferNotificationsOn Boolean whether to show owner transfer messages.
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.AbstractPresenterWithChat
     * @since 2021-01-02
     */
    @Inject
    public LobbyPresenter(@Named("joinLeaveMsgsOn") boolean joinLeaveMsgsOn,
                          @Named("ownerReadyNotificationsOn") boolean ownerReadyNotificationsOn,
                          @Named("ownerTransferNotificationsOn") boolean ownerTransferNotificationsOn) {
        super.init(LogManager.getLogger(LobbyPresenter.class));
        this.joinLeaveMsgsOn = joinLeaveMsgsOn;
        this.ownerReadyNotificationsOn = ownerReadyNotificationsOn;
        this.ownerTransferNotificationsOn = ownerTransferNotificationsOn;
    }

    /**
     * Initialises the Presenter by setting up the membersView, the
     * inventory view and the tradeWithUserButton.
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
        if (!Util.equals(lobbyName, rsp.getLobbyName())) return;
        LOG.debug("Received AllLobbyMembersResponse for Lobby {}", rsp.getLobbyName());
        LOG.debug("---- Update of Lobby member list");
        LOG.debug("---- Owner of this Lobby: {}", rsp.getOwner().getUsername());
        LOG.debug("---- Update of ready users");
        this.owner = (User) rsp.getOwner();
        if (this.readyUsers == null) this.readyUsers = new ActorSet();
        this.readyUsers.clear();
        this.readyUsers.addAll(rsp.getReadyUsers());
        updateUsersList(rsp.getUsers());
        Platform.runLater(() -> {
            if (!inGame) {
                setStartSessionButtonState();
            }
            setPreGameSettings();
        });
        SystemMessage ownerNotice = rsp.getOwnerNotice();
        boolean isOwner = userService.getLoggedInUser().equals(owner);
        if (!ownerReadyNotificationsOn || !isOwner || inGame || ownerNotice == null) return;
        Platform.runLater(() -> chatMessages.add(ownerNotice));
    }

    /**
     * Handles a click on the LeaveLobby button
     * <p>
     * Method called when the leaveLobby button is pressed.
     * If the leaveLobby button is pressed this method requests the lobby service
     * through a ConfirmationAlert that the user wants to leave the lobby.
     * If the user presses Confirm while he is in a Lobby which is not in a Game, the User leaves the Lobby.
     * If the user presses Confirm while he is in a Lobby that is in a Game, he leaves the lobby and gets replaced by an AI.
     *
     * @since 2020-12-14
     */
    @FXML
    private void onLeaveLobbyButtonPressed() {
        soundService.button();
        //Create new alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(ResourceManager.get("lobby.leave.confirmation.window"));
        alert.setHeaderText(ResourceManager.get("lobby.leave.confirmation.question"));
        alert.getDialogPane().getStylesheets().add(styleSheet);
        //Create the buttons
        ButtonType lConfirm = new ButtonType(ResourceManager.get("button.confirm"));
        ButtonType lCancel = new ButtonType(ResourceManager.get("button.cancel"));
        //Show the dialogue and get the result
        alert.getButtonTypes().setAll(lConfirm, lCancel);
        Optional<ButtonType> result = alert.showAndWait();
        //Result is the button the user has clicked on
        if (result.isPresent() && result.get() == lConfirm) {
            closeWindow(false);
            if (!leftGame) lobbyService.replaceUserWithAI(lobbyName, userColoursMap.get(userService.getLoggedInUser()));
            leftGame = true;
        }
        soundService.button();
    }

    /**
     * Handles LobbyUpdateEvents on the EventBus
     * <p>
     * If a new LobbyUpdateEvent is posted to the EventBus, this method checks
     * whether the lobbyName, window, or readyUsers attributes of the current
     * LobbyPresenter are null. If they are, it sets these attributes to the
     * values found in the LobbyUpdateEvent or creates a new, empty instance.
     * Also makes sure that the lobby will be left gracefully should the window
     * be closed without using the Leave Lobby button.
     * It also sets the pre-game Setting according to the Lobby.
     * <p>
     * Additionally, this method sets the accelerators for the LobbyPresenter, namely
     * <ul>
     *     <li> CTRL/META + S = Start Session button
     *     <li> CTRL/META + K = Kick User Function
     *     <li> CTRL/META + E = End Turn button
     *     <li> CTRL/META + R = Roll Dice button
     *     <li> CTRL/META + T = Make Offer to User button
     *     <li> CTRL/META + B = Trade with Bank button
     *     <li> CTRL/META + C = Play a Card button
     *     <li> CTRL/META + H = Return to Lobby button
     *     <li> F1            = Toggle help action list
     *     <li> F2            = Open Rules menu
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
        if (lobbyName != null) return;
        LOG.debug("Received LobbyUpdateEvent for Lobby {}", event.getLobby().getName());
        if (lobbyName == null) {
            lobbyName = event.getLobby().getName();
            chatService.askLatestMessages(10, lobbyName);
        }
        if (window == null) {
            window = membersView.getScene().getWindow();
        }
        if (readyUsers == null) {
            readyUsers = new ActorSet();
        }
        if (lobbyMembers == null) {
            ActorSet actorSet = new ActorSet();
            actorSet.addAll(event.getLobby().getActors());
            updateUsersList(actorSet);
        }
        if (event.getLobby().getReadyUsers().contains(userService.getLoggedInUser())) readyCheckBox.setSelected(true);

        Map<KeyCombination, Runnable> accelerators = new HashMap<>();
        // pre-game hotkeys
        accelerators.put(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), // CTRL/META + S
                         this::onStartSessionButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.K, KeyCombination.SHORTCUT_DOWN), // CTRL/META + K
                         this::onKickUserButtonPressed);
        // in-game hotkeys
        accelerators.put(new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN), // CTRL/META + E
                         this::onEndTurnButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN), // CTRL/META + R
                         this::onRollDiceButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN), // CTRL/META + T
                         this::onTradeWithUserButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN), // CTRL/META + B
                         this::onTradeWithBankButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), // CTRL/META + C
                         this::onPlayCardButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.H, KeyCombination.SHORTCUT_DOWN), // CTRL/META + H
                         this::onReturnToLobbyButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.F1), this::onHelpButtonPressed); // F1 for help
        accelerators.put(new KeyCodeCombination(KeyCode.F2), this::onRulesMenuClicked); // F2 for rules
        membersView.getScene().getAccelerators().putAll(accelerators);

        // onCloseRequest already set by SceneManager, so do not overwrite
        this.window.setOnHiding(windowEvent -> {
            closeWindow(false);
            if (!leftGame) lobbyService.replaceUserWithAI(lobbyName, userColoursMap.get(userService.getLoggedInUser()));
            leftGame = true;
            clearEventBus();
        });
        lobbyService.retrieveAllLobbyMembers(lobbyName);
        lobbyService.setColour(lobbyName, null);

        addSizeChangeListener();
        fitCanvasToSize();

        setAllowedPlayers(event.getLobby().getMaxPlayers());
        startUpPhaseEnabled = event.getLobby().isStartUpPhaseEnabled();
        moveTime = event.getLobby().getMoveTime();
        maxTradeDiff = event.getLobby().getMaxTradeDiff();
        randomPlayFieldCheckbox.setSelected(event.getLobby().isRandomPlayFieldEnabled());
        setStartUpPhaseCheckBox.setSelected(event.getLobby().isStartUpPhaseEnabled());

        Platform.runLater(() -> {
            tradeWithUserButton.setText(ResourceManager.get("lobby.game.buttons.playertrade.noneselected"));
            playCard.setText(ResourceManager.get("lobby.game.buttons.playcard.nonselected"));
            moveTimeLabel.setText(ResourceManager.get("lobby.labels.movetime", moveTime));
            moveTimeTextField.setText(String.valueOf(moveTime));
            maxTradeDiffLabel.setText(ResourceManager.get("game.trade.change.select.diff", maxTradeDiff));
        });
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
        for (Map.Entry<LobbyName, ISimpleLobby> entry : rsp.getLobbiesWithUser().entrySet()) {
            lobbyService.leaveLobby(entry.getKey());
        }
    }

    /**
     * Handles a click on the Show Rules Overview menu item
     * <p>
     * Method called when the Show Rules Overview menu item is clicked.
     * It posts a ShowRulesOverviewViewEvent onto the EventBus.
     *
     * @author Phillip-André Suhr
     * @since 2021-04-24
     */
    @FXML
    private void onRulesMenuClicked() {
        soundService.button();
        sceneService.openRulesWindow();
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
        if (!Util.equals(lobbyName, msg.getName())) return;
        LOG.debug("Received UpdateLobbyMessage for Lobby {}", msg.getLobby().getName());
        setAllowedPlayers(msg.getLobby().getMaxPlayers() == 3 ? 3 : 4);
        if (!Util.equals(owner, msg.getLobby().getOwner())) {
            if (ownerTransferNotificationsOn) {
                if (userService.getLoggedInUser().equals(owner)) {
                    I18nWrapper content = new I18nWrapper("lobby.owner.transferred",
                                                          msg.getLobby().getOwner().getUsername());
                    Platform.runLater(() -> chatMessages.add(new SystemMessageDTO(content)));
                } else if (userService.getLoggedInUser().equals(msg.getLobby().getOwner())) {
                    I18nWrapper content = new I18nWrapper("lobby.owner.promoted", owner.getUsername());
                    Platform.runLater(() -> chatMessages.add(new SystemMessageDTO(content)));
                }
            }
            owner = msg.getLobby().getOwner();
            prepareMembersView();
            setStartSessionButtonState();
            setPreGameSettings();
        }
        setStartUpPhaseCheckBox.setSelected(msg.getLobby().isStartUpPhaseEnabled());
        startUpPhaseEnabled = msg.getLobby().isStartUpPhaseEnabled();
        randomPlayFieldCheckbox.setSelected(msg.getLobby().isRandomPlayFieldEnabled());
        moveTimeTextField.setText(String.valueOf(msg.getLobby().getMoveTime()));
        moveTime = msg.getLobby().getMoveTime();
        maxTradeDiff = msg.getLobby().getMaxTradeDiff();
        Platform.runLater(() -> moveTimeLabel.setText(ResourceManager.get("lobby.labels.movetime", moveTime)));
        maxTradeDiffLabel.setText(ResourceManager.get("game.trade.change.select.diff", maxTradeDiff));
    }

    /**
     * Handles new joined users
     * <p>
     * If a new UserJoinedLobbyMessage object is posted onto the EventBus, the name of the newly
     * joined user is appended to the user list in the lobby menu.
     * The state of the "Start Session" button is updated accordingly. If the user enabled the
     * appropriate option, a SystemMessage is added to the chat to display to them who joined.
     * <p>
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
        if (!Util.equals(lobbyName, msg.getName())) return;
        LOG.debug("Received UserJoinedLobbyMessage for Lobby {}", msg.getName());
        Actor user = msg.getActor();
        LOG.debug("---- User {} joined", user.getUsername());
        Platform.runLater(() -> {
            if (joinLeaveMsgsOn)
                chatMessages.add(new SystemMessageDTO(new I18nWrapper("lobby.user.join", user.getUsername())));
            if (lobbyMembers != null && userService.getLoggedInUser() != user && !lobbyMembers.contains(user))
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
     * the appropriate state. If the user enabled the
     * appropriate option, a SystemMessage is added to the chat to display
     * to them who left.
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
        if (!Util.equals(lobbyName, msg.getName())) return;
        LOG.debug("Received UserLeftLobbyMessage for Lobby {}", msg.getName());
        Actor user = msg.getActor();
        if (Util.equals(user, owner)) LOG.debug("---- Owner {} left", user.getUsername());
        else LOG.debug("---- User {} left", user.getUsername());
        Platform.runLater(() -> {
            if (joinLeaveMsgsOn)
                chatMessages.add(new SystemMessageDTO(new I18nWrapper("lobby.user.leave", user.getUsername())));
            lobbyMembers.remove(user);
            readyUsers.remove(user);
            setStartSessionButtonState();
            setPreGameSettings();
        });
        lobbyService.retrieveAllLobbyMembers(lobbyName);
    }

    /**
     * Prepares the MembersView
     * <p>
     * Adds listeners for the MembersView
     *
     * @author Temmo Junkhoff
     * @author Maximilian Lindner
     * @since 2021-03-24
     */
    private void prepareMembersView() {
        membersView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Actor user, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(user, empty);
                    //if the background should be in colour you need to use setBackground
                    setTextFill(Color.BLACK); // No clue why this is needed, but it is (It really is)
                    if (user != null && actorPlayerMap != null && actorPlayerMap.containsKey(user)) {
                        switch (actorPlayerMap.get(user)) {
                            case PLAYER_1:
                                setTextFill(GameRendering.PLAYER_1_COLOUR);
                                break;
                            case PLAYER_2:
                                setTextFill(GameRendering.PLAYER_2_COLOUR);
                                break;
                            case PLAYER_3:
                                setTextFill(GameRendering.PLAYER_3_COLOUR);
                                break;
                            case PLAYER_4:
                                setTextFill(GameRendering.PLAYER_4_COLOUR);
                                break;
                        }
                    }
                    if (empty || user == null) setText("");
                    else {
                        String name = user.getUsername();
                        if (readyUsers.contains(user)) name = ResourceManager.get("lobby.members.ready", name);
                        if (user.equals(owner)) name = ResourceManager.get("lobby.members.owner", name);
                        if (inGame) {
                            if (cardAmountsList == null) {
                                cardAmountsList = new ArrayList<>();
                                // At the start of the game nobody has any cards, so add 0s for each user
                                for (Actor u : lobbyMembers) cardAmountsList.add(new CardsAmount(u, 0, 0));
                            }
                            for (CardsAmount cardsAmount : cardAmountsList) {
                                if (Util.equals(cardsAmount.getActor(), user)) {
                                    name = ResourceManager
                                            .get("lobby.members.amount", name, cardsAmount.getResourceCardsAmount(),
                                                 cardsAmount.getDevelopmentCardsAmount());
                                    break;
                                }
                            }
                        }
                        setText(name);
                    }
                });
            }
        });

        membersView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            String name = newValue.getUsername();
            boolean isSelf = newValue.equals(userService.getLoggedInUser());
            tradeWithUserButton.setDisable(isSelf || !tradingCurrentlyAllowed);
            if (isSelf) {
                tradeWithUserButton.setText(ResourceManager.get("lobby.game.buttons.playertrade.noneselected"));
            } else {
                tradeWithUserButton.setText(ResourceManager.get("lobby.game.buttons.playertrade", name));
            }
        });
        developmentCardTableView.getSelectionModel().selectedItemProperty()
                                .addListener((observableValue, oldValue, newValue) -> {
                                    if (newValue == null) {
                                        return;
                                    }
                                    String name = newValue.getType().toString();
                                    playCard.setText(ResourceManager.get("lobby.game.buttons.playcard", name));
                                });
    }
}
