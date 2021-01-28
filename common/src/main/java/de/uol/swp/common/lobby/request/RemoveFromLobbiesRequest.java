package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

public class RemoveFromLobbiesRequest extends AbstractRequestMessage {

    private User user;

    public RemoveFromLobbiesRequest() {
    }

    /**
     * Constructor
     *
     * @param user      User who wants to logout
     * @since 2021-01-28
     */
    public RemoveFromLobbiesRequest(User user){
        System.out.println("Request");
        this.user = user;
    }

    public User getUser(){
        return user;
    }
}
