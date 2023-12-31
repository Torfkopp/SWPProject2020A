package de.uol.swp.server.sessionmanagement;

import de.uol.swp.common.sessions.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SessionManagementTest {

    static final User user = new UserDTO(69, "Joe Mama", "12345", "epic@mail.gg");
    static final User anotherUser = new UserDTO(420, "Dixie Normous", "54321", "mail@blaze.it");
    static SessionManagement sessionManagement;

    @BeforeEach
    void cleanSessions() {
        sessionManagement = new SessionManagement();
    }

    @Test
    void createSession() {
        Session session = sessionManagement.createSession(user);
        assertTrue(sessionManagement.hasSession(session));
        assertEquals(session.getUser(), user);
    }

    @Test
    void getAllUsers() {
        Collection<User> users = new HashSet<>();
        users.add(user);
        users.add(anotherUser);

        Session s1 = sessionManagement.createSession(user);
        assertTrue(sessionManagement.hasSession(s1));
        Session s2 = sessionManagement.createSession(anotherUser);
        assertTrue(sessionManagement.hasSession(s2));

        assertTrue(users.containsAll(sessionManagement.getAllUsers()));
    }

    @Test
    void getSession() {
        Session session = sessionManagement.createSession(user);
        assertTrue(sessionManagement.hasSession(session));

        assertTrue(sessionManagement.getSession(user).isPresent());
        assertEquals(sessionManagement.getSession(user).get(), session);
    }

    @Test
    void getSessions() {
        Set<User> users = new TreeSet<>();
        users.add(user);
        users.add(anotherUser);

        Session s1 = sessionManagement.createSession(user);
        assertTrue(sessionManagement.hasSession(s1));
        Session s2 = sessionManagement.createSession(anotherUser);
        assertTrue(sessionManagement.hasSession(s2));

        List<Session> sessions = new ArrayList<>();
        sessions.add(s1);
        sessions.add(s2);

        assertEquals(sessionManagement.getSessions(users), sessions);
    }

    @Test
    void removeSession() {
        Session session = sessionManagement.createSession(user);
        assertTrue(sessionManagement.hasSession(session));

        sessionManagement.removeSession(session);
        assertFalse(sessionManagement.hasSession(session));
        assertFalse(sessionManagement.hasSession(user));
    }

    @Test
    void removeSession_NotFound() {
        Session session = UUIDSession.create(user);
        assertFalse(sessionManagement.hasSession(session));

        assertThrows(SessionManagementException.class, () -> sessionManagement.removeSession(session));
    }
}
