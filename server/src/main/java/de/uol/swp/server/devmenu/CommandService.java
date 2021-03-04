package de.uol.swp.server.devmenu;

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
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

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
    private static Set<Class<?>> allClasses;
    private final IUserManagement userManagement; //use to find users by name
    private final ILobbyManagement lobbyManagement;
    private final IGameManagement gameManagement;
    private final CountDownLatch latch = new CountDownLatch(1);
    private final Map<String, Map.Entry<String, BiConsumer<List<String>, NewChatMessageRequest>>> commandMap = new HashMap<>();

    /**
     * Constructor
     *
     * @param eventBus        The {@link com.google.common.eventbus.EventBus} (injected)
     * @param lobbyManagement The {@link de.uol.swp.server.lobby.ILobbyManagement} (injected)
     * @param userManagement  The {@link de.uol.swp.server.usermanagement.IUserManagement} (injected)
     */
    @Inject
    public CommandService(EventBus eventBus, ILobbyManagement lobbyManagement, IUserManagement userManagement,
                          IGameManagement gameManagement) {
        super(eventBus);
        this.lobbyManagement = lobbyManagement;
        this.userManagement = userManagement;
        this.gameManagement = gameManagement;
        getAllClasses();

        //@formatter:off
        //TODO: build help message on client side
        commandMap.put("devmenu", new AbstractMap.SimpleEntry<>("/devmenu - opens the devmenu", this::command_DevMenu));
        commandMap.put("forceendturn", new AbstractMap.SimpleEntry<>("/forceendturn - shows this help text", this::command_ForceEndTurn));
        commandMap.put("give", new AbstractMap.SimpleEntry<>("/give [lobby] <player> <resource> <amount> - gives a player in an optionally specified lobby a specified amount of a specified resource", this::command_Give));
        commandMap.put("help", new AbstractMap.SimpleEntry<>("/help - shows this help text", this::command_Help));
        commandMap.put("post", new AbstractMap.SimpleEntry<>("/help - shows this help text", this::command_Post));
        commandMap.put("quicklobby", new AbstractMap.SimpleEntry<>("/help - shows this help text", this::command_QuickLobby));
        commandMap.put("removetemp", new AbstractMap.SimpleEntry<>("/help - shows this help text", this::command_RemoveTemp));
        commandMap.put("skip", new AbstractMap.SimpleEntry<>("/help - shows this help text", this::command_NextPlayerIfTemp));
        commandMap.put("skipall", new AbstractMap.SimpleEntry<>("/help - shows this help text", this::command_SkipBots));
        commandMap.put("remove", new AbstractMap.SimpleEntry<>("/remove", this::command_Remove));
        //@formatter:on

        LOG.debug("CommandService started");
    }

    /**
     * Handles the /devmenu command
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to use the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_DevMenu(List<String> args, NewChatMessageRequest originalMessage) {
        OpenDevMenuResponse msg = new OpenDevMenuResponse();
        msg.initWithMessage(originalMessage);
        post(msg);
    }

    private void command_ForceEndTurn(List<String> args, NewChatMessageRequest originalMessage) {
        //TODO: implement this
    }

    private void command_Give(List<String> args, NewChatMessageRequest originalMessage) {
        //TODO: implement this
    }

    /**
     * Handles the /help command
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to use the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_Help(List<String> args, NewChatMessageRequest originalMessage) {
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
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to use the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_Invalid(List<String> args, NewChatMessageRequest originalMessage) {
        String content = new StringBuilder().append("\"" + String.join(" ", args) + "\" isn't a valid command\n")
                                            .append("----------------------------\n")
                                            .append("Type \"/help\" for a list of valid commands").toString();
        sendSystemMessageResponse(originalMessage, content);
    }

    private void command_NextPlayerIfTemp(List<String> args, NewChatMessageRequest originalMessage) {
        //TODO: docs, simplify if possible
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
     * the instance of the class to the {@link com.google.common.eventbus.EventBus}.
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to use the command
     *
     * @see java.lang.Class#getConstructors()
     * @see java.lang.reflect.Constructor#getParameterCount()
     */
    private void command_Post(List<String> args, Message originalMessage) {
        for (Class<?> cls : allClasses) {
            if (!cls.getSimpleName().equals(args.get(0))) continue;
            try {
                Constructor<?>[] constructors = cls.getConstructors();
                for (Constructor<?> constr : constructors) {
                    // 0: command name, 1: Class name, 2+: Class constructor args
                    if (constr.getParameterCount() != args.size() - 1) continue;
                    Message msg = parseArguments(args, constr, (originalMessage.getSession().isPresent() ? Optional.of(
                            originalMessage.getSession().get().getUser()) : Optional.empty()));
                    msg.initWithMessage(originalMessage);
                    post(msg);
                    break;
                }
            } catch (ReflectiveOperationException ignored) {}
            break;
        }
    }

    private void command_QuickLobby(List<String> args, NewChatMessageRequest originalMessage) {
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

    private void command_Remove(List<String> args, NewChatMessageRequest originalMessage) {
        args.set(args.size() - 1, String.valueOf(Integer.parseInt(args.get(args.size() - 1)) * -1));
        command_Give(args, originalMessage);
    }

    private void command_RemoveTemp(List<String> args, NewChatMessageRequest originalMessage) {
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

    private void command_SkipBots(List<String> args, NewChatMessageRequest originalMessage) {
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
     *
     * @see java.lang.Class#isInterface()
     * @see java.lang.Class#getClassLoader()
     * @see java.lang.reflect.Modifier#isAbstract(int)
     * @see java.lang.Class#isAssignableFrom(Class)
     * @see com.google.common.reflect.ClassPath#getTopLevelClassesRecursive(String)
     */
    public void getAllClasses() {
        allClasses = new HashSet<>();
        Set<Class<?>> clsSet = new HashSet<>();
        ClassLoader cl = getClass().getClassLoader();
        try {
            Set<ClassPath.ClassInfo> clsinSet = ClassPath.from(cl).getTopLevelClassesRecursive("de.uol.swp");
            for (ClassPath.ClassInfo clsin : clsinSet) clsSet.add(Class.forName(clsin.getName()));
            clsSet.stream().filter(Message.class::isAssignableFrom) // Only things that implement the Message interface
                  .filter(cls -> !Modifier.isAbstract(cls.getModifiers())) // No Abstract classes
                  .filter(cls -> !cls.isInterface()) // No interfaces
                  .forEach(allClasses::add);
        } catch (IOException | ClassNotFoundException e) {
            // TODO: error handling
        }
    }

    /**
     * Helper method used to lex the String which represents the command and
     * its arguments
     *
     * @param commandString A String representing the command and its arguments
     *
     * @return A List with the
     */
    private List<String> lexCommand(String commandString) {
        List<String> command = new LinkedList<>();
        int start = 0;
        String subString;
        boolean inQuotes = false;
        for (int i = 0; i <= commandString.length(); i++) {
            subString = commandString.substring(start, i);
            if ((subString.endsWith(" ") || i == commandString.length()) && !inQuotes) {
                if (subString.strip().length() == 0) continue;
                command.add(subString.strip());
                start = i;
            } else if (subString.endsWith("\" ")) {
                if (inQuotes) {
                    command.add(subString.replace("\" $", "").strip());
                    start = i;
                }
                inQuotes = !inQuotes;
            }
        }
        return command;
    }

    /**
     * Handles a {@link de.uol.swp.common.devmenu.request.DevMenuClassesRequest}
     * found on the {@link com.google.common.eventbus.EventBus}
     * <p>
     * Will create a Map of class names to a List of their constructors, each
     * as a Map of their parameter names to the parameter's type.
     *
     * @param req The {@link de.uol.swp.common.devmenu.request.DevMenuClassesRequest}
     *            found on the {@link com.google.common.eventbus.EventBus}
     *
     * @see de.uol.swp.common.devmenu.request.DevMenuClassesRequest
     */
    @Subscribe
    private void onDevMenuClassesRequest(DevMenuClassesRequest req) {
        //classes<classname, constructors<arguments<argumentname, argumenttype>>>
        SortedMap<String, List<Map<String, Class<?>>>> classesMap = new TreeMap<>();
        for (Class<?> cls : allClasses) {
            List<Map<String, Class<?>>> constructorArgList = new ArrayList<>();
            for (Constructor<?> constructor : cls.getConstructors()) {
                Map<String, Class<?>> constructorArgs = new LinkedHashMap<>();
                for (Parameter parameter : constructor.getParameters()) {
                    constructorArgs.put(parameter.getName(), parameter.getType()); // e.g.: lobbyName, java.lang.String
                }
                constructorArgList.add(constructorArgs);
            }
            classesMap.put(cls.getSimpleName(), constructorArgList);
        }
        ResponseMessage response = new DevMenuClassesResponse(classesMap);
        response.initWithMessage(req);
        post(response);
    }

    /**
     * Handles a {@link de.uol.swp.common.devmenu.request.DevMenuCommandRequest}
     * found on the {@link com.google.common.eventbus.EventBus}
     * <p>
     * Prepends the requested class name to the List of arguments in the
     * request and then calls
     * {@link de.uol.swp.server.devmenu.CommandService#parseArguments(java.util.List, java.lang.reflect.Constructor, java.util.Optional)}
     * with that prepended list.
     *
     * @param req The {@link de.uol.swp.common.devmenu.request.DevMenuCommandRequest}
     *            found on the {@link com.google.common.eventbus.EventBus}
     *
     * @see de.uol.swp.common.devmenu.request.DevMenuCommandRequest
     */
    @Subscribe
    private void onDevMenuCommandRequest(DevMenuCommandRequest req) {
        LOG.debug("Received DevMenuCommandRequest");
        req.getArgs().add(0, req.getClassname());
        command_Post(req.getArgs(), req);
    }

    /**
     * Handles a {@link de.uol.swp.server.devmenu.message.NewChatCommandMessage}
     * found on the {@link com.google.common.eventbus.EventBus}
     * <p>
     * This means a command was typed into the chat box and the
     * {@link de.uol.swp.server.chat.ChatService} recognised the command prefix.
     * This method decides which method to call based on the command String
     * contained in the message.
     * <p>
     * This method calls on {@link de.uol.swp.server.devmenu.CommandService#lexCommand(String)}
     * to divy up the command String.
     *
     * @param msg The {@link de.uol.swp.server.devmenu.message.NewChatCommandMessage}
     *            found on the {@link com.google.common.eventbus.EventBus}
     *
     * @see de.uol.swp.server.devmenu.message.NewChatCommandMessage
     */
    @Subscribe
    private void onNewChatCommandMessage(NewChatCommandMessage msg) {
        LOG.debug("Received NewChatCommandMessage");
        List<String> cmd = lexCommand(msg.getCommand());
        List<String> args = cmd.size() > 0 ? new LinkedList<>(cmd.subList(1, cmd.size())) : new LinkedList<>();

        var a = commandMap.get(cmd.get(0).trim().toLowerCase());
        if (a != null) a.getValue().accept(args, msg.getOriginalMessage());
        else command_Invalid(args, msg.getOriginalMessage());

        // TODO: more cases (aka commands)
    }

    /**
     * Helper method used to parse the arguments provided to
     * {@link de.uol.swp.server.devmenu.CommandService#command_Post(java.util.List, de.uol.swp.common.message.Message)}
     * <p>
     * This method matches the parameter names provided as their canonical
     * long names (e.g. {@code java.lang.String}) and tries to convert the
     * given argument to that specific type.
     * <p>
     * In the end returns an instance of the class whose constructor was
     * provided
     *
     * @param args        List of Strings to be used as arguments
     * @param constr      The specific {@link java.lang.reflect.Constructor}
     *                    to be used in instantiation
     * @param currentUser The {@link de.uol.swp.common.user.User} who invoked the
     *                    command (to replace '.' or 'me')
     *
     * @return The instance of a {@link de.uol.swp.common.message.Message}
     * subclass as returned by the provided constructor
     *
     * @throws java.lang.ReflectiveOperationException Thrown when something goes awry
     *                                                during the reflection process
     * @see java.lang.reflect.Constructor#getParameterTypes()
     * @see de.uol.swp.server.lobby.ILobbyManagement
     * @see de.uol.swp.server.usermanagement.IUserManagement
     */
    private Message parseArguments(List<String> args, Constructor<?> constr,
                                   Optional<User> currentUser) throws ReflectiveOperationException {
        List<Object> argList = new ArrayList<>();
        Class<?>[] parameters = constr.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (args.get(i + 1).equals("§null") || args.get(i + 1).equals("§n")) argList.add(null);
            else switch (parameters[i].getName()) {
                case "de.uol.swp.common.user.User":
                    if (args.get(i + 1).equals(".") || args.get(i + 1).equals("me")) {
                        if (currentUser.isPresent()) argList.add(currentUser.get());
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
                case "java.util.List":
                default:
                    argList.add(args.get(i + 1));
                    break;
            }
        }
        return (Message) constr.newInstance(argList.toArray());
    }

    /**
     * Helper method used to post a {@link de.uol.swp.common.chat.response.SystemMessageResponse}
     * onto the {@link com.google.common.eventbus.EventBus}
     *
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        that provoked a {@link de.uol.swp.common.chat.SystemMessage}
     * @param content         The content of the {@link de.uol.swp.common.chat.SystemMessage}
     *
     * @see de.uol.swp.common.chat.response.SystemMessageResponse
     */
    private void sendSystemMessageResponse(NewChatMessageRequest originalMessage, String content) {
        ResponseMessage response = new SystemMessageResponse(originalMessage.getOriginLobby(), content);
        response.initWithMessage(originalMessage);
        post(response);
    }
}
