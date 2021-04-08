package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenterWithChat;
import de.uol.swp.client.GameRendering;
import de.uol.swp.client.trade.event.*;
import de.uol.swp.common.game.map.IGameMapDTO;
import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.TradeWithBankRequest;
import de.uol.swp.common.game.request.TradeWithUserRequest;
import de.uol.swp.common.game.response.*;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Pair;

import java.util.*;

import static de.uol.swp.common.game.map.MapPoint.Type.EDGE;
import static de.uol.swp.common.game.map.MapPoint.Type.INTERSECTION;

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
@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractPresenterWithChatWithGame extends AbstractPresenterWithChat {

    @FXML
    protected Button endTurn;
    @FXML
    protected Canvas gameMapCanvas;
    @FXML
    protected ListView<Pair<String, String>> inventoryView;
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
    protected Label turnIndicator;
    @FXML
    protected ListView<Triple<String, UserOrDummy, Integer>> uniqueCardView;

    protected List<Triple<UserOrDummy, Integer, Integer>> cardAmountTripleList;
    protected Integer dice1;
    protected Integer dice2;
    protected IGameMapDTO gameMap;
    protected GameRendering gameRendering;
    protected boolean gameWon = false;
    protected boolean inGame;
    protected int moveTime;
    protected User owner;
    protected ObservableList<Triple<String, UserOrDummy, Integer>> uniqueCardList;
    protected Window window;
    protected UserOrDummy winner = null;

    private ObservableList<Pair<String, String>> resourceList;
    private boolean buildingCurrentlyAllowed;

    @Override
    @FXML
    protected void initialize() {
        super.initialize();
        prepareInventoryView();
        prepareUniqueCardView();
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
        if (gameWon && Objects.equals(owner, loggedInUser)) heightDiff = 40;
        double hexFactor = 10.0 / 11.0; // <~0.91 (ratio of tiled hexagons (less high than wide))
        double heightValue = (gameMapCanvas.getScene().getWindow().getHeight() - 60) / hexFactor;
        double widthValue = gameMapCanvas.getScene().getWindow().getWidth() - LobbyPresenter.MIN_WIDTH_PRE_GAME;
        double dimension = Math.min(heightValue, widthValue);
        gameMapCanvas.setHeight(dimension * hexFactor - heightDiff);
        gameMapCanvas.setWidth(dimension);
        gameRendering = new GameRendering(gameMapCanvas);

        if (gameWon) {
            gameRendering.showWinnerText(!Objects.equals(winner, loggedInUser) ?
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
     * Helper function that sets the disable state of the rollDiceButton
     *
     * @author Sven Ahrens
     * @author Mario Fokken
     * @since 2021-02-22
     */
    protected void setRollDiceButtonState(UserOrDummy user) {
        rollDice.setDisable(!loggedInUser.equals(user));
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
    protected void setTurnIndicatorText(UserOrDummy user) {
        Platform.runLater(() -> turnIndicator
                .setText(String.format(resourceBundle.getString("lobby.game.text.turnindicator"), user.getUsername())));
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
        this.rollDice.setDisable(true);
        lobbyService.updateInventory(lobbyName, loggedInUser);
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
        resetButtonStates(rsp.getUser());
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
        resetButtonStates(msg.getUser());
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
     * @see de.uol.swp.client.lobby.ILobbyService
     * @since 2021-01-15
     */
    @FXML
    private void onEndTurnButtonPressed() {
        disableButtonsAfterTurn();
        lobbyService.endTurn(loggedInUser, lobbyName);
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
        if (buildingCurrentlyAllowed && (mapPoint.getType() == INTERSECTION || mapPoint.getType() == EDGE))
            lobbyService.buildRequest(lobbyName, loggedInUser, mapPoint);
    }

    @Subscribe
    private void onBuildingFailedResponse(BuildingFailedResponse rsp) {
        if (!lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received BuildingFailedResponse");
        gameRendering.drawGameMap(gameMap);
        switch (rsp.getReason()) {
            case CANT_BUILD_HERE:
                gameRendering.showText(resourceBundle.getString("game.building.cantbuildhere"));
                break;
            case NOT_ENOUGH_RESOURCES:
                gameRendering.showText(resourceBundle.getString("game.building.notenoughresources"));
        }
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
            lobbyService.playKnightCard(lobbyName, loggedInUser);
        } else if (result.get() == btnMonopoly) { //Play a Monopoly Card
            playMonopolyCard(ore, grain, brick, lumber, wool, choices);
        } else if (result.get() == btnRoadBuilding) { //Play a Road Building Card
            lobbyService.playRoadBuildingCard(lobbyName, loggedInUser);
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

    @Subscribe
    private void onBuildingSuccessfulMessage(BuildingSuccessfulMessage msg) {
        if (!Objects.equals(msg.getLobbyName(), lobbyName)) return;
        LOG.debug("Received BuildingSuccessfullMessage");
        lobbyService.updateGameMap(lobbyName);
        if (Objects.equals(msg.getUser(), loggedInUser)) lobbyService.updateInventory(lobbyName, loggedInUser);
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
        lobbyService.updateGameMap(lobbyName);
        setTurnIndicatorText(msg.getActivePlayer());
        setRollDiceButtonState(msg.getActivePlayer());
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
        lobbyService.updateInventory(rsp.getLobbyName(), rsp.getUser());
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
     * @see de.uol.swp.common.game.message.PlayerWonGameMessage
     * @since 2021-03-22
     */
    @Subscribe
    private void onPlayerWonGameMessage(PlayerWonGameMessage msg) {
        if (!msg.getLobbyName().equals(this.lobbyName)) return;
        gameMap = null;
        gameWon = true;
        winner = msg.getUser();
        if (Objects.equals(owner, loggedInUser)) {
            returnToLobby.setVisible(true);
            returnToLobby.setPrefHeight(30);
            returnToLobby.setPrefWidth(250);
        }
        fitCanvasToSize();
    }

    @Subscribe
    private void onUpdateGameMapResponse(UpdateGameMapResponse rsp) {
        if (!Objects.equals(rsp.getLobbyName(), lobbyName)) return;
        LOG.debug("Received UpdateGameMapResponse");
        if (rsp.getGameMapDTO() == null) return;
        gameMap = rsp.getGameMapDTO();
        gameRendering.drawGameMap(gameMap);
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
            resetButtonStates(event.getUser());
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
        lobbyService.rollDice(lobbyName, loggedInUser);
        this.rollDice.setDisable(true);
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
        resetButtonStates(rsp.getUser());
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
        disableButtonStates();
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
        } else if (Objects.equals(user, loggedInUser)) {
            eventBus.post(new TradeErrorEvent(resourceBundle.getString("game.trade.error.selfplayer")));
        } else {
            disableButtonStates();
            LOG.debug("Sending ShowTradeWithUserViewEvent");
            eventBus.post(new ShowTradeWithUserViewEvent(loggedInUser, lobbyName, user));
            LOG.debug("Sending a TradeWithUserRequest for Lobby " + lobbyName);
            eventBus.post(new TradeWithUserRequest(lobbyName, loggedInUser, user));
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
        if (!rsp.getActivePlayer().equals(loggedInUser)) return;
        resetButtonStates(loggedInUser);
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
        if (!rsp.getLobbyName().equals(lobbyName)) return;
        LOG.debug("Sending ShowTradeWithUserRespondViewEvent");
        eventBus.post(new ShowTradeWithUserRespondViewEvent(rsp.getOfferingUser(), loggedInUser, lobbyName, rsp));
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
        if (!rsp.getLobbyName().equals(lobbyName)) return;
        LOG.debug("Received UpdateInventoryResponse for Lobby " + lobbyName);
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
        lobbyService.checkVictoryPoints(this.lobbyName, this.loggedInUser);
    }

    /**
     * Helper Method to play a monopoly card
     *
     * @author Mario Fokken
     * @author Temmo Junkhoff
     * @author Maximilian Lindner
     * @since 2021-03-29
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
    }

    /**
     * Helper Method to play a year of plenty card.
     *
     * @author Mario Fokken
     * @author Temmo Junkhoff
     * @author Maximilian Lindner
     * @since 2021-03-29
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

    /**
     * Prepares the InventoryView
     * <p>
     * Prepares the inventoryView for proper formatting.
     *
     * @author Temmo Junkhoff
     * @author Maximilian Lindner
     * @since 2021-03-29
     */
    private void prepareInventoryView() {
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
        tradeWithBankButton.setDisable(!loggedInUser.equals(user));
        endTurn.setDisable(!loggedInUser.equals(user));
        tradeWithUserButton.setDisable(!loggedInUser.equals(user));
        playCard.setDisable(!loggedInUser.equals(user));
        buildingCurrentlyAllowed = loggedInUser.equals(user);
    }
}
