package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.ArrayList;
import java.util.List;

public class UpdateInventoryResponse extends AbstractResponseMessage {

    private final User user;
    private List ressourceList = new ArrayList();

    /**
     * Constructor
     *
     * @param list the list used to update the clients Inventory
     * @author Sven Ahrens
     * @author Finn Haase
     * @since 2021-1-25
     **/
    public UpdateInventoryResponse(List list, User user) {
        ressourceList = list;
        this.user = user;

    }
}
