package de.uol.swp.common.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the UserDTO class
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.user.UserDTO
 * @since 2019-09-04
 */
class UserDTOTest {

    private static final User defaultUser = new UserDTO(1, "marco", "marco", "marco@grawunder.de");
    private static final User secondsUser = new UserDTO(2, "marco2", "marco", "marco@grawunder.de");

    /**
     * This test checks if the username can be null.
     * <p>
     * If the constructor does not throw an exception, this test fails.
     *
     * @since 2019-09-04
     */
    @Test
    void createUserWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new UserDTO(100, null, "", ""));
    }

    /**
     * This test checks if the password can be null.
     * <p>
     * If the constructor does not throw an exception, this test fails.
     *
     * @since 2019-09-04
     */
    @Test
    void createUserWithEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> new UserDTO(100, "", null, ""));
    }

    /**
     * This test checks if the copy constructor works correctly.
     * <p>
     * This test fails if any of the fields mismatch or the objects are not considered equal.
     *
     * @since 2019-09-04
     */
    @Test
    void createWithExistingUser() {
        User newUser = UserDTO.create(defaultUser);

        // Test with equals method
        assertEquals(defaultUser, newUser);

        // Test every attribute
        assertEquals(defaultUser.getUsername(), newUser.getUsername());
        assertEquals(defaultUser.getPassword(), newUser.getPassword());
        assertEquals(defaultUser.getEMail(), newUser.getEMail());
    }

    /**
     * This test checks if the createWithoutPassword function generates the Object correctly.
     * <p>
     * This test fails when the usernames or e-mails do not match, or when the password is not empty.
     *
     * @since 2019-09-04
     */
    @Test
    void createWithExistingUserWithoutPassword() {
        User newUser = UserDTO.createWithoutPassword(defaultUser);

        // Test every attribute
        assertEquals(defaultUser.getUsername(), newUser.getUsername());
        assertEquals("", newUser.getPassword());
        assertEquals(defaultUser.getEMail(), newUser.getEMail());

        // Test with equals method
        assertEquals(defaultUser, newUser);
    }

    /**
     * This test checks if the getWithoutPassword function generates the Object correctly.
     * <p>
     * This test fails when the usernames do not match, or when the password is not empty.
     *
     * @since 2019-09-04
     */
    @Test
    void getWithoutPassword() {
        User userWithoutPassword = defaultUser.getWithoutPassword();

        assertEquals(defaultUser.getPassword(), userWithoutPassword.getUsername());
        assertEquals("", userWithoutPassword.getPassword());
        assertEquals(defaultUser.getUsername(), userWithoutPassword.getUsername());
    }

    /**
     * Tests if the HashCode of a copied object matches the one of the original.
     * <p>
     * This test fails when the codes do not match.
     *
     * @since 2019-09-04
     */
    @Test
    void testHashCode() {
        User newUser = UserDTO.create(defaultUser);
        assertEquals(defaultUser.hashCode(), newUser.hashCode());
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
        assertEquals(-1, defaultUser.compareTo(secondsUser));
    }

    /**
     * Tests if a UserDTO object is different from a String.
     * <p>
     * This test fails when the UserDTO object is considered equal to the String "Test".
     *
     * @since 2019-09-04
     */
    @Test
    void usersNotEquals_String() {
        assertNotEquals("Test", defaultUser);
    }

    /**
     * Tests if two different users are equal.
     * <p>
     * This test fails when they are considered equal.
     *
     * @since 2019-09-04
     */
    @Test
    void usersNotEquals_User() {
        assertNotEquals(defaultUser, secondsUser);
    }
}