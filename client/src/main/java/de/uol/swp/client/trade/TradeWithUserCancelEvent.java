package de.uol.swp.client.trade;

public class TradeWithUserCancelEvent {
    private final String lobbyName;
    public TradeWithUserCancelEvent(String lobbyName){
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }
}
