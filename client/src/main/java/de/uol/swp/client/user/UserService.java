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
 * @see ClientUserService
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
     * @see de.uol.swp.client.di.ClientModule
     * @since 2017-03-17
     */
    @Inject
    public UserService(EventBus bus) {
        this.bus = bus;
        // Currently not needed, will only post on bus
        // bus.register(this);
    }

	/**
	 * Posts a LoginRequest onto the EventBus
	 *
	 * @param username the user's name
	 * @param password the user's password
	 * @since 2017-03-17
	 */
	@Override
	public void login(String username, String password){
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
		Message msg = new LogoutRequest();
		bus.post(msg);
	}

	/**
	 * Posts a RegisterUserRequest onto the EventBus
	 *
	 * @param user The user to create
	 */
	@Override
	public void createUser(User user) {
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
     * @see de.uol.swp.common.user.request.DeleteUserRequest
     * @since 2020-11-02
     */
    public void dropUser(User user) {
        Message request = new DeleteUserRequest(user);
        bus.post(request);
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
		Message request = new UpdateUserRequest(user);
		bus.post(request);
	}

	/**
	 * Posts a RetrieveAllOnlineUsersRequest onto the EventBus.
	 *
	 */
	@Override
	public void retrieveAllUsers() {
		Message cmd = new RetrieveAllOnlineUsersRequest();
		bus.post(cmd);
	}

	/**
	 * Method to change a user's password
	 * <p>
	 * This method creates a new UpdateUserPasswordRequest object
	 * with the user and his oldPassword as parameter,
	 * and posts this instance onto the EventBus.
	 *
	 * @param user        The user to update
	 * @param oldPassword The password to change and verified
	 * @author Eric Vuong
	 * @author Steven Luong
	 * @since 2020-12-17
	 */
	@Override
	public void updateUserPassword(User user, String oldPassword) {
		Message request = new UpdateUserPasswordRequest(user, oldPassword);
		bus.post(request);
	}
}
