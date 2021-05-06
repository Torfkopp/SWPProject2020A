package de.uol.swp.common.game.response;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.lobby.LobbyName;
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
     *
     * @param user            User who bought a development card
     * @param lobbyName       Name of the lobby where the user bought the card
     * @param developmentCard The Development Card the User bought
     */
    public BuyDevelopmentCardResponse(UserOrDummy user, LobbyName lobbyName, DevelopmentCardType developmentCard) {
        super(lobbyName);
        this.user = user;
        this.developmentCard = developmentCard;
    }

    /**
     * Gets the name of the bought development card
     *
     * @return The bought development card
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
