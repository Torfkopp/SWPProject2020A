package de.uol.swp.common.user.request;

import de.uol.swp.common.user.User;

/**
 * Request to update an user´s password
 *
 * @see de.uol.swp.common.user.User
 * @author Eric Vuong, Steven Luong
 * @since 2020-12-17
 */
public class UpdateUserPasswordRequest extends UpdateUserRequest{

    private String oldPassword;


    /**
     * Constructor
     *
     * @author Eric Vuong, Steven Luong
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
