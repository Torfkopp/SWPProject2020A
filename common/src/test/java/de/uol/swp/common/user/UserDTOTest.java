package de.uol.swp.common.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the UserDTO
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.user.UserDTO
 * @since 2019-09-04
 */
class UserDTOTest {

    private static final User defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
    private static final User secondsUser = new UserDTO("marco2", "marco", "marco@grawunder.de");

    /**
     * This test check whether the username can be null
     * <p>
     * If the constructor does not throw an Exception the test fails
     *
     * @since 2019-09-04
     */
    @Test
    void createUserWithEmptyName() {
        assertThrows(AssertionError.class, () -> new UserDTO(null, "", ""));
    }

    /**
     * This test check whether the password can be null
     * <p>
     * If the constructor does not throw an Exception the test fails
     *
     * @since 2019-09-04
     */
    @Test
    void createUserWithEmptyPassword() {
        assertThrows(AssertionError.class, () -> new UserDTO("", null, ""));
    }

    /**
     * This test checks if the copy constructor works correctly
     * <p>
     * This test fails if any of the fields mismatch or the objects are not considered equal
     *
     * @since 2019-09-04
     */
    @Test
    void createWithExistingUser() {
        User newUser = UserDTO.create(defaultUser);

        // Test with equals method
        assertEquals(newUser, defaultUser);

        // Test every attribute
        assertEquals(newUser.getUsername(), defaultUser.getUsername());
        assertEquals(newUser.getPassword(), defaultUser.getPassword());
        assertEquals(newUser.getEMail(), defaultUser.getEMail());
    }

    /**
     * This test checks if the createWithoutPassword function generates the Object correctly
     * <p>
     * This test fails if the usernames or emails do not match or the password is not empty.
     *
     * @since 2019-09-04
     */
    @Test
    void createWithExistingUserWithoutPassword() {
        User newUser = UserDTO.createWithoutPassword(defaultUser);

        // Test every attribute
        assertEquals(newUser.getUsername(), defaultUser.getUsername());
        assertEquals(newUser.getPassword(), "");
        assertEquals(newUser.getEMail(), defaultUser.getEMail());

        // Test with equals method
        assertEquals(newUser, defaultUser);
    }

    /**
     * This test checks if the getWithoutPassword function generates the Object correctly
     * <p>
     * This test fails if the usernames do not match or the password is not empty.
     *
     * @since 2019-09-04
     */
    @Test
    void getWithoutPassword() {
        User userWithoutPassword = defaultUser.getWithoutPassword();

        assertEquals(userWithoutPassword.getUsername(), defaultUser.getPassword());
        assertEquals(userWithoutPassword.getPassword(), "");
        assertEquals(userWithoutPassword.getUsername(), defaultUser.getUsername());
    }

    /**
     * Test if two different users are equal
     * <p>
     * This test fails if they are considered equal
     *
     * @since 2019-09-04
     */
    @Test
    void usersNotEquals_User() {
        assertNotEquals(defaultUser, secondsUser);
    }

    /**
     * Test if a UserDTO object is different from a String.
     * <p>
     * This test fails if the UserDTO object ist considered equal to the String "Test"
     *
     * @since 2019-09-04
     */
    @Test
    void usersNotEquals_String() {
        assertNotEquals(defaultUser, "Test");
    }

    /**
     * Test of compare function
     * <p>
     * This test compares two different users. It fails if the function returns
     * that both of them are equal.
     *
     * @since 2019-09-04
     */
    @Test
    void userCompare() {
        assertEquals(defaultUser.compareTo(secondsUser), -1);
    }

    /**
     * Test if the HashCode of a copied object matches the one of the original
     * <p>
     * This test fails if the codes do not match
     *
     * @since 2019-09-04
     */
    @Test
    void testHashCode() {
        User newUser = UserDTO.create(defaultUser);
        assertEquals(newUser.hashCode(), defaultUser.hashCode());
    }
}