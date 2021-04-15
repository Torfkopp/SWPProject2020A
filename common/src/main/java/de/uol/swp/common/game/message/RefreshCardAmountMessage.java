package de.uol.swp.common.game.message;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.util.Triple;

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

    private final List<Triple<UserOrDummy, Integer, Integer>> cardAmountTriples;

    /**
     * Constructor
     *
     * @param lobbyName         The name of the lobby in which the game is taking place
     * @param user              The user who caused a change in inventories
     * @param cardAmountTriples List of Triples containing a UserOrDummy, an
     *                          Integer representing their inventory size, and
     *                          an Integer representing the amount of Development
     *                          Cards they have
     */
    public RefreshCardAmountMessage(LobbyName lobbyName, UserOrDummy user,
                                    List<Triple<UserOrDummy, Integer, Integer>> cardAmountTriples) {
        super(lobbyName, user);
        this.cardAmountTriples = cardAmountTriples;
    }

    /**
     * Gets the List of Triples containing inventory information
     *
     * @return List of Triples of UserOrDummy, Integer, Integer
     */
    public List<Triple<UserOrDummy, Integer, Integer>> getCardAmountTriples() {
        return cardAmountTriples;
    }
}
