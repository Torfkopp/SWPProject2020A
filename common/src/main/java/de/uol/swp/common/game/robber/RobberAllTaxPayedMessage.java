package de.uol.swp.common.game.robber;

import de.uol.swp.common.game.message.AbstractGameMessage;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Gets sent when every player payed their taxes
 *
 * @author Mario Fokken
 * @since 2021-04-23
 */
public class RobberAllTaxPayedMessage extends AbstractGameMessage {

    /**
     * Constructor
     *
     * @param lobbyName The lobby name
     * @param user      The user
     */
    public RobberAllTaxPayedMessage(String lobbyName, UserOrDummy user) {
        super(lobbyName, user);
    }
}
