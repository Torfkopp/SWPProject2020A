package de.uol.swp.server.devmenu;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.chat.response.SystemMessageResponse;
import de.uol.swp.common.devmenu.request.DevMenuClassesRequest;
import de.uol.swp.common.devmenu.request.DevMenuCommandRequest;
import de.uol.swp.common.devmenu.response.DevMenuClassesResponse;
import de.uol.swp.common.devmenu.response.OpenDevMenuResponse;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.game.message.NextPlayerMessage;
import de.uol.swp.common.game.request.EditInventoryRequest;
import de.uol.swp.common.game.request.EndTurnRequest;
import de.uol.swp.common.game.request.RollDiceRequest;
import de.uol.swp.common.game.response.TurnSkippedResponse;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.request.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.request.RemoveFromLobbiesRequest;
import de.uol.swp.common.lobby.request.UserReadyRequest;
import de.uol.swp.common.lobby.response.CreateLobbyResponse;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.user.*;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.devmenu.message.NewChatCommandMessage;
import de.uol.swp.server.game.IGameManagement;
import de.uol.swp.server.game.event.GetUserSessionEvent;
import de.uol.swp.server.lobby.ILobbyManagement;
import de.uol.swp.server.usermanagement.IUserManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
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

    public static final String QUICK_LOBBY_NAME = "§Quick Lobby";
    public static final String TEMP_1_NAME = "§temp1";
    public static final String TEMP_2_NAME = "§temp2";
    private static final Logger LOG = LogManager.getLogger(CommandService.class);
    private static Set<Class<?>> allClasses;
    private final IUserManagement userManagement; //use to find users by name
    private final ILobbyManagement lobbyManagement;
    private final IGameManagement gameManagement;
    private final Map<String, BiConsumer<List<String>, NewChatMessageRequest>> commandMap = new HashMap<>();

    /**
     * Constructor
     *
     * @param eventBus        The {@link com.google.common.eventbus.EventBus} (injected)
     * @param lobbyManagement The {@link de.uol.swp.server.lobby.ILobbyManagement} (injected)
     * @param userManagement  The {@link de.uol.swp.server.usermanagement.IUserManagement} (injected)
     * @param gameManagement  The {@link de.uol.swp.server.game.IGameManagement} (injected)
     *
     * @see de.uol.swp.server.game.IGameManagement
     * @see de.uol.swp.server.lobby.ILobbyManagement
     * @see de.uol.swp.server.usermanagement.IUserManagement
     */
    @Inject
    public CommandService(EventBus eventBus, ILobbyManagement lobbyManagement, IUserManagement userManagement,
                          IGameManagement gameManagement) {
        super(eventBus);
        this.lobbyManagement = lobbyManagement;
        this.userManagement = userManagement;
        this.gameManagement = gameManagement;
        getAllClasses();
        commandMap.put("devmenu", this::command_DevMenu);
        commandMap.put("droptemp", this::command_DropTemp);
        commandMap.put("forceendturn", this::command_ForceEndTurn);
        commandMap.put("give", this::command_Give);
        commandMap.put("help", this::command_Help);
        commandMap.put("post", this::command_Post);
        commandMap.put("quicklobby", this::command_QuickLobby);
        commandMap.put("remove", this::command_Remove);
        commandMap.put("skip", this::command_NextPlayerIfTemp);
        commandMap.put("skipall", this::command_SkipBots);
        commandMap.put("adddummy", this::command_AddDummy);
        //TODO: add help text
        LOG.debug("CommandService started");
    }

    /**
     * Handles the /devmenu command
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_DevMenu(List<String> args, NewChatMessageRequest originalMessage) {
        LOG.debug("Received /devmenu command");
        OpenDevMenuResponse msg = new OpenDevMenuResponse();
        msg.initWithMessage(originalMessage);
        post(msg);
    }

    /**
     * Handles the /droptemp command
     * <p>
     * Usage: {@code /droptemp}
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_DropTemp(List<String> args, NewChatMessageRequest originalMessage) {
        LOG.debug("Received /droptemp command");
        Optional<User> temp1 = userManagement.getUser(TEMP_1_NAME);
        Optional<User> temp2 = userManagement.getUser(TEMP_2_NAME);
        if (temp1.isPresent()) {
            post(new RemoveFromLobbiesRequest(temp1.get()));
            userManagement.dropUser(temp1.get());
        }
        if (temp2.isPresent()) {
            post(new RemoveFromLobbiesRequest(temp2.get()));
            userManagement.dropUser(temp2.get());
        }
    }

    /**
     * Handles the /forceendturn command
     * <p>
     * Usage: {@code /forceendturn <player>}
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_ForceEndTurn(List<String> args, NewChatMessageRequest originalMessage) {
        LOG.debug("Received /forceendturn command");
        if (args.size() != 1 || !originalMessage.isFromLobby()) {
            args.add(0, "forceendturn");
            command_Invalid(args, originalMessage);
            return;
        }
        try {
            args.add(originalMessage.getOriginLobby());
            // roll dice for the skipped player
            Message req = parseArguments(args, RollDiceRequest.class.getConstructors()[0],
                                         Optional.of(originalMessage.getAuthor()));
            post(req);
            // end their turn for them
            req = parseArguments(args, EndTurnRequest.class.getConstructors()[0],
                                 Optional.of(originalMessage.getAuthor()));
            post(req);
            // try to send them a TurnSkippedResponse to disable their buttons, etc.
            Optional<User> user = userManagement.getUser(args.get(0));
            if (user.isEmpty()) return;
            post(new GetUserSessionEvent(user.get(), new TurnSkippedResponse(originalMessage.getOriginLobby())));
        } catch (ReflectiveOperationException ignored) {}
    }

    /**
     * Handles the /give command
     * <p>
     * Usage: {@code /give [lobby] <player> <resource> <amount>}
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_Give(List<String> args, NewChatMessageRequest originalMessage) {
        LOG.debug("Received /give command");
        try {
            if (args.size() == 3) args.add(0, originalMessage.getOriginLobby());
            Message msg = parseArguments(args, EditInventoryRequest.class.getConstructors()[0],
                                         Optional.of(originalMessage.getAuthor()));
            post(msg);
        } catch (Exception ignored) {}
    }

    /**
     * Handles the /help command
     * <p>
     * Usage: {@code /help}
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_Help(List<String> args, NewChatMessageRequest originalMessage) {
        LOG.debug("Received /help command");
        if (args.size() == 0) sendSystemMessageResponse(originalMessage, new I18nWrapper("devmenu.commands.help"));
        else if (commandMap.containsKey(args.get(0))) sendSystemMessageResponse(originalMessage, new I18nWrapper(
                "devmenu.commands.help." + args.get(0).trim().toLowerCase()));
        else sendSystemMessageResponse(originalMessage, new I18nWrapper("devmenu.commands.help.invalid",
                                                                        args.get(0).trim().toLowerCase()));
    }

    /**
     * Handles misspelled or nonexistent commands by sending an error message
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_Invalid(List<String> args, NewChatMessageRequest originalMessage) {
        LOG.debug("Received an invalid command");
        sendSystemMessageResponse(originalMessage, new I18nWrapper("devmenu.commands.invalid", String.join(" ", args)));
    }

    /**
     * Handles the /skip command
     * <p>
     * Usage: {@code /skip}
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_NextPlayerIfTemp(List<String> args, NewChatMessageRequest originalMessage) {
        LOG.debug("Received /skip command");
        if (!originalMessage.isFromLobby()) return;
        String lobbyName = originalMessage.getOriginLobby();
        Game game = gameManagement.getGame(lobbyName);
        if (game == null) return;
        UserOrDummy activePlayer = game.getActivePlayer();
        if (activePlayer.getUsername().equals(TEMP_1_NAME) || activePlayer.getUsername().equals(TEMP_2_NAME)) {
            post(new NextPlayerMessage(lobbyName, game.nextPlayer()));
        }
    }

    /**
     * Handles the /post command
     * <p>
     * Usage: {@code /post <? extends Message> <*args>}
     * <p>
     * Finds the requested class in the List of allowed classes, figures out
     * which constructor was requested, and, after parsing the arguments, posts
     * the instance of the class to the {@link com.google.common.eventbus.EventBus}.
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     * @see java.lang.Class#getConstructors()
     * @see java.lang.reflect.Constructor#getParameterCount()
     */
    private void command_Post(List<String> args, Message originalMessage) {
        LOG.debug("Received /post command");
        for (Class<?> cls : allClasses) {
            if (!cls.getSimpleName().equals(args.get(0))) continue;
            try {
                Constructor<?>[] constructors = cls.getConstructors();
                for (Constructor<?> constr : constructors) {
                    // 0: command name, 1: Class name, 2+: Class constructor args
                    List<String> conArgs =
                            args.size() > 0 ? new LinkedList<>(args.subList(1, args.size())) : new LinkedList<>();
                    if (constr.getParameterCount() != conArgs.size()) continue;
                    Message msg = parseArguments(conArgs, constr, (originalMessage.getSession().isPresent() ?
                                                                   Optional.of(originalMessage.getSession().get()
                                                                                              .getUser()) :
                                                                   Optional.empty()));
                    msg.initWithMessage(originalMessage);
                    post(msg);
                    break;
                }
            } catch (ReflectiveOperationException ignored) {}
            break;
        }
    }

    /**
     * Handles the /quicklobby command
     * <p>
     * {@code /quicklobby}
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_QuickLobby(List<String> args, NewChatMessageRequest originalMessage) {
        LOG.debug("Received /quicklobby command");
        if (originalMessage.getSession().isEmpty()) return;
        User invoker = originalMessage.getSession().get().getUser();
        lobbyManagement.createLobby(QUICK_LOBBY_NAME, invoker);
        ResponseMessage rsp = new CreateLobbyResponse(QUICK_LOBBY_NAME);
        rsp.initWithMessage(originalMessage);
        post(rsp);

        User temp1, temp2;
        try {
            Optional<User> found = userManagement.getUser(TEMP_1_NAME);
            if (found.isEmpty()) throw new RuntimeException("User not found");
            temp1 = found.get();
        } catch (Exception e) {
            temp1 = userManagement.createUser(new UserDTO(-1, TEMP_1_NAME, TEMP_1_NAME, ""));
        }

        try {
            Optional<User> found = userManagement.getUser(TEMP_2_NAME);
            if (found.isEmpty()) throw new RuntimeException("User not found");
            temp2 = found.get();
        } catch (Exception e) {
            temp2 = userManagement.createUser(new UserDTO(-1, TEMP_2_NAME, TEMP_2_NAME, ""));
        }

        post(new LobbyJoinUserRequest(QUICK_LOBBY_NAME, temp1));
        post(new LobbyJoinUserRequest(QUICK_LOBBY_NAME, temp2));
        post(new UserReadyRequest(QUICK_LOBBY_NAME, temp1, true));
        post(new UserReadyRequest(QUICK_LOBBY_NAME, temp2, true));
    }

    /**
     * Handles the /remove command
     * <p>
     * Usage: {@code /remove [lobby] <player> <resource> <amount>}
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_Remove(List<String> args, NewChatMessageRequest originalMessage) {
        LOG.debug("Received /remove command");
        args.set(args.size() - 1, String.valueOf(Integer.parseInt(args.get(args.size() - 1)) * -1));
        command_Give(args, originalMessage);
    }

    /**
     * Handles the /skipall command
     * <p>
     * Usage: {@code /skipall}
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_SkipBots(List<String> args, NewChatMessageRequest originalMessage) {
        LOG.debug("Received /skipall command");
        String lobbyName = originalMessage.getOriginLobby();
        Game game = gameManagement.getGame(lobbyName);
        if (game == null) return;
        UserOrDummy activePlayer = game.getActivePlayer();
        if (activePlayer.getUsername().equals(TEMP_1_NAME) || activePlayer.getUsername().equals(TEMP_2_NAME)) {
            post(new NextPlayerMessage(lobbyName, game.nextPlayer()));
            activePlayer = game.getActivePlayer();
            if (activePlayer.getUsername().equals(TEMP_1_NAME) || activePlayer.getUsername().equals(TEMP_2_NAME)) {
                post(new NextPlayerMessage(lobbyName, game.nextPlayer()));
            }
        }
    }

    /**
     * Handles the /adddummy command
     * <p>
     * Usage: {@code /adddummy}
     *
     * @param args            List of Strings to be used as arguments
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        used to invoke the command
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     */
    private void command_AddDummy(List<String> args, NewChatMessageRequest originalMessage) {
        LOG.debug("Received /adddummy command");
        int dummyAmount;
        if (args.size() > 0) dummyAmount = Integer.parseInt(args.get(0));
        else dummyAmount = 1;
        //TODO: Add Multiple dummies add once
        if (originalMessage.isFromLobby()) {
            String lobbyName = originalMessage.getOriginLobby();
            Optional<Lobby> optLobby = lobbyManagement.getLobby(lobbyName);
            if (optLobby.isPresent()) {
                Lobby lobby = optLobby.get();
                int freeUsers = lobby.getMaxUsers() - lobby.getUserOrDummies().size();
                if (dummyAmount > freeUsers) dummyAmount = freeUsers;
                for (; dummyAmount > 0; dummyAmount--) {
                    post(new LobbyJoinUserRequest(lobbyName, new DummyDTO()));
                }
            }
        } else {
            command_Invalid(args, originalMessage);
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
    private void getAllClasses() {
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
        } catch (IOException | ClassNotFoundException ignored) {}
    }

    /**
     * Helper method used to lex the String which represents the command and
     * its arguments
     *
     * @param commandString A String representing the command and its arguments
     *
     * @return A List with the String properly lexed
     */
    private List<String> lexCommand(String commandString, String separator) {
        List<String> command = new LinkedList<>();
        int start = 0;
        String subString;
        boolean inQuotes = false;
        for (int i = 0; i <= commandString.length(); i++) {
            subString = commandString.substring(start, i);
            if ((subString.endsWith(separator) || i == commandString.length()) && !inQuotes) {
                if (subString.strip().length() == 0) continue;
                command.add(subString.strip());
                start = i;
            } else if (subString.endsWith("\"")) {
                if (inQuotes) {
                    command.add(subString.replace("\"" + separator, "").replace("\"", "").strip());
                    start = i;
                }
                inQuotes = !inQuotes;
            }
        }
        return command;
    }

    /**
     * Helper method used when the separator for lexing is the standard space
     *
     * @see #lexCommand(String, String)
     */
    private List<String> lexCommand(String commandString) {
        return lexCommand(commandString, " ");
    }

    /**
     * Handles a {@link de.uol.swp.common.devmenu.request.DevMenuClassesRequest}
     * found on the {@link com.google.common.eventbus.EventBus}
     * <p>
     * Will create a Map of class names to a List of their constructors, each
     * as a Map of their parameter names to the parameter's type.
     *
     * @param req The DevMenuClassesRequest found on the EventBus
     *
     * @see de.uol.swp.common.devmenu.request.DevMenuClassesRequest
     */
    @Subscribe
    private void onDevMenuClassesRequest(DevMenuClassesRequest req) {
        LOG.debug("Received DevMenuClassesRequest");
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
     * {@link #parseArguments(java.util.List, java.lang.reflect.Constructor, java.util.Optional)}
     * with that prepended list.
     *
     * @param req The DevMenuCommandRequest} found on the EventBus
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
     * This method calls on {@link #lexCommand(String)} to divy up the command
     * String.
     *
     * @param msg The NewChatCommandMessage found on the EventBus
     *
     * @see de.uol.swp.server.devmenu.message.NewChatCommandMessage
     */
    @Subscribe
    private void onNewChatCommandMessage(NewChatCommandMessage msg) {
        LOG.debug("Received NewChatCommandMessage");
        List<String> cmd = lexCommand(msg.getCommand());
        List<String> args = cmd.size() > 0 ? new LinkedList<>(cmd.subList(1, cmd.size())) : new LinkedList<>();

        BiConsumer<List<String>, NewChatMessageRequest> command = commandMap.get(cmd.get(0).trim().toLowerCase());
        if (command != null) command.accept(args, msg.getOriginalMessage());
        else command_Invalid(cmd, msg.getOriginalMessage());
    }

    /**
     * Helper method used to parse the arguments provided to
     * {@link #command_Post(java.util.List, de.uol.swp.common.message.Message)}
     * and arguments used for instantiation of other Message subclasses
     * <p>
     * This method matches the parameter names provided as their canonical
     * long names (e.g. {@code java.lang.String}) and tries to convert the
     * given argument to that specific type.
     * <p>
     * In the end returns an instance of the class whose constructor was
     * provided
     *
     * @param args        List of Strings to be used as arguments
     * @param constr      The specific Constructor to be used in instantiation
     * @param currentUser The User who invoked the command (to replace '.' or 'me')
     *
     * @return The instance of a Message subclass as returned by the provided constructor
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
            if (args.get(i).equals("§null") || args.get(i).equals("§n")) argList.add(null);
            else switch (parameters[i].getName()) {
                case "de.uol.swp.common.user.User":
                    if (args.get(i).equals(".") || args.get(i).equals("me")) {
                        if (currentUser.isPresent()) argList.add(currentUser.get());
                    } else {
                        Optional<User> foundUser = userManagement.getUser(args.get(i));
                        if (foundUser.isPresent()) argList.add(foundUser.get());
                    }
                    break;
                case "de.uol.swp.common.lobby.Lobby":
                    Optional<Lobby> foundLobby = lobbyManagement.getLobby(args.get(i));
                    if (foundLobby.isPresent()) argList.add(foundLobby.get());
                    break;
                case "boolean":
                    argList.add(Boolean.parseBoolean(args.get(i)));
                    break;
                case "int":
                    argList.add(Integer.parseInt(args.get(i)));
                    break;
                case "java.util.List": { // format: [$item1 $item2 $item no. 3 $item4]
                    args.set(i, args.get(i).substring(1, args.get(i).length() - 1));
                    String[] arr = args.get(i).split("\\$");
                    ParameterizedType type = (ParameterizedType) constr.getGenericParameterTypes()[i];
                    Class<?> cls = (Class<?>) type.getActualTypeArguments()[0];
                    List<Object> list = parseList(arr, cls.getName());
                    argList.add(list);
                }
                break;
                case "java.util.Map": { //format: {$key1: value1, $key2: value no. 2, $keyI: valueI}
                    args.set(i, args.get(i).substring(1, args.get(i).length() - 1));
                    String[] arr = args.get(i).split("\\$");
                    ParameterizedType mapType = (ParameterizedType) constr.getGenericParameterTypes()[i];
                    Class<?> keyCls = (Class<?>) mapType.getActualTypeArguments()[0];
                    Class<?> valueCls = (Class<?>) mapType.getActualTypeArguments()[1];
                    Map<Object, Object> map = parseMap(arr, keyCls.getName(), valueCls.getName());
                    argList.add(map);
                }
                break;
                case "de.uol.swp.common.game.map.MapPoint": { //format: y,x
                    List<String> tokens = lexCommand(args.get(i), ",");
                    argList.add(tokens.size() < 1 ? null :
                                new MapPoint(Integer.parseInt(tokens.get(0)), Integer.parseInt(tokens.get(1))));
                }
                break;
                case "de.uol.swp.common.I18nWrapper": { //format: attributeName!replacementString
                    List<String> tokens = lexCommand(args.get(i), "!");
                    argList.add(tokens.size() < 1 ? null : (tokens.size() < 2 ? new I18nWrapper(tokens.get(0)) :
                                                            new I18nWrapper(tokens.get(0), tokens.get(1))));
                }
                break;
                default:
                    argList.add(args.get(i));
                    break;
            }
        }
        return (Message) constr.newInstance(argList.toArray());
    }

    /**
     * Helper method to parse a list when one is required by a constructor
     *
     * @param strings       Array of Strings, each a single item for the list
     *                      (will be parsed to the appropriate Class)
     * @param itemClassName The name of the class serving as the List type
     *
     * @return List of Object (with the correct type hidden behind the Object)
     *
     * @throws java.lang.IllegalArgumentException Thrown when an unsupported
     *                                            List type is provided
     * @implNote Only supports Lists with {@link java.lang.Integer},
     * {@link java.lang.String}, or {@link de.uol.swp.common.user.User}.
     */
    private List<Object> parseList(String[] strings, String itemClassName) {
        List<Object> list = new LinkedList<>();
        if (strings[0].isEmpty()) strings = Arrays.copyOfRange(strings, 1, strings.length);
        switch (itemClassName) {
            case "java.lang.Integer":
                for (String s : strings) list.add(Integer.parseInt(s.trim()));
                break;
            case "java.lang.String":
                for (String s : strings) list.add(s.trim());
                break;
            case "de.uol.swp.common.user.User":
                for (String s : strings) {
                    Optional<User> foundUser = userManagement.getUser(s.trim());
                    if (foundUser.isPresent()) list.add(foundUser.get());
                }
                break;
            case "de.uol.swp.common.chat.ChatMessage": // this is not in my capabilities right now
            default:
                throw new IllegalArgumentException("Unsupported List type");
        }
        return list;
    }

    /**
     * Helper method to parse a map when one is required by a constructor
     *
     * @param strings        Array of Strings, each looking like "key:value"
     *                       or "key: value"
     * @param keyClassName   The name of the class serving as the Map key type
     * @param valueClassName The name of the class serving as the Map value type
     *
     * @return Map of Object, Object (with the correct type hidden behind the
     * Object)
     *
     * @throws java.lang.IllegalArgumentException Thrown when an unsupported
     *                                            key or value type is provided
     * @implNote Only supports Maps with {@link java.lang.String} key type and one of
     * {@link java.lang.Boolean}, {@link java.lang.Integer}, or
     * {@link de.uol.swp.common.lobby.Lobby} as value type.
     */
    private Map<Object, Object> parseMap(String[] strings, String keyClassName, String valueClassName) {
        Map<Object, Object> map = new HashMap<>();
        if (strings[0].isEmpty()) strings = Arrays.copyOfRange(strings, 1, strings.length);
        if (keyClassName.equals("java.lang.String")) {
            switch (valueClassName) {
                case "java.lang.Boolean":
                    for (String s : strings) {
                        String[] kvarr = s.split(":");
                        String valueStr = kvarr[1].trim();
                        StringBuilder valBuilder = new StringBuilder(valueStr);
                        try {
                            valBuilder.replace(valueStr.lastIndexOf(","), valueStr.lastIndexOf(",") + 1, "");
                        } catch (Exception ignored) {}
                        map.put(kvarr[0].trim(), Boolean.parseBoolean(valBuilder.toString()));
                    }
                    break;
                case "java.lang.Integer":
                    for (String s : strings) {
                        String[] kvarr = s.split(":");
                        String valueStr = kvarr[1].trim();
                        StringBuilder valBuilder = new StringBuilder(valueStr);
                        try {
                            valBuilder.replace(valueStr.lastIndexOf(","), valueStr.lastIndexOf(",") + 1, "");
                        } catch (Exception ignored) {}
                        map.put(kvarr[0].trim(), Integer.parseInt(valBuilder.toString()));
                    }
                    break;
                case "de.uol.swp.common.lobby.Lobby":
                    for (String s : strings) {
                        String[] kvarr = s.split(":");
                        String valueStr = kvarr[1].trim();
                        StringBuilder valBuilder = new StringBuilder(valueStr);
                        try {
                            valBuilder.replace(valueStr.lastIndexOf(","), valueStr.lastIndexOf(",") + 1, "");
                        } catch (Exception ignored) {}
                        Optional<Lobby> foundLobby = lobbyManagement.getLobby(valBuilder.toString());
                        if (foundLobby.isEmpty()) throw new RuntimeException("Lobby not found");
                        map.put(kvarr[0].trim(), foundLobby.get());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported Map value type");
            }
            return map;
        } else throw new IllegalArgumentException("Unsupported Map key type");
    }

    /**
     * Helper method used to post a SystemMessageResponse onto the EventBus
     *
     * @param originalMessage The NewChatMessageRequest that provoked a
     *                        SystemMessageResponse
     * @param content         The content of the SystemMessageResponse
     *
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     * @see de.uol.swp.common.chat.response.SystemMessageResponse
     */
    private void sendSystemMessageResponse(NewChatMessageRequest originalMessage, I18nWrapper content) {
        ResponseMessage response = new SystemMessageResponse(originalMessage.getOriginLobby(), content);
        response.initWithMessage(originalMessage);
        LOG.debug("Sending SystemMessageResponse");
        post(response);
    }
}
