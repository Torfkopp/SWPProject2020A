package de.uol.swp.server.chat;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.chat.message.NewChatCommandMessage;
import de.uol.swp.server.lobby.ILobbyManagement;
import de.uol.swp.server.usermanagement.IUserManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class CommandService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(CommandService.class);
    private static Set<ClassPath.ClassInfo> allClasses;
    private final IUserManagement userManagement; //use to find users by name
    private final ILobbyManagement lobbyManagement;

    @Inject
    public CommandService(EventBus eventBus, ILobbyManagement lobbyManagement, IUserManagement userManagement) {
        super(eventBus);
        this.lobbyManagement = lobbyManagement;
        this.userManagement = userManagement;
        getAllClasses();
        LOG.debug("CommandService started");
    }

    private void command_DevMenu(List<String> args) {
    }

    private void command_Help(List<String> args) {
        //Post a systemmessage
        //
        new StringBuilder().append("The following Commands are available:\n")
                           .append("-------------------------------------\n")
                           .append("\"/help\": Shows this Help screen")
                           .append("\"/post <messagename> <*args>\": Posts a message on the bus")
                           .append("\"/devmenu\": Opens the developer menu").toString();
    }

    private void command_Post(List<String> args) {
        for (ClassPath.ClassInfo cInfo : allClasses) {
            if (cInfo.getSimpleName().equals(args.get(1))) {
                System.out.println(cInfo.getSimpleName());
                try {
                    Class<?> cls = Class.forName(cInfo.getName());
                    Constructor<?>[] constructors = cls.getConstructors();
                    for (Constructor<?> constr : constructors) {
                        // 0: command name, 1: Class name, 2+: Class constructor args
                        if (constr.getParameterCount() == args.size() - 2) {
                            parseArguments(args, constr);
                            break;
                        }
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException ignored) {
                }
                break;
            }
        }
    }

    public void getAllClasses() {
        ClassLoader cl = getClass().getClassLoader();
        try {
            allClasses = ClassPath.from(cl).getTopLevelClassesRecursive("de.uol.swp");
        } catch (IOException e) {
            // TODO: error handling
        }
    }

    private void lexCommand(NewChatCommandMessage msg, List<String> command) {
        String commandString = msg.getCommand();
        int start = 0;
        String subString;
        boolean inQuotes = false;
        for (int i = 0; i <= commandString.length(); i++) {
            subString = commandString.substring(start, i);
            if (subString.endsWith(" ") && !inQuotes) {
                if (subString.strip().length() == 0) continue;
                command.add(subString.strip());
                start = i;
            } else if (subString.endsWith("\"")) {
                if (inQuotes) {
                    command.add(subString.replace("\"", "").strip());
                    start = i;
                }
                inQuotes = !inQuotes;
            }
        }
    }

    @Subscribe
    private void onNewChatCommandMessage(NewChatCommandMessage msg) {
        LOG.debug("Received NewChatCommandMessage");
        List<String> command = new LinkedList<>();
        lexCommand(msg, command);

        for (String s : command) { // TODO: remove
            System.out.println('"' + s + '"');
        }

        switch (command.get(0)) {
            case "post":
                command_Post(command);
                break;
            case "devmenu":
                command_DevMenu(command);
                break;
            case "help":
                command_Help(command);
                break;
            // TODO: more cases (aka commands)
            // Some shortcuts for common Messages/ Requests
        }
        /* Examples:
        fixme: /post UpdateInventoryRequest . Lobby2
        works: /post UpdateInventoryRequest test2 "test lobby"
         */
    }

    private void parseArguments(List<String> args,
                                Constructor<?> constr) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        List<Object> argList = new ArrayList<>();
        Class<?>[] parameters = constr.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            switch (parameters[i].getName()) {
                case "de.uol.swp.common.user.User":
                    if (args.get(i + 2).equals(".") || args.get(i + 2).equals("me")) {
                        // TODO: aktuellen User durchreichen
                    } else {
                        Optional<User> foundUser = userManagement.getUser(args.get(i + 2));
                        if (foundUser.isPresent()) argList.add(foundUser.get());
                    }
                    break;
                case "de.uol.swp.common.lobby.Lobby":
                    Optional<Lobby> foundLobby = lobbyManagement.getLobby(args.get(i + 2));
                    if (foundLobby.isPresent()) argList.add(foundLobby.get());
                default:
                    argList.add(args.get(i + 2));
                    break;
            }
        }
        Object instance = constr.newInstance(argList.toArray());
        //TODO: Post on bus
    }
}
