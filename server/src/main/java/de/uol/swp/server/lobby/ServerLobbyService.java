package de.uol.swp.server.lobby;

import de.uol.swp.common.user.User;

import java.util.List;

public interface ServerLobbyService {

    List<User> retrieveAllLobbyUsers();
}
