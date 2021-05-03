package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uol.swp.client.AbstractPresenterWithChat;
import de.uol.swp.client.GameRendering;
import de.uol.swp.client.game.IGameService;
import de.uol.swp.client.lobby.event.ShowRobberTaxViewEvent;
import de.uol.swp.client.trade.ITradeService;
import de.uol.swp.client.trade.event.ResetTradeWithBankButtonEvent;
import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.dto.InGameSystemMessageDTO;
import de.uol.swp.common.game.RoadBuildingCardPhase;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.map.gamemapDTO.IGameMap;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.PauseTimerRequest;
import de.uol.swp.common.game.request.UnpauseTimerRequest;
import de.uol.swp.common.game.response.*;
import de.uol.swp.common.game.robber.*;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.util.Triple;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static de.uol.swp.common.game.map.management.MapPoint.Type.*;

/**
 * This class is the base for creating a new Presenter that uses the game.
 * <p>
 * This class prepares the child classes to have game-related methods and attributes.
 *
 * @author Temmo Junkhoff
 * @author Maximillian Lindner
 * @see de.uol.swp.client.AbstractPresenter
 * @see de.uol.swp.client.lobby.AbstractPresenterWithChatWithGameWithPreGamePhase
 * @see de.uol.swp.client.lobby.LobbyPresenter
 * @since 2021-03-23
 */
@SuppressWarnings({"UnstableApiUsage", "rawtypes"})
public abstract class AbstractPresenterWithChatWithGame extends AbstractPresenterWithChat {

    @Inject
    @Named("theme")
    private static String theme;
    @Inject
    @Named("styleSheet")
    private static String styleSheet;

    @FXML
    protected Button endTurn;
    @FXML
    protected Canvas gameMapCanvas;
    @FXML
    protected Timer moveTimeTimer;
    @FXML
    protected TableView<Map<String, Object>> developmentCardTableView;
    @FXML
    protected Menu moveTimerLabel = new Menu();
    @FXML
    protected TableView<Map<String, Object>> resourceTableView;
    @FXML
    protected ListView<UserOrDummy> membersView;
    @FXML
    protected Button playCard;
    @FXML
    protected Button returnToLobby;
    @FXML
    protected Button rollDice;
    @FXML
    protected Button tradeWithBankButton;
    @FXML
    protected Button tradeWithUserButton;
    @FXML
    protected TextFlow turnIndicator;
    @FXML
    protected Label notice;
    @FXML
    protected ListView<Triple<String, UserOrDummy, Integer>> uniqueCardView;
    @FXML
    protected Label buildingCosts;
    @FXML
    protected CheckBox autoRoll;
    @FXML
    protected ColumnConstraints helpColumn;
    @FXML
    protected TextFlow helpLabel;
    @FXML
    protected Menu infoMenu;

    @Inject
    protected IGameService gameService;

    protected List<Triple<UserOrDummy, Integer, Integer>> cardAmountTripleList;
    protected Integer dice1;
    protected Integer dice2;
    protected IGameMap gameMap;
    protected GameRendering gameRendering;
    protected boolean gameWon = false;
    protected boolean robberNewPosition = false;
    protected RoadBuildingCardPhase roadBuildingCardPhase = RoadBuildingCardPhase.NO_ROAD_BUILDING_CARD_PLAYED;
    protected boolean autoRollEnabled = false;
    protected boolean playedCard = false;
    protected boolean inGame;
    protected boolean ownTurn;
    protected boolean tradingCurrentlyAllowed;
    protected boolean paused;
    protected int moveTime;
    protected int remainingMoveTime;
    protected User owner;
    protected ObservableList<Triple<String, UserOrDummy, Integer>> uniqueCardList;
    protected Window window;
    protected UserOrDummy winner = null;
    protected boolean helpActivated = false;
    @FXML
    protected CheckMenuItem helpCheckBox;
    private boolean diceRolled = false;
    // MapValueFactory doesn't support specifying a Map's generics, so the Map type is used raw here (Warning suppressed)
    @FXML
    private TableColumn<Map, Integer> developmentCardAmountCol;
    @FXML
    private TableColumn<Map, String> developmentCardNameCol;
    @FXML
    private TableColumn<Map, Integer> resourceAmountCol;
    @FXML
    private TableColumn<Map, String> resourceNameCol;

    @Inject
    private ITradeService tradeService;

    private boolean buildingCurrentlyAllowed;

    @Override
    @FXML
    protected void initialize() {
        super.initialize();
        prepareInventoryTables();
        prepareUniqueCardView();
    }

    /**
     * Handles a PauseTimerMessage
     * <p>
     * If a new PauseTimerMessage object is posted onto the EventBus,
     * this method is called.
     * It sets the boolean paused on true.
     *
     * @param msg The PauseTimerMessage object seen on the EventBus
     *
     * @author Alwin Bossert
     * @see de.uol.swp.common.game.message.PauseTimerMessage
     * @since 2021-05-02
     */
    @Subscribe
    public void onPauseTimerMessage(PauseTimerMessage msg) {
        LOG.debug("Received PauseTimerMessage for Lobby {}", msg.getName());
        paused = true;
    }

    /**
     * Handles a UnpauseTimerMessage
     * <p>
     * If a new UnpauseTimerMessage object is posted onto the EventBus,
     * this method is called.
     * It sets the boolean paused on false.
     *
     * @param msg The UnpauseTimerMessage object seen on the EventBus
     *
     * @author Alwin Bossert
     * @see de.uol.swp.common.game.message.UnpauseTimerMessage
     * @since 2021-05-02
     */
    @Subscribe
    public void onUnpauseTimerResponse(UnpauseTimerMessage msg) {
        LOG.debug("Received UnpauseTimerMessage for Lobby {}", msg.getName());
        paused = false;
    }

