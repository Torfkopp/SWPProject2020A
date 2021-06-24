package de.uol.swp.common.user;

import java.io.Serializable;

/**
 * Interface for different kinds of user objects.
 * <p>
 * This interface is for unifying different kinds of user objects throughout the
 * project. With this being the base project it is currently only used for the UserDTO
 * objects.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.UserDTO
 * @see java.io.Serializable
 * @see java.lang.Comparable
 * @since 2019-08-05
 */
public interface User extends Actor, Serializable {

    /**
     * Gets the e-mail variable
     *
     * @return E-mail address of the user as a string
     *
     * @since 2019-08-05
     */
    String getEMail();

    /**
     * Gets the password variable
     *
     * @return Password of the user as a string
     *
     * @since 2019-08-05
     */
    String getPassword();

    /**
     * Creates a duplicate of this object leaving its password empty
     *
     * @return Copy of this with an empty password field
     *
     * @since 2019-08-05
     */
    User getWithoutPassword();
}
