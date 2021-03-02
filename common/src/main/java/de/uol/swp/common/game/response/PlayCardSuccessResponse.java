package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

/**
 * This response is sent when a card
 * was successfully played
 *
 * @author Mario Fokken
 * @since 2021-03-02
 */
public class PlayCardSuccessResponse extends AbstractLobbyResponse {

    public PlayCardSuccessResponse(String lobbyName) {
        super(lobbyName);
    }
}
