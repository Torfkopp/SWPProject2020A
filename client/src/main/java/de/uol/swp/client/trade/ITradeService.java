package de.uol.swp.client.trade;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourceThingies.resource.ResourceList;
import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
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
     */
    void acceptUserTrade(LobbyName lobbyName, UserOrDummy offeringUser, ResourceList demandedResources,
                         ResourceList offeredResources);

    /**
     * Posts a request to buy a Development Card
     *
     * @param lobbyName The name of the lobby
     */
    void buyDevelopmentCard(LobbyName lobbyName);

    /**
     * Posts a request to close the Trade Accept window on the responding user's end
     *
     * @param lobbyName      The name of the lobby
     * @param respondingUser The responding user (on whose end to close the window)
     */
    void cancelTrade(LobbyName lobbyName, UserOrDummy respondingUser);

    /**
     * Posts an event to close the Bank trade window and re-enable the in-game buttons
     *
     * @param lobbyName The name of the lobby
     */
    void closeBankTradeWindow(LobbyName lobbyName);

    /**
     * Posts an event to close a Trade Response window
     *
     * @param lobbyName The name of the lobby
     */
    void closeTradeResponseWindow(LobbyName lobbyName);

    /**
     * Posts an event to close a User trade window
     *
     * @param lobbyName The name of the lobby
     */
    void closeUserTradeWindow(LobbyName lobbyName);

    /**
     * Posts a request to execute a resource trade with the Bank
     *
     * @param lobbyName      The name of the lobby
     * @param gainedResource The resource the User wants from the Bank
     * @param lostResource   The resource the User is offering to the Bank
     */
    void executeTradeWithBank(LobbyName lobbyName, ResourceType gainedResource, ResourceType lostResource);

    /**
     * Posts a request to offer a trade to another user
     *
     * @param lobbyName         The name of the lobby
     * @param respondingUser    The user to whom the trade offer is being made
     * @param offeredResources  Map of resources being offered to the responding user
     * @param demandedResources Map of resources being demanded from the responding user
     */
    void offerTrade(LobbyName lobbyName, UserOrDummy respondingUser, ResourceList offeredResources,
                    ResourceList demandedResources);

    /**
     * Posts a request to reset the Offer Trade button for the user who proposed a trade
     * that got rejected
     *
     * @param lobbyName    The name of the lobby
     * @param offeringUser The user whose trade offer was rejected
     */
    void resetOfferTradeButton(LobbyName lobbyName, UserOrDummy offeringUser);

    /**
     * Posts an event to show the Bank trade window
     *
     * @param lobbyName The name of the lobby
     */
    void showBankTradeWindow(LobbyName lobbyName);

    /**
     * Posts an event to show the Trade Offered window displaying a trade offer
     * from another user
     *
     * @param lobbyName    The name of the lobby
     * @param offeringUser The user who offered a trade
     * @param rsp          The TradeWithUserOfferResponse containing the
     *                     details of the offer
     */
    void showOfferWindow(LobbyName lobbyName, UserOrDummy offeringUser, TradeWithUserOfferResponse rsp);

    /**
     * Posts an event to show a Trade Error alert with the provided message
     *
     * @param message The message to display
     */
    void showTradeError(String message);

    /**
     * Posts an event to show the User trade window for making a trade offer
     *
     * @param lobbyName      The name of the lobby
     * @param respondingUser The user to whom the offer is being made
     */
    void showUserTradeWindow(LobbyName lobbyName, UserOrDummy respondingUser);

    /**
     * Posts a request for the Bank's inventory in order to trade with the Bank
     *
     * @param lobbyName The name of the lobby
     */
    void tradeWithBank(LobbyName lobbyName);

    /**
     * Posts a request for another User's inventory overview in order to make a
     * trade offer to them
     *
     * @param lobbyName      The name of the lobby
     * @param respondingUser The user to whom the offer is being made
     */
    void tradeWithUser(LobbyName lobbyName, UserOrDummy respondingUser);
}
