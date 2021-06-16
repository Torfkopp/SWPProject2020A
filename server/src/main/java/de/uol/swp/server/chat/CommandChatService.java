package de.uol.swp.server.chat;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.exception.ExceptionMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.devmenu.message.NewChatCommandMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

/**
 * Mapping EventBus calls to CommandChatService calls
 *
 * @author Sven Ahrens
 * @see de.uol.swp.server.AbstractService
 * @since 2021-06-15
 */

@SuppressWarnings("UnstableApiUsage")
public class CommandChatService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(ChatService.class);
    private final List<String> gameCommands = new LinkedList<>();

    /**
     * Constructor
     *
     * @param bus The EventBus used throughout the entire server (injected)
     *
     * @since 2021-06-16
     */
    @Inject
    public CommandChatService(EventBus bus) {
        super(bus);
        LOG.debug("CommandChatService started");
    }

    /**
     * Adds a gameCommand to the gameCommands List
     *
     * @param gameCommand The gameCommand to be added to the gameCommands List
     *
     * @author Sven Ahrens
     * @since 2021-06-12
     */
    public void addGameCommand(String gameCommand) {
        gameCommands.add(gameCommand.substring(0, 5));
    }

    /**
     * Handles a incoming chatCommand, in case they are forbidden by the server
     * If chatCommands are forbidden in the server properties and a newChatMessageRequest is found on the eventbus,
     * containing the prefix / (Sign for a command), this method is called by the ChatService in order
     * to decide whether it is a forbidden devCommand, in which case an appropriate Exception is thrown,
     * or a gameCommand. If the latter is the case, this method posts a newChatCommandMessage onto the eventbus.
     *
     * @param req The newChatMessageRequest provided by the ChatService, which contains the command
     *
     * @author Sven Ahrens
     * @since 2021-06-16
     */
    public void newGameOrDevCommand(NewChatMessageRequest req) {
        if (req.getContent().length() > 4 && gameCommands.contains(req.getContent().substring(0, 5))) {
            post(new NewChatCommandMessage(req.getAuthor(), req.getContent().substring(1), req));
        } else {
            ExceptionMessage msg = new ExceptionMessage("This server doesn't allow the use of commands!");
            msg.initWithMessage(req);
            post(msg);
        }
    }
}
