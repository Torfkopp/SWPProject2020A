package de.uol.swp.client.di;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import de.uol.swp.client.*;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.chat.IChatService;
import de.uol.swp.client.game.GameService;
import de.uol.swp.client.game.IGameService;
import de.uol.swp.client.lobby.ILobbyService;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.trade.ITradeService;
import de.uol.swp.client.trade.TradeService;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.I18nWrapper;
import javafx.fxml.FXMLLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Module that provides classes needed by the client.
 *
 * @author Marco Grawunder
 * @since 2019-09-18
 */
@SuppressWarnings("UnstableApiUsage")
public class ClientModule extends AbstractModule {

    final EventBus eventBus = new EventBus();
    final Logger LOG = LogManager.getLogger(ClientModule.class);

    @Override
    protected void configure() {

        //Default Properties
        Properties defaultProps = new Properties();

        //Default settings
        defaultProps.setProperty("lang", "en_GB");
        defaultProps.setProperty("debug.draw_hitbox_grid", "false");
        defaultProps.setProperty("debug.loglevel", "DEBUG");
        defaultProps.setProperty("join_leave_msgs_on", "false");
        defaultProps.setProperty("owner_ready_notifs_on", "false");
        defaultProps.setProperty("owner_transfer_notifs_on", "false");

        //Reading properties-file
        final Properties properties = new Properties(defaultProps);
        final String filepath = "client" + File.separator + "target" + File.separator + "classes" + File.separator + "config.properties";
        try (FileInputStream file = new FileInputStream(filepath)) {
            properties.load(file);
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find config file: " + filepath + "\n----But this is nothing to worry about");
        } catch (IOException e) {
            System.out.println("Error reading config file");
        }

        Level loglevel = Level.toLevel(properties.getProperty("debug.loglevel"));
        LOG.info("Switching to selected LOG-Level in config File: {}", loglevel);
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), loglevel);
        // override io.netty Logger to WARN level (has always been the standard in the log4j2.xml configuration)
        Configurator.setLevel("io.netty", Level.WARN);

        LOG.debug("Selected Language in config File: {}", properties.getProperty("lang"));

        //Reading the language property into a locale
        String[] lang = properties.getProperty("lang").split("_");
        Locale locale;
        switch (lang.length) {
            case 1:
                locale = new Locale(lang[0]);
                break;
            case 2:
                locale = new Locale(lang[0], lang[1]);
                break;
            case 3:
                locale = new Locale(lang[0], lang[1], lang[2]);
                break;
            default:
                System.out.println("Invalid Argument in config option \"lang\"" + "\n----Using UK english");
                locale = Locale.UK;
        }

        //Setting the language
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", locale);

        //Setting the drawHitboxGrid value
        final boolean drawHitboxGrid = Boolean.parseBoolean(properties.getProperty("debug.draw_hitbox_grid"));

        //Setting the join_leave_msgs_on value
        final boolean joinLeaveMsgsOn = Boolean.parseBoolean(properties.getProperty("join_leave_msgs_on"));

        //Setting the owner_ready_notifs_on value
        final boolean ownerReadyNotificationsOn = Boolean.parseBoolean(properties.getProperty("owner_ready_notifs_on"));

        //Setting the owner_transfer_notifs_on value
        final boolean ownerTransferNotificationsOn;
        ownerTransferNotificationsOn = Boolean.parseBoolean(properties.getProperty("owner_transfer_notifs_on"));

        //DI stuff
        install(new FactoryModuleBuilder().implement(SceneManager.class, SceneManager.class)
                                          .build(SceneManagerFactory.class));
        install(new FactoryModuleBuilder().implement(ClientConnection.class, ClientConnection.class)
                                          .build(ClientConnectionFactory.class));
        bind(FXMLLoader.class).toProvider(FXMLLoaderProvider.class);
        bind(EventBus.class).toInstance(eventBus);
        bind(Properties.class).toInstance(properties);
        bind(ResourceBundle.class).toInstance(resourceBundle);
        bindConstant().annotatedWith(Names.named("drawHitboxGrid")).to(drawHitboxGrid);
        bindConstant().annotatedWith(Names.named("joinLeaveMsgsOn")).to(joinLeaveMsgsOn);
        bindConstant().annotatedWith(Names.named("ownerReadyNotificationsOn")).to(ownerReadyNotificationsOn);
        bindConstant().annotatedWith(Names.named("ownerTransferNotificationsOn")).to(ownerTransferNotificationsOn);

        // Scopes.SINGLETON forces Singleton behaviour without @Singleton annotation in the class
        bind(IUserService.class).to(UserService.class).in(Scopes.SINGLETON);
        bind(IChatService.class).to(ChatService.class).in(Scopes.SINGLETON);
        bind(IGameService.class).to(GameService.class).in(Scopes.SINGLETON);
        bind(ILobbyService.class).to(LobbyService.class).in(Scopes.SINGLETON);
        bind(ITradeService.class).to(TradeService.class).in(Scopes.SINGLETON);
        requestStaticInjection(GameRendering.class);
        requestStaticInjection(ClientApp.class);
        requestStaticInjection(I18nWrapper.class);
        requestStaticInjection(SceneManager.class);
    }
}
