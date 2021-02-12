package de.uol.swp.common.user.request;

import de.uol.swp.common.user.User;

/**
 * Request to update a user's password
 *
 * @author Eric Vuong
 * @author Steven Luong
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.user.request.UpdateUserRequest
 * @since 2020-12-17
 */
public class UpdateUserPasswordRequest extends UpdateUserRequest {

    private final String oldPassword;

    /**
     * Constructor
     *
     * @param user        The user whose password to update
     * @param oldPassword The user's old password
     */
    public UpdateUserPasswordRequest(User user, String oldPassword) {
        super(user);
        this.oldPassword = oldPassword;
    }

    /**
     * Gets the user's old password
     *
     * @return The user's old password
     */
    public String getOldPassword() {
        return oldPassword;
    }
}
