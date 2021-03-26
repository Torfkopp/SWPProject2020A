package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.GameRendering;
import de.uol.swp.common.game.map.GameMap;
import de.uol.swp.common.lobby.message.StartSessionMessage;
import de.uol.swp.common.lobby.message.UserReadyMessage;
import de.uol.swp.common.lobby.request.KickUserRequest;
import de.uol.swp.common.lobby.response.KickUserResponse;
import de.uol.swp.common.user.UserOrDummy;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * This class is the base for creating a new Presenter that uses the game and needs the pre game phase.
 * <p>
 * This class prepares the child classes to have methods and attributes related to the pre game phase.
 *
 * @author Temmo Junkhoff
 * @author Maximillian Lindner
 * @see de.uol.swp.client.AbstractPresenter
 * @see de.uol.swp.client.lobby.AbstractPresenterWithChatWithGame
 * @see de.uol.swp.client.lobby.LobbyPresenter
 * @since 2021-03-26
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractPresenterWithChatWithGameWithPreGamePhase extends AbstractPresenterWithChatWithGame {

    protected ObservableList<UserOrDummy> lobbyMembers;
    protected Set<UserOrDummy> readyUsers;
    @FXML
    protected Button kickUserButton;
    @FXML
    protected Label moveTimeLabel;
    @FXML
    protected TextField moveTimeTextField;
    @FXML
    protected CheckBox setStartUpPhaseCheckBox;
    @FXML
    protected CheckBox randomPlayFieldCheckbox;
    @FXML
    protected CheckBox commandsActivated;
    protected Window window;
    @FXML
    private CheckBox readyCheckBox;
    @FXML
    private Button startSession;
    @FXML
    private VBox preGameSettingBox;
    @FXML
    private ToggleGroup maxPlayersToggleGroup;
    @FXML
    private RadioButton threePlayerRadioButton;
    @FXML
    private RadioButton fourPlayerRadioButton;
    @FXML
    private Button changeMoveTimeButton;

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        prepareMoveTimeTextField();
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
    protected void closeWindow(boolean kicked) {
        if (lobbyName != null || loggedInUser != null || !kicked) {
            lobbyService.leaveLobby(lobbyName, loggedInUser);
        }
        ((Stage) window).close();
        clearEventBus();
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
    protected void setAllowedPlayers(int allowedPlayers) {
        threePlayerRadioButton.setSelected(allowedPlayers == 3);
        fourPlayerRadioButton.setSelected(allowedPlayers == 4);
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
    protected void setKickUserButtonState() {
        Platform.runLater(() -> {
            kickUserButton.setVisible(loggedInUser.equals(owner));
            kickUserButton.setDisable(loggedInUser.equals(owner));
        });
    }

    /**
     * Helper method to disable pre-game Buttons and Checkboxes
     * for everyone, expect the owner.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    protected void setPreGameSettings() {
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
    protected void setStartSessionButtonState() {
        if (loggedInUser.equals(owner)) {
            startSession.setVisible(true);
            startSession.setDisable(readyUsers.size() < 3 || lobbyMembers.size() != readyUsers.size());
        } else {
            startSession.setDisable(true);
            startSession.setVisible(false);
        }
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
     * Prepare the MoveTimeTextField
     * Lets the moveTimeTextField only accept numbers.
     */
    private void prepareMoveTimeTextField() {
        UnaryOperator<TextFormatter.Change> integerFilter = (s) ->
                s.getText().matches("\\d") || s.isDeleted() || s.getText().equals("") ? s : null;
        moveTimeTextField.setTextFormatter(new TextFormatter<>(integerFilter));
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
            window.setWidth(LobbyPresenter.LOBBY_WIDTH_IN_GAME);
            window.setHeight(LobbyPresenter.LOBBY_HEIGHT_IN_GAME);
            ((Stage) window).setMinWidth(LobbyPresenter.LOBBY_WIDTH_IN_GAME);
            ((Stage) window).setMinHeight(LobbyPresenter.LOBBY_HEIGHT_IN_GAME);
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
}
