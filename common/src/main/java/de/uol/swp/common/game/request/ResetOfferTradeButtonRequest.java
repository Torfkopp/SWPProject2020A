package de.uol.swp.common.game.request;

/**
 * Event used to re-enable the offer trade button.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @since 2021-02-24
 */
public class ResetOfferTradeButtonRequest extends AbstractGameRequest {

    private final String offeringUserName;

    /**
     * Constructor
     *
     * @param originLobby The name of the Lobby where the offer trade button should be re-enabled
     */
    public ResetOfferTradeButtonRequest(String originLobby, String offeringUserName) {
        super(originLobby);
        this.offeringUserName = offeringUserName;
    }

    /**
     * Getter for the offering user´s name
     *
     * @return Name of the offering user
     */
    public String getOfferingUserName() {
        return offeringUserName;
    }
}