package de.uol.swp.client.user;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to hide the communication details.
 * It implements de.uol.common.user.UserService
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.user.ClientUserService
 * @since 2017-03-17
 */
@SuppressWarnings("UnstableApiUsage")
public class UserService implements ClientUserService {

    private static final Logger LOG = LogManager.getLogger(UserService.class);
    private final EventBus bus;

    /**
     * Constructor
     *
     * @param bus The EventBus set in ClientModule
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2017-03-17
     */
    @Inject
    public UserService(EventBus bus) {
        LOG.debug("UserService started");
        this.bus = bus;
        // Currently not needed, will only post on bus
        // bus.register(this);
    }

    /**
     * Posts a RegisterUserRequest onto the EventBus
     *
     * @param user The user to create
     */
    @Override
    public void createUser(User user) {
        LOG.debug("Sending RegisterUserRequest");
        Message request = new RegisterUserRequest(user);
        bus.post(request);
    }

    /**
     * Method to delete a user's account
     * <p>
     * This method creates a new DeleteUserRequest object with the user as parameter,
     * and posts this instance onto the EventBus.
     *
     * @param user The user to remove
     *
     * @see de.uol.swp.common.user.request.DeleteUserRequest
     * @since 2020-11-02
     */
    public void dropUser(User user) {
        LOG.debug("Sending DeleteUserRequest");
        Message request = new DeleteUserRequest(user);
        bus.post(request);
    }

    /**
     * Posts a LoginRequest onto the EventBus
     *
     * @param username the user's name
     * @param password the user's password
     *
     * @since 2017-03-17
     */
    @Override
    public void login(String username, String password) {
        LOG.debug("Sending LoginRequest");
        Message msg = new LoginRequest(username, password);
        bus.post(msg);
    }

    /**
     * Posts a LogoutRequest onto the EventBus
     *
     * @param username the user's name
     */
    @Override
    public void logout(User username) {
        LOG.debug("Sending LogoutRequest");
        Message msg = new LogoutRequest();
        bus.post(msg);
    }

    /**
     * Posts a RetrieveAllOnlineUsersRequest onto the EventBus.
     */
    @Override
    public void retrieveAllUsers() {
        LOG.debug("Sending RetrieveAllOnlineUsersRequest");
        Message cmd = new RetrieveAllOnlineUsersRequest();
        bus.post(cmd);
    }

    /**
     * Posts a UpdateUserRequest onto the EventBus.
     *
     * @param user The User object containing all infos to update.
     *             If some values are not set (e.g. password is ""),
     *             these fields are not updated
     */
    @Override
    public void updateUser(User user) {
        LOG.debug("Sending UpdateUserRequest");
        Message request = new UpdateUserRequest(user);
        bus.post(request);
    }

    /**
     * Method to change a user's account details
     * <p>
     * This method creates a new UpdateUserAccountDetailsRequest object
     * with the user, his oldPassword, oldUsername and oldEMail as parameter
     * and posts this instance onto the EventBus.
     *
     * @param user        The user to update
     * @param oldPassword The password to change and verified
     * @param oldUsername The Username to change
     * @param oldEMail    The EMail to change
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @since 2020-12-17
     */
    @Override
    public void updateAccountDetails(User user, String oldPassword, String oldUsername, String oldEMail) {
        LOG.debug("Sending UpdateAccountDetailsRequest");
        Message request = new UpdateUserAccountDetailsRequest(user, oldPassword, oldUsername, oldEMail);
        bus.post(request);
    }
}
