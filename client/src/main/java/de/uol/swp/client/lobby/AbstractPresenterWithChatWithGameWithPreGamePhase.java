package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.jfoenix.utils.JFXUtilities;
import de.uol.swp.client.GameRendering;
import de.uol.swp.common.Colour;
import de.uol.swp.common.chat.ChatOrSystemMessage;
import de.uol.swp.common.chat.dto.InGameSystemMessageDTO;
import de.uol.swp.common.chat.dto.ReadySystemMessageDTO;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.message.PlayerWonGameMessage;
import de.uol.swp.common.game.message.ReturnToPreGameLobbyMessage;
import de.uol.swp.common.lobby.message.ColourChangedMessage;
import de.uol.swp.common.lobby.message.StartSessionMessage;
import de.uol.swp.common.lobby.message.UserReadyMessage;
import de.uol.swp.common.lobby.response.KickUserResponse;
import de.uol.swp.common.specialisedUtil.ActorPlayerMap;
import de.uol.swp.common.specialisedUtil.ActorSet;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.util.ResourceManager;
import de.uol.swp.common.util.Util;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

/**
 * This class is the base for creating a new Presenter that uses the game and needs the pre-game phase.
 * <p>
 * This class prepares the child classes to have methods and attributes related to the pre-game phase.
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
    protected Label moveTimeLabel;
    @FXML
    protected TextField moveTimeTextField;
    @FXML
    protected CheckBox setStartUpPhaseCheckBox;
    @FXML
    protected CheckBox randomPlayFieldCheckbox;
    @FXML
    protected CheckBox readyCheckBox;

    protected ActorSet readyUsers;
    @FXML
    protected AnimationTimer elapsedTimer;
    @FXML
    protected Label timerLabel;
    @FXML
    protected Label maxTradeDiffLabel;
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
    private VBox aiVBox;
    @FXML
    private CheckBox talkingAICheckBox;
    @FXML
    private ToggleGroup difficultyAIToggleGroup;
    @FXML
    private RadioButton easyAIRadioButton;
    @FXML
    private VBox preGameSettingBox;
    @FXML
    private TextField maxTradeDiffTextField;
    @FXML
    private Button maxTradeChangeButton;
    @FXML
    private ComboBox<Colour> colourComboBox;

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        prepareMoveTimeTextField();
        prepareMaxTradeDiffTextfield();
        prepareColourComboBox();
        LOG.debug("AbstractPresenterWithChatWithGameWithPreGamePhase initialised");
    }

    /**
     * Helper method to clean chat history of old owner notices
     * <p>
     * This method removes all SystemMessages from the chat history
     * that match the text used to notify the owner that every player
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
     * <p>
     * Also clears the EventBus of the instance to avoid NullPointerExceptions.
     *
     * @param kicked Whether the user was kicked (true) or is leaving
     *               voluntarily (false)
     *
     * @author Temmo Junkhoff
     * @since 2021-01-06
     */
    protected void closeWindow(boolean kicked) {
        if (lobbyName != null || !kicked) lobbyService.leaveLobby(lobbyName);
        if (moveTimeTimer != null) moveTimeTimer.cancel();
        JFXUtilities.runInFX(() -> {
            if (membersView.getScene().getWindow() != null) membersView.getScene().getWindow().hide();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        sceneService.closeUserTradeWindow(lobbyName);
        sceneService.closeAcceptTradeWindow(lobbyName);
        sceneService.closeRobberTaxWindow(lobbyName);
    }

    /**
     * Method called when the KickUserButton is pressed
     * <p>
     * If the KickUserButton is pressed, this method requests to kick
     * the selected User inside the MembersView.
     *
     * @author Maximilian Lindner
     * @author Sven Ahrens
     * @see de.uol.swp.common.lobby.request.KickUserRequest
     * @since 2021-03-02
     */
    @FXML
    protected void onKickUserButtonPressed() {
        soundService.button();
        membersView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        Actor selectedUser = membersView.getSelectionModel().getSelectedItem();
        if (selectedUser == userService.getLoggedInUser()) return;
        lobbyService.kickUser(lobbyName, selectedUser);
    }

    /**
     * Handles a click on the StartSession Button
     * <p>
     * Method called when the StartSessionButton is pressed.
     * The Method calls the GameService to start the Session and
     * makes the BuildingCosts, TimerLabel, and MoveTimeLabel visible.
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-20
     */
    @FXML
    protected void onStartSessionButtonPressed() {
        if (startSession.isDisabled()) {
            LOG.trace("onStartSessionButtonPressed called with disabled button, returning");
            return;
        }
        soundService.button();
        buildingCosts.setVisible(true);
        gameService.startSession(lobbyName, moveTime);
        timerLabel.setVisible(true);
        moveTimerLabel.setVisible(true);
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
     * Helper method that sets the visibility and state of buttons and checkboxes for the
     * pre-game settings. The pre-game settings are disabled for everyone, except the
     * owner of the lobby.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    protected void setPreGameSettings() {
        moveTimeTextField.setDisable(!userService.getLoggedInUser().equals(owner));
        moveTimeTextField.setVisible(userService.getLoggedInUser().equals(owner));
        maxTradeChangeButton.setDisable(!userService.getLoggedInUser().equals(owner));
        maxTradeChangeButton.setVisible(userService.getLoggedInUser().equals(owner));
        maxTradeDiffTextField.setDisable(!userService.getLoggedInUser().equals(owner));
        maxTradeDiffTextField.setVisible(userService.getLoggedInUser().equals(owner));
        changeMoveTimeButton.setDisable(!userService.getLoggedInUser().equals(owner));
        changeMoveTimeButton.setVisible(userService.getLoggedInUser().equals(owner));
        setStartUpPhaseCheckBox.setDisable(!userService.getLoggedInUser().equals(owner));
        randomPlayFieldCheckbox.setDisable(!userService.getLoggedInUser().equals(owner));
        fourPlayerRadioButton.setDisable(!userService.getLoggedInUser().equals(owner));
        threePlayerRadioButton.setDisable(!userService.getLoggedInUser().equals(owner) || lobbyMembers.size() == 4);
        aiVBox.setVisible(userService.getLoggedInUser().equals(owner));
    }

    /**
     * Helper function that sets the visibility and state of the StartSessionButton.
     * <p>
     * The button is only visible to the lobby owner and only enabled
     * if there are 3 or more lobby members and all members are marked as ready.
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-20
     */
    protected void setStartSessionButtonState() {
        if (!inGame) {
            if (userService.getLoggedInUser().equals(owner)) {
                startSession.setVisible(true);
                startSession.setDisable(readyUsers.size() < 3 || lobbyMembers.size() != readyUsers.size());
            } else {
                startSession.setDisable(true);
                startSession.setVisible(false);
            }
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
     * @see de.uol.swp.common.user.Actor
     * @since 2021-01-05
     */
    protected void updateUsersList(ActorSet userLobbyList) {
        Platform.runLater(() -> {
            if (inGame) {
                lobbyMembers.clear();
                lobbyMembers.addAll(userLobbyList);
                return;
            }
            if (lobbyMembers == null) {
                lobbyMembers = FXCollections.observableArrayList();
                membersView.setItems(lobbyMembers);
            }
            lobbyMembers.clear();
            lobbyMembers.addAll(userLobbyList);
        });
    }

    /**
     * Handles a click on the AddAI Button
     * <p>
     * Method called when the AddAIButton is pressed.
     * This Method calls the lobbyService to post an AddAIRequest.
     *
     * @author Mario Fokken
     * @since 2021-05-21
     */
    @FXML
    private void onAddAIButtonPressed() {
        soundService.button();
        boolean talking = talkingAICheckBox.isSelected();
        AI.Difficulty difficulty =
                difficultyAIToggleGroup.getSelectedToggle() == easyAIRadioButton ? AI.Difficulty.EASY :
                AI.Difficulty.HARD;
        AI ai = new AIDTO(difficulty, talking);
        lobbyService.addAI(lobbyName, ai);
    }

    /**
     * Method called when the ChangeOwnerButtonPressed is pressed
     * <p>
     * If the ChangeOwnerButton is pressed, this method requests to change
     * the owner status to the selected User of the members view.
     *
     * @author Maximilian Lindner
     * @see de.uol.swp.common.lobby.request.ChangeOwnerRequest
     * @since 2021-04-13
     */
    @FXML
    private void onChangeOwnerButtonPressed() {
        soundService.button();
        membersView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        Actor selectedUser = membersView.getSelectionModel().getSelectedItem();
        if (selectedUser == userService.getLoggedInUser()) return;
        lobbyService.changeOwner(lobbyName, selectedUser);
    }

    /**
     * Handles a click on the ColourChangeButton
     * <p>
     * Method called when the ColourChangeButton is pressed.
     * This method calls the lobbyService to post a setColourRequest.
     *
     * @author Mario Fokken
     * @since 2021-06-04
     */
    @FXML
    private void onColourChangeButtonPressed() {
        Colour colour = colourComboBox.getValue();
        lobbyService.setColour(lobbyName, colour);
    }

    /**
     * Handles a ColourChangedMessage found on the EventBus
     * <p>
     * The message gets sent by the server if a user changed their colour.
     * It tells the GameRendering to adapt those new colours.
     *
     * @param msg The ColourChangedMessage found on the EventBus
     *
     * @author Mario Fokken
     * @since 2021-06-02
     */
    @Subscribe
    private void onColourChangedMessage(ColourChangedMessage msg) {
        if (!Util.equals(lobbyName, msg.getName())) return;
        LOG.debug("Received ColourChangedMessage for {}", msg.getName());
        ActorPlayerMap map = new ActorPlayerMap();
        int i = 0;
        for (Actor u : msg.getUserColours().keySet())
            map.put(u, Player.byIndex(i++));
        actorPlayerMap = map;
        userColoursMap = msg.getUserColours();
        gameRendering.setPlayerColours(userColoursMap.makePlayerColourMap(actorPlayerMap));
        lobbyService.retrieveAllLobbyMembers(lobbyName);//for updating the list
        Platform.runLater(this::prepareColourComboBox);
    }

    /**
     * Handles a KickUserResponse found on the EventBus
     * <p>
     * If a KickUserResponse is detected on the EventBus and it is
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
        if (!Util.equals(lobbyName, rsp.getLobbyName())) return;
        if (!userService.getLoggedInUser().equals(rsp.getToBeKickedUser())) return;
        LOG.debug("Received KickUserResponse for Lobby {}", rsp.getLobbyName());
        Platform.runLater(() -> closeWindow(true));
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
        if (!Util.equals(lobbyName, msg.getLobbyName())) return;
        LOG.debug("Received PlayerWonGameMessage for Lobby {}", msg.getLobbyName());
        gameMap = null;
        gameWon = true;
        victoryPointsOverTimeMap = msg.getVictoryPointMap();
        winner = msg.getActor();
        Platform.runLater(() -> {
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
            constructionMode.setVisible(false);
            endTurn.setVisible(false);
            tradeWithUserButton.setVisible(false);
            tradeWithUserButton.setDisable(false);
            tradeWithBankButton.setVisible(false);
            turnIndicator.setVisible(false);
            playCard.setVisible(false);
            timerLabel.setVisible(false);
            helpButton.setDisable(true);
            helpButton.setVisible(false);
            turnIndicator.setAccessibleText("");
            buildingCosts.setVisible(false);
            victoryPointsLabel.setVisible(false);
            cardAmountsList.clear();
            moveTimeTimer.cancel();
            moveTimerLabel.setVisible(false);
            for (ChatOrSystemMessage m : chatMessages)
                if (m instanceof InGameSystemMessageDTO) Platform.runLater(() -> chatMessages.remove(m));
            currentRound.setVisible(false);
            roundCounter = 0;
            this.elapsedTimer.stop();
            displayVictoryPointChartButton.setVisible(true);
            displayVictoryPointChartButton.setDisable(false);
            displayVictoryPointChartButton.setPrefHeight(30);
            displayVictoryPointChartButton.setPrefWidth(230);
            if (Util.equals(owner, userService.getLoggedInUser())) {
                returnToLobby.setVisible(true);
                returnToLobby.setDisable(false);
                returnToLobby.setPrefHeight(30);
                returnToLobby.setPrefWidth(250);
            }
            gameRendering.redraw();
            gameMapDescription.setCenterText(
                    winner == userService.getLoggedInUser() ? ResourceManager.get("game.won.you") :
                    ResourceManager.get("game.won.info", winner));
            fitCanvasToSize();
        });
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
        soundService.button();
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
        if (!Util.equals(lobbyName, msg.getName())) return;
        LOG.debug("Received ReturnToPreGameLobbyMessage for Lobby {}", lobbyName);
        Platform.runLater(() -> {
            returnToLobby.setVisible(false);
            returnToLobby.setDisable(true);
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
            displayVictoryPointChartButton.setVisible(false);
            displayVictoryPointChartButton.setDisable(true);
            displayVictoryPointChartButton.setPrefHeight(0);
            displayVictoryPointChartButton.setPrefWidth(0);
            gameMapDescription.setCenterText("");
            gameMapDescription.clear();
            lobbyService.retrieveAllLobbyMembers(this.lobbyName);
            setStartSessionButtonState();
        });
    }

    /**
     * Handles a StartSessionMessage found on the EventBus
     * <p>
     * Sets the play field to visible.
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
        if (!Util.equals(lobbyName, msg.getName())) return;
        LOG.debug("Received StartSessionMessage for Lobby {}", lobbyName);
        gameWon = false;
        winner = null;
        inGame = true;
        updateUsersList(msg.getPlayerList());
        actorPlayerMap = msg.getActorPlayerMap();
        userColoursMap = msg.getActorColourMap();
        gameRendering.setPlayerColours(userColoursMap.makePlayerColourMap(actorPlayerMap));
        lobbyService.retrieveAllLobbyMembers(lobbyName);
        cleanChatHistoryOfOldOwnerNotices();
        Platform.runLater(() -> {
            if (startUpPhaseEnabled) {
                notice.setVisible(true);
                notice.setText(ResourceManager.get("game.setupphase.building.firstsettlement"));
            }
            setTurnIndicatorText(msg.getActor());
            prepareInGameArrangement();
            endTurn.setDisable(true);
            autoRoll.setVisible(true);
            constructionMode.setVisible(true);
            buildingCosts.setVisible(true);
            tradeWithUserButton.setVisible(true);
            tradeWithUserButton.setDisable(true);
            tradeWithBankButton.setVisible(true);
            tradeWithBankButton.setDisable(true);
            turnIndicator.setVisible(true);
            victoryPointsLabel.setVisible(true);
            currentRound.setVisible(true);
            currentRound.setText(ResourceManager.get("lobby.menu.round", 1));
            setRollDiceButtonState(msg.getActor());
            if (msg.getActor().equals(userService.getLoggedInUser())) ownTurn = true;
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
        if (!Util.equals(lobbyName, msg.getName())) return;
        LOG.debug("Received UserReadyMessage for Lobby {}", lobbyName);
        lobbyService.retrieveAllLobbyMembers(lobbyName); // for updateUserList
    }

    /**
     * Prepares the ColourComboBox
     * <p>
     * Colours the text in the right colour.
     *
     * @author Mario Fokken
     * @since 2021-06-04
     */
    private void prepareColourComboBox() {
        colourComboBox.getItems().clear();
        Colour[] colours = new Colour[Colour.values().length - 1];
        System.arraycopy(Colour.values(), 0, colours, 0, colours.length);

        colourComboBox.getItems().addAll(colours);
        colourComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Colour> call(ListView<Colour> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Colour item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(ResourceManager.get("colours." + item));
                            int[] colourCode = item.getColourCode();
                            setTextFill(Color.rgb(colourCode[0], colourCode[1], colourCode[2]));
                            setDisable(userColoursMap.containsValue(item));
                        } else setText(null);
                    }
                };
            }
        });
        colourComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Colour item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) setText(ResourceManager.get("colours." + item));
                else setText(null);
            }
        });
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
        gameRendering = new GameRendering(gameMapCanvas, userService, drawHitboxGrid, renderingStyle);
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
        uniqueCardView.setMaxHeight(75);
        uniqueCardView.setMinHeight(75);
        uniqueCardView.setPrefHeight(75);
        uniqueCardView.setVisible(true);
        readyCheckBox.setVisible(false);
        startSession.setVisible(false);
        rollDice.setVisible(true);
        endTurn.setVisible(true);
        helpButton.setDisable(false);
        helpButton.setVisible(true);
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
            int newMaxTradeDiff =
                    !maxTradeDiffTextField.getText().equals("") ? Integer.parseInt(maxTradeDiffTextField.getText()) :
                    this.maxTradeDiff;

            if (moveTime < 30 || moveTime > 500) {
                sceneService.showError(ResourceManager.get("lobby.error.movetime"));
            } else {
                soundService.button();
                lobbyService.updateLobbySettings(lobbyName, maxPlayers, setStartUpPhaseCheckBox.isSelected(), moveTime,
                                                 randomPlayFieldCheckbox.isSelected(), newMaxTradeDiff);
            }
        } catch (NumberFormatException ignored) {
            sceneService.showError(ResourceManager.get("lobby.error.movetime"));
        }
    }

    /**
     * Prepare the MaxTradeTextfield
     * <p>
     * Lets the maxTradeTextfield only accept positive numbers.
     *
     * @author Aldin Dervisi
     * @since 2021-06-08
     */
    private void prepareMaxTradeDiffTextfield() {
        UnaryOperator<TextFormatter.Change> integerFilter = (s) ->
                s.getText().matches("^[0-9]\\d*(\\.\\d+)?$") || s.isDeleted() || s.getText().equals("") ? s : null;
        maxTradeDiffTextField.setTextFormatter(new TextFormatter<>(integerFilter));
    }

    /**
     * Prepares the MoveTimeTextField
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
