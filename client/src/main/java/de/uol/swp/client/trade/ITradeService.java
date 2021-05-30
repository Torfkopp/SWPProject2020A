package de.uol.swp.client.trade;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * An interface for all methods of the TradeService
 *
 * @author Maximilian Lindner
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.trade.TradeService
 * @since 2021-04-07
 */
public interface ITradeService {

    /**
     * Posts a request to accept a proposed trade
     *
     * @param lobbyName         The name of the lobby
     * @param offeringUser      The User who offered the trade
     * @param demandedResources The resources the offering User wants
     * @param offeredResources  The resources the offering User is offering
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void acceptUserTrade(LobbyName lobbyName, UserOrDummy offeringUser, ResourceList demandedResources,
                         ResourceList offeredResources);

    /**
     * Posts a request to buy a Development Card
     *
     * @param lobbyName The name of the lobby
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void buyDevelopmentCard(LobbyName lobbyName);

    /**
     * Posts a request to close the Trade Accept window on the responding user's end
     *
     * @param lobbyName      The name of the lobby
     * @param respondingUser The responding user (on whose end to close the window)
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void cancelTrade(LobbyName lobbyName, UserOrDummy respondingUser);

    /**
     * Posts an event to close the Bank trade window and re-enable the in-game buttons
     *
     * @param lobbyName The name of the lobby
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void closeBankTradeWindow(LobbyName lobbyName);

    /**
     * Posts an event to close a Trade Response window
     *
     * @param lobbyName The name of the lobby
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void closeTradeResponseWindow(LobbyName lobbyName);

    /**
     * Posts an event to close a User trade window
     *
     * @param lobbyName The name of the lobby
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void closeUserTradeWindow(LobbyName lobbyName);

    /**
     * Posts a request to execute a resource trade with the Bank
     *
     * @param lobbyName      The name of the lobby
     * @param gainedResource The resource the User wants from the Bank
     * @param lostResource   The resource the User is offering to the Bank
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void executeTradeWithBank(LobbyName lobbyName, ResourceType gainedResource, ResourceType lostResource);

    /**
     * Posts a request to offer a trade to another user
     *
     * @param lobbyName         The name of the lobby
     * @param respondingUser    The user to whom the trade offer is being made
     * @param offeredResources  Map of resources being offered to the responding user
     * @param demandedResources Map of resources being demanded from the responding user
     * @param counterOffer      Whether the offer is a counter offer or not
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void offerTrade(LobbyName lobbyName, UserOrDummy respondingUser, ResourceList offeredResources,
                    ResourceList demandedResources, boolean counterOffer);

    /**
     * Posts a request to reset the Offer Trade button for the user who proposed a trade
     * that got rejected
     *
     * @param lobbyName    The name of the lobby
     * @param offeringUser The user whose trade offer was rejected
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void resetOfferTradeButton(LobbyName lobbyName, UserOrDummy offeringUser);

    /**
     * Posts an event to show the Trade Offered window displaying a trade offer
     * from another user
     *
     * @param lobbyName    The name of the lobby
     * @param offeringUser The user who offered a trade
     * @param rsp          The TradeWithUserOfferResponse containing the
     *                     details of the offer
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void showOfferWindow(LobbyName lobbyName, UserOrDummy offeringUser, TradeWithUserOfferResponse rsp);

    /**
     * Posts an event to show a Trade Error alert with the provided message
     *
     * @param message The message to display
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void showTradeError(String message);

    /**
     * Posts an event to show the User trade window for making a trade offer
     *
     * @param lobbyName      The name of the lobby
     * @param respondingUser The user to whom the offer is being made
     * @param isCounterOffer Whether the trade is a counter offer or not
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void showUserTradeWindow(LobbyName lobbyName, UserOrDummy respondingUser, boolean isCounterOffer);

    /**
     * Posts a request for the Bank's inventory in order to trade with the Bank
     *
     * @param lobbyName The name of the lobby
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void tradeWithBank(LobbyName lobbyName);

    /**
     * Posts a request for another User's inventory overview in order to make a
     * trade offer to them
     *
     * @param lobbyName      The name of the lobby
     * @param respondingUser The user to whom the offer is being made
     * @param counterOffer   Whether the offer is a counter offer or not
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void tradeWithUser(LobbyName lobbyName, UserOrDummy respondingUser, boolean counterOffer);
}
