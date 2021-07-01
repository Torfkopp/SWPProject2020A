package de.uol.swp.client.changeSettings.event;

/**
 * Event used to change the settings selected in the ChangeGameSettingsView
 * <p>
 * The Presenter subscribed to this event will be able to change game related
 * settings based on the data provided by this event.
 *
 * @author Marvin Drees
 * @see de.uol.swp.client.lobby.AbstractPresenterWithChatWithGame
 * @since 2021-06-22
 */
public class ChangedGameSettingsEvent {

    private final String renderingStyle;

    /**
     * Constructor
     *
     * @param renderingStyle The rendering styled used to draw the GameMap
     */
    public ChangedGameSettingsEvent(String renderingStyle) {
        this.renderingStyle = renderingStyle;
    }

    /**
     * Getter
     *
     * @return renderingStyle String
     */
    public String getRenderingStyle() {
        return renderingStyle;
    }
}
