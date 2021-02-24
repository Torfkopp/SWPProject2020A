package de.uol.swp.server.devmenu;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.chat.response.SystemMessageResponse;
import de.uol.swp.common.devmenu.CommandParser;
import de.uol.swp.common.devmenu.request.DevMenuClassesRequest;
import de.uol.swp.common.devmenu.request.DevMenuCommandRequest;
import de.uol.swp.common.devmenu.response.DevMenuClassesResponse;
import de.uol.swp.common.devmenu.response.OpenDevMenuResponse;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.devmenu.message.NewChatCommandMessage;
import de.uol.swp.server.lobby.ILobbyManagement;
import de.uol.swp.server.usermanagement.IUserManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Handles commands sent in by a client through the chat
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.server.AbstractService
 * @since 2021-02-22
 */
@SuppressWarnings("UnstableApiUsage")
public class CommandService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(CommandService.class);
    private static final List<String> allowedNames = new ArrayList<>(
            Arrays.asList("event", "message", "request", "response"));
    private static final List<String> excludedNames = new ArrayList<>(Arrays.asList("abstract", "store", "context"));
    private static Set<ClassPath.ClassInfo> allClasses;
    private final IUserManagement userManagement; //use to find users by name
    private final ILobbyManagement lobbyManagement;

    /**
     * Constructor
     *
     * @param eventBus        The EventBus (injected)
     * @param lobbyManagement The LobbyManagement (injected)
     * @param userManagement  The UserManagement (injected)
     */
    @Inject
    public CommandService(EventBus eventBus, ILobbyManagement lobbyManagement, IUserManagement userManagement) {
        super(eventBus);
        this.lobbyManagement = lobbyManagement;
        this.userManagement = userManagement;
        getAllClasses();
        LOG.debug("CommandService started");
    }

    //TODO: Remove method
    //TODO 2: undocumented because of impending removal
    String astToString(List<CommandParser.ASTToken> tokens) {
        String text = "";
        for (CommandParser.ASTToken token : tokens) {
            if (token.hasCollection()) text += "[ " + astToString(token.getAstTokens()) + " ]";
            else text += "(" + token.getString() + ") ";
        }
        return text;
    }

    /**
     * Handles the /devmenu command
     *
     * @param args            List of CommandParser.ASTToken to be used as args //TODO: remove?
     * @param originalMessage The NewChatMessageRequest used to use the command
     */
    private void command_DevMenu(List<CommandParser.ASTToken> args, NewChatMessageRequest originalMessage) {
        OpenDevMenuResponse msg = new OpenDevMenuResponse();
        msg.initWithMessage(originalMessage);
        post(msg);
    }

    /**
     * Handles the /help command
     *
     * @param args            List of CommandParser.ASTToken to be used as args //TODO: remove?
     * @param originalMessage The NewChatMessageRequest used to use the command
     */
    private void command_Help(List<CommandParser.ASTToken> args, NewChatMessageRequest originalMessage) {
        String str = new StringBuilder().append("The following Commands are available:\n")
                                        .append("-------------------------------------\n")
                                        .append("\"/help\": Shows this Help screen\n")
                                        .append("\"/post <messagename> <*args>\": Posts a message on the bus\n")
                                        .append("\"/devmenu\": Opens the developer menu").toString();
        sendSystemMessageResponse(originalMessage, str);
    }

    /**
     * Handles misspelled or nonexistent commands by sending an error message
     *
     * @param args            List of CommandParser.ASTToken to be used as args //TODO: remove?
     * @param originalMessage The NewChatMessageRequest used to use the command
     */
    private void command_Invalid(List<CommandParser.ASTToken> args, NewChatMessageRequest originalMessage) {
        String content = new StringBuilder().append("You typed an invalid command\n")
                                            .append("----------------------------\n")
                                            .append("Type \"/help\" for a list of valid commands").toString();
        sendSystemMessageResponse(originalMessage, content);
    }

    /**
     * Handles the /post command
     * <p>
     * Finds the requested class in the List of allowed classes, figures out
     * which constructor was requested, and, after parsing the arguments, posts
     * the instance of the class to the EventBus.
     *
     * @param args            List of CommandParser.ASTToken to be used as args
     * @param originalMessage the original message
     */
    private void command_Post(List<CommandParser.ASTToken> args, Message originalMessage) {
        for (ClassPath.ClassInfo cInfo : allClasses) {
            if (cInfo.getSimpleName().equals(args.get(0).getString())) {
                try {
                    Class<?> cls = Class.forName(cInfo.getName());
                    Constructor<?>[] constructors = cls.getConstructors();
                    for (Constructor<?> constr : constructors) {
                        // 0: command name, 1: Class name, 2+: Class constructor args
                        if (constr.getParameterCount() == args.size() - 1) {
                            Message msg = parseArguments(args, constr, (originalMessage.getSession().isPresent() ?
                                                                        Optional.of(originalMessage.getSession().get()
                                                                                                   .getUser()) :
                                                                        Optional.empty()));
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

    /**
     * Helper method that filters through all classes in the project modules
     * and returns a list that contains only classes the Developer Menu is
     * allowed to request an instantiation and posting of.
     */
    public void getAllClasses() {
        //TODO: filter for "extends Message" and "not interface"
        ClassLoader cl = getClass().getClassLoader();
        try {
            Set<ClassPath.ClassInfo> clsSet = ClassPath.from(cl).getTopLevelClassesRecursive("de.uol.swp");
            Set<ClassPath.ClassInfo> noExcludedClsSet = new HashSet<>();
            allClasses = new HashSet<>();
            clsSet.stream()
                  .filter(clsin -> excludedNames.stream().noneMatch(clsin.getSimpleName().toLowerCase()::contains))
                  .forEach(noExcludedClsSet::add);
            noExcludedClsSet.stream().filter(clsin -> allowedNames.stream().anyMatch(
                    clsin.getSimpleName().toLowerCase()::contains)).forEach(allClasses::add);
        } catch (IOException e) {
            // TODO: error handling
        }
    }

    /**
     * Handles a DevMenuClassesRequest found on the EventBus
     * <p>
     * Will create a Map of class names to a List of their constructors, each
     * as a Map of their parameter names to the parameter's type.
     *
     * @param req The DevMenuClassesRequest found on the EventBus
     */
    @Subscribe
    private void onDevMenuClassesRequest(DevMenuClassesRequest req) {
        //classes<classname, constructors<arguments<argumentname, argumenttype>>>
        SortedMap<String, List<Map<String, Class<?>>>> classesMap = new TreeMap<>();
        for (ClassPath.ClassInfo cinfo : allClasses) {
            String clsn = cinfo.getSimpleName();
            List<Map<String, Class<?>>> constructorArgList = new ArrayList<>();
            try {
                Class<?> cls = Class.forName(cinfo.getName());
                for (Constructor<?> cons : cls.getConstructors()) {
                    Map<String, Class<?>> constructorArgs = new LinkedHashMap<>();
                    for (Parameter cls2 : cons.getParameters()) {
                        constructorArgs.put(cls2.getName(), cls2.getType()); // e.g.: lobbyName, java.lang.String
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

    /**
     * Handles a DevMenuCommandRequest found on the Eventbus
     * <p>
     * Prepends the requested class name to the List of arguments in the
     * request and then calls {@code command_Post(List<ASTToken>, Message)}
     * with that prepended list.
     *
     * @param req The DevMenuCommandRequest found on the EventBus
     */
    @Subscribe
    private void onDevMenuCommandRequest(DevMenuCommandRequest req) {
        LOG.debug("Received DevMenuCommandRequest");
        req.getArgs().add(0, new CommandParser.ASTToken(CommandParser.ASTToken.Type.UNTYPED, req.getClassname()));
        command_Post(req.getArgs(), req);
    }

    /**
     * Handles a NewChatCommandMessage found on the EventBus
     * <p>
     * This means a command was typed into the chat box and the ChatService
     * recognised the command prefix. This method decides which method to call
     * based on the command String contained in the message.
     * <p>
     * This method uses the CommandParser to parse the String in the message,
     * and then calls the method corresponding to the String in the first
     * position.
     *
     * @param msg The NewChatCommandMessage found on the EventBus
     *
     * @see de.uol.swp.common.devmenu.CommandParser
     * @see de.uol.swp.common.devmenu.CommandParser#parse(java.util.List)
     * @see de.uol.swp.common.devmenu.CommandParser#lex(String)
     */
    @Subscribe
    private void onNewChatCommandMessage(NewChatCommandMessage msg) {
        LOG.debug("Received NewChatCommandMessage");
        List<CommandParser.ASTToken> commandAST = CommandParser.parse(CommandParser.lex(msg.getCommand()));
        List<CommandParser.ASTToken> argsAST =
                commandAST.size() > 0 ? new LinkedList<>(commandAST.subList(1, commandAST.size())) : new LinkedList<>();

        if (!commandAST.get(0).hasCollection()) switch (commandAST.get(0).getString().trim()) {
            case "post":
                command_Post(argsAST, msg.getOriginalMessage());
                break;
            case "devmenu":
                command_DevMenu(argsAST, msg.getOriginalMessage());
                break;
            case "help":
                command_Help(argsAST, msg.getOriginalMessage());
                break;
            case "showast": //TODO: Remove
                sendSystemMessageResponse(msg.getOriginalMessage(), astToString(argsAST));
                break;
            default:
                command_Invalid(argsAST, msg.getOriginalMessage());
                // TODO: more cases (aka commands)
                // Some shortcuts for common Messages/ Requests
                /* Ideas for shortcuts:
                 * 1) create a lobby with 4 members on the spot
                 * 2) ???
                 * 3) Profit.
                 */
        }
    }

    /**
     * Helper method used to parse the arguments provided to {@code command_Post()}
     * <p>
     * This method matches the parameter names provided as their canonical
     * long names (like {@code java.lang.String}) and tries to convert the
     * given argument to that specific type.
     * <p>
     * In the end returns an instance of the class whose constructor was
     * provided
     *
     * @param args        List of CommandParser.ASTToken to be used as args
     * @param constr      The specific Constructor to be used in instantiation
     * @param currentUser The User who invoked the command (to replace '.' or 'me')
     *
     * @return The instance of a Message subclass as returned by the provided
     * constructor
     *
     * @throws ReflectiveOperationException Thrown when something goes awry
     *                                      during the reflection process
     */
    private Message parseArguments(List<CommandParser.ASTToken> args, Constructor<?> constr,
                                   Optional<User> currentUser) throws ReflectiveOperationException {
        List<Object> argList = new ArrayList<>();
        Class<?>[] parameters = constr.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            switch (parameters[i].getName()) {
                case "de.uol.swp.common.user.User":
                    if (args.get(i + 1).getString().equals(".") || args.get(i + 1).getString().equals("me")) {
                        if (currentUser.isPresent()) argList.add(currentUser.get());
                    } else {
                        if (args.get(i + 1).hasCollection()) System.err.println("Bad syntax");
                        else {
                            Optional<User> foundUser = userManagement.getUser(args.get(i + 1).getString());
                            if (foundUser.isPresent()) argList.add(foundUser.get());
                        }
                    }
                    break;
                case "de.uol.swp.common.lobby.Lobby":
                    if (args.get(i + 1).hasCollection()) System.err.println("Bad syntax");
                    else {
                        Optional<Lobby> foundLobby = lobbyManagement.getLobby(args.get(i + 1).getString());
                        if (foundLobby.isPresent()) argList.add(foundLobby.get());
                    }
                    break;
                case "boolean":
                    if (args.get(i + 1).hasCollection()) System.err.println("Bad syntax");
                    else argList.add(Boolean.parseBoolean(args.get(i + 1).getString()));
                    break;
                case "int":
                    if (args.get(i + 1).hasCollection()) System.err.println("Bad syntax");
                    else argList.add(Integer.parseInt(args.get(i + 1).getString()));
                    break;
                case "java.util.List":
                default:
                    if (args.get(i + 1).hasCollection()) System.err.println("Bad syntax");
                    else argList.add(args.get(i + 1).getString());
                    break;
            }
        }
        return (Message) constr.newInstance(argList.toArray());
    }

    /**
     * Helper method used to post a SystemMessageResponse onto the EventBus
     *
     * @param originalMessage The NewChatMessageRequest that provoked a
     *                        SystemMessage
     * @param content         The content of the SystemMessage
     */
    private void sendSystemMessageResponse(NewChatMessageRequest originalMessage, String content) {
        final SystemMessageResponse response = new SystemMessageResponse(originalMessage.getOriginLobby(), content);
        response.initWithMessage(originalMessage);
        post(response);
    }
}
