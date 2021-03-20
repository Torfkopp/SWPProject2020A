package de.uol.swp.server.usermanagement.store;

import com.google.common.base.Strings;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.*;

/**
 * This is a user store.
 * <p>
 * This is the user store that is used at the start of the software project. The
 * user accounts in this user store only reside within the RAM of your computer
 * and only for as long as the server is running. Therefore, the users have to be
 * added every time the server is started.
 *
 * @author Marco Grawunder
 * @implNote This store will never return the password of a user!
 * @see de.uol.swp.server.usermanagement.store.AbstractUserStore
 * @see de.uol.swp.server.usermanagement.store.UserStore
 * @since 2019-08-05
 */
public class MainMemoryBasedUserStore extends AbstractUserStore implements UserStore {

    private final Map<String, User> usersByName = new HashMap<>();
    private final Map<Integer, User> usersById = new HashMap<>();
    private int id_counter;

    @Override
    public User createUser(String username, String password, String eMail) {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }
        int id;
        if (usersByName.containsKey(username)) id = usersByName.get(username).getID();
        else id = id_counter++;
        User usr = new UserDTO(id, username, hash(password), eMail);
        usersById.put(id, usr);
        usersByName.put(username, usr);
        return usr.getWithoutPassword();
    }

    @Override
    public Optional<User> findUser(int id) {
        User usr = usersById.get(id);
        if (usr != null) {
            return Optional.of(usr.getWithoutPassword());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(String username) {
        User usr = usersByName.get(username);
        if (usr != null) {
            return Optional.of(usr.getWithoutPassword());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(String username, String password) {
        User usr = usersByName.get(username);
        if (usr != null && Objects.equals(usr.getPassword(), hash(password))) {
            return Optional.of(usr.getWithoutPassword());
        }
        return Optional.empty();
    }

    @Override
    public List<User> getAllUsers() {
        List<User> retUsers = new ArrayList<>();
        usersByName.values().forEach(u -> retUsers.add(u.getWithoutPassword()));
        return retUsers;
    }

    @Override
    public int getNextUserID() {
        return id_counter;
    }

    @Override
    public void removeUser(int id) {
        Optional<User> user = findUser(id);
        if (user.isPresent()) {
            usersByName.remove(user.get().getUsername());
            usersById.remove(id);
        }
    }

    @Override
    public void removeUser(String username) {
        Optional<User> user = findUser(username);
        if (user.isPresent()) {
            usersById.remove(user.get().getID());
            usersByName.remove(username);
        }
    }

    @Override
    public User updateUser(int id, String username, String password, String eMail) {
        Optional<User> user = findUser(id);
        if (user.isEmpty()) throw new IllegalArgumentException("No user with this ID found");
        else {
            if (Strings.isNullOrEmpty(username)) {
                throw new IllegalArgumentException("Username must not be null");
            }
            removeUser(user.get().getUsername());
            User usr = new UserDTO(id, username, hash(password), eMail);
            usersByName.put(username, usr);
            usersById.put(id, usr);
            return usr.getWithoutPassword();
        }
    }

    @Override
    public User updateUser(String username, String password, String eMail) {
        Optional<User> user = findUser(username);
        if (user.isEmpty()) throw new IllegalArgumentException("No user with this name found");
        else return createUser(username, password, eMail);
    }
}
