package de.uol.swp.server.di;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import de.uol.swp.server.chat.ChatService;
import de.uol.swp.server.chat.store.ChatMessageStore;
import de.uol.swp.server.chat.store.MainMemoryBasedChatMessageStore;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.game.GameService;
import de.uol.swp.server.game.IGameManagement;
import de.uol.swp.server.lobby.ILobbyManagement;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.*;
import de.uol.swp.server.usermanagement.store.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Module that provides classes needed by the Server.
 *
 * @author Marco Grawunder
 * @since 2019-09-18
 */
@SuppressWarnings("UnstableApiUsage")
public class ServerModule extends AbstractModule {

    private final Logger LOG = LogManager.getLogger(ServerModule.class);
    private final EventBus bus = new EventBus();
    private final ChatMessageStore chatMessageStore = new MainMemoryBasedChatMessageStore();

    @Override
    protected void configure() {
        final UserStore store;
        //Default Properties
        java.util.Properties defaultProps = new Properties();

        //Default language
        defaultProps.setProperty("db", "h2");

        //Reading properties-file
        final Properties serverProperties = new Properties(defaultProps);
        final String filepath = "server" + File.separator + "target" + File.separator + "classes" + File.separator + "serverconfig.properties";
        try (FileInputStream file = new FileInputStream(filepath)) {
            serverProperties.load(file);
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find config file: " + filepath + "\n----But this is nothing to worry about");
        } catch (IOException e) {
            System.out.println("Error reading config file");
        }

        LOG.debug("Selected database backend: " + serverProperties.getProperty("db"));
        switch (serverProperties.getProperty("db")) {
            case "h2":
                store = new H2BasedUserStore();
                break;
            case "mainmemory":
                store = new MainMemoryBasedUserStore();
                break;
            case "mysql":
                store = new MySQLBasedUserStore();
                break;
            default:
                System.err.println("Invalid value for 'db' in serverconfig.properties\n----Using h2");
                store = new H2BasedUserStore();
        }

        bind(ChatMessageStore.class).toInstance(chatMessageStore);
        bind(EventBus.class).toInstance(bus);
        bind(Properties.class).toInstance(serverProperties);
        bind(UserStore.class).toInstance(store);

        // Scopes.SINGLETON forces Singleton behaviour without @Singleton annotation in the class
        bind(AuthenticationService.class).in(Scopes.SINGLETON);
        bind(ChatService.class).in(Scopes.SINGLETON);
        bind(IGameManagement.class).to(GameManagement.class).in(Scopes.SINGLETON);
        bind(ILobbyManagement.class).to(LobbyManagement.class).in(Scopes.SINGLETON);
        bind(IUserManagement.class).to(UserManagement.class).in(Scopes.SINGLETON);
        bind(GameService.class).in(Scopes.SINGLETON);
        bind(LobbyService.class).in(Scopes.SINGLETON);
        bind(UserService.class).in(Scopes.SINGLETON);
    }
}
