package de.uol.swp.common.user.request;

import de.uol.swp.common.user.User;

/**
 * Request to update a user's account details
 *
 * @author Eric Vuong
 * @author Steven Luong
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.user.request.UpdateUserRequest
 * @since 2020-12-17
 */
public class UpdateUserAccountDetailsRequest extends UpdateUserRequest {

    private final String oldPassword;
    private final String oldUsername;
    private final String oldEMail;

    /**
     * Constructor
     *
     * @param user        The user whose password to update
     * @param oldPassword The user's old password
     * @param oldUsername The user's old Username
     * @param oldEMail    The user's old EMail
     */
    public UpdateUserAccountDetailsRequest(User user, String oldPassword, String oldUsername, String oldEMail) {
        super(user);
        this.oldPassword = oldPassword;
        this.oldUsername = oldUsername;
        this.oldEMail = oldEMail;
    }

    /**
     * Gets the user's old password
     *
     * @return The user's old password
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Gets the user's new username
     *
     * @return The user's new username
     */
    public String getOldUsername() {
        return oldUsername;
    }

    /**
     * Gets the user's new EMail
     *
     * @return The user's new EMail
     */
    public String getOldEMail() {
        return oldEMail;
    }
}
