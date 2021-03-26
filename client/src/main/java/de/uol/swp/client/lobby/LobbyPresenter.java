package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.GameRendering;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.UpdateLobbyMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.lobby.response.RemoveFromLobbiesResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
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
    public static final int LOBBY_HEIGHT_PRE_GAME = 700;
    public static final int LOBBY_WIDTH_PRE_GAME = 535;
    public static final int LOBBY_HEIGHT_IN_GAME = 740;
    public static final int LOBBY_WIDTH_IN_GAME = 1285;
    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

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
        this.readyUsers = rsp.getReadyUsers();
        updateUsersList(rsp.getUsers());
        Platform.runLater(() -> {
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
