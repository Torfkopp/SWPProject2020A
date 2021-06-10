package de.uol.swp.common.game.request;

import de.uol.swp.common.Colour;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Request is send when the user leaves a lobby that is in a Game and it tries to replace the User with an AI
 *
 * @author Eric Vuong
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @since 2021-06-10
 */
public class ReplaceUserWithAIRequest extends AbstractGameRequest {

    private final UserOrDummy userToReplace;
    private final Colour oldColour;

    /**
     * Constructor
     * <p>
     * This constructor sets the originLobby to the parameter provided when
     * calling the constructor.
     *
     * @param originLobby The Lobby from which a request originated from
     * @param userOrDummy The User to replace with the AI
     * @param oldColour   The Colour of the User who left the Lobby while inGame
     */
    public ReplaceUserWithAIRequest(LobbyName originLobby, UserOrDummy userOrDummy, Colour oldColour) {
        super(originLobby);
        this.userToReplace = userOrDummy;
        this.oldColour = oldColour;
    }

    /**
     * Gets the Colour of the User who left the Lobby
     *
     * @return The oldColour of the User who left the Lobby
     */
    public Colour getOldColour() {
        return oldColour;
    }

    /**
     * Gets the User that is to replace with the AI
     *
     * @return The User that is to be replaced
     */
    public UserOrDummy getUserToReplace() {
        return userToReplace;
    }
}
