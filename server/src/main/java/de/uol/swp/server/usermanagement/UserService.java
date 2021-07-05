package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.exception.ChangeAccountDetailsExceptionMessage;
import de.uol.swp.common.exception.RegistrationExceptionMessage;
import de.uol.swp.common.exception.UserDeletionExceptionMessage;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.DeleteUserRequest;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.request.UpdateUserAccountDetailsRequest;
import de.uol.swp.common.user.response.ChangeAccountDetailsSuccessfulResponse;
import de.uol.swp.common.user.response.RegistrationSuccessfulResponse;
import de.uol.swp.common.user.response.UserDeletionSuccessfulResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.message.ReplaceUserSessionInternalRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * Mapping EventBus calls to UserManagement calls
 *
 * @author Marco Grawunder
 * @see de.uol.swp.server.AbstractService
 * @since 2019-08-05
 */
@SuppressWarnings("UnstableApiUsage")
public class UserService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(UserService.class);

    private final IUserManagement userManagement;

    /**
     * Constructor
     *
     * @param eventBus       The EventBus used throughout the entire server (injected)
     * @param userManagement Object of the UserManagement to use
     *
     * @see de.uol.swp.server.usermanagement.UserManagement
     * @since 2019-08-05
     */
    @Inject
    public UserService(EventBus eventBus, UserManagement userManagement) {
        super(eventBus);
        this.userManagement = userManagement;
        LOG.debug("UserService started");
    }

    /**
     * Handles a UpdateUserAccountDetailsRequest found on the EventBus
     * <p>
     * If a UpdateUserAccountDetailsRequest is detected on the EventBus, this method is called.
     * It tries to update the Details of a user via the UserManagement.
     * If this succeeds, a ChangeAccountDetailsSuccessfulResponse is posted onto the EventBus.
     * Otherwise, a ChangeAccountDetailsExceptionMessage gets posted there.
     *
     * @param req The ChangeAccountDetailsRequest found on the Eventbus
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @since 2020-12-03
     */
    @Subscribe
    private void onChangeAccountDetailsRequest(UpdateUserAccountDetailsRequest req) {
        LOG.debug("Received ChangeAccountDetailsRequest for User {}", req.getOldUsername());
        ResponseMessage returnMessage;
        try {
            Optional<User> optionalUser = userManagement.getUser(req.getOldUsername(), req.getOldPassword());
            if (optionalUser.isPresent()) {
                User user = userManagement.updateUser(req.getUser());
                returnMessage = new ChangeAccountDetailsSuccessfulResponse(user);
                LOG.debug("Account Details were changed for {}", req.getUser().getUsername());
                if (!req.getOldUsername().equals(req.getUser().getUsername())) {
                    LOG.debug("Sending UserLoggedOutMessage for User {}", req.getOldUsername());
                    post(new UserLoggedOutMessage(req.getOldUsername()));
                    LOG.debug("Sending UserLoggedInMessage for User {}", req.getUser().getUsername());
                    post(new UserLoggedInMessage(req.getUser().getUsername()));
                    LOG.debug("Sending ReplaceUserSessionInternalRequest");
                    post(new ReplaceUserSessionInternalRequest(user, req.getSession().get()));
                }
                LOG.debug("Sending ChangeAccountDetailsSuccessfulResponse for User {}", req.getUser().getUsername());
            } else {
                returnMessage = new ChangeAccountDetailsExceptionMessage("Old Password was not correct");
                LOG.debug("Sending ChangeAccountDetailsExceptionMessage");
            }
        } catch (UserManagementException e) {
            LOG.error(e);
            returnMessage = new ChangeAccountDetailsExceptionMessage(e.getMessage());
            LOG.debug("Sending ChangeAccountDetailsExceptionMessage");
        }
        returnMessage.initWithMessage(req);
        post(returnMessage);
    }

    /**
     * Handles a DeleteUserRequest found on the EventBus
     * <p>
     * If a DeleteUserRequest is detected on the EventBus, this method is called.
     * It requests the UserManagement to drop the user. If this succeeds,
     * a UserDeletionSuccessfulResponse is posted onto the EventBus.
     * Otherwise, a UserDeletionExceptionMessage gets posted there.
     *
     * @param req The DeleteUserRequest found on the EventBus
     *
     * @see de.uol.swp.server.usermanagement.UserManagement#dropUser(User)
     * @see de.uol.swp.common.user.request.DeleteUserRequest
     * @see de.uol.swp.common.user.response.UserDeletionSuccessfulResponse
     * @see de.uol.swp.common.exception.UserDeletionExceptionMessage
     * @since 2020-11-02
     */
    @Subscribe
    private void onDeleteUserRequest(DeleteUserRequest req) {
        LOG.debug("Received DeleteUserRequest for User {}", req.getUser());
        ResponseMessage returnMessage;
        try {
            Optional<User> user = userManagement.getUser(req.getUser().getUsername(), req.getPassword());
            if (user.isPresent()) {
                userManagement.dropUser(req.getUser());
                returnMessage = new UserDeletionSuccessfulResponse();
                LOG.debug("Sending UserDeletionSuccessfulResponse");
            } else {
                returnMessage = new UserDeletionExceptionMessage(
                        "User deletion unsuccessful for user [" + req.getUser().getUsername() + "]");
                LOG.debug("Sending UserDeletionExceptionMessage");
            }
        } catch (UserManagementException e) {
            LOG.error(e);
            returnMessage = new UserDeletionExceptionMessage(
                    "Cannot delete user [" + req.getUser().getUsername() + "] " + e.getMessage());
            LOG.debug("Sending UserDeletionExceptionMessage");
        }
        returnMessage.initWithMessage(req);
        post(returnMessage);
    }

    /**
     * Handles a RegisterUserRequest found on the EventBus
     * <p>
     * If a RegisterUserRequest is detected on the EventBus, this method is called.
     * It tries to create a new user via the UserManagement. If this succeeds,
     * a RegistrationSuccessfulResponse is posted onto the EventBus.
     * Otherwise, a RegistrationExceptionMessage gets posted there.
     *
     * @param req The RegisterUserRequest found on the EventBus
     *
     * @see de.uol.swp.server.usermanagement.UserManagement#createUser(User)
     * @see de.uol.swp.common.user.request.RegisterUserRequest
     * @see de.uol.swp.common.user.response.RegistrationSuccessfulResponse
     * @see de.uol.swp.common.exception.RegistrationExceptionMessage
     * @since 2019-09-02
     */
    @Subscribe
    private void onRegisterUserRequest(RegisterUserRequest req) {
        LOG.debug("Received RegisterUserRequest for User {}", req.getUser());
        ResponseMessage returnMessage;
        try {
            userManagement.createUser(req.getUser());
            returnMessage = new RegistrationSuccessfulResponse();
            LOG.debug("Sending RegistrationSuccessfulResponse");
        } catch (UserManagementException e) {
            LOG.error(e);
            returnMessage = new RegistrationExceptionMessage(
                    "Cannot create user [" + req.getUser().getUsername() + "] " + e.getMessage());
            LOG.debug("Sending RegistrationExceptionMessage");
        }
        returnMessage.initWithMessage(req);
        post(returnMessage);
    }
}
