package de.uol.swp.common.game.message;

import de.uol.swp.common.game.CardsAmount;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

import java.util.List;

/**
 * This Message contains information about the size of each player's inventory,
 * as well as how many Development Cards they currently possess.
 *
 * @author Alwin Bossert
 * @author Eric Vuong
 * @since 2021-03-27
 */
public class RefreshCardAmountMessage extends AbstractGameMessage {

    private final List<CardsAmount> cardAmountsList;

    /**
     * Constructor
     *
     * @param lobbyName       The name of the lobby in which the game is taking place
     * @param user            The user who caused a change in inventories
     * @param cardAmountsList List of CardsAmount objects
     */
    public RefreshCardAmountMessage(LobbyName lobbyName, Actor user, List<CardsAmount> cardAmountsList) {
        super(lobbyName, user);
        this.cardAmountsList = cardAmountsList;
    }

    /**
     * Gets the List of CardsAmount objects
     *
     * @return List of CardsAmount objects
     */
    public List<CardsAmount> getCardAmountsList() {
        return cardAmountsList;
    }
}
