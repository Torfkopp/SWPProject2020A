package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class UserServiceTest {

    static final User userToRegister = new UserDTO(1, "Marco", "Marco", "Marco@Grawunder.com");
    static final User userWithSameName = new UserDTO(2, "Marco", "Marco2", "Marco2@Grawunder.com");

    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final UserService userService = new UserService(bus, userManagement);

    @Test
    void registerSecondUserWithSameName() {
        final Message request = new RegisterUserRequest(userToRegister);
        final Message request2 = new RegisterUserRequest(userWithSameName);

        bus.post(request);
        bus.post(request2);

        final User loggedInUser = userManagement.login(userToRegister.getUsername(), userToRegister.getPassword());

        // old user should be still in the store
        assertNotNull(loggedInUser);
        // Cannot compare against the object or ID because RegisterUserRequest doesn't know which ID the user will get
        assertEquals(userToRegister.getUsername(), loggedInUser.getUsername());
        assertEquals(userToRegister.getEMail(), loggedInUser.getEMail());

        // old user should not be overwritten!
        assertNotEquals(userWithSameName.getEMail(), loggedInUser.getEMail());
    }

    @Test
    void registerUserTest() {
        final Message request = new RegisterUserRequest(userToRegister);

        // The post will lead to a call of a UserService function
        bus.post(request);

        // can only test, if something in the state has changed
        final User loggedInUser = userManagement.login(userToRegister.getUsername(), userToRegister.getPassword());

        assertNotNull(loggedInUser);
        // Cannot compare against the object or ID because RegisterUserRequest doesn't know which ID the user will get
        assertEquals(userToRegister.getUsername(), loggedInUser.getUsername());
        assertEquals(userToRegister.getEMail(), loggedInUser.getEMail());
    }
}
