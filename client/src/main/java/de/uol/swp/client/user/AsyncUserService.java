package de.uol.swp.client.user;

import com.google.inject.Inject;
import de.uol.swp.client.util.ThreadManager;
import de.uol.swp.common.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An asynchronous wrapper for the IUserService implementation
 * <p>
 * This class handles putting calls to an injected ChatService into
 * their own Task-Thread which is then executed away from the JavaFX
 * Application Thread, isolating non-UI calls onto their own threads.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.user.IUserService
 * @since 2021-05-23
 */
public class AsyncUserService implements IUserService {

    private static final Logger LOG = LogManager.getLogger(AsyncUserService.class);
    private final UserService syncUserService;

    @Inject
    public AsyncUserService(UserService syncUserService) {
        this.syncUserService = syncUserService;
        LOG.debug("AsyncUserService initialised");
    }

    @Override
    public void createUser(User user) {
        ThreadManager.runNow(() -> syncUserService.createUser(user));
    }

    @Override
    public void dropUser(User user, String password) {
        ThreadManager.runNow(() -> syncUserService.dropUser(user, password));
    }

    @Override
    public User getLoggedInUser() {
        // synchronous call
        return syncUserService.getLoggedInUser();
    }

    @Override
    public void setLoggedInUser(User loggedInUser) {
        ThreadManager.runNow(() -> syncUserService.setLoggedInUser(loggedInUser));
    }

    @Override
    public void login(String username, String passwordHash, boolean rememberMe) {
        ThreadManager.runNow(() -> syncUserService.login(username, passwordHash, rememberMe));
    }

    @Override
    public void logout(boolean resetRememberMe) {
        ThreadManager.runNow(() -> syncUserService.logout(resetRememberMe));
    }

    @Override
    public void retrieveAllUsers() {
        ThreadManager.runNow(syncUserService::retrieveAllUsers);
    }

    @Override
    public void updateAccountDetails(User user, String oldHashedPassword, String oldUsername, String oldEMail) {
        ThreadManager
                .runNow(() -> syncUserService.updateAccountDetails(user, oldHashedPassword, oldUsername, oldEMail));
    }
}
