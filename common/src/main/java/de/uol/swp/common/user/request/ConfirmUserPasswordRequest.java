package de.uol.swp.common.user.request;

import de.uol.swp.common.user.User;

/**
 * Request to confirm a user's password
 *
 * @author Eric Vuong
 * @author Alwin Bossert
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.user.request.UpdateUserRequest
 * @since 2021-03-16
 */
public class ConfirmUserPasswordRequest extends UpdateUserRequest {

    private final String password;

    /**
     * Constructor
     *
     * @param user     The user whose password to update
     * @param password The user's password
     */
    public ConfirmUserPasswordRequest(User user, String password) {
        super(user);
        this.password = password;
    }

    /**
     * Gets the user's password
     *
     * @return The user's password
     */
    public String getPassword() {
        return password;
    }
}
