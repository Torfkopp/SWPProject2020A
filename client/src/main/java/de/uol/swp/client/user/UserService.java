package de.uol.swp.client.user;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * This class is used to hide the communication details
 * implements de.uol.common.user.UserService
 *
 * @author Marco Grawunder
 * @see ClientUserService
 * @since 2017-03-17
 *
 */
@SuppressWarnings("UnstableApiUsage")
public class UserService implements ClientUserService {

	private static final Logger LOG = LogManager.getLogger(UserService.class);
	private final EventBus bus;

	/**
	 * Constructor
	 *
	 * @param bus The  EventBus set in ClientModule
	 * @see de.uol.swp.client.di.ClientModule
	 * @since 2017-03-17
	 */
	@Inject
	public UserService(EventBus bus) {
		this.bus = bus;
		// Currently not need, will only post on bus
		//bus.register(this);
	}

	/**
	 * Posts a login request to the EventBus
	 *
	 * @param username the name of the user
	 * @param password the password of the user
	 * @since 2017-03-17
	 */
	@Override
	public void login(String username, String password){
		LoginRequest msg = new LoginRequest(username, password);
		bus.post(msg);
	}


	@Override
	public void logout(User username){
		LogoutRequest msg = new LogoutRequest();
		bus.post(msg);
	}

	@Override
	public void createUser(User user) {
		RegisterUserRequest request = new RegisterUserRequest(user);
		bus.post(request);
	}

	/**
	 * Method to delete a user's account
	 *
	 * This method creates a new DeleteUserRequest object with the user as parameter,
	 * and posts this instance to the EventBus.
	 *
	 * @param user The user to remove
	 * @see de.uol.swp.common.user.request.DeleteUserRequest
	 * @since 2020-11-02
	 */
	public void dropUser(User user) {
		DeleteUserRequest request = new DeleteUserRequest(user);
		bus.post(request);
	}

	@Override
	public void updateUser(User user) {
		UpdateUserRequest request = new UpdateUserRequest(user);
		bus.post(request);
	}


	@Override
	public void retrieveAllUsers() {
		RetrieveAllOnlineUsersRequest cmd = new RetrieveAllOnlineUsersRequest();
		bus.post(cmd);
	}
}
