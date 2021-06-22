package de.uol.swp.client.specialisedUtil;

import de.uol.swp.common.lobby.ISimpleLobby;
import javafx.util.Pair;

/**
 * Specialised Class for a list item
 * consisting of a ISimpleLobby-String-Pair
 *
 * @author Mario Fokken
 * @since 2021-06-17
 */
public class LobbyListItem extends Pair<ISimpleLobby, String> {

    /**
     * Constructor
     *
     * @param iSimpleLobby The lobby object
     * @param s            The name of the lobby
     */
    public LobbyListItem(ISimpleLobby iSimpleLobby, String s) {
        super(iSimpleLobby, s);
    }
}
