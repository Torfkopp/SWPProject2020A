package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Message used to update the lobby list in the main menu.
 *
 * @author Maximilian Lindner
 * @author Aldin Dervisi
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @since 2021-03-14
 */
public class AllowedAmountOfPlayersChangedMessage extends AbstractLobbyMessage {

    /**
     * Constructor
     *
     * @param name Name of the lobby
     * @param user User who updated the allowed player amount
     */
    public AllowedAmountOfPlayersChangedMessage(String name, UserOrDummy user) {
        super(name, user);
    }
}
