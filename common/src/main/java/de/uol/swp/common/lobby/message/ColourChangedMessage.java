package de.uol.swp.common.lobby.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.specialisedUtil.ActorColourMap;
import de.uol.swp.common.user.Actor;

/**
 * Response to a SetColourRequest.
 *
 * @author Mario Fokken
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @see de.uol.swp.common.lobby.request.SetColourRequest
 * @since 2021-06-02
 */
public class ColourChangedMessage extends AbstractLobbyMessage {

    private final ActorColourMap userColours;

    /**
     * Constructor
     *
     * @param name name of the lobby
     * @param user user responsible for the creation of this message
     *
     * @since 2019-10-08
     */
    public ColourChangedMessage(LobbyName name, Actor user, ActorColourMap userColours) {
        super(name, user);
        this.userColours = userColours;
    }

    /**
     * Gets the userColours Map
     *
     * @return Map of users and their colours
     */
    public ActorColourMap getUserColours() {
        return userColours;
    }
}
