package de.uol.swp.common.lobby.message;

import de.uol.swp.common.message.AbstractServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A message containing all currently logged in usernames
 *
 * @author Alwin Bossert
 * @author Steven Luong
 * @since 2020.12.21
 */
public class RetrieveAllLobbyMembersMessage extends AbstractServerMessage {

    private static final long serialVersionUID = -7968574381977330152L;
    private ArrayList<String> users;

    /**
     * Constructor
     *
     * @param users List containing all users currently logged in
     * @since 2017-03-17
     */
    public void RetrieveAllLobbyUserListMessage(List<String> users){this.users = new ArrayList<>(users);}

    /**
     * Gets the List containing all users currently logged in
     *
     * @return List containing all users currently logged in
     * @since 2017-03-17
     */
    public ArrayList<String> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RetrieveAllLobbyMembersMessage that = (RetrieveAllLobbyMembersMessage) o;
        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users);
    }
}
