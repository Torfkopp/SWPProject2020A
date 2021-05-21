package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.AI;

/**
 * Request sent to the server to add an AI to the lobby
 *
 * @author Mario Fokken
 * @since 2021-05-21
 */
public class AddAIRequest extends AbstractLobbyRequest {

    /**
     * Constructor
     *
     * @param name   The name of the lobby
     * @param uehara The AI to join
     */
    public AddAIRequest(LobbyName name, AI uehara) {
        super(name, uehara);
    }
}
