package de.uol.swp.common.game.map;

/**
 * Enum for the Players
 */
public enum Player {
    PLAYER_1(0),
    PLAYER_2(1),
    PLAYER_3(2),
    PLAYER_4(3);

    private final int index;

    Player(int index) {
        this.index = index;
    }

    public static Player byIndex(int index) {
        try {
            return Player.values()[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public Player nextPlayer(int playersInGame) {
        return Player.byIndex((index + 1) % playersInGame);
    }
}
