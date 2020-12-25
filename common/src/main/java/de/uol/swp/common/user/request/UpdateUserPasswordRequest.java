package de.uol.swp.common.user.request;

import de.uol.swp.common.user.User;

/**
 * Request to update an user's password
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
     * @author Eric Vuong
     * @author Steven Luong
     * @since 2020-12-17
     */
    public UpdateUserPasswordRequest(User user, String oldPassword) {
        super(user);
        this.oldPassword = oldPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }
}
