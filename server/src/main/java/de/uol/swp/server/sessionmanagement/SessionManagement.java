package de.uol.swp.server.sessionmanagement;

import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.sessions.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

import java.util.*;

/**
 * Handles Session related issues
 *
 * @author Eric Vuong
 * @author Marvin Drees
 * @since 2021-04-07
 */
public class SessionManagement implements ISessionManagement {

    private final Map<Session, User> userSessions = new HashMap<>();
    private final Map<MessageContext, Session> activeSessions = new HashMap<>();

    @Override
    public Session createSession(User user) {
        Session session = UUIDSession.create(user);
        userSessions.put(session, user);
        return session;
    }

    @Override
    public Collection<User> getAllUsers() {
        return userSessions.values();
    }

    @Override
    public Optional<MessageContext> getCtx(Session session) {
        for (Map.Entry<MessageContext, Session> e : activeSessions.entrySet()) {
            if (e.getValue().equals(session)) {
                return Optional.of(e.getKey());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Session> getSession(UserOrDummy user) {
        Optional<Map.Entry<Session, User>> entry = userSessions.entrySet().stream()
                                                               .filter(e -> e.getValue().equals(user)).findFirst();
        return entry.map(Map.Entry::getKey);
    }

    @Override
    public Optional<Session> getSession(MessageContext ctx) {
        Session session = activeSessions.get(ctx);
        return session != null ? Optional.of(session) : Optional.empty();
    }

    @Override
    public List<Session> getSessions(Set<User> users) {
        List<Session> sessions = new ArrayList<>();
        users.forEach(u -> {
            Optional<Session> session = getSession(u);
            session.ifPresent(sessions::add);
        });
        return sessions;
    }

    @Override
    public boolean hasSession(Session session) {
        return userSessions.containsKey(session);
    }

    @Override
    public boolean hasSession(User user) {
        return userSessions.containsValue(user);
    }

    @Override
    public void putSession(MessageContext ctx, Session session) throws SessionManagementException {
        if (!activeSessions.containsValue(session)) activeSessions.put(ctx, session);
        else throw new SessionManagementException("Session already bound to connection!");
    }

    @Override
    public void removeSession(Session session) throws SessionManagementException {
        if (userSessions.containsKey(session)) userSessions.remove(session);
        else throw new SessionManagementException("Session not found");
    }

    @Override
    public void removeSession(MessageContext ctx) {
        activeSessions.remove(ctx);
    }
}