    /**
     * Helper method to set the timer for the players round.
     * The user gets forced to end his turn, if the timer gets zero.
     * If paused is true, the timer is paused.
     *
     * @param moveTime The moveTime for the Lobby
     *
     * @author Alwin Bossert
     * @since 2021-05-01
     */
    public void setMoveTimer(int moveTime) {
        moveTimeTimer = new Timer();
        AtomicInteger moveTimeToDecrement = new AtomicInteger(moveTime);
        moveTimeTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (!paused) {
                    Platform.runLater(() -> moveTimerLabel.setText(String.format(
                            resourceBundle.getString("game.labels.movetime") + moveTimeToDecrement.getAndDecrement())));
                    if (moveTimeToDecrement.get() == 0) {
                        gameService.rollDice(lobbyName);
                        tradeService.closeBankTradeWindow(lobbyName);
                        gameService.endTurn(lobbyName);
                    }
                } else {remainingMoveTime = moveTimeToDecrement.get();}
            }
        }, 0, 1000);
    }

    /**
     * Prepares the change size listener
     * <p>
     * Changes the size of the game map when the window size
     * gets changed.
     *
     * @author Temmo Junkhoff
     * @author Maximilian Lindner
     * @since 2021-03-24
     */
    protected void addSizeChangeListener() {
        ChangeListener<Number> listener = (observable, oldValue, newValue) -> fitCanvasToSize();
        window.widthProperty().addListener(listener);
        window.heightProperty().addListener(listener);
    }

    /**
     * Helper method to resize canvas
     * <p>
     * If the window size gets changed, the size of the
     * canvas gets updated accordingly.
     *
     * @author Temmo Junkhoff
     * @author Maximilian Lindner
     * @since 2021-03-29
     */
    protected void fitCanvasToSize() {
        double heightDiff = 0;
        if (gameWon && Objects.equals(owner, userService.getLoggedInUser())) heightDiff = 40;
        double hexFactor = 10.0 / 11.0; // <~0.91 (ratio of tiled hexagons (less high than wide))
        double heightValue = (gameMapCanvas.getScene().getWindow().getHeight() - 60) / hexFactor;
        double widthValue = gameMapCanvas.getScene().getWindow().getWidth() - LobbyPresenter.MIN_WIDTH_PRE_GAME;
        double dimension = Math.min(heightValue, widthValue);
        gameMapCanvas.setHeight(dimension * hexFactor - heightDiff);
        gameMapCanvas.setWidth(dimension);
        gameRendering = new GameRendering(gameMapCanvas);

        if (gameWon) {
            gameRendering.showWinnerText(!Objects.equals(winner, userService.getLoggedInUser()) ?
                                         String.format(resourceBundle.getString("game.won.info"), winner) :
                                         resourceBundle.getString("game.won.you"));
        } else {
            if (gameMap != null)
                // gameMap exists, so redraw map to fit the new canvas dimensions
                gameRendering.drawGameMap(gameMap);
            if (dice1 != null && dice2 != null) gameRendering.drawDice(dice1, dice2);
        }
    }

    /**
     * Helper method to set the help text
     * <p>
     * If this method gets called it checks the current
     * game state to show the user what he can do
     * currently.
     *
     * @author Maximilian Lindner
     * @since 2021-05-01
     */
    protected void setHelpText() {
        if (gameWon) return;
        if (!ownTurn) {
            Text wait = new Text(resourceBundle.getString("game.help.labels.waitforturn"));
            Platform.runLater(() -> {
                helpLabel.getChildren().clear();
                helpLabel.getChildren().add(wait);
            });
        } else {
            Platform.runLater(() -> {
                helpLabel.getChildren().clear();
                Text turn = new Text(resourceBundle.getString("game.help.labels.turn"));
                Text rollDiceText = new Text(resourceBundle.getString("game.help.labels.rolldice"));
                if (!diceRolled) helpLabel.getChildren().addAll(turn, rollDiceText);
                else {
                    rollDiceText.setStrikethrough(true);
                    if (robberNewPosition) {
                        Text setRobber = new Text(resourceBundle.getString("game.help.labels.setrobber"));
                        helpLabel.getChildren().addAll(turn, rollDiceText, setRobber);
                    } else {
                        Text endTurn = new Text(resourceBundle.getString("game.help.labels.endturn"));
                        Text trade = new Text(resourceBundle.getString("game.help.labels.trade"));
                        Text build = new Text(resourceBundle.getString("game.help.labels.build"));
                        Text playCard = new Text(resourceBundle.getString("game.help.labels.playacard"));
                        helpLabel.getChildren().addAll(turn, rollDiceText, trade, build);
                        if (playedCard) {
                            playCard.setStrikethrough(true);
                            helpLabel.getChildren().add(playCard);
                        } else {
                            int cardAmount = 0;
                            for (int i = 0; i < 4; i++) {
                                cardAmount += (int) developmentCardTableView.getItems().get(i + 1).get("amount");
                            }
                            if (cardAmount == 0) playCard.setStrikethrough(true);
                            helpLabel.getChildren().add(playCard);
                            for (int i = 0; i < 4; i++) {
                                Map<String, Object> cardMap = developmentCardTableView.getItems().get(i + 1);
                                if ((int) cardMap.get("amount") > 0) {
                                    Text card = new Text(
                                            String.format(resourceBundle.getString("game.help.labels.playcard"),
                                                          cardMap.get("card")));
                                    helpLabel.getChildren().add(card);
                                }
                            }
                        }
                        helpLabel.getChildren().add(endTurn);
                    }
                }
            });
        }
    }

    /**
     * Helper function that sets the disable state of the rollDiceButton
     *
     * @author Sven Ahrens
     * @author Mario Fokken
     * @since 2021-02-22
     */
    protected void setRollDiceButtonState(UserOrDummy user) {
        rollDice.setDisable(!userService.getLoggedInUser().equals(user));
    }

    /**
     * Helper function that sets the text's text.
     * <p>
     * The text states whose turn it is. The name of the player whose turn it is, is coloured in his personal colour.
     * It also shortens the player's name, if it's longer than 15 characters.
     *
     * @author Sven Ahrens
     * @author Alwin Bossert
     * @author Mario Fokken
     * @author Marvin Drees
     * @since 2021-01-23
     */
    protected void setTurnIndicatorText(UserOrDummy user) {

        Platform.runLater(() -> {
            turnIndicator.getChildren().clear();
            Text preUsernameText = new Text(resourceBundle.getString("lobby.game.text.turnindicator1"));
            preUsernameText.setFont(Font.font(20.0));
            if (theme.equals("dark")) preUsernameText.setFill(Color.web("#F3F5F3"));

            String name = user.getUsername();
            if (name.length() > 15) name = name.substring(0, 15) + "...";
            Text username = new Text(name);
            username.setFont(Font.font(20.0));

            ObservableList<UserOrDummy> membersList = membersView.getItems();
            if (user.equals(membersList.get(0))) {
                username.setFill(GameRendering.PLAYER_1_COLOUR);
            }
            if (user.equals(membersList.get(1))) {
                username.setFill(GameRendering.PLAYER_2_COLOUR);
            }
            if (user.equals(membersList.get(2))) {
                username.setFill(GameRendering.PLAYER_3_COLOUR);
            }
            if (membersList.size() == 4) {
                if (user.equals(membersList.get(3))) {
                    username.setFill(GameRendering.PLAYER_4_COLOUR);
                }
            }
            Text postUsernameText = new Text(resourceBundle.getString("lobby.game.text.turnindicator2"));
            postUsernameText.setFont(Font.font(20.0));
            if (theme.equals("dark")) postUsernameText.setFill(Color.web("#F3F5F3"));
            turnIndicator.getChildren().addAll(preUsernameText, username, postUsernameText);
        });
    }

    /**
     * Helper Method to disable all game related buttons
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @since 2021-03-23
     */
    private void disableButtonStates() {
        tradeWithBankButton.setDisable(true);
        endTurn.setDisable(true);
        tradeWithUserButton.setDisable(true);
        playCard.setDisable(true);
        buildingCurrentlyAllowed = false;
        tradingCurrentlyAllowed = false;
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
        disableButtonStates();
        rollDice.setDisable(true);
        gameService.updateInventory(lobbyName);
        if (helpActivated) setHelpText();
    }

    /**
     * Handles the click on the autoRollCheckBox
     * <p>
     * Method called when the autoRollCheckBox is clicked.
     * It enables and disables autoRoll and posts a request
     * to save the status on the server.
     *
     * @author Mario Fokken
     * @since 2021-04-15
     */
    @FXML
    private void onAutoRollCheckBoxClicked() {
        autoRollEnabled = autoRoll.isSelected();
        gameService.changeAutoRollState(lobbyName, autoRoll.isSelected());
    }

    /**
     * Handles a BuildingFailedResponse
     * If a BuildingFailedResponse is found on the bus this method is called.
     * It shows a small text on the gamemap indicating what went wrong.
     *
     * @param rsp The BuildingFailedMessage found on the bus
     *
     * @author Aldin Dervisi
     * @author Temmo Junkhoff
     * @since 2021-04-07
     */
    @Subscribe
    private void onBuildingFailedResponse(BuildingFailedResponse rsp) {
        if (!lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received BuildingFailedResponse");
        gameRendering.drawGameMap(gameMap);
        switch (rsp.getReason()) {
            case ALREADY_BUILT_HERE:
                gameRendering.showText(resourceBundle.getString("game.building.failed.alreadybuildhere"));
                break;
            case BAD_GROUND:
                gameRendering.showText(resourceBundle.getString("game.building.failed.badground"));
                break;
            case CANT_BUILD_HERE:
                gameRendering.showText(resourceBundle.getString("game.building.failed.cantbuildhere"));
                break;
            case NOTHING_HERE:
                gameRendering.showText(resourceBundle.getString("game.building.failed.nothinghere"));
                break;
            case NOT_ENOUGH_RESOURCES:
                gameRendering.showText(resourceBundle.getString("game.building.failed.notenoughresources"));
                break;
            case NOT_THE_RIGHT_TIME:
                gameRendering.showText(resourceBundle.getString("game.building.failed.nottherighttime"));
                break;
        }
    }

    /**
     * Handles a BuildingSuccessfulMessage
     * If a BuildingSuccessfulMessage is found on the bus this method is called.
     * It updates the gamemap and for the user that build something the inventory.
     *
     * @param msg The BuildingSuccessfulMessage found on the bus
     *
     * @author Aldin Dervisi
     * @author Temmo Junkhoff
     * @since 2021-04-07
     */
    @Subscribe
    private void onBuildingSuccessfulMessage(BuildingSuccessfulMessage msg) {
        if (!Objects.equals(msg.getLobbyName(), lobbyName)) return;
        LOG.debug("Received BuildingSuccessfulMessage");
        if (roadBuildingCardPhase == RoadBuildingCardPhase.WAITING_FOR_FIRST_ROAD) {
            roadBuildingCardPhase = RoadBuildingCardPhase.WAITING_FOR_SECOND_ROAD;
            LOG.debug("---- First road successfully built");
            Platform.runLater(() -> notice.setText(resourceBundle.getString("game.playcards.roadbuilding.second")));
        } else if (roadBuildingCardPhase == RoadBuildingCardPhase.WAITING_FOR_SECOND_ROAD) {
            roadBuildingCardPhase = RoadBuildingCardPhase.NO_ROAD_BUILDING_CARD_PLAYED;
            LOG.debug("---- Second road successfully built");
            Platform.runLater(() -> notice.setVisible(false));
            resetButtonStates(userService.getLoggedInUser());
        }
        gameService.updateGameMap(lobbyName);
        String attr = null;
        switch (msg.getType()) {
            case ROAD:
                attr = "game.building.success.road";
                break;
            case SETTLEMENT:
                attr = "game.building.success.settlement";
                break;
            case CITY:
                attr = "game.building.success.city";
                break;
        }
        final String finalAttr = attr;
        if (Objects.equals(msg.getUser(), userService.getLoggedInUser())) {
            gameService.updateInventory(lobbyName);
            if (finalAttr != null) Platform.runLater(
                    () -> chatMessages.add(new InGameSystemMessageDTO(new I18nWrapper(finalAttr + ".you"))));
        } else {
            if (finalAttr != null) Platform.runLater(() -> chatMessages
                    .add(new InGameSystemMessageDTO(new I18nWrapper(finalAttr + ".other", msg.getUser().toString()))));
        }
        if (helpActivated) setHelpText();
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
        if (!lobbyName.equals(msg.getLobbyName())) return;
        LOG.debug("Received DiceCastMessage");
        LOG.debug("---- The dices show: {} and {}", msg.getDice1(), msg.getDice2());
        playedCard = false;
        dice1 = msg.getDice1();
        dice2 = msg.getDice2();
        if ((dice1 + dice2) != 7) {
            resetButtonStates(msg.getUser());
        }
        gameRendering.drawDice(msg.getDice1(), msg.getDice2());
        gameService.updateInventory(lobbyName);
        if (helpActivated) setHelpText();
    }

    /**
     * Method called when the EndTurnButton is pressed
     * <p>
     * If the EndTurnButton is pressed, this method disables all appropriate
     * buttons and then requests the LobbyService to end the current turn.
     *
     * @see de.uol.swp.client.lobby.ILobbyService
     * @since 2021-01-15
     */
    @FXML
    private void onEndTurnButtonPressed() {
        disableButtonsAfterTurn();
        gameService.endTurn(lobbyName);
        diceRolled = false;
    }

    /**
     * Method called when the HelpButton is pressed
     * <p>
     * If the help button gets pressed and help is not activated yet,
     * this method increases the size of the game window for the help
     * section and calls a method to fill the help text.
     * Otherwise the size of the window decreases.
     *
     * @author Maximilian Lindner
     * @since 2021-05-01
     */
    @FXML
    private void onHelpButtonPressed() {
        if (!helpActivated) {
            int size = LobbyPresenter.MIN_WIDTH_IN_GAME + LobbyPresenter.HELP_MIN_WIDTH;
            helpColumn.setMinWidth(LobbyPresenter.HELP_MIN_WIDTH);
            ((Stage) window).setMinWidth(size);
            window.setWidth(size);
            setHelpText();
        } else {
            helpColumn.setMaxWidth(0);
            helpColumn.setMinWidth(0);
            helpLabel.getChildren().clear();
            ((Stage) window).setMinWidth(LobbyPresenter.MIN_WIDTH_IN_GAME);
            window.setWidth(LobbyPresenter.MIN_WIDTH_IN_GAME);
        }
        helpActivated = !helpActivated;
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
        if ((roadBuildingCardPhase == RoadBuildingCardPhase.WAITING_FOR_FIRST_ROAD || roadBuildingCardPhase == RoadBuildingCardPhase.WAITING_FOR_SECOND_ROAD) && mapPoint.getType() == EDGE) {
            gameService.buildRequest(lobbyName, mapPoint);
        }
        if (buildingCurrentlyAllowed && (mapPoint.getType() == INTERSECTION || mapPoint.getType() == EDGE))
            gameService.buildRequest(lobbyName, mapPoint);
        if (mapPoint.getType() == HEX && robberNewPosition) {
            gameService.robberNewPosition(lobbyName, mapPoint);
            robberNewPosition = false;
            notice.setVisible(false);
            resetButtonStates(userService.getLoggedInUser());
            if (helpActivated)setHelpText();
        }
    }

    /**
     * Handles a NextPlayerMessage
     * <p>
     * If a new NextPlayerMessage object is posted onto the EventBus,
     * this method is called.
     * It changes the text of a textField to state whose turn it is.
     * It also sets the timer back to the moveTime
     *
     * @param msg The NextPlayerMessage object seen on the EventBus
     */
    @Subscribe
    private void onNextPlayerMessage(NextPlayerMessage msg) {
        if (!msg.getLobbyName().equals(lobbyName)) return;
        LOG.debug("Received NextPlayerMessage for Lobby {}", msg.getLobbyName());
        gameService.updateGameMap(lobbyName);
        setTurnIndicatorText(msg.getActivePlayer());
        setRollDiceButtonState(msg.getActivePlayer());
        ownTurn = msg.getActivePlayer().equals(userService.getLoggedInUser());
        if (helpActivated)setHelpText();
        if (!rollDice.isDisabled() && autoRollEnabled) onRollDiceButtonPressed();
        moveTimeTimer.cancel();
        setMoveTimer(moveTime);
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
        ButtonType btnKnight = new ButtonType(resourceBundle.getString("game.resources.cards.knight"));
        ButtonType btnMonopoly = new ButtonType(resourceBundle.getString("game.resources.cards.monopoly"));
        ButtonType btnRoadBuilding = new ButtonType(resourceBundle.getString("game.resources.cards.roadbuilding"));
        ButtonType btnYearOfPlenty = new ButtonType(resourceBundle.getString("game.resources.cards.yearofplenty"));
        ButtonType btnCancel = new ButtonType(resourceBundle.getString("button.cancel"),
                                              ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnKnight, btnMonopoly, btnRoadBuilding, btnYearOfPlenty, btnCancel);
        alert.getDialogPane().getStylesheets().add(styleSheet);
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
        if (result.get() == btnKnight) { //Play a Knight Card
            gameService.playKnightCard(lobbyName);
            disableButtonStates();
        } else if (result.get() == btnMonopoly) { //Play a Monopoly Card
            playMonopolyCard(ore, grain, brick, lumber, wool, choices);
        } else if (result.get() == btnRoadBuilding) { //Play a Road Building Card
            notice.setText(resourceBundle.getString("game.playcards.roadbuilding.first"));
            notice.setVisible(true);
            disableButtonStates();
            roadBuildingCardPhase = RoadBuildingCardPhase.WAITING_FOR_FIRST_ROAD;
            gameService.playRoadBuildingCard(lobbyName);
        } else if (result.get() == btnYearOfPlenty) { //Play a Year Of Plenty Card
            playYearOfPlentyCard(ore, grain, brick, lumber, wool, choices);
        }
    }

    /**
     * Handles a PlayCardFailureResponse found on the EventBus
     *
     * @param rsp The PlayCardFailureResponse found on the EventBus
     *
     * @author Mario Fokken
     * @author Eric Vuong
     * @see de.uol.swp.common.game.response.PlayCardFailureResponse
     * @since 2021-02-26
     */
    @Subscribe
    private void onPlayCardFailureResponse(PlayCardFailureResponse rsp) {
        if (!lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received PlayCardFailureResponse");
        if (!userService.getLoggedInUser().equals(rsp.getUser())) return;
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resourceBundle.getString("game.playcards.failure.title"));
            alert.setHeaderText(resourceBundle.getString("game.playcards.failure.header"));
            ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"),
                                                ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(confirm);
            if (rsp.getReason().equals(PlayCardFailureResponse.Reasons.NO_CARDS))
                alert.setContentText(resourceBundle.getString("game.playcards.failure.context.noCards"));
            alert.getDialogPane().getStylesheets().add(styleSheet);
            alert.showAndWait();
        });
    }

    /**
     * Handles a PlayCardSuccessResponse found on the EventBus
     *
     * @param rsp The PlayCardSuccessResponse found on the EventBus
     *
     * @author Mario Fokken
     * @author Eric Vuong
     * @see de.uol.swp.common.game.response.PlayCardSuccessResponse
     * @since 2021-02-26
     */
    @Subscribe
    private void onPlayCardSuccessResponse(PlayCardSuccessResponse rsp) {
        if (!lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received PlayCardSuccessResponse");
        playCard.setDisable(true);
        playedCard = true;
        if (helpActivated) setHelpText();
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
        LOG.debug("Received RefreshCardAmountMessage");
        if (!lobbyName.equals(msg.getLobbyName())) return;
        cardAmountTripleList = msg.getCardAmountTriples();
        lobbyService.retrieveAllLobbyMembers(lobbyName);
        if (helpActivated) setHelpText();
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
        if (!lobbyName.equals(event.getLobbyName())) return;
        resetButtonStates(userService.getLoggedInUser());
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
        buildingCosts.setVisible(false);
        inGame = false;
        lobbyService.returnToPreGameLobby(lobbyName);
    }

    /**
     * Handles a RobberAllTaxPayedMessage
     *
     * @param msg The RobberAllTaxPayedMessage found on the EventBus
     *
     * @author Mario Fokken
     * @since 2021-04-23
     */
    @Subscribe
    private void onRobberAllTaxPayedMessage(RobberAllTaxPayedMessage msg) {
        if (msg.getLobbyName().equals(lobbyName)) {
            resetButtonStates(msg.getUser());
            if (helpActivated) setHelpText();
        }
        if (msg.getLobbyName().equals(lobbyName)) resetButtonStates(msg.getUser());
        eventBus.post(new UnpauseTimerRequest(lobbyName, userService.getLoggedInUser()));
    }

    /**
     * Handles a RobberChooseVictimResponse
     *
     * @param rsp The RobberChooseVictimResponse found on the EventBus
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-06
     */
    @Subscribe
    private void onRobberChooseVictimResponse(RobberChooseVictimResponse rsp) {
        LOG.debug("Received RobberChooseVictimResponse");
        if (userService.getLoggedInUser().equals(rsp.getPlayer())) {
            Platform.runLater(() -> {
                List<UserOrDummy> victims = new ArrayList<>(rsp.getVictims());
                ChoiceDialog<UserOrDummy> dialogue = new ChoiceDialog<>(victims.get(0), victims);
                dialogue.setTitle(resourceBundle.getString("game.robber.victim.title"));
                dialogue.setHeaderText(resourceBundle.getString("game.robber.victim.header"));
                dialogue.setContentText(resourceBundle.getString("game.robber.victim.content"));
                DialogPane pane = new DialogPane();
                pane.setContent(dialogue.getDialogPane().getContent());
                ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"),
                                                    ButtonBar.ButtonData.OK_DONE);
                ButtonType cancel = new ButtonType(resourceBundle.getString("button.cancel"),
                                                   ButtonBar.ButtonData.CANCEL_CLOSE);
                dialogue.setDialogPane(pane);
                dialogue.getDialogPane().getButtonTypes().addAll(confirm, cancel);
                dialogue.getDialogPane().getStylesheets().add(styleSheet);
                Optional<UserOrDummy> rst = dialogue.showAndWait();
                rst.ifPresent(userOrDummy -> gameService.robberChooseVictim(lobbyName, userOrDummy));
            });
        }
    }

    /**
     * Handles a RobberNewPositionResponse
     *
     * @param rsp The RobberNewPositionResponse found on the EventBus
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-06
     */
    @Subscribe
    private void onRobberNewPositionResponse(RobberNewPositionResponse rsp) {
        LOG.debug("Received RobberNewPositionResponse");
        Platform.runLater(() -> notice.setText(resourceBundle.getString("game.robber.position")));
        notice.setVisible(true);
        robberNewPosition = true;
        if (helpActivated) setHelpText();
    }

    /**
     * Handles a RobberPositionMessage
     *
     * @param msg The RobberPositionMessage found on the EventBus
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-08
     */
    @Subscribe
    private void onRobberPositionMessage(RobberPositionMessage msg) {
        LOG.debug("Received RobberPositionMessage for Lobby {}", msg.getLobbyName());
        if (lobbyName.equals(msg.getLobbyName())) {
            resetButtonStates(msg.getUser());
            gameService.updateGameMap(msg.getLobbyName());
            if (helpActivated) setHelpText();
        }
    }

    /**
     * Handles a RobberTaxMessage
     * It also posts a new PauseTimerRequest onto the EventBus
     *
     * @param msg The RobberTaxMessage found on the EventBus
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-06
     */
    @Subscribe
    private void onRobberTaxMessage(RobberTaxMessage msg) {
        LOG.debug("Received RobberTaxMessage");
        if (msg.getLobbyName().equals(lobbyName)) {
            disableButtonStates();
            if (helpActivated) setHelpText();
            if (msg.getPlayers().containsKey(userService.getLoggedInUser())) {
                LOG.debug("Sending ShowRobberTaxViewEvent");
                User user = userService.getLoggedInUser();
                eventBus.post(new ShowRobberTaxViewEvent(msg.getLobbyName(), msg.getPlayers().get(user),
                                                         msg.getInventory().get(user)));
                eventBus.post(new PauseTimerRequest(lobbyName, userService.getLoggedInUser()));
            }
        }
    }

    /**
     * Method called when the rollDice Button is pressed
     * <p>
     * If the rollDice Button is pressed, this method requests the LobbyService
     * to roll the dices.
     *
     * @author Mario Fokken
     * @author Sven Ahrens
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2021-02-22
     */
    @FXML
    private void onRollDiceButtonPressed() {
        gameService.rollDice(lobbyName);
        rollDice.setDisable(true);
        diceRolled = true;
        if (helpActivated) setHelpText();
    }

    /**
     * Handles a TradeOfUsersAcceptedResponse found on the EventBus
     * Updates the Inventories of the trading User.
     * It also posts a new UnpauseTimerRequest onto the EventBus
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
        gameService.updateInventory(lobbyName);
        eventBus.post(new UnpauseTimerRequest(lobbyName, userService.getLoggedInUser()));
    }

    /**
     * Handles a click on the TradeWithBank Button
     * <p>
     * Method called when the TradeWithBankButton is pressed. It calls on
     * the TradeService to show the Trade with Bank window and request the
     * Bank's inventory.
     *
     * @author Alwin Bossert
     * @author Maximilian Lindner
     * @since 2021-02-20
     */
    @FXML
    private void onTradeWithBankButtonPressed() {
        disableButtonStates();
        tradeService.showBankTradeWindow(lobbyName);
        tradeService.tradeWithBank(lobbyName);
    }

    /**
     * Handles a Click on the TradeWithUserButton
     * <p>
     * If another player of the lobby-member-list is selected and the button gets pressed,
     * this button gets disabled, this method calls on the TradeService to show the Trade
     * with User window and request the inventory overview for the selected user.
     * It also posts a new PauseTimerRequest onto the EventBus.
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @since 2021-02-23
     */
    @FXML
    private void onTradeWithUserButtonPressed() {
        membersView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        UserOrDummy user = membersView.getSelectionModel().getSelectedItem();
        if (membersView.getSelectionModel().isEmpty() || user == null) {
            tradeService.showTradeError(resourceBundle.getString("game.trade.error.noplayer"));
        } else if (Objects.equals(user, userService.getLoggedInUser())) {
            tradeService.showTradeError(resourceBundle.getString("game.trade.error.selfplayer"));
        } else {
            disableButtonStates();
            tradeService.showUserTradeWindow(lobbyName, user);
            tradeService.tradeWithUser(lobbyName, user);
            eventBus.post(new PauseTimerRequest(lobbyName, userService.getLoggedInUser()));
        }
    }

    /**
     * Handles a TradeWithUserCancelResponse found on the event bus
     * <p>
     * If a TradeWithUserCancelResponse is posted onto the EventBus the
     * the possible options for the active player are re-enabled.
     * It also posts a new UnpauseTimerRequest onto the EventBus
     *
     * @param rsp The TradeWithUserCancelResponse seen on the EventBus
     *
     * @author Aldin Dervisi
     * @author Maximilian Lindner
     * @since 2021-03-19
     */
    @Subscribe
    private void onTradeWithUserCancelResponse(TradeWithUserCancelResponse rsp) {
        if (!rsp.getActivePlayer().equals(userService.getLoggedInUser())) return;
        resetButtonStates(userService.getLoggedInUser());
        if (helpActivated) setHelpText();
        eventBus.post(new UnpauseTimerRequest(lobbyName, userService.getLoggedInUser()));
    }

    /**
     * Handles the TradeWithUserOfferResponse found on the EventBus
     * If a user gets a trading offer, this method calls the TradeService
     * to display the Accept Offer window.
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
        if (!rsp.getLobbyName().equals(lobbyName)) return;
        LOG.debug("Sending ShowTradeWithUserRespondViewEvent");
        tradeService.showOfferWindow(lobbyName, rsp.getOfferingUser(), rsp);
    }

    /**
     * Handles a UpdateGameMapResponse
     * If a UpdateGameMapResponse is found on the bus this method is called.
     * It updates the gamemap and redraws it.
     *
     * @param rsp The UpdateGameMapResponse found on the bus
     *
     * @author Aldin Dervisi
     * @author Temmo Junkhoff
     * @since 2021-04-08
     */
    @Subscribe
    private void onUpdateGameMapResponse(UpdateGameMapResponse rsp) {
        if (!Objects.equals(rsp.getLobbyName(), lobbyName)) return;
        LOG.debug("Received UpdateGameMapResponse");
        if (rsp.getGameMapDTO() == null) return;
        gameMap = rsp.getGameMapDTO();
        gameRendering.drawGameMap(gameMap);
        gameRendering.drawDice(dice1, dice2);
    }

    /**
     * Handles an UpdateInventoryResponse found on the EventBus
     * <p>
     * If the UpdateInventoryResponse is intended for the current Lobby, the
     * contained lists of Maps are localised with the ResourceBundle injected
     * into the LobbyPresenter and afterwards added into the respective
     * TableView, where they are processed by the TableView columns'
     * MapValueFactories.
     *
     * @param rsp The UpdateInventoryResponse found on the EventBus
     *
     * @author Phillip-André Suhr
     * @see de.uol.swp.common.game.response.UpdateInventoryResponse
     * @since 2021-04-17
     */
    @Subscribe
    private void onUpdateInventoryResponse(UpdateInventoryResponse rsp) {
        if (!rsp.getLobbyName().equals(lobbyName)) return;
        LOG.debug("Received UpdateInventoryResponse for Lobby {}", lobbyName);
        Platform.runLater(() -> {
            resourceTableView.getItems().setAll(rsp.getResourceList());
            resourceTableView.sort();
            developmentCardTableView.getItems().setAll(rsp.getDevelopmentCardList());
            developmentCardTableView.sort();
        });
    }

    /**
     * Helper Method to play a monopoly card
     *
     * @author Mario Fokken
     * @since 2021-02-25
     */
    private void playMonopolyCard(String ore, String grain, String brick, String lumber, String wool,
                                  List<String> choices) {
        //Creating a dialogue
        ChoiceDialog<String> dialogue = new ChoiceDialog<>(brick, choices);
        dialogue.setTitle(resourceBundle.getString("game.playcards.monopoly.title"));
        dialogue.setHeaderText(resourceBundle.getString("game.playcards.monopoly.header"));
        dialogue.setContentText(resourceBundle.getString("game.playcards.monopoly.context"));
        //Creating a new DialogPane so the button text can be customised
        DialogPane pane = new DialogPane();
        pane.setContent(dialogue.getDialogPane().getContent());
        ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(resourceBundle.getString("button.cancel"),
                                           ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogue.setDialogPane(pane);
        dialogue.getDialogPane().getButtonTypes().addAll(confirm, cancel);
        dialogue.getDialogPane().getStylesheets().add(styleSheet);
        //Show the dialogue and get the result
        Optional<String> rst = dialogue.showAndWait();
        //Convert String to Resources and send the request
        Resources resource = Resources.BRICK;
        if (rst.isPresent()) {
            if (rst.get().equals(ore)) resource = Resources.ORE;
            else if (rst.get().equals(grain)) resource = Resources.GRAIN;
            else if (rst.get().equals(lumber)) resource = Resources.LUMBER;
            else if (rst.get().equals(wool)) resource = Resources.WOOL;
            gameService.playMonopolyCard(lobbyName, resource);
        }
    }

    /**
     * Helper Method to play a year of plenty card.
     *
     * @author Mario Fokken
     * @since 2021-02-25
     */
    private void playYearOfPlentyCard(String ore, String grain, String brick, String lumber, String wool,
                                      List<String> choices) {
        //Create a dialogue
        Dialog<String> dialogue = new Dialog<>();
        dialogue.setTitle(resourceBundle.getString("game.playcards.yearofplenty.title"));
        dialogue.setHeaderText(resourceBundle.getString("game.playcards.yearofplenty.header"));
        //Create its buttons
        ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"), ButtonBar.ButtonData.OK_DONE);
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
        dialogue.getDialogPane().getStylesheets().add(styleSheet);
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
            gameService.playYearOfPlentyCard(lobbyName, resource1, resource2);
        }
    }

    /**
     * Helper method to create the necessary Map structure
     * required for MapValueFactories
     * <p>
     * This method creates a Map containing a key "amount" with value 0 and
     * another key "resource" or "card", depending on the parameter {@literal <type>},
     * with the internationalised name of the Resource or Development Card based
     * on the parameter {@literal <item>}.
     *
     * @param type Either "resource" for Resources or "card" for Development Cards
     * @param item Lowercase Resource name for Resources or fully qualified i18n key
     *             for Development Cards <p>
     *             e.g. {@code prepareEmptyResourceMap("card", "game.resources.cards.knight")}
     *             or {@code prepareEmptyResourceMap("resource", "brick")}
     *
     * @return A Map representing 0 of a resource
     *
     * @author Phillip-André Suhr
     * @since 2021-04-18
     */
    private Map<String, Object> prepareEmptyResourceMap(String type, String item) {
        Map<String, Object> resourceMap = new HashMap<>();
        resourceMap.put("amount", 0);
        String preFormat;
        if (type.equals("resource")) { // Resource like Brick
            preFormat = "game.resources.%s";
            resourceMap.put("enumType", Resources.valueOf(item.toUpperCase()));
        } else { // Development Card like Knight Card
            preFormat = "%s";
        }
        resourceMap.put(type, new I18nWrapper(String.format(preFormat, item)));
        return resourceMap;
    }

    /**
     * Prepares the TableViews displaying the inventory
     * <p>
     * Prepares the TableView by setting the CellValueFactories of the
     * different TableColumns to MapValueFactories. Also adds entries
     * to each TableView displaying 0 of all resources and development cards.
     *
     * @author Phillip-André Suhr
     * @since 2021-04-18
     */
    private void prepareInventoryTables() {
        resourceAmountCol.setCellValueFactory(new MapValueFactory<>("amount"));
        resourceNameCol.setCellValueFactory(new MapValueFactory<>("resource"));
        developmentCardAmountCol.setCellValueFactory(new MapValueFactory<>("amount"));
        developmentCardNameCol.setCellValueFactory(new MapValueFactory<>("card"));

        List<Map<String, Object>> inventoryItems = new ArrayList<>();
        for (Resources resource : Resources.values()) {
            inventoryItems.add(prepareEmptyResourceMap("resource", resource.name().toLowerCase()));
        }
        resourceTableView.getItems().addAll(inventoryItems);

        List<Map<String, Object>> developmentCards = new ArrayList<>();
        developmentCards.add(prepareEmptyResourceMap("card", "game.resources.cards.victorypoints"));
        developmentCards.add(prepareEmptyResourceMap("card", "game.resources.cards.knight"));
        developmentCards.add(prepareEmptyResourceMap("card", "game.resources.cards.roadbuilding"));
        developmentCards.add(prepareEmptyResourceMap("card", "game.resources.cards.yearofplenty"));
        developmentCards.add(prepareEmptyResourceMap("card", "game.resources.cards.monopoly"));
        developmentCardTableView.getItems().setAll(developmentCards);
    }

    /**
     * Prepares the UniqueCardView
     * <p>
     * Adds listener to the UniqueCardView
     *
     * @author Temmo Junkhoff
     * @author Maximilian Lindner
     * @since 2021-03-29
     */
    private void prepareUniqueCardView() {
        uniqueCardView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Triple<String, UserOrDummy, Integer> uniqueCardTriple, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(uniqueCardTriple, empty);
                    if (empty || uniqueCardTriple == null) setText("");
                    else {
                        UserOrDummy value2 = uniqueCardTriple.getValue2();
                        String who;
                        if (value2 == null) who = resourceBundle.getString("game.resources.whohas.nobody");
                        else who = value2.getUsername();
                        setText(String.format(resourceBundle.getString(uniqueCardTriple.getValue1()), who,
                                              uniqueCardTriple.getValue3()));
                    }
                });
            }
        });
        //TODO: remove the following from initialize when largest army and longest road are tracked by the game
        if (uniqueCardList == null) {
            uniqueCardList = FXCollections.observableArrayList();
            uniqueCardView.setItems(uniqueCardList);
        }
        uniqueCardList.add(new Triple<>("game.resources.whohas.largestarmy", null, 0));
        uniqueCardList.add(new Triple<>("game.resources.whohas.longestroad", null, 0));
    }

    /**
     * Helper Method to reset all game related states
     *
     * @param user The user who is currently active
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @since 2021-03-23
     */
    private void resetButtonStates(UserOrDummy user) {
        tradeWithBankButton.setDisable(!userService.getLoggedInUser().equals(user));
        endTurn.setDisable(!userService.getLoggedInUser().equals(user));
        tradeWithUserButton.setDisable(!userService.getLoggedInUser().equals(user));
        playCard.setDisable(playedCard || !userService.getLoggedInUser().equals(user));
        buildingCurrentlyAllowed = userService.getLoggedInUser().equals(user);
        tradingCurrentlyAllowed = userService.getLoggedInUser().equals(user);
    }
}
