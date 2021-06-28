package de.uol.swp.client.scene.util;

import de.uol.swp.common.util.ResourceManager;

/**
 * A utility class used to properly internationalise error messages
 * sent by the server.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.scene.SceneManager
 * @since 2021-06-24
 */
public class ErrorMessageI18nHelper {

    /**
     * Internationalises a Message coming from the server
     *
     * @param e The original exception message
     *
     * @return The internationalised message
     *
     * @author Mario Fokken
     * @author Marvin Drees
     * @since 2021-03-12
     */
    public static String internationaliseServerMessage(String e) {
        String context = e;
        switch (e) {
            //Found in ChatService
            case "This lobby doesn't allow the use of commands!":
                context = ResourceManager.get("error.context.commandsforbidden");
                break;
            //Found in LobbyService
            case "Game session started already!":
                context = ResourceManager.get("error.context.sessionstarted");
                break;
            case "You're already in this lobby!":
                context = ResourceManager.get("error.context.alreadyin");
                break;
            case "This lobby is full!":
                context = ResourceManager.get("error.context.full");
                break;
            case "This lobby does not exist!":
                context = ResourceManager.get("error.context.nonexistant");
                break;
            //Found in GameService
            case "Can not kick while a game is ongoing":
                context = ResourceManager.get("error.context.ongoing");
                break;
            //Found in ServerHandler
            case "Authorisation required. Client not logged in!":
                context = ResourceManager.get("error.context.authneeded");
                break;
            //Found in UserManagement
            case "Username already used!":
            case "Username already taken":
                context = ResourceManager.get("error.context.nameused");
                break;
            case "Username unknown!":
                context = ResourceManager.get("error.context.unknown");
                break;
            case "User unknown!":
                context = ResourceManager.get("error.context.unknownuser");
                break;
            //Found in UserService
            case "Old Passwords are not equal":
                context = ResourceManager.get("error.context.oldpw");
                break;
            case "Old Password was not correct":
                context = ResourceManager.get("error.context.oldpwincorrect");
                break;
        }
        //found in UserManagement
        if (e.contains("Cannot auth user ")) context = ResourceManager.get("error.context.cannotauth", e.substring(17));

        String substring = "";
        if (e.contains("[") && e.contains("]")) substring = e.substring(e.indexOf('[') + 1, e.lastIndexOf(']'));

        if (e.contains("already logged in")) context = ResourceManager.get("error.context.alreadyloggedin", substring);
        //found in UserService
        if (e.contains("Cannot delete user ")) {
            context = ResourceManager
                    .get("error.context.cannotdelete", substring, ResourceManager.get("error.context.unknown"));
        }
        if (e.contains("User deletion unsuccessful for user ")) {
            context = ResourceManager
                    .get("error.context.cannotdelete", substring, ResourceManager.get("error.context.wrongpw"));
        }
        if (e.contains("Cannot create user ")) {
            context = ResourceManager
                    .get("error.context.cannotcreate", substring, ResourceManager.get("error.context.nameused"));
        }
        if (e.contains("Cannot change Password of ")) {
            context = ResourceManager
                    .get("error.context.cannotchangepw", substring, ResourceManager.get("error.context.unknown"));
        }
        //found in LobbyManagement
        if (e.contains("Lobby") && e.contains(" already exists!")) {
            context = ResourceManager.get("error.context.lobby.alreadyused", substring);
        }
        if (e.contains("Lobby") && e.contains(" not found!")) {
            context = ResourceManager.get("error.context.lobby.notfound", substring);
        }
        //found in GameManagement
        if (e.contains("Game") && e.contains(" already exists!")) {
            context = ResourceManager.get("error.context.game.alreadyexists", substring);
        }
        if (e.contains("Game") && e.contains(" not found!")) {
            context = ResourceManager.get("error.context.game.notfound", substring);
        }
        return context;
    }
}
