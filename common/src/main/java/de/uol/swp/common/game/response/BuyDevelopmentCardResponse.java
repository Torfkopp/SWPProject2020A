package de.uol.swp.common.game.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourceThingies.developmentCard.DevelopmentCardType;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

/**
 * This Response has up-to-date info about the bought development card to
 * show him what card he got.
 *
 * @author Maximilian Lindner
 * @author Alwin Bossert
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-21
 */
public class BuyDevelopmentCardResponse extends AbstractLobbyResponse {

    private final UserOrDummy user;
    private final DevelopmentCardType developmentCard;

    /**
     * Constructor
     *  @param user      user who bought a development card
     * @param lobbyName name of the lobby where the user bought the card
     * @param developmentCard
     */
    public BuyDevelopmentCardResponse(UserOrDummy user, LobbyName lobbyName, DevelopmentCardType developmentCard) {
        super(lobbyName);
        this.user = user;
        this.developmentCard = developmentCard;
    }

    /**
     * Gets the name of the bought development card
     *
     * @return String name of the bought development card
     */
    public DevelopmentCardType getDevelopmentCard() {
        return developmentCard;
    }

    /**
     * Gets the user who bought a development card
     *
     * @return User The User who bought a development card
     */
    public UserOrDummy getUser() {
        return user;
    }
}
