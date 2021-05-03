package de.uol.swp.common.game.response;

import de.uol.swp.common.game.map.gamemapDTO.IGameMap;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.UserOrDummy;

/**
 * This Response is used to inform the user that there is an
 * existing game in his lobby and to give him the status
 * of the game
 *
 * @author Maximilian Lindner
 * @author Marvin Drees
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-25
 */
public class StartSessionResponse extends AbstractResponseMessage {

    private final ISimpleLobby lobby;
    private final UserOrDummy player;
    private final IGameMap gameMapDTO;
    private final int[] dices;
    private final boolean rolledDiceAlready;
    private final boolean autoRollState;
    private final int moveTime;

    /**
     * Constructor
     *
     * @param lobby         The lobby where the has already started
     * @param player        The player who has the turn
     * @param dices         The last rolled dices
     * @param autoRollState The autoRoll state
     */
    public StartSessionResponse(ISimpleLobby lobby, UserOrDummy player, IGameMap gameMapDTO,
                                int[] dices, boolean rolledDiceAlready, boolean autoRollState, int moveTime) {
        this.lobby = lobby;
        this.player = player;
        this.gameMapDTO = gameMapDTO;
        this.dices = dices;
        this.rolledDiceAlready = rolledDiceAlready;
        this.autoRollState = autoRollState;
        this.moveTime = moveTime;
    }

    /**
     * Gets the RolledDiceAlready attribute
     *
     * @return Whether the current turn rolled the dice or not
     */
    public boolean areDiceRolledAlready() {
        return rolledDiceAlready;
    }

    /**
     * Gets the last rolled dices
     *
     * @return The value of the last rolled dices
     */
    public int[] getDices() {
        return dices;
    }

    /**
     * Gets the GameMapDTO
     *
     * @return The stored GameMapDTO object
     */
    public IGameMap getGameMapDTO() {
        return gameMapDTO;
    }

    /**
     * Gets the Lobby where the user wants to join the game
     *
     * @return The Lobby the User wants to join
     */
    public ISimpleLobby getLobby() {
        return lobby;
    }

    /**
     * Gets the moveTime for the game
     *
     * @return moveTime
     */
    public int getMoveTime() {
        return moveTime;
    }

    /**
     * Gets the user who wants to join the game
     *
     * @return The User who wants to join the game
     */
    public UserOrDummy getPlayer() {
        return player;
    }

    /**
     * Gets the autoRoll state.
     *
     * @return The autoRoll state
     */
    public boolean isAutoRollState() {
        return autoRollState;
    }
}
