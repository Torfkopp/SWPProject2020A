package de.uol.swp.common.game.request;

public class TradeWithUserCancelRequest extends AbstractGameRequest{
    private final String respondingUser;

    public TradeWithUserCancelRequest(String originLobby, String respondingUser) {
        super(originLobby);
        this.respondingUser = respondingUser;
    }

    public String getRespondingUser() {
        return respondingUser;
    }
}
