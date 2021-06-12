package de.uol.swp.client.scene;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

public interface ISceneService {

    void closeAcceptTradeWindow(LobbyName lobbyName);

    void closeBankTradeWindow(LobbyName lobbyName);

    void closeLobbyWindows();

    void closeRobberTaxWindow(LobbyName lobbyName);

    void closeUserTradeWindow(LobbyName lobbyName);

    void showAcceptTradeWindow(LobbyName lobbyName, UserOrDummy offeringUser, TradeWithUserOfferResponse rsp);

    void showBankTradeWindow(LobbyName lobbyName);

    void showChangeAccountDetailsScreen();

    void showChangePropertiesScreen();

    void showError(String message);

    void showLobbyWindow(ISimpleLobby lobby);

    void showLoginScreen();

    void showMainMenuScreen();

    void showRegistrationScreen();

    void showRobberTaxWindow(LobbyName lobbyName, Integer taxAmount, ResourceList inventory);

    void showRulesWindow();

    void showServerError(String message);

    void showServerError(Throwable e, String cause);

    void showUserTradeWindow(LobbyName lobbyName, UserOrDummy respondingUser, boolean isCounterOffer);
}
