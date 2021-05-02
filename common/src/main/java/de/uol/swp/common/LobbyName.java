package de.uol.swp.common;

import java.io.Serializable;
import java.util.Objects;

public class LobbyName implements Serializable, Comparable<LobbyName> {

    String LobbyName;

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
}
