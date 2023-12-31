package de.uol.swp.common.lobby.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.AbstractServerMessage;
import de.uol.swp.common.user.Actor;

import java.util.Objects;

/**
 * Base class of all lobby messages. Basic handling of lobby data.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2019-10-08
 */
public abstract class AbstractLobbyMessage extends AbstractServerMessage {

    private final LobbyName name;
    private final Actor user;

    /**
     * Constructor
     *
     * @param name name of the lobby
     * @param user user responsible for the creation of this message
     *
     * @since 2019-10-08
     */
    public AbstractLobbyMessage(LobbyName name, Actor user) {
        this.name = name;
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toString(), user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractLobbyMessage that = (AbstractLobbyMessage) o;
        return Objects.equals(name, that.name) && Objects.equals(user, that.user);
    }

    /**
     * Gets the name variable
     *
     * @return String containing the lobby's name
     *
     * @since 2019-10-08
     */
    public LobbyName getName() {
        return name;
    }

    /**
     * Gets the user variable
     *
     * @return User responsible for the creation of this message
     *
     * @since 2019-10-08
     */
    public Actor getActor() {
        return user;
    }
}
