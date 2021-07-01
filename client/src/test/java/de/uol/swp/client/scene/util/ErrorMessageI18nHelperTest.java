package de.uol.swp.client.scene.util;

import de.uol.swp.common.util.ResourceManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorMessageI18nHelperTest {

    @Test
    void internationaliseServerMessage() {
        String expected = ResourceManager.get("error.context.commandsforbidden");
        assertEquals(expected, ErrorMessageI18nHelper
                .internationaliseServerMessage("This lobby doesn't allow the use of commands!"));

        expected = ResourceManager.get("error.context.sessionstarted");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("Game session started already!"));

        expected = ResourceManager.get("error.context.alreadyin");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("You're already in this lobby!"));

        expected = ResourceManager.get("error.context.full");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("This lobby is full!"));

        expected = ResourceManager.get("error.context.nonexistant");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("This lobby does not exist!"));

        expected = ResourceManager.get("error.context.ongoing");
        assertEquals(expected,
                     ErrorMessageI18nHelper.internationaliseServerMessage("Can not kick while a game is ongoing"));

        expected = ResourceManager.get("error.context.authneeded");
        assertEquals(expected, ErrorMessageI18nHelper
                .internationaliseServerMessage("Authorisation required. Client not logged in!"));

        expected = ResourceManager.get("error.context.nameused");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("Username already taken"));

        expected = ResourceManager.get("error.context.nameused");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("Username already used!"));

        expected = ResourceManager.get("error.context.unknown");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("Username unknown!"));

        expected = ResourceManager.get("error.context.unknownuser");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("User unknown!"));

        expected = ResourceManager.get("error.context.oldpw");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("Old Passwords are not equal"));

        expected = ResourceManager.get("error.context.oldpwincorrect");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("Old Password was not correct"));

        expected = ResourceManager.get("error.context.cannotauth", "test");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("Cannot auth user test"));

        expected = ResourceManager.get("error.context.alreadyloggedin", "test");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("[test] already logged in"));

        expected = ResourceManager
                .get("error.context.cannotdelete", "test", ResourceManager.get("error.context.unknown"));
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("Cannot delete user [test]"));

        expected = ResourceManager
                .get("error.context.cannotdelete", "test", ResourceManager.get("error.context.wrongpw"));
        assertEquals(expected, ErrorMessageI18nHelper
                .internationaliseServerMessage("User deletion unsuccessful for user [test]"));

        expected = ResourceManager
                .get("error.context.cannotcreate", "test", ResourceManager.get("error.context.nameused"));
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("Cannot create user [test]"));

        expected = ResourceManager
                .get("error.context.cannotchangepw", "test", ResourceManager.get("error.context.unknown"));
        assertEquals(expected,
                     ErrorMessageI18nHelper.internationaliseServerMessage("Cannot change Password of [test]"));

        expected = ResourceManager.get("error.context.lobby.alreadyused", "lobbyname");
        assertEquals(expected,
                     ErrorMessageI18nHelper.internationaliseServerMessage("Lobby [lobbyname] already exists!"));

        expected = ResourceManager.get("error.context.lobby.notfound", "lobbyname");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("Lobby [lobbyname] not found!"));

        expected = ResourceManager.get("error.context.game.alreadyexists", "lobbyname");
        assertEquals(expected,
                     ErrorMessageI18nHelper.internationaliseServerMessage("Game [lobbyname] already exists!"));

        expected = ResourceManager.get("error.context.game.notfound", "lobbyname");
        assertEquals(expected, ErrorMessageI18nHelper.internationaliseServerMessage("Game [lobbyname] not found!"));

        assertEquals("context", ErrorMessageI18nHelper.internationaliseServerMessage("context"));
    }
}