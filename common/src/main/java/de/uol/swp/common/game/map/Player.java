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

    /**
     * Gets the Player Object for a specified index
     *
     * @param index The index for the players
     *
     * @return The player with the specified index
     */
    public static Player byIndex(int index) {
        try {
            return Player.values()[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Gets the next player for a specified amount of players in the game
     *
     * @param playersInGame The amount of players in the game
     *
     * @return The next player
     */
    public Player nextPlayer(int playersInGame) {
        return Player.byIndex((index + 1) % playersInGame);
    }
}
