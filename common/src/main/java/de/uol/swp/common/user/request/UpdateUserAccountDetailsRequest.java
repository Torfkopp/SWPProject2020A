package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Request to update a user's account details
 *
 * @author Eric Vuong
 * @author Steven Luong
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.user.request.UpdateUserAccountDetailsRequest
 * @since 2020-12-17
 */
public class UpdateUserAccountDetailsRequest extends AbstractRequestMessage {

    private final String oldPassword;
    private final String oldUsername;
    private final String oldEMail;
    private final User toUpdate;

    /**
     * Constructor
     *
     * @param user        The user whose account details to update
     * @param oldPassword The user's old password
     * @param oldUsername The user's old Username
     * @param oldEMail    The user's old EMail
     */
    public UpdateUserAccountDetailsRequest(User user, String oldPassword, String oldUsername, String oldEMail) {
        this.toUpdate = user;
        this.oldPassword = oldPassword;
        this.oldUsername = oldUsername;
        this.oldEMail = oldEMail;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), oldPassword, oldUsername, oldEMail, toUpdate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UpdateUserAccountDetailsRequest request = (UpdateUserAccountDetailsRequest) o;
        return oldPassword.equals(request.oldPassword) && oldUsername.equals(request.oldUsername) && oldEMail
                .equals(request.oldEMail) && toUpdate.equals(request.toUpdate);
    }

    /**
     * Gets the user's new EMail
     *
     * @return The user's new EMail
     */
    public String getOldEMail() {
        return oldEMail;
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
     * Gets the updated User object
     *
     * @return The updated User object
     *
     * @since 2019-09-02
     */
    public User getUser() {
        return toUpdate;
    }
}
