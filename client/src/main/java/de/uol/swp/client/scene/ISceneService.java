package de.uol.swp.client.scene;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * An interface for all methods of the SceneService
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.scene.SceneService
 * @see de.uol.swp.client.scene.AsyncSceneService
 * @since 2021-06-25
 */
public interface ISceneService {

    /**
     * Closes the Accept Trade window associated with the provided LobbyName,
     * if one exists.
     *
     * @param lobbyName The LobbyName to close the associated Accept Trade window of
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void closeAcceptTradeWindow(LobbyName lobbyName);

    /**
     * Closes all opened Lobby Windows, if any exist.
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void closeAllLobbyWindows();

    /**
     * Closes the Bank Trade window associated with the provided LobbyName,
     * if one exists.
     *
     * @param lobbyName   The LobbyName to close the associated Bank Trade window of
     * @param wasCanceled Whether the window closure occurs because the User closed
     *                    the window without completing a trade.
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void closeBankTradeWindow(LobbyName lobbyName, boolean wasCanceled);

    /**
     * Closes the Robber Tax window associated with the provided LobbyName,
     * if one exists.
     *
     * @param lobbyName The LobbyName to close the associated Robber Tax window of
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void closeRobberTaxWindow(LobbyName lobbyName);

    /**
     * Closes the User Trade window associated with the provided LobbyName,
     * if one exists.
     *
     * @param lobbyName The LobbyName to close the associated User Trade window of
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void closeUserTradeWindow(LobbyName lobbyName);

    /**
     * Displays the Change Account Details screen on the primary Stage
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void displayChangeAccountDetailsScreen();

    /**
     * Displays the Change Settings screen on the primary Stage
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void displayChangeSettingsScreen();

    /**
     * Displays the Login screen on the primary Stage
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void displayLoginScreen();

    /**
     * Displays the Main Menu screen on the primary Stage
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void displayMainMenuScreen();

    /**
     * Displays the Registration screen on the primary Stage
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void displayRegistrationScreen();

    /**
     * Opens a new Accept Trade window associated with the provided LobbyName
     *
     * @param lobbyName    The LobbyName to associate the Accept Trade window with
     * @param offeringUser The Actor who offered the trade
     * @param rsp          The TradeWithUserOfferResponse containing the details
     *                     of the trade offer.
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void openAcceptTradeWindow(LobbyName lobbyName, Actor offeringUser, TradeWithUserOfferResponse rsp);

    /**
     * Opens a new Bank Trade window associated with the provided LobbyName
     *
     * @param lobbyName The LobbyName to associate the Bank Trade window with
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void openBankTradeWindow(LobbyName lobbyName);

    /**
     * Opens a new ChangeGameSettings window
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @author Marvin Drees
     * @since 2021-06-28
     */
    void openChangeGameSettingsWindow();

    /**
     * Opens a new Lobby window
     *
     * @param lobby The ISimpleLobby object describing the Lobby to be displayed
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void openLobbyWindow(ISimpleLobby lobby);

    /**
     * Opens a new Robber Tax window associated with the provided LobbyName
     *
     * @param lobbyName The LobbyName to associate the Robber Tax window with
     * @param taxAmount The amount of Tax the User has to pay
     * @param inventory The User's inventory used for reference display in the window
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void openRobberTaxWindow(LobbyName lobbyName, int taxAmount, ResourceList inventory);

    /**
     * Opens the Rules Overview window
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void openRulesWindow();

    /**
     * Opens a new User Trade window associated with the provided LobbyName
     *
     * @param lobbyName      The LobbyName to associate the User Trade window with
     * @param respondingUser The User to whom a Trade Offer is being made
     * @param isCounterOffer Whether the Trade Offer is a Counter Offer
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void openUserTradeWindow(LobbyName lobbyName, Actor respondingUser, boolean isCounterOffer);

    /**
     * Opens a generic Alert dialogue window displaying the provided message
     *
     * @param message The error message to show the User
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void showError(String message);

    /**
     * Opens a Server Error Alert dialogue window displaying the provided message
     *
     * @param message The error message to display
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void showServerError(String message);

    /**
     * Opens a Server Error Alert dialogue window displaying the provided message
     * and forwards a Throwable for additional error handling
     *
     * @param e     The Throwable that caused the Error
     * @param cause The Error cause message
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void showServerError(Throwable e, String cause);
}
