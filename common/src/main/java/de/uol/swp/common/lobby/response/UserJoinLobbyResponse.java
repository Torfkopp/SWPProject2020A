package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import org.checkerframework.common.value.qual.ArrayLen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ObjDoubleConsumer;

public class UserJoinLobbyResponse extends AbstractResponseMessage {

    private final String name;

    private final User user;

    public UserJoinLobbyResponse(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }
}
