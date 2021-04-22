package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.DeleteUserRequest;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.request.UpdateUserAccountDetailsRequest;
import de.uol.swp.server.usermanagement.store.H2BasedUserStore;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("UnstableApiUsage")
class UserServiceTest {

    private static final User user = new UserDTO(69, "Ramsi Hartman", "123456", "fourtwenty@blaze.it");
    private final EventBus bus = new EventBus();
    private final UserManagement userManagement = new UserManagement(new H2BasedUserStore());
    private final UserService userService = new UserService(bus, userManagement);

    @Test
    void changeUserDetailsTest() {
        // Create basic user
        userManagement.createUser(user);
        Optional<User> usr = userManagement.getUser(user.getUsername(), user.getPassword());
        assertTrue(usr.isPresent());

        // Test changing details
        User newUser = new UserDTO(usr.get().getID(), "Kakmir Indihos", "654321", "fourtwentytwo@blaze.it");
        Message changeAccountDetailsRequest = new UpdateUserAccountDetailsRequest(newUser, user.getPassword(),
                                                                                  user.getUsername(), user.getEMail());

        // The post will lead to a call of a UserService function
        bus.post(changeAccountDetailsRequest);

        // Check the new details
        Optional<User> changedUser = userManagement.getUser(newUser.getID());
        assertTrue(changedUser.isPresent());
        assertEquals(newUser.getID(), changedUser.get().getID());
        assertEquals(newUser.getUsername(), changedUser.get().getUsername());
        assertEquals("", changedUser.get().getPassword());
        assertEquals(newUser.getEMail(), changedUser.get().getEMail());

        // Check that the old username was overwritten
        Optional<User> userOptional = userManagement.getUser(usr.get().getUsername());
        assertTrue(userOptional.isEmpty());

        userManagement.dropUser(changedUser.get());
    }

    @Test
    void deleteUserTest() {
        // Create basic user
        userManagement.createUser(user);
        Optional<User> usr = userManagement.getUser(user.getUsername(), user.getPassword());
        assertTrue(usr.isPresent());

        // Test Deletion of User
        Message deletionRequest = new DeleteUserRequest(usr.get());

        // The post will lead to a call of a UserService function
        bus.post(deletionRequest);

        // Test if user is deleted again
        Optional<User> optional = userManagement.getUser(user.getUsername(), user.getPassword());
        assertTrue(optional.isEmpty());
    }

    @Test
    void registerUserTest() {
        // Create entirely new user
        UserDTO user = new UserDTO(-1, "Mike Oxlong", "64209", "mike@ox.long.com");

        Message createUserRequest = new RegisterUserRequest(user);
        bus.post(createUserRequest);

        Optional<User> usr = userManagement.getUser(user.getUsername(), user.getPassword());
        assertTrue(usr.isPresent());

        // Drop the created user again
        userManagement.dropUser(usr.get());
    }
}
