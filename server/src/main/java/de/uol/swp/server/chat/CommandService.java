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
        ClassLoader cl = getClass().getClassLoader();
        try {
            allClasses = ClassPath.from(cl).getTopLevelClassesRecursive("de.uol.swp");
        } catch (IOException e) {
            // TODO: error handling
            e.printStackTrace();
        }
        LOG.debug("CommandService started");
    }

    @Subscribe
    private void onNewChatCommandMessage(NewChatCommandMessage msg) {
        LOG.debug("Received NewChatCommandMessage");
        List<String> command = new LinkedList<>();
        {
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

        for (String s : command) { // TODO: remove
            System.out.println('"' + s + '"');
        }

        switch (command.get(0)) {
            case "/post":
                System.out.println("hey we used the post command wow :o"); //TODO: remove
                postCommand(command);
                break;
            // TODO: more cases (aka commands)
        }
        /* Examples:
        fixme: /post UpdateInventoryRequest . Lobby2
        works: /post UpdateInventoryRequest test2 "test lobby"
         */
    }

    private void postCommand(List<String> args) {
        for (ClassPath.ClassInfo cinfo : allClasses) {
            if (cinfo.getSimpleName().equals(args.get(1))) {
                System.out.println(cinfo.getSimpleName());
                System.out.println("Heureka, die Klasse ist gefunden"); //TODO: remove
                try {
                    Class<?> cls = Class.forName(cinfo.getName());
                    Constructor<?>[] constrs = cls.getConstructors();
                    for (Constructor<?> constr : constrs) {
                        // 0: command name, 1: Class name, 2+: Class constructor args
                        if (constr.getParameterCount() == args.size() - 2) {
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
                            System.out.println("instance = " + instance);
                            break;
                        }
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException ignored) {
                }
                break;
            }
        }
    }
}
