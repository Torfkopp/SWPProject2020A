package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.sessions.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.server.message.ClientAuthorisedMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import de.uol.swp.server.sessionmanagement.SessionManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class AuthenticationServiceTest {

    final User user = new UserDTO(1, "name", "password", "email@test.de");
    final User user2 = new UserDTO(2, "name2", "password2", "email@test.de2");
    final User user3 = new UserDTO(3, "name3", "password3", "email@test.de3");
    final UserStore userStore = new MainMemoryBasedUserStore();
    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement(userStore);
    final SessionManagement sessionManagement = new SessionManagement();
    final AuthenticationService authService = new AuthenticationService(bus, userManagement, sessionManagement);
    private final CountDownLatch lock = new CountDownLatch(1);
    private Object event;

    @AfterEach
    protected void deregisterBus() {
        bus.unregister(this);
    }

    @BeforeEach
    protected void registerBus() {
        event = null;
        bus.register(this);
    }

    @Test
    void getSessionsForUsersTest() {
        User usr1 = loginUser(user);
        User usr2 = loginUser(user2);
        User usr3 = loginUser(user3);
        Set<User> users = new TreeSet<>();
        users.add(usr1);
        users.add(usr2);
        users.add(usr3);

        Optional<Session> session1 = sessionManagement.getSession(usr1);
        Optional<Session> session2 = sessionManagement.getSession(usr2);
        Optional<Session> session3 = sessionManagement.getSession(usr2);

        assertTrue(session1.isPresent());
        assertTrue(session2.isPresent());
        assertTrue(session3.isPresent());

        List<Session> sessions = sessionManagement.getSessions(users);

        assertEquals(3, sessions.size());
        assertTrue(sessions.contains(session1.get()));
        assertTrue(sessions.contains(session2.get()));
        assertTrue(sessions.contains(session3.get()));
    }

    @Test
    void loggedInUsers() throws InterruptedException {
        User usr = loginUser(user);

        Message request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);

        lock.await(250, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof AllOnlineUsersResponse);

        assertEquals(1, ((AllOnlineUsersResponse) event).getUsers().size());
        assertEquals(usr, ((AllOnlineUsersResponse) event).getUsers().get(0));
    }

    @Test
    void loggedInUsersEmpty() throws InterruptedException {
        Message request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);

        lock.await(250, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof AllOnlineUsersResponse);

        assertTrue(((AllOnlineUsersResponse) event).getUsers().isEmpty());
    }

    @Test
    void loginTest() throws InterruptedException {
        User usr = userManagement.createUser(user);
        final Message loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        bus.post(loginRequest);
        lock.await(250, TimeUnit.MILLISECONDS);
        assertTrue(userManagement.isLoggedIn(usr));
        // is message sent
        assertTrue(event instanceof ClientAuthorisedMessage);
        userManagement.dropUser(user);
    }

    @Test
    void loginTestFail() throws InterruptedException {
        User usr = userManagement.createUser(user);
        final Message loginRequest = new LoginRequest(user.getUsername(), user.getPassword() + "äüö");
        bus.post(loginRequest);

        lock.await(250, TimeUnit.MILLISECONDS);
        assertFalse(userManagement.isLoggedIn(usr));
        assertTrue(event instanceof ServerExceptionMessage);
        userManagement.dropUser(usr);
    }

    @Test
    void logoutTest() throws InterruptedException {
        User usr = loginUser(user);
        Optional<Session> session = sessionManagement.getSession(usr);

        assertTrue(session.isPresent());
        final Message logoutRequest = new LogoutRequest();
        logoutRequest.setSession(session.get());

        bus.post(logoutRequest);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertFalse(userManagement.isLoggedIn(user));
        assertFalse(sessionManagement.getSession(user).isPresent());
        assertTrue(event instanceof UserLoggedOutMessage);
    }

    // TODO: replace with parametrized test
    @Test
    void twoLoggedInUsers() throws InterruptedException {
        List<User> users = new ArrayList<>();
        User usr = loginUser(user);
        User usr2 = loginUser(user2);
        users.add(usr);
        users.add(usr2);

        Collections.sort(users);

        Message request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);

        lock.await(250, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof AllOnlineUsersResponse);

        List<User> returnedUsers = new ArrayList<>(((AllOnlineUsersResponse) event).getUsers());

        assertEquals(2, returnedUsers.size());

        Collections.sort(returnedUsers);
        assertEquals(users, returnedUsers);
    }

    private User loginUser(User userToLogin) {
        User usr = userManagement.createUser(userToLogin);
        final Message loginRequest = new LoginRequest(userToLogin.getUsername(), userToLogin.getPassword());
        bus.post(loginRequest);

        assertTrue(userManagement.isLoggedIn(usr));
        userManagement.dropUser(userToLogin);
        return usr;
    }

    @Subscribe
    private void onDeadEvent(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }
}
