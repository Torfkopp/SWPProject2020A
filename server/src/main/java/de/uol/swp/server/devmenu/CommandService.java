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
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.message.NextPlayerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.request.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.request.LobbyLeaveUserRequest;
import de.uol.swp.common.lobby.request.UserReadyRequest;
import de.uol.swp.common.lobby.response.CreateLobbyResponse;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.devmenu.message.NewChatCommandMessage;
import de.uol.swp.server.game.IGameManagement;
import de.uol.swp.server.lobby.ILobbyManagement;
import de.uol.swp.server.usermanagement.IUserManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    private static final List<String> excludedNames = new ArrayList<>(Arrays.asList("abstract", "store", "context"));
    private static Set<Class<?>> allClasses;
    private final IUserManagement userManagement; //use to find users by name
    private final ILobbyManagement lobbyManagement;
    private final IGameManagement gameManagement;
    private final CountDownLatch latch = new CountDownLatch(1);

    /**
     * Constructor
     *
     * @param eventBus        The EventBus (injected)
     * @param lobbyManagement The LobbyManagement (injected)
     * @param userManagement  The UserManagement (injected)
     */
    @Inject
    public CommandService(EventBus eventBus, ILobbyManagement lobbyManagement, IUserManagement userManagement,
                          IGameManagement gameManagement) {
        super(eventBus);
        this.lobbyManagement = lobbyManagement;
        this.userManagement = userManagement;
        this.gameManagement = gameManagement;
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

    private void command_NextPlayerIfTemp(List<CommandParser.ASTToken> argsAST, NewChatMessageRequest originalMessage) {
        String lobbyName = "Quick Lobby";
        Game game = gameManagement.getGame(lobbyName);
        if (game == null) return;
        User activePlayer = game.getActivePlayer();
        if (activePlayer.getUsername().equals("temp1") || activePlayer.getUsername().equals("temp2")) {
            post(new NextPlayerMessage(lobbyName, game.nextPlayer()));
        }
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
        for (Class<?> cls : allClasses) {
            if (cls.getSimpleName().equals(args.get(0).getString())) {
                try {
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

    private void command_QuickLobby(List<CommandParser.ASTToken> argsAST, NewChatMessageRequest originalMessage) {
        //TODO: docs, simplify if possible
        if (originalMessage.getSession().isEmpty()) return;
        String lobbyName = "Quick Lobby";
        User invoker = originalMessage.getSession().get().getUser();
        lobbyManagement.createLobby(lobbyName, invoker);
        ResponseMessage rsp = new CreateLobbyResponse(lobbyName);
        rsp.initWithMessage(originalMessage);
        post(rsp);

        User temp1, temp2;
        try {
            Optional<User> found = userManagement.getUser("temp1");
            if (found.isEmpty()) throw new RuntimeException("User not found");
            temp1 = found.get();
        } catch (Exception e) {
            temp1 = userManagement.createUser(new UserDTO("temp1", "temp1", "")); //UserDTO(-1, "temp1", "temp1", ""));
        }

        try {
            Optional<User> found = userManagement.getUser("temp2");
            if (found.isEmpty()) throw new RuntimeException("User not found");
            temp2 = found.get();
        } catch (Exception e) {
            temp2 = userManagement.createUser(new UserDTO("temp2", "temp2", "")); //UserDTO(-1, "temp2", "temp2", ""));
        }

        try {
            // wait for the client to be ready to receive the ensuing notifications without NullPointerExceptions
            latch.await(250, TimeUnit.MILLISECONDS);
            post(new LobbyJoinUserRequest(lobbyName, temp1));
            post(new LobbyJoinUserRequest(lobbyName, temp2));
            post(new UserReadyRequest(lobbyName, temp1, true));
            post(new UserReadyRequest(lobbyName, temp2, true));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void command_RemoveTemp(List<CommandParser.ASTToken> argsAST, NewChatMessageRequest originalMessage) {
        //TODO: docs, simplify if possible
        String lobbyName = "Quick Lobby";
        Optional<Lobby> found = lobbyManagement.getLobby(lobbyName);
        if (found.isEmpty()) return;
        for (User user : found.get().getUsers()) {
            if (user.getUsername().equals("temp1") || user.getUsername().equals("temp2")) {
                post(new UserReadyRequest(lobbyName, user, false));
                post(new LobbyLeaveUserRequest(lobbyName, user));
                userManagement.dropUser(user);
            }
        }
    }

    private void command_SkipBots(List<CommandParser.ASTToken> argsAST, NewChatMessageRequest originalMessage) {
        //TODO: docs, simplify if possible
        String lobbyName = "Quick Lobby";
        Game game = gameManagement.getGame(lobbyName);
        if (game == null) return;
        User[] players = game.getPlayers();
        for (User ignored : players) {
            User activePlayer = game.getActivePlayer();
            if (activePlayer.getUsername().equals("temp1") || activePlayer.getUsername().equals("temp2")) {
                post(new NextPlayerMessage(lobbyName, game.nextPlayer()));
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
        allClasses = new HashSet<>();
        Set<Class<?>> clsSet = new HashSet<>();
        ClassLoader cl = getClass().getClassLoader();
        try {
            Set<ClassPath.ClassInfo> clsinSet = ClassPath.from(cl).getTopLevelClassesRecursive("de.uol.swp");
            for (ClassPath.ClassInfo clsin : clsinSet) clsSet.add(Class.forName(clsin.getName()));
            clsSet.stream().filter(cls -> !cls.isInterface())
                  .filter(cls -> excludedNames.stream().noneMatch(cls.getSimpleName().toLowerCase()::contains))
                  .filter(Message.class::isAssignableFrom).forEach(allClasses::add);
        } catch (IOException | ClassNotFoundException e) {
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
        for (Class<?> cls : allClasses) {
            String clsn = cls.getSimpleName();
            List<Map<String, Class<?>>> constructorArgList = new ArrayList<>();
            for (Constructor<?> constructor : cls.getConstructors()) {
                Map<String, Class<?>> constructorArgs = new LinkedHashMap<>();
                for (Parameter parameter : constructor.getParameters()) {
                    constructorArgs.put(parameter.getName(), parameter.getType()); // e.g.: lobbyName, java.lang.String
                }
                constructorArgList.add(constructorArgs);
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
            case "quicklobby":
                command_QuickLobby(argsAST, msg.getOriginalMessage());
                break;
            case "skip":
                command_NextPlayerIfTemp(argsAST, msg.getOriginalMessage());
                break;
            case "skipall":
                command_SkipBots(argsAST, msg.getOriginalMessage());
                break;
            case "removetemp":
                command_RemoveTemp(argsAST, msg.getOriginalMessage());
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
     * @throws java.lang.ReflectiveOperationException Thrown when something goes awry
     *                                                during the reflection process
     */
    private Message parseArguments(List<CommandParser.ASTToken> args, Constructor<?> constr,
                                   Optional<User> currentUser) throws ReflectiveOperationException {
        List<Object> argList = new ArrayList<>();
        Class<?>[] parameters = constr.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            switch (parameters[i].getName()) { //TODO: handle int[] (e.g. DiceCastMessage)
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
