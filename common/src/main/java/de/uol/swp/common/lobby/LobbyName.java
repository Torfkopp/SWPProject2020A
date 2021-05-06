package de.uol.swp.common.lobby;

import java.io.Serializable;
import java.util.Objects;

/**
 * A class to store a lobby name.
 *
 * @author Temmo Junkhoff
 */
public class LobbyName implements Serializable, Comparable<LobbyName> {

    private final String LobbyName;

    /**
     * Constructor.
     *
     * @param lobbyName The lobby name
     *
     * @author Temmo Junkhoff
     * @since 2021-04-28
     */
    public LobbyName(String lobbyName) {
        LobbyName = lobbyName;
    }

    @Override
    public int compareTo(LobbyName other) {
        return this.toString().compareTo(other.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(LobbyName);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        LobbyName otherLobbyName = (LobbyName) other;
        return Objects.equals(this.toString(), otherLobbyName.toString());
    }

    @Override
    public String toString() {
        return LobbyName;
    }

    /**
     * Gets the lobby name.
     *
     * @return The lobby name
     *
     * @author Temmo Junkhoff
     * @since 2021-04-28
     */
    public String getLobbyName() {
        return LobbyName;
    }
}
