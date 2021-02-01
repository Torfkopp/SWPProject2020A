package de.uol.swp.common.chat.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Base class of all ChatMessage requests.
 * <p>
 * This class abstracts away the fromLobby and originLobby attributes
 * needed for checking which chat the request originated from and where the
 * response or message should be sent to.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @since 2020-12-30
 */
public abstract class AbstractChatMessageRequest extends AbstractRequestMessage {

    private final boolean fromLobby;
    private final String originLobby;

    /**
     * Constructor
     * <p>
     * This constructor sets the originLobby to the parameter provided when
     * calling the constructor and sets the fromLobby attribute to True if the
     * originLobby parameter isn't null and to False, if it is.
     *
     * @param originLobby The Lobby the ChatMessage request originated from (null if
     *                    from global chat)
     */
    public AbstractChatMessageRequest(String originLobby) {
        this.originLobby = originLobby;
        this.fromLobby = (originLobby != null);
    }

    /**
     * Getter for the lobbyName attribute
     *
     * @return The name of the Lobby the ChatMessage request originated from (null if from global chat)
     */
    public String getOriginLobby() {
        return originLobby;
    }

    /**
     * Check if the ChatMessage request originated from a lobby chat
     *
     * @return True, if the ChatMessage request originated from a lobby chat; False if not
     */
    public boolean isFromLobby() {
        return fromLobby;
    }
}
