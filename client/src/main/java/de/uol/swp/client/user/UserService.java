package de.uol.swp.client.user;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.ClientApp;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.*;
import de.uol.swp.common.user.response.ChangeAccountDetailsSuccessfulResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.prefs.Preferences;

/**
 * This class is used to hide the communication details.
 * It implements de.uol.common.user.UserService
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.user.IUserService
 * @since 2017-03-17
 */
@SuppressWarnings("UnstableApiUsage")
public class UserService implements IUserService {

    private static final Logger LOG = LogManager.getLogger(UserService.class);
    private static final Preferences preferences = Preferences.userNodeForPackage(ClientApp.class);
    private final EventBus bus;
    private User loggedInUser;

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
        this.bus = bus;
        bus.register(this);
        LOG.debug("UserService started");
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
     * @param user     The user to remove
     * @param password The user's password
     *
     * @see de.uol.swp.common.user.request.DeleteUserRequest
     * @since 2020-11-02
     */
    @Override
    public void dropUser(User user, String password) {
        LOG.debug("Sending DeleteUserRequest");
        Message request = new DeleteUserRequest(user, password);
        bus.post(request);
    }

    @Override
    public User getLoggedInUser() {
        return loggedInUser;
    }

    @Override
    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    /**
     * Posts a LoginRequest onto the EventBus
     *
     * @param username     The user's name
     * @param passwordHash The user's hashed password
     * @param rememberMe   whether to remember the user details for automatic login
     *
     * @since 2017-03-17
     */
    @Override
    public void login(String username, String passwordHash, boolean rememberMe) {
        preferences.putBoolean("rememberMeEnabled", rememberMe);
        preferences.put("username", username);
        preferences.put("password", passwordHash);
        LOG.debug("Sending LoginRequest");
        Message msg = new LoginRequest(username, passwordHash);
        bus.post(msg);
    }

    /**
     * Posts a LogoutRequest onto the EventBus
     * <p>
     * This also disables the "Remember Me" setting and empties the stored user details
     *
     * @param resetRememberMe Whether to reset the stored user details
     */
    @Override
    public void logout(boolean resetRememberMe) {
        if (resetRememberMe) {
            preferences.putBoolean("rememberMeEnabled", false);
            preferences.put("username", "");
            preferences.put("password", "");
        }
        LOG.debug("Sending LogoutRequest");
        Message msg = new LogoutRequest();
        bus.post(msg);
        setLoggedInUser(null);
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
     * Method to change a user's account details
     * <p>
     * This method creates a new UpdateUserAccountDetailsRequest object
     * with the user, his oldPassword, oldUsername and oldEMail as parameter
     * and posts this instance onto the EventBus.
     *
     * @param user              The user to update
     * @param oldHashedPassword The hashed password to change and verified
     * @param oldUsername       The Username to change
     * @param oldEMail          The EMail to change
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @since 2020-12-17
     */
    @Override
    public void updateAccountDetails(User user, String oldHashedPassword, String oldUsername, String oldEMail) {
        LOG.debug("Sending UpdateAccountDetailsRequest");
        Message request = new UpdateUserAccountDetailsRequest(user, oldHashedPassword, oldUsername, oldEMail);
        bus.post(request);
    }

    /**
     * Handles ChangeAccountDetailsSuccessfulResponse
     * <p>
     * If a ChangeAccountDetailsSuccessfulResponse is found on the EventBus,
     * this method overwrites the currently saved loggedInUser if and only if
     * the ID of the updated User is identical to the one of the loggedInUser.
     *
     * @param rsp The ChangeAccountDetailsSuccessfulResponse found on the EventBus
     *
     * @author Eric Vuong
     * @author Alwin Bossert
     * @see de.uol.swp.common.user.response.ChangeAccountDetailsSuccessfulResponse
     * @since 2021-03-23
     */
    @Subscribe
    protected void onChangeAccountDetailsSuccessfulResponse(ChangeAccountDetailsSuccessfulResponse rsp) {
        if (getLoggedInUser().getID() != rsp.getUser().getID()) return;
        LOG.debug("Received ChangeAccountDetailsSuccessfulResponse");
        setLoggedInUser(rsp.getUser());
    }

    /**
     * Handles a LoginSuccessfulResponse found on the EventBus
     * <p>
     * If a LoginSuccessfulResponse is found on the EventBus, this method saves
     * the contained user as the currently logged in user to serve as the
     * single point of truth for the currently logged in user object.
     *
     * @param rsp The LoginSuccessfulResponse found on the EventBus
     *
     * @author Phillip-André Suhr
     * @author Steven Luong
     * @see de.uol.swp.common.user.response.LoginSuccessfulResponse
     * @since 2021-04-04
     */
    @Subscribe
    private void onLoginSuccessfulResponse(LoginSuccessfulResponse rsp) {
        LOG.debug("Received LoginSuccessfulResponse");
        setLoggedInUser(rsp.getUser());
    }
}
