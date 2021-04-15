package de.uol.swp.common.game.message;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * This message gets sent when the player
 * has done the mandatory part of a turn.
 *
 * @see de.uol.swp.common.game.message.AbstractGameMessage
 * @since 2021-01-15
 */
public class DiceCastMessage extends AbstractGameMessage {

    private final int dice1;
    private final int dice2;

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user      Active user
     * @param dice1     Eyes of the first dice rolled
     * @param dice2     Eyes of the second dice rolled
     */
    public DiceCastMessage(LobbyName lobbyName, UserOrDummy user, int dice1, int dice2) {
        super(lobbyName, user);
        this.dice1 = dice1;
        this.dice2 = dice2;
    }

    /**
     * Gets the eye count of the first dice
     *
     * @return Eyes on the first dice rolled
     */
    public int getDice1() {
        return dice1;
    }

    /**
     * Gets the eye count of the second dice
     *
     * @return Eyes on the second dice rolled
     */
    public int getDice2() {
        return dice2;
    }
}
