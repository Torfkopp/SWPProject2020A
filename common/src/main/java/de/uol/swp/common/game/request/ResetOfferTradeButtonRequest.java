package de.uol.swp.common.game.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Event used to re-enable the offer trade button.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @since 2021-02-24
 */
public class ResetOfferTradeButtonRequest extends AbstractGameRequest {

    private final UserOrDummy offeringUserName;

    /**
     * Constructor
     *
     * @param originLobby The name of the Lobby where the offer trade button should be re-enabled
     */
    public ResetOfferTradeButtonRequest(LobbyName originLobby, UserOrDummy offeringUserName) {
        super(originLobby);
        this.offeringUserName = offeringUserName;
    }

    /**
     * Getter for the offering user´s name
     *
     * @return Name of the offering user
     */
    public UserOrDummy getOfferingUser() {
        return offeringUserName;
    }
}