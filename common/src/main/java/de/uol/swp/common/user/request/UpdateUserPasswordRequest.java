package de.uol.swp.common.user.request;

import de.uol.swp.common.user.User;

public class UpdateUserPasswordRequest extends UpdateUserRequest{
    private String oldPassword;
    /**
     * Constructor
     *
     * @param user the user object the sender shall be updated to unchanged fields
     *             being empty
     * @since 2019-09-02
     */
    public UpdateUserPasswordRequest(User user, String oldPassword) {
        super(user);
        this.oldPassword = oldPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }
}
