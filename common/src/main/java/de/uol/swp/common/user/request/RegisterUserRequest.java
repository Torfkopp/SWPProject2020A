package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Request to register a new user
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @since 2019-09-02
 */
public class RegisterUserRequest extends AbstractRequestMessage {

    private final User toCreate;

    /**
     * Constructor
     *
     * @param user the new User to create
     * @since 2019-09-02
     */
    public RegisterUserRequest(User user) {
        this.toCreate = user;
    }

    @Override
    public boolean authorisationNeeded() {
        return false;
    }

    /**
     * Getter for the user variable
     *
     * @return the new user to create
     * @since 2019-09-02
     */
    public User getUser() {
        return toCreate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toCreate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterUserRequest that = (RegisterUserRequest) o;
        return Objects.equals(toCreate, that.toCreate);
    }
}
