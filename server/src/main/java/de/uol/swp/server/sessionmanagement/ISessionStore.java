package de.uol.swp.server.sessionmanagement;

/**
 * Interface to unify different kinds of SessionStores
 * in order to able to exchange them easily.
 *
 * @author Eric Vuong
 * @author Marvin Drees
 * @see de.uol.swp.common.sessions.Session
 * @since 2021-04-07
 */
public interface ISessionStore {
    // This is empty as we don't yet implement scaling k/v stores
    // as we don't expect that much users
}
