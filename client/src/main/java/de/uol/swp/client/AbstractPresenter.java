package de.uol.swp.client;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.ILobbyService;
import de.uol.swp.client.sound.ISoundService;
import de.uol.swp.client.user.IUserService;
import javafx.concurrent.Task;

import java.util.ResourceBundle;

/**
 * This class is the base for creating a new Presenter.
 * <p>
 * This class prepares the child classes to have the UserService, the LobbyService, and EventBus set
 * in order to reduce unnecessary code repetition.
 *
 * @author Marco Grawunder
 * @since 2019-08-29
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractPresenter {

    @Inject
    protected ISoundService soundService;

    @Inject
    protected IUserService userService;

    @Inject
    protected ILobbyService lobbyService;

    @Inject
    protected ResourceBundle resourceBundle;

    private EventBus eventBus;

    /**
     * Clears the field EventBus
     * <p>
     * This method clears the field EventBus. Before clearing, it unregisters this
     * class from EventBus previously used.
     *
     * @implNote This method does not check if the field EventBus is null.
     * The field is cleared by setting it to null.
     * @since 2019-08-29
     */
    public void clearEventBus() {
        this.eventBus.unregister(this);
        this.eventBus = null;
    }

    /**
     * Posts an Object to the EventBus
     * <p>
     * This method posts the provided object onto the EventBus.
     *
     * @param obj the obj
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @author Phillip-André Suhr
     * @since 2021-05-22
     */
    public void post(Object obj) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                eventBus.post(obj);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Sets the field eventBus
     * <p>
     * This method sets the field EventBus to the EventBus given via parameter.
     * Afterwards it registers this class to the new EventBus.
     *
     * @param eventBus The EventBus this class should use.
     *
     * @implNote This method does not unregister this class from any EventBus it
     * may already be registered to.
     * @since 2019-08-29
     */
    @Inject
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }
}
