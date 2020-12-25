package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;

public class CreateLobbyResponse extends AbstractResponseMessage {

    private final String name;
    /**
     * Constructor
     *
     * @param name The name for the new lobby
     * @since 2020.12.21
     *
     **/
    public CreateLobbyResponse(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the created lobby
     *
     * @return Name name of the created lobby
     * @since 2020.12.21
     */
    public String getName() {
        return name;
    }
}
