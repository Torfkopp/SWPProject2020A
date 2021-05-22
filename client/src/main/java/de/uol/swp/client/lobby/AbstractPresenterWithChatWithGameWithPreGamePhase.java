package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.GameRendering;
import de.uol.swp.client.lobby.event.SetMoveTimeErrorEvent;
import de.uol.swp.client.trade.event.CloseTradeResponseEvent;
import de.uol.swp.client.trade.event.TradeCancelEvent;
import de.uol.swp.common.chat.ChatOrSystemMessage;
import de.uol.swp.common.chat.dto.InGameSystemMessageDTO;
import de.uol.swp.common.chat.dto.ReadySystemMessageDTO;
import de.uol.swp.common.game.StartUpPhaseBuiltStructures;
import de.uol.swp.common.game.message.PlayerWonGameMessage;
import de.uol.swp.common.game.message.ReturnToPreGameLobbyMessage;
import de.uol.swp.common.game.response.RecoverSessionResponse;
import de.uol.swp.common.lobby.message.StartSessionMessage;
import de.uol.swp.common.lobby.message.UserReadyMessage;
import de.uol.swp.common.lobby.response.KickUserResponse;
import de.uol.swp.common.user.UserOrDummy;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

    @FXML
    protected Button kickUserButton;
    @FXML
    protected Button changeOwnerButton;
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
    @FXML
    protected CheckBox readyCheckBox;

    protected ObservableList<UserOrDummy> lobbyMembers;
    protected Set<UserOrDummy> readyUsers;
    @FXML
    protected AnimationTimer elapsedTimer;
    @FXML
    protected Label timerLabel;
    @FXML
    private Button changeMoveTimeButton;
    @FXML
    private Button startSession;
    @FXML
    private ToggleGroup maxPlayersToggleGroup;
    @FXML
    private RadioButton threePlayerRadioButton;
    @FXML
    private RadioButton fourPlayerRadioButton;
    @FXML
    private VBox preGameSettingBox;

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        prepareMoveTimeTextField();
    }

    /**
     * Helper method to clean chat history of old owner notices
     * <p>
     * This method removes all SystemMessages from the chat history
     * that match the text used notify the owner that every player
     * (or every player except the owner) is ready to play and that
     * the owner should press the "Start Session" button to proceed
     * to the game.
     *
     * @author Phillip-André Suhr
     * @since 2021-04-25
     */
    protected void cleanChatHistoryOfOldOwnerNotices() {
        Platform.runLater(() -> chatMessages.removeIf(msg -> msg instanceof ReadySystemMessageDTO));
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
        if (lobbyName != null || !kicked) {
            lobbyService.leaveLobby(lobbyName);
        }
        moveTimeTimer.cancel();
        eventBus.post(new TradeCancelEvent(lobbyName));
        eventBus.post(new CloseTradeResponseEvent(lobbyName));
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
     * Helper function that sets the disable and visible state of the changeOwnerButton.
     * <p>
     * The button is only enabled the lobby owner when a game
     * has not started yet and if the logged in user is the
     * owner
     *
     * @author Maximilian Lindner
     * @since 2021-04-13
     */
    protected void setChangeOwnerButtonState() {
        Platform.runLater(() -> {
            changeOwnerButton.setVisible(userService.getLoggedInUser().equals(owner));
            changeOwnerButton.setDisable(userService.getLoggedInUser().equals(owner));
        });
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
            kickUserButton.setVisible(userService.getLoggedInUser().equals(owner));
            kickUserButton.setDisable(userService.getLoggedInUser().equals(owner));
        });
    }

    /**
     * Helper method that sets the visibility for the lobby owner and disables pre-game Buttons and Checkboxes
     * for everyone, expect the owner.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    protected void setPreGameSettings() {
        moveTimeTextField.setDisable(!userService.getLoggedInUser().equals(owner));
        moveTimeTextField.setVisible(userService.getLoggedInUser().equals(owner));
        changeMoveTimeButton.setDisable(!userService.getLoggedInUser().equals(owner));
        changeMoveTimeButton.setVisible(userService.getLoggedInUser().equals(owner));
        setStartUpPhaseCheckBox.setDisable(!userService.getLoggedInUser().equals(owner));
        commandsActivated.setDisable(!userService.getLoggedInUser().equals(owner));
        randomPlayFieldCheckbox.setDisable(!userService.getLoggedInUser().equals(owner));
        fourPlayerRadioButton.setDisable(!userService.getLoggedInUser().equals(owner));
        threePlayerRadioButton.setDisable(!userService.getLoggedInUser().equals(owner) || lobbyMembers.size() == 4);
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
        if (userService.getLoggedInUser().equals(owner)) {
            startSession.setVisible(true);
            startSession.setDisable(readyUsers.size() < 3 || lobbyMembers.size() != readyUsers.size());
        } else {
            startSession.setDisable(true);
            startSession.setVisible(false);
        }
    }

    /**
     * Method called when the ChangeOwnerButtonPressed is pressed
     * <p>
     * If the ChangeOwnerButtonPressed is pressed, this method requests to change
     * the owner status of the selected User of the members view .
     *
     * @author Maximilian Lindner
     * @see de.uol.swp.common.lobby.request.ChangeOwnerRequest
     * @since 2021-04-13
     */
    @FXML
    private void onChangeOwnerButtonPressed() {
        soundService.button();
        membersView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        UserOrDummy selectedUser = membersView.getSelectionModel().getSelectedItem();
        if (selectedUser == userService.getLoggedInUser()) return;
        lobbyService.changeOwner(lobbyName, selectedUser);
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
        soundService.button();
        membersView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        UserOrDummy selectedUser = membersView.getSelectionModel().getSelectedItem();
        if (selectedUser == userService.getLoggedInUser()) return;
        lobbyService.kickUser(lobbyName, selectedUser);
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
        if (lobbyName.equals(rsp.getLobbyName()) && userService.getLoggedInUser().equals(rsp.getToBeKickedUser())) {
            Platform.runLater(() -> closeWindow(true));
        }
    }

    /**
     * Handles the PlayerWonGameMessage
     * <p>
     * If the Message belongs to this Lobby, the GameMap gets cleared and a Text
     * with the Player that won is shown. For the owner of the Lobby appears a
     * ReturnToPreGameLobbyButton that resets the Lobby to its Pre-Game state.
     *
     * @param msg The PlayerWonGameMessage found on the EventBus
     *
     * @author Steven Luong
     * @author Finn Haase
     * @see de.uol.swp.common.game.message.PlayerWonGameMessage
     * @since 2021-03-22
     */
    @Subscribe
    private void onPlayerWonGameMessage(PlayerWonGameMessage msg) {
        if (!lobbyName.equals(msg.getLobbyName())) return;
        gameMap = null;
        gameWon = true;
        winner = msg.getUser();
        uniqueCardView.setMaxHeight(0);
        uniqueCardView.setMinHeight(0);
        uniqueCardView.setPrefHeight(0);
        uniqueCardView.setVisible(false);
        resourceTableView.setMaxHeight(0);
        resourceTableView.setMinHeight(0);
        resourceTableView.setPrefHeight(0);
        resourceTableView.setVisible(false);
        developmentCardTableView.setMaxHeight(0);
        developmentCardTableView.setMinHeight(0);
        developmentCardTableView.setPrefHeight(0);
        developmentCardTableView.setVisible(false);
        rollDice.setVisible(false);
        autoRoll.setVisible(false);
        endTurn.setVisible(false);
        tradeWithUserButton.setVisible(false);
        tradeWithUserButton.setDisable(false);
        tradeWithBankButton.setVisible(false);
        turnIndicator.setVisible(false);
        playCard.setVisible(false);
        timerLabel.setVisible(false);
        helpCheckBox.setDisable(true);
        helpCheckBox.setVisible(false);
        turnIndicator.setAccessibleText("");
        buildingCosts.setVisible(false);
        cardAmountsList.clear();
        moveTimeTimer.cancel();
        moveTimerLabel.setVisible(false);
        for (ChatOrSystemMessage m : chatMessages)
            if (m instanceof InGameSystemMessageDTO) Platform.runLater(() -> chatMessages.remove(m));
        currentRound.setVisible(false);
        roundCounter = 0;
        this.elapsedTimer.stop();
        if (Objects.equals(owner, userService.getLoggedInUser())) {
            returnToLobby.setVisible(true);
            returnToLobby.setPrefHeight(30);
            returnToLobby.setPrefWidth(250);
        }
        gameMapDescription.clear();
        gameMapDescription.setCenterText(
                winner == userService.getLoggedInUser() ? resourceBundle.getString("game.won.you") :
                String.format(resourceBundle.getString("game.won.info"), winner));
        fitCanvasToSize();
        soundService.victory();
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
        lobbyService.userReady(lobbyName, isReady);
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
            LOG.debug("Received ReturnToPreGameLobbyMessage for Lobby {}", lobbyName);
            returnToLobby.setVisible(false);
            returnToLobby.setPrefHeight(0);
            returnToLobby.setPrefWidth(0);
            window.setWidth(LobbyPresenter.MIN_WIDTH_PRE_GAME);
            window.setHeight(LobbyPresenter.MIN_HEIGHT_PRE_GAME);
            ((Stage) window).setMinWidth(LobbyPresenter.MIN_WIDTH_PRE_GAME);
            ((Stage) window).setMinHeight(LobbyPresenter.MIN_HEIGHT_PRE_GAME);
            preGameSettingBox.setVisible(true);
            preGameSettingBox.setPrefHeight(190);
            preGameSettingBox.setMaxHeight(190);
            preGameSettingBox.setMinHeight(190);
            readyCheckBox.setVisible(true);
            readyCheckBox.setSelected(false);
            lobbyService.retrieveAllLobbyMembers(this.lobbyName);
            setStartSessionButtonState();
            kickUserButton.setVisible(true);
            changeOwnerButton.setVisible(true);
        });
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
        soundService.button();
        buildingCosts.setVisible(true);
        gameService.startSession(lobbyName, moveTime);
        timerLabel.setVisible(true);
        moveTimerLabel.setVisible(true);
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
        LOG.debug("Received StartSessionMessage for Lobby {}", lobbyName);
        gameWon = false;
        winner = null;
        inGame = true;
        userOrDummyPlayerMap = msg.getUserOrDummyPlayerMap();
        lobbyService.retrieveAllLobbyMembers(lobbyName);
        cleanChatHistoryOfOldOwnerNotices();
        Platform.runLater(() -> {
            if (startUpPhaseEnabled) {
                notice.setVisible(true);
                notice.setText(resourceBundle.getString("game.setupphase.building.firstsettlement"));
            }
            setTurnIndicatorText(msg.getUser());
            prepareInGameArrangement();
            endTurn.setDisable(true);
            autoRoll.setVisible(true);
            buildingCosts.setVisible(true);
            tradeWithUserButton.setVisible(true);
            tradeWithUserButton.setDisable(true);
            tradeWithBankButton.setVisible(true);
            tradeWithBankButton.setDisable(true);
            turnIndicator.setVisible(true);
            currentRound.setVisible(true);
            currentRound.setText(String.format(resourceBundle.getString("lobby.menu.round"), 1));
            setRollDiceButtonState(msg.getUser());
            if (msg.getUser().equals(userService.getLoggedInUser())) ownTurn = true;
            kickUserButton.setVisible(false);
            changeOwnerButton.setVisible(false);
            playCard.setVisible(true);
            playCard.setDisable(true);
            setMoveTimer(moveTime);
            gameService.updateGameMap(lobbyName);
            long startTime = System.currentTimeMillis();
            this.elapsedTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    long elapsedMillis = System.currentTimeMillis() - startTime;
                    Platform.runLater(() -> timerLabel.setText(
                            String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(elapsedMillis),
                                          TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) % 60,
                                          TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60)));
                }
            };
            this.elapsedTimer.start();
        });
        if (helpActivated) setHelpText();
    }

    /**
     * Handles a StartSessionResponse found on the EventBus
     * <p>
     * Sets the play field visible.
     * The startSessionButton and every readyCheckbox are getting invisible for
     * the user.
     *
     * @param rsp The StartSessionResponse found on the EventBus
     *
     * @author MarvinDrees
     * @author Maximilian Lindner
     * @since 2021-02-04
     */
    @Subscribe
    private void onStartSessionResponse(RecoverSessionResponse rsp) {
        if (!rsp.getLobby().getName().equals(lobbyName)) return;
        LOG.debug("Received StartSessionResponse for Lobby {}", lobbyName);
        gameWon = false;
        winner = null;
        inGame = true;
        lobbyService.retrieveAllLobbyMembers(lobbyName);
        cleanChatHistoryOfOldOwnerNotices();
        Platform.runLater(() -> {
            startUpPhaseBuiltStructures = rsp.getBuiltStructures();
            // because startUpPhaseEnabled tracks whether it's _ongoing_, we check if player built everything
            startUpPhaseEnabled = startUpPhaseBuiltStructures != StartUpPhaseBuiltStructures.ALL_BUILT;
            if (startUpPhaseEnabled) {
                switch (startUpPhaseBuiltStructures) {
                    case NONE_BUILT:
                        notice.setVisible(true);
                        notice.setText(resourceBundle.getString("game.setupphase.building.firstsettlement"));
                        break;
                    case FIRST_SETTLEMENT_BUILT:
                        notice.setText(resourceBundle.getString("game.setupphase.building.firstroad"));
                        break;
                    case FIRST_BOTH_BUILT:
                        notice.setText(resourceBundle.getString("game.setupphase.building.secondsettlement"));
                        break;
                    case SECOND_SETTLEMENT_BUILT:
                        notice.setText(resourceBundle.getString("game.setupphase.building.secondroad"));
                        break;
                }
            }
            autoRollEnabled = rsp.isAutoRollState();
            autoRoll.setSelected(autoRollEnabled);
            int[] dices = rsp.getDices();
            dice1 = dices[0];
            dice2 = dices[1];
            setTurnIndicatorText(rsp.getPlayer());
            setMoveTimer(rsp.getMoveTime());
            gameService.updateGameMap(lobbyName);
            prepareInGameArrangement();
            endTurn.setDisable(!rsp.areDiceRolledAlready());
            autoRoll.setVisible(true);
            tradeWithUserButton.setVisible(true);
            tradeWithUserButton.setDisable(!rsp.areDiceRolledAlready());
            tradeWithBankButton.setVisible(true);
            tradeWithBankButton.setDisable(!rsp.areDiceRolledAlready());
            turnIndicator.setVisible(true);
            if (!rsp.areDiceRolledAlready()) setRollDiceButtonState(rsp.getPlayer());
            if (rsp.getPlayer().equals(userService.getLoggedInUser())) ownTurn = true;
            kickUserButton.setVisible(false);
            changeOwnerButton.setVisible(false);
            playCard.setVisible(true);
            playCard.setDisable(!rsp.areDiceRolledAlready());
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
        LOG.debug("Received UserReadyMessage for Lobby {}", lobbyName);
        lobbyService.retrieveAllLobbyMembers(lobbyName); // for updateUserList
    }

    /**
     * Helper method to set the in-game Buttons and Lists
     *
     * @author Marvin Drees
     * @author Maximilian Lindner
     * @since 2021-04-11
     */
    private void prepareInGameArrangement() {
        preGameSettingBox.setVisible(false);
        preGameSettingBox.setPrefHeight(0);
        preGameSettingBox.setMaxHeight(0);
        preGameSettingBox.setMinHeight(0);
        gameRendering = new GameRendering(gameMapCanvas);
        gameRendering.bindGameMapDescription(gameMapDescription);
        gameService.updateInventory(lobbyName);
        window.setWidth(LobbyPresenter.MIN_WIDTH_IN_GAME);
        window.setHeight(LobbyPresenter.MIN_HEIGHT_IN_GAME);
        ((Stage) window).setMinWidth(LobbyPresenter.MIN_WIDTH_IN_GAME);
        ((Stage) window).setMinHeight(LobbyPresenter.MIN_HEIGHT_IN_GAME);
        resourceTableView.setMaxHeight(150);
        resourceTableView.setMinHeight(150);
        resourceTableView.setPrefHeight(150);
        resourceTableView.setVisible(true);
        developmentCardTableView.setMaxHeight(150);
        developmentCardTableView.setMinHeight(150);
        developmentCardTableView.setPrefHeight(150);
        developmentCardTableView.setVisible(true);
        uniqueCardView.setMaxHeight(48);
        uniqueCardView.setMinHeight(48);
        uniqueCardView.setPrefHeight(48);
        uniqueCardView.setVisible(true);
        readyCheckBox.setVisible(false);
        startSession.setVisible(false);
        rollDice.setVisible(true);
        endTurn.setVisible(true);
        helpCheckBox.setDisable(false);
        helpCheckBox.setVisible(true);
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
        if (!userService.getLoggedInUser().equals(owner)) return;
        try {
            int moveTime = !moveTimeTextField.getText().equals("") ? Integer.parseInt(moveTimeTextField.getText()) :
                           this.moveTime;
            int maxPlayers = maxPlayersToggleGroup.getSelectedToggle() == threePlayerRadioButton ? 3 : 4;

            if (moveTime < 30 || moveTime > 500) {
                eventBus.post(new SetMoveTimeErrorEvent(resourceBundle.getString("lobby.error.movetime")));
            } else {

                lobbyService.updateLobbySettings(lobbyName, maxPlayers, setStartUpPhaseCheckBox.isSelected(),
                                                 commandsActivated.isSelected(), moveTime,
                                                 randomPlayFieldCheckbox.isSelected());
            }
        } catch (NumberFormatException ignored) {
            eventBus.post(new SetMoveTimeErrorEvent(resourceBundle.getString("lobby.error.movetime")));
        }
    }

    /**
     * Prepare the MoveTimeTextField
     * <p>
     * Lets the moveTimeTextField only accept numbers.
     *
     * @author Temmo Junkhoff
     * @author Maximilian Lindner
     * @since 2021-03-24
     */
    private void prepareMoveTimeTextField() {
        UnaryOperator<TextFormatter.Change> integerFilter = (s) ->
                s.getText().matches("\\d") || s.isDeleted() || s.getText().equals("") ? s : null;
        moveTimeTextField.setTextFormatter(new TextFormatter<>(integerFilter));
    }
}
