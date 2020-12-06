package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import de.uol.swp.common.user.UserDTO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
package de.uol.swp.client.lobby;

/** Class that gives User ability to join existing Lobbies
 *
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-30
 *
 */





@FXML
    void onJoinLobby(ActionEvent event){
            lobbyService.joinLobby("test",new UserDTO("ich","",""));
            }



/**
 * Message if a user successfully joins an existing Lobby
 */

@Subscribe
public void joinedSuccessful(LobbyCreatedMessage message) {
        this.joinedSuccessful(); = message.getUser();
        userService.retrieveAllUsers();
        }


        }

