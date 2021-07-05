package de.uol.swp.client.user;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class AsyncUserServiceTest {

    private static final long DURATION = 500L;
    private final UserService syncUserService = mock(UserService.class);
    private final User user = mock(User.class);
    private AsyncUserService userService;

    @BeforeEach
    protected void setUp() {
        assertNotNull(syncUserService);
        userService = new AsyncUserService(syncUserService);
    }

    @AfterEach
    protected void tearDown() {
        userService = null;
    }

    @Test
    void createUser() {
        doNothing().when(syncUserService).createUser(isA(User.class));

        userService.createUser(user);

        verify(syncUserService, timeout(DURATION)).createUser(user);
    }

    @Test
    void dropUser() {
        String password = "test";
        doNothing().when(syncUserService).dropUser(isA(User.class), isA(String.class));

        userService.dropUser(user, password);

        verify(syncUserService, timeout(DURATION)).dropUser(user, password);
    }

    @Test
    void getLoggedInUser() {
        when(syncUserService.getLoggedInUser()).thenReturn(user);

        userService.getLoggedInUser();

        verify(syncUserService, timeout(DURATION)).getLoggedInUser();
    }

    @Test
    void login() {
        String name = "test";
        String password = "6strngpsswrd9";
        doNothing().when(syncUserService).login(isA(String.class), isA(String.class), isA(Boolean.class));

        userService.login(name, password, false);

        verify(syncUserService, timeout(DURATION)).login(name, password, false);
    }

    @Test
    void logout() {
        doNothing().when(syncUserService).logout(isA(Boolean.class));

        userService.logout(false);

        verify(syncUserService, timeout(DURATION)).logout(false);
    }

    @Test
    void retrieveAllUsers() {
        doNothing().when(syncUserService).retrieveAllUsers();

        userService.retrieveAllUsers();

        verify(syncUserService, timeout(DURATION)).retrieveAllUsers();
    }

    @Test
    void setLoggedInUser() {
        doNothing().when(syncUserService).setLoggedInUser(isA(User.class));

        userService.setLoggedInUser(user);

        verify(syncUserService, timeout(DURATION)).setLoggedInUser(user);
    }

    @Test
    void updateAccountDetails() {
        String password = "tst247!";
        String username = "X_24_tester_7_X";
        String email = "tst@tst.tst";
        doNothing().when(syncUserService)
                   .updateAccountDetails(isA(User.class), isA(String.class), isA(String.class), isA(String.class));

        userService.updateAccountDetails(user, password, username, email);

        verify(syncUserService, timeout(DURATION)).updateAccountDetails(user, password, username, email);
    }
}