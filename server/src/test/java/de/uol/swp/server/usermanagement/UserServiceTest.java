package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.DeleteUserRequest;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.request.UpdateUserAccountDetailsRequest;
import de.uol.swp.server.usermanagement.store.H2BasedUserStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class UserServiceTest {

    static final User user = new UserDTO(69, "Ramsi Hartman", "123456", "fourtwenty@blaze.it");

    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement(new H2BasedUserStore());
    final UserService userService = new UserService(bus, userManagement);

    @BeforeEach
    void registerUser() {
        // Register user if not present, will have no effect on an already registered user
        final Message registerRequest = new RegisterUserRequest(user);

        // The post will lead to a call of a UserService function
        bus.post(registerRequest);
    }

    @Test
    void registerUserTest() {
        // We can only test, if something in the state has changed
        final User loggedInUser = userManagement.login(user.getUsername(), user.getPassword());
        assertNotNull(loggedInUser);

        // Cannot compare against the object or ID because RegisterUserRequest doesn't know which ID the user will get
        assertEquals(user.getUsername(), loggedInUser.getUsername());
        assertEquals(user.getEMail(), loggedInUser.getEMail());
    }

    @Test
    void deleteUserTest() {
        // We can only test, if something in the state has changed
        final User loggedInUser = userManagement.login(user.getUsername(), user.getPassword());
        assertNotNull(loggedInUser);

        // Test deletion
        final Message deletionRequest = new DeleteUserRequest(loggedInUser,user.getPassword());

        // The post will lead to a call of a UserService function
        bus.post(deletionRequest);

        // Test if user is deleted again
        assertFalse(userManagement.getUser(user.getID()).isPresent());
    }

    @Test
    void changeUserDetailsTest() {
        // We can only test, if something in the state has changed
        final User loggedInUser = userManagement.login(user.getUsername(), user.getPassword());
        assertNotNull(loggedInUser);

        // Test changing details
        final User newUser = new UserDTO(loggedInUser.getID(), "Kakmir Indihos", "654321", "fourtwentytwo@blaze.it");
        final Message changeAccountDetailsRequest = new UpdateUserAccountDetailsRequest(newUser, user.getPassword(),
                                                                                        user.getUsername(),
                                                                                        user.getEMail());

        // The post will lead to a call of a UserService function
        bus.post(changeAccountDetailsRequest);

        // Check the new details
        final Optional<User> changedUser = userManagement.getUser(loggedInUser.getID());
        assertEquals(newUser.getUsername(), changedUser.get().getUsername());
        assertEquals(newUser.getEMail(), changedUser.get().getEMail());
    }
}
