package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.exception.ChangePasswordExceptionMessage;
import de.uol.swp.common.user.exception.RegistrationExceptionMessage;
import de.uol.swp.common.user.exception.UserDeletionExceptionMessage;
import de.uol.swp.common.user.request.DeleteUserRequest;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.request.UpdateUserPasswordRequest;
import de.uol.swp.common.user.response.ChangePasswordSuccessfulResponse;
import de.uol.swp.common.user.response.RegistrationSuccessfulResponse;
import de.uol.swp.common.user.response.UserDeletionSuccessfulResponse;
import de.uol.swp.server.AbstractService;
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
@Singleton
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
        if (LOG.isDebugEnabled()) LOG.debug("UserService started");
        this.userManagement = userManagement;
    }

    /**
     * Handles a UpdateUserPasswordRequest found on the EventBus
     * <p>
     * If a UpdateUserPasswordRequest is detected on the EventBus, this method is called.
     * It tries to change the Password of a user via the UserManagement.
     * If this succeeds, a ChangePasswordSuccessfulResponse is posted onto the EventBus.
     * Otherwise, a ChangePasswordExceptionMessage gets posted there.
     *
     * @param msg The ChangePasswordRequest found on the Eventbus
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @since 2020-12-03
     */
    @Subscribe
    private void onChangePasswordRequest(UpdateUserPasswordRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received ChangePasswordRequest for User " + msg.getUser().getUsername());
        }
        ResponseMessage returnMessage;
        try {
            Optional<User> optionalUser = userManagement
                    .getUserWithPassword(msg.getUser().getUsername(), msg.getOldPassword());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                userManagement.updateUser(msg.getUser());
                returnMessage = new ChangePasswordSuccessfulResponse();
            } else {
                returnMessage = new ChangePasswordExceptionMessage("Old Passwords are not equal");
            }
        } catch (Exception e) {
            LOG.error(e);
            returnMessage = new ChangePasswordExceptionMessage(
                    "Cannot change Password of " + msg.getUser() + " " + e.getMessage());
        }
        if (msg.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(msg.getMessageContext().get());
            post(returnMessage);
        }
    }

    /**
     * Handles a DeleteUserRequest found on the EventBus
     * <p>
     * If a DeleteUserRequest is detected on the EventBus, this method is called.
     * It requests the UserManagement to drop the user. If this succeeds,
     * a UserDeletionSuccessfulResponse is posted onto the EventBus.
     * Otherwise, a UserDeletionExceptionMessage gets posted there.
     *
     * @param msg The DeleteUserRequest found on the EventBus
     *
     * @see de.uol.swp.server.usermanagement.UserManagement#dropUser(User)
     * @see de.uol.swp.common.user.request.DeleteUserRequest
     * @see de.uol.swp.common.user.response.UserDeletionSuccessfulResponse
     * @see de.uol.swp.common.user.exception.UserDeletionExceptionMessage
     * @since 2020-11-02
     */
    @Subscribe
    private void onDeleteUserRequest(DeleteUserRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received DeleteUserRequest for User " + msg.getUser());
        }
        ResponseMessage returnMessage;
        try {
            userManagement.dropUser(msg.getUser());
            returnMessage = new UserDeletionSuccessfulResponse();
        } catch (Exception e) {
            LOG.error(e);
            returnMessage = new UserDeletionExceptionMessage(
                    "Cannot delete user " + msg.getUser() + " " + e.getMessage());
        }
        if (msg.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(msg.getMessageContext().get());
        }
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
     * @param msg The RegisterUserRequest found on the EventBus
     *
     * @see de.uol.swp.server.usermanagement.UserManagement#createUser(User)
     * @see de.uol.swp.common.user.request.RegisterUserRequest
     * @see de.uol.swp.common.user.response.RegistrationSuccessfulResponse
     * @see de.uol.swp.common.user.exception.RegistrationExceptionMessage
     * @since 2019-09-02
     */
    @Subscribe
    private void onRegisterUserRequest(RegisterUserRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received RegisterUserRequest for User " + msg.getUser());
        }
        ResponseMessage returnMessage;
        try {
            User newUser = userManagement.createUser(msg.getUser());
            returnMessage = new RegistrationSuccessfulResponse();
        } catch (Exception e) {
            LOG.error(e);
            returnMessage = new RegistrationExceptionMessage(
                    "Cannot create user " + msg.getUser() + " " + e.getMessage());
        }
        if (msg.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(msg.getMessageContext().get());
        }
        post(returnMessage);
    }
}
