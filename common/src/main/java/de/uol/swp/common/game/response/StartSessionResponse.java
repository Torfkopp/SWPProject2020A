package de.uol.swp.common.game.response;

import de.uol.swp.common.game.map.IGameMap;
import de.uol.swp.common.game.map.configuration.IConfiguration;
import de.uol.swp.common.lobby.Lobby;
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

    private final Lobby lobby;
    private final UserOrDummy player;
    private final IConfiguration configuration;
    private final IGameMap gameMapDTO;
    private final int[] dices;
    private final boolean rolledDiceAlready;

    /**
     * Constructor
     *
     * @param lobby         The lobby where the has already started
     * @param player        The player who has the turn
     * @param configuration The game map configuration
     * @param dices         The last rolled dices
     */
    public StartSessionResponse(Lobby lobby, UserOrDummy player, IConfiguration configuration, IGameMap gameMapDTO,
                                int[] dices, boolean rolledDiceAlready) {
        this.lobby = lobby;
        this.player = player;
        this.configuration = configuration;
        this.gameMapDTO = gameMapDTO;
        this.dices = dices;
        this.rolledDiceAlready = rolledDiceAlready;
    }

    /**
     * Gets the status of the game
     *
     * @return The configuration of the game map
     */
    public IConfiguration getConfiguration() {
        return configuration;
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
     * @return The User who joined a lobby
     */
    public Lobby getLobby() {
        return lobby;
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
     * Gets the RolledDiceAlready attribute
     *
     * @return Whether the current turn rolled the dice or not
     */
    public boolean areDiceRolledAlready() {
        return rolledDiceAlready;
    }
}
