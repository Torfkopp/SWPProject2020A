package de.uol.swp.server.chat;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.chat.response.SystemMessageResponse;
import de.uol.swp.common.devmenu.request.DevMenuClassesRequest;
import de.uol.swp.common.devmenu.request.DevMenuCommandRequest;
import de.uol.swp.common.devmenu.response.DevMenuClassesResponse;
import de.uol.swp.common.devmenu.response.OpenDevMenuResponse;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.chat.message.NewChatCommandMessage;
import de.uol.swp.server.lobby.ILobbyManagement;
import de.uol.swp.server.usermanagement.IUserManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
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

    private void command_DevMenu(List<String> args, NewChatMessageRequest originalMessage) {
        OpenDevMenuResponse msg = new OpenDevMenuResponse();
        msg.initWithMessage(originalMessage);
        post(msg);
    }

    private void command_Help(List<String> args, NewChatMessageRequest originalMessage) {
        String str = new StringBuilder().append("The following Commands are available:\n")
                                        .append("-------------------------------------\n")
                                        .append("\"/help\": Shows this Help screen\n")
                                        .append("\"/post <messagename> <*args>\": Posts a message on the bus\n")
                                        .append("\"/devmenu\": Opens the developer menu").toString();
        sendSystemMessageResponse(originalMessage, str);
    }

    private void command_Invalid(List<String> args, NewChatMessageRequest originalMessage) {
        String content = new StringBuilder().append("You typed an invalid command\n")
                                            .append("----------------------------\n")
                                            .append("Type \"/help\" for a list of valid commands").toString();
        sendSystemMessageResponse(originalMessage, content);
    }

    private void command_Post(List<String> args, Message originalMessage) {
        for (ClassPath.ClassInfo cInfo : allClasses) {
            if (cInfo.getSimpleName().equals(args.get(0))) {
                try {
                    Class<?> cls = Class.forName(cInfo.getName());
                    Constructor<?>[] constructors = cls.getConstructors();
                    for (Constructor<?> constr : constructors) {
                        // 0: command name, 1: Class name, 2+: Class constructor args
                        if (constr.getParameterCount() == args.size() - 1) {
                            Message msg = parseArguments(args, constr);
                            msg.initWithMessage(originalMessage);
                            post(msg);
                            break;
                        }
                    }
                } catch (ReflectiveOperationException ignored) {
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
            if ((subString.endsWith(" ") || i == commandString.length()) && !inQuotes) {
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
    private void onDevMenuClassesRequest(DevMenuClassesRequest req) {
        //classes<classname, constructors<arguments<argumentname, argumenttype>>>
        SortedMap<String, List<Map<String, Class<?>>>> classesMap = new TreeMap<>();
        for (ClassPath.ClassInfo cinfo : allClasses) {
            String clsn = cinfo.getSimpleName();
            if (!clsn.toLowerCase().contains("message") && !clsn.toLowerCase().contains("request") && !clsn
                    .toLowerCase().contains("response")) continue;
            List<Map<String, Class<?>>> constructorArgList = new ArrayList<>();
            try {
                Class<?> cls = Class.forName(cinfo.getName());
                for (Constructor<?> cons : cls.getConstructors()) {
                    Map<String, Class<?>> constructorArgs = new LinkedHashMap<>();
                    for (Parameter cls2 : cons.getParameters()) {
                        constructorArgs.put(cls2.getName(), cls2.getType()); // name, java.lang.String
                    }
                    constructorArgList.add(constructorArgs);
                }
            } catch (ClassNotFoundException ignored) {
            }
            classesMap.put(clsn, constructorArgList);
        }
        final DevMenuClassesResponse response = new DevMenuClassesResponse(classesMap);
        response.initWithMessage(req);
        post(response);
    }

    @Subscribe
    private void onDevMenuCommandRequest(DevMenuCommandRequest msg) {
        LOG.debug("Received DevMenuCommandRequest");
        msg.getArgs().add(0, msg.getClassname());
        command_Post(msg.getArgs(), msg);
    }

    @Subscribe
    private void onNewChatCommandMessage(NewChatCommandMessage msg) {
        LOG.debug("Received NewChatCommandMessage");
        List<String> cmd = new LinkedList<>();
        lexCommand(msg, cmd);
        List<String> args = cmd.size() > 0 ? new LinkedList<>(cmd.subList(1, cmd.size())) : new LinkedList<>();

        switch (cmd.get(0)) {
            case "post":
                command_Post(args, msg.getOriginalMessage());
                break;
            case "devmenu":
                command_DevMenu(args, msg.getOriginalMessage());
                break;
            case "help":
                command_Help(args, msg.getOriginalMessage());
                break;
            default:
                command_Invalid(args, msg.getOriginalMessage());
                // TODO: more cases (aka commands)
                // Some shortcuts for common Messages/ Requests
        }
        /* Examples:
        fixme: /post UpdateInventoryRequest . Lobby2
        works: /post UpdateInventoryRequest test2 "test lobby"
         */
    }

    private Message parseArguments(List<String> args, Constructor<?> constr) throws ReflectiveOperationException {
        List<Object> argList = new ArrayList<>();
        Class<?>[] parameters = constr.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            switch (parameters[i].getName()) {
                case "de.uol.swp.common.user.User":
                    if (args.get(i + 1).equals(".") || args.get(i + 1).equals("me")) {
                        // TODO: aktuellen User durchreichen
                    } else {
                        Optional<User> foundUser = userManagement.getUser(args.get(i + 1));
                        if (foundUser.isPresent()) argList.add(foundUser.get());
                    }
                    break;
                case "de.uol.swp.common.lobby.Lobby":
                    Optional<Lobby> foundLobby = lobbyManagement.getLobby(args.get(i + 1));
                    if (foundLobby.isPresent()) argList.add(foundLobby.get());
                    break;
                case "boolean":
                    argList.add(Boolean.parseBoolean(args.get(i + 1)));
                    break;
                case "int":
                    argList.add(Integer.parseInt(args.get(i + 1)));
                    break;
                case "java.lang.String":
                default:
                    argList.add(args.get(i + 1));
                    break;
            }
        }
        return (Message) constr.newInstance(argList.toArray());
    }

    private void sendSystemMessageResponse(NewChatMessageRequest originalMessage, String content) {
        final SystemMessageResponse response = new SystemMessageResponse(originalMessage.getOriginLobby(), content);
        response.initWithMessage(originalMessage);
        post(response);
    }
}
