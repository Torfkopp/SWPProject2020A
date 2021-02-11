package de.uol.swp.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.chat.ChatService;
import de.uol.swp.server.communication.ServerHandler;
import de.uol.swp.server.communication.netty.NettyServerHandler;
import de.uol.swp.server.communication.netty.Server;
import de.uol.swp.server.di.ServerModule;
import de.uol.swp.server.game.GameService;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.*;
import io.netty.channel.ChannelHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class handles the startup of the server and the creation of default users
 * whilst the H2BasedUserStore is still in use.
 *
 * @author Marco Grawunder
 * @author Marvin Drees
 * @see de.uol.swp.server.usermanagement.store.H2BasedUserStore
 * @since 2021-01-26
 */
class ServerApp {

    private static final Logger LOG = LogManager.getLogger(ServerApp.class);

    /**
     * Helper method to create the services the server is using and
     * (for the time being) the test users
     *
     * @param injector The google guice injector used for dependency injection
     * @since 2019-09-18
     */
    private static void createServices(Injector injector) {
        ServerUserService userManagement = injector.getInstance(UserManagement.class);

        // Create 5 test user for ease of development
        for (int i = 0; i < 5; i++) {
            if (userManagement.getUser("test" + i).isEmpty()) {
                userManagement.createUser(new UserDTO("test" + i, "test" + i, "test" + i + "@test.de"));
            }
        }

        // Remark: As these services are not referenced by any other class
        // we will need to create instances here (and inject dependencies)
        injector.getInstance(UserService.class);
        injector.getInstance(AuthenticationService.class);
        injector.getInstance(LobbyService.class);
        injector.getInstance(ChatService.class);
        injector.getInstance(GameService.class);
    }

    /**
     * Main method
     * <p>
     * This method handles the creation of the server components and the start of
     * the server
     *
     * @param args Any arguments given when starting the application, e.g. a port
     *             number
     * @since 2017-03-17
     */
    public static void main(String[] args) throws Exception {
        int port = -1;
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                // Ignore and use default value
            }
        }
        if (port < 0) {
            port = 8889;
        }
        LOG.info("Starting Server on port " + port);

        // create components
        Injector injector = Guice.createInjector(new ServerModule());
        createServices(injector);
        ServerHandler serverHandler = injector.getInstance(ServerHandler.class);
        ChannelHandler channelHandler = new NettyServerHandler(serverHandler);
        Server server = new Server(channelHandler);
        server.start(port);
    }
}
