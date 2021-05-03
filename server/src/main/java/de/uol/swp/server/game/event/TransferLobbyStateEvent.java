package de.uol.swp.server.game.event;

import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.server.lobby.Lobby;
import de.uol.swp.server.message.AbstractServerInternalMessage;

import java.util.Optional;

/**
 * This event is sent to the game service.
 * <p>
 * The GameService takes information about the specific lobby/game
 * from the LobbyService to send it back to the client.
 *
 * @author Marvin Drees
 * @author Maximilian Lindner
 * @since 2021-04-09
 */
public class TransferLobbyStateEvent extends AbstractServerInternalMessage {

    private final Lobby lobby;
    private final UserOrDummy user;
    private final Optional<MessageContext> context;

    /**
     * Constructor
     *
     * @param lobby   The lobby to get data from
     * @param user    The user that requested data
     * @param context The MessageContext from the previous message, which cause this event to be posted
     */
    public TransferLobbyStateEvent(Lobby lobby, UserOrDummy user, Optional<MessageContext> context) {
        this.lobby = lobby;
        this.user = user;
        this.context = context;
    }

    @Override
    public Optional<MessageContext> getMessageContext() {
        return context;
    }

    /**
     * Getter
     *
     * @return The stored lobby object
     */
    public Lobby getLobby() {
        return lobby;
    }

    /**
     * Getter
     *
     * @return The stored user object
     */
    public UserOrDummy getUser() {
        return user;
    }
}
