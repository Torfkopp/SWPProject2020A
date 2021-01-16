package de.uol.swp.server.game;

/**
 *
 */
public class gameHex implements iGameHex {
    private type type;
    private int token;

    public gameHex(type type, int token){
        this.type = type;
        this.token = token;
    }

    @Override
    public int getToken() {
        return token;
    }

    @Override
    public type getType() {
        return type;
    }
}
