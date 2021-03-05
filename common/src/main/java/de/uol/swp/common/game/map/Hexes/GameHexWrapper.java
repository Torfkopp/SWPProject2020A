package de.uol.swp.common.game.map.Hexes;

public class GameHexWrapper {

    IGameHex hex;

    public GameHexWrapper() {
        hex = new WaterHex();
    }

    public IGameHex get() {
        return hex;
    }

    public void set(IGameHex hex) {
        this.hex = hex;
    }
}
